package com.example.myapplication.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myapplication.DealActivity;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseFragment;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import Adapter.HistoryAdapter;
import JavaBean.HistoryInfo;
import JavaBean.UrlNet;
import Utils.NetUtils;
import Utils.OKCall;
import Utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * @Description:历史记录
 * @author: cyq7on
 * @date: 2016/8/12 10:55
 * @version: V1.0
 */
public class HistoryFragment extends BaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.btnAdd)
    ImageView btnAdd;
//    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    private HistoryAdapter adapter;
    private List<HistoryInfo.ResultBean.CheckBean> data;
    private List<HistoryInfo.ResultBean.CheckBean> dailyCheckData;
    private List<HistoryInfo.ResultBean.CheckBean> troubleCheckData;
    private boolean dailyCheck=true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
        dailyCheckData = new ArrayList<>();
        troubleCheckData = new ArrayList<>();
        if (dailyCheck){
            data.addAll(dailyCheckData);
        }else {
            data.addAll(troubleCheckData);
        }
        adapter = new HistoryAdapter(data);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d("hidden:" + hidden);
        if (hidden) {
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        radioGroup= (RadioGroup) view.findViewById(R.id.radio_group);
        radioGroup.check(R.id.daily_check);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (R.id.daily_check == i){
                    dailyCheck = true;
                }else if (R.id.trouble_check == i){
                    dailyCheck = false;
                    data.addAll(troubleCheckData);
                }
                getData();
            }
        });
        Logger.d("onCreateView");
        return view;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                Logger.d("true");
            }
        });
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.isFirstOnly(false);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                FragmentTransaction transaction = getFragmentManager().
                        beginTransaction();
                WebFragment tableInfoFragment = (WebFragment) getFragmentManager().
                        findFragmentByTag("tableInfoFragment");
                tableInfoFragment.table = true;
                String type = dailyCheck ? "firetableid=" : "troubletableid=";
                String tableId = dailyCheck ? data.get(i).getFiretableid():data.get(i).getTroubletableid();
                        tableInfoFragment.setUrl(UrlNet.HISTORY_TABLE +
                        "?checkdate=" + data.get(i).getCheckdate() + "&" +
                        type + tableId);
                transaction.hide(HistoryFragment.this);
                transaction.show(tableInfoFragment);
                transaction.commit();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,
                Color.BLUE, Color.YELLOW);
    }


    @Override
    protected void initData() {
        getData();
    }

    private void getData() {
        final DealActivity dealActivity = (DealActivity) context;
        OkHttpUtils.get()
                .url(UrlNet.HISTORY)
                .addParams("unitid", "" + dealActivity.bean.getId())
                .build()
                .execute(new OKCall<HistoryInfo>(HistoryInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Logger.d("false");
                            }
                        });
                        if (NetUtils.isNetworkAvailable(context)) {
                            ToastUtil toastUtil = new ToastUtil();
                            toastUtil.Short(context, e.getMessage()).
                                    setToastColor(Color.WHITE, 0xffffc107).show();
                        }else {
                            ToastUtil toastUtil = new ToastUtil();
                            toastUtil.Short(context, "网络出错啦").
                                    setToastColor(Color.WHITE, 0xffffc107).show();
                        }

                    }

                    @Override
                    public void onResponse(HistoryInfo response, int id) {
                        if (response.result.getDailyCheck().size() == 0 && response.result.getTroubleCheck().size() == 0) {
                            cardView.setVisibility(View.VISIBLE);
                        } else {
                            cardView.setVisibility(View.GONE);
                            if (data.size() > 0) {
                                data.clear();
                            }
                            if (troubleCheckData.size() > 0) {
                                troubleCheckData.clear();
                            }
                            if (dailyCheck){
                                data.addAll(response.result.getDailyCheck());
                            }else {
                                data.addAll(response.result.getTroubleCheck());
                            }

                            adapter.notifyDataSetChanged();
                        }
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Logger.d("false");
                            }
                        });
                    }
                });
    }

    @OnClick(R.id.btnAdd)
    public void onClick() {
        final DealActivity dealActivity = (DealActivity) context;
        final String[] items = new String[]{getString(R.string.type_table1),
                getString(R.string.type_table2), getString(R.string.type_table3),
                getString(R.string.type_table4), getString(R.string.type_table5)};
        new AlertDialog.Builder(dealActivity).setTitle("请添加")
                .setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        dealActivity.choose(R.string.type_table1);
                                        break;
                                    case 1:
                                        dealActivity.choose(R.string.type_table2);
                                        break;
                                    case 2:
                                        dealActivity.choose(R.string.type_table3);
                                        break;
                                    case 3:
                                        dealActivity.choose(R.string.type_table4);
                                        break;
                                    case 4:
                                        dealActivity.choose(R.string.type_table5);
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
    }


}
