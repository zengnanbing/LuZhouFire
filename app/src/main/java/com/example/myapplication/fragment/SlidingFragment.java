package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.SubmitActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;

import Utils.ActivityCollector;
import Utils.SPUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import widget.CircleImageView;
import widget.MyCirCleTextView;

/**
 * Created by 澄鱼 on 2016/4/28.
 */
public class SlidingFragment extends Fragment {

    //
//    @BindView(R.id.center_task)
//    RippleView centerTask;
//    @BindView(R.id.tv_setting)
//    RippleView tvSetting;
    @BindView(R.id.tv_img)
    CircleImageView tvImg;
    @BindView(R.id.tv_personim)
    TextView tvPersonim;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tvCheck1)
    TextView tvCheck1;
    @BindView(R.id.tvCheck2)
    TextView tvCheck2;
    @BindView(R.id.tvCheck3)
    TextView tvCheck3;
    @BindView(R.id.check_record)
    View checkRecord;
    @BindView(R.id.logout)
    Button mLogout;
    @BindView(R.id.unCommitted)
    LinearLayout unCommitted;
    private Context context;
    private Boolean Cache;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Cache= (Boolean) SPUtils.get(getActivity(),"cache",false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.sliding_content, null);
        unbinder = ButterKnife.bind(this, view);
        tvPersonim.setText(""+SPUtils.get(context, "id", 0));
        tvPhoneNum.setText((String) SPUtils.get(context, "tel", ""));
        tvLocation.setText((String) SPUtils.get(context, "location", ""));
        tvCheck1.setText(String.format("常规检查：已完成%d项",(int)(100 * Math.random())));
        tvCheck2.setText(String.format("整改检查：已完成%d项",(int)(100 * Math.random())));
        tvCheck3.setText(String.format("举报检查：已完成%d项",(int)(100 * Math.random())));
//        checkRecord.setText((int)(10*Math.random())+"");
        if (Cache){
            checkRecord.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unCommitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SubmitActivity.class));
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                SPUtils.put(getActivity(),"IsLogout",true);

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
