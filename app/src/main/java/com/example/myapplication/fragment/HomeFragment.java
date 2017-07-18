package com.example.myapplication.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.myapplication.DealActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import Adapter.CustomExpandableListAdapter;
import JavaBean.CheckInfo;
import JavaBean.UrlNet;
import Utils.FileUtils;
import Utils.NetUtils;
import Utils.OKCall;
import Utils.OKCallNew;
import Utils.SPUtils;
import Utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * @Description: 主页面后三个fragment
 * @author: cyq7on
 * @date: 2016/8/1 19:54
 * @version: V1.0
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private CustomExpandableListAdapter expandableListAdapter;
    private String which;
    private List<CheckInfo.ResultBean> result = new ArrayList<>();
    private Unbinder unbinder;
    private CheckInfo.ResultBean.ChildrenBean bean;
    private String type;//店铺类型


    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        which = getArguments().getString("which");
        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), result);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && result.size() == 0) {
            getData(which);
        }
        Logger.d(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public void search(String keyWords) {
        if (result.size() == 0) {
            return;
        }
        for (int i = 0; i < result.size(); i++) {
            List<CheckInfo.ResultBean.ChildrenBean> children = result.get(i).getChildren();
            for (int j = children.size() - 1; j >= 0; j--) {
                CheckInfo.ResultBean.ChildrenBean bean = children.get(j);
                String title = bean.getTitle();
                if (!title.contains(keyWords)) {
                    children.remove(j);
                }
            }
        }
        expandableListAdapter.notifyDataSetChanged();
    }


    private void initView() {
        expandableListView.setAdapter(expandableListAdapter);
        //子条目点击监听
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                bean = expandableListAdapter.getChild(groupPosition, childPosition);
                type = result.get(groupPosition).getType();
                Logger.d(type);
//                String childId = result.get(groupPosition).
//                        getChildren().get(childPosition).getId();
//                String name = result.get(groupPosition).
//                        getChildren().get(childPosition).getTitle();
                choose();
                return true;
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,
                Color.BLUE, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(which);
            }
        });
        if (result.size() == 0) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    Logger.d("true");
                }
            });
        }
    }

    private void choose() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        new AlertDialog.Builder(mainActivity).setTitle("请选择")
                .setSingleChoiceItems(new String[]{"查看详情", "查看位置"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(mainActivity, DealActivity.class);
//                                        intent.putExtra("id", id);
                                        intent.putExtra("bean", bean);
                                        intent.putExtra("type", type);
                                        intent.putExtra("which",
                                                HomeFragment.this.which);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        if (NetUtils.isNetworkAvailable(mainActivity)) {
                                            mainActivity.changeFragment(0);
                                            mainActivity.search(bean.getTitle());
                                            break;
                                        } else {
                                            ToastUtil toastUtil = new ToastUtil();
                                            toastUtil.Short(mainActivity, "网络出错啦").
                                                    setToastColor(Color.WHITE, 0xffffc107).show();
                                            break;
                                        }

                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void getData(String type) {

        if (NetUtils.isNetworkAvailable(getActivity())) {
            OkHttpUtils.get()
                    .url(UrlNet.GET_UNIT)
                    .addParams("policestation", (String) SPUtils.get(getContext(), "location", ""))
                    .addParams("type", which)
                    .build()
                    .execute(new OKCallNew<CheckInfo>(CheckInfo.class, type) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
//                            Logger.d(e.getMessage());
                            Toast.makeText(getContext(), "失败", Toast.LENGTH_SHORT).show();
                            if (swipeRefreshLayout == null) {
                                return;
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            Logger.d("false");
                        }

                        @Override
                        public void onResponse(CheckInfo response, int id) {
                            if (result.size() > 0) {
                                result.clear();
                            }
                            result.addAll(response.getResult());
                            expandableListAdapter.notifyDataSetChanged();
                            if (swipeRefreshLayout == null) {
                                return;
                            }
                            swipeRefreshLayout.setRefreshing(false);

//                          缓存数据
                            Gson gs = new Gson();
                            String out = gs.toJson(response);
//                             获取SD卡路径
                            String path = FileUtils.getPath();

                            switch (which) {
                                case "0":
                                    // 文件名
                                    path = path + "/CheckInfo1.txt";
                                    FileUtils.writeFile(path, out);
                                    break;
                                case "1":
                                    // 文件名
                                    path = path + "/" + "CheckInfo2.txt";
                                    FileUtils.writeFile(path, out);
                                    break;
                                case "2":
                                    // 文件名
                                    path = path + "/" + "CheckInfo3.txt";
                                    FileUtils.writeFile(path, out);
                                    break;
                            }
                            Logger.d("false");
                        }
                    });

        } else {
            CheckInfo checkInfo = null;
            String path = FileUtils.getPath();
            switch (type) {
                case "0":
                    path = path + "/" + "CheckInfo1.txt";
                    checkInfo = new Gson().fromJson(FileUtils.readFile(path), CheckInfo.class);
                    break;
                case "1":
                    path = path + "/" + "CheckInfo2.txt";
                    checkInfo = new Gson().fromJson(FileUtils.readFile(path), CheckInfo.class);
                    break;
                case "2":
                    path = path + "/" + "CheckInfo3.txt";
                    checkInfo = new Gson().fromJson(FileUtils.readFile(path), CheckInfo.class);
                    break;
            }
            if (result.size() > 0) {
                result.clear();
            }
            if (checkInfo != null) {
                result.addAll(checkInfo.getResult());
            }
            expandableListAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout == null) {
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            Logger.d("false");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
