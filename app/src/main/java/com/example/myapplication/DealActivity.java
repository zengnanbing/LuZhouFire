package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.example.myapplication.base.BaseToolbarActivity;
import com.example.myapplication.fragment.HistoryFragment;
import com.example.myapplication.fragment.UploadFileFragment;
import com.example.myapplication.fragment.WebFragment;

import JavaBean.CheckInfo;
import Utils.NetUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Description: 查看历史记录，并可以选择不同的表进行填写
 * @author: cyq7on
 * @date: 2016/8/12 10:56
 * @version: V1.0
 */
public class DealActivity extends BaseToolbarActivity {

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    public CheckInfo.ResultBean.ChildrenBean bean;
    public String type;
    private FragmentTransaction transaction;
    private Fragment historyFragment;
    private WebFragment webFragment,tableInfoFragment;
    private UploadFileFragment upLoadFragment;
    private static final String [] urls = new String[]{
            "http://112.74.37.240:8080/LuZhouFire/excel/daily_check.jsp",
            "http://112.74.37.240:8080/LuZhouFire/excel/trouble_table.jsp",
            "http://112.74.37.240:8080/LuZhouFire/excel/report_table.jsp",
            "http://112.74.37.240:8080/LuZhouFire/excel/transfer_table.jsp",
            "http://112.74.37.240:8080/LuZhouFire/excel/check_table.jsp",
            "file:///android_asset/html/daily_check.html",
            "file:///android_asset/html/trouble_table.html",
            "file:///android_asset/html/report_table.html",
            "file:///android_asset/html/transfer_table.html",
            "file:///android_asset/html/check_table.html"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {
        historyFragment = new HistoryFragment();
        webFragment = new WebFragment();
        tableInfoFragment = new WebFragment();
        tableInfoFragment.table = true;
        upLoadFragment = new UploadFileFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout,historyFragment,
                HistoryFragment.class.getSimpleName());
        transaction.add(R.id.frameLayout,webFragment,
                WebFragment.class.getSimpleName());
        transaction.add(R.id.frameLayout,tableInfoFragment,"tableInfoFragment");
        transaction.add(R.id.frameLayout,upLoadFragment,
                UploadFileFragment.class.getSimpleName());
        transaction.hide(webFragment);
        transaction.hide(tableInfoFragment);
        transaction.hide(upLoadFragment);
        transaction.show(historyFragment);
        transaction.commit();
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        String which = intent.getStringExtra("which");
        bean = (CheckInfo.ResultBean.ChildrenBean)
                intent.getSerializableExtra("bean");
        switch (which) {
            case "0":
                setUpToolbar("常规检查",R.menu.menu_activity_deal0,MODE_BACK);
                break;
            case "1":
                setUpToolbar("整改检查",R.menu.menu_activity_deal1,MODE_BACK);
                break;
            case "2":
                setUpToolbar("举报检查",R.menu.menu_activity_deal2,MODE_BACK);
                break;
            default:
                break;
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void choose(int which) {
        transaction = getSupportFragmentManager().beginTransaction();
        switch (which) {
            case R.string.type_table1:
                if (NetUtils.isNetworkAvailable(DealActivity.this)){
                    webFragment.setUrl(urls[0]);
                }else {
                    webFragment.setUrl(urls[5]);
                }
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                break;
            case R.string.type_table2:
                if (NetUtils.isNetworkAvailable(DealActivity.this)){
                    webFragment.setUrl(urls[1]);
                }else {
                    webFragment.setUrl(urls[6]);
                }
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                break;
            case R.string.type_table3:
                if (NetUtils.isNetworkAvailable(DealActivity.this)){
                    webFragment.setUrl(urls[2]);
                }else {
                    webFragment.setUrl(urls[7]);
                }
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                break;
            case R.string.type_table4:
                if (NetUtils.isNetworkAvailable(DealActivity.this)){
                    webFragment.setUrl(urls[3]);
                }else {
                    webFragment.setUrl(urls[8]);
                }
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                break;
            case R.string.type_table5:
                if (NetUtils.isNetworkAvailable(DealActivity.this)){
                    webFragment.setUrl(urls[4]);
                }else {
                    webFragment.setUrl(urls[9]);
                }
                transaction.hide(tableInfoFragment);
                transaction.hide(historyFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    //改需求，心疼，不要了。。。
    /*@Override
    public boolean onMenuItemClick(MenuItem item) {
        transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.history:
                transaction.hide(webFragment);
                transaction.hide(tableInfoFragment);
                transaction.hide(upLoadFragment);
                transaction.show(historyFragment);
                break;
            case R.id.normal_record:
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                webFragment.setUrl(urls[0]);
                break;
            case R.id.complain:
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                webFragment.setUrl(urls[1]);
                break;
            case R.id.hidden_trouble:
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                webFragment.setUrl(urls[2]);
                break;
            case R.id.transfer:
                transaction.hide(historyFragment);
                transaction.hide(tableInfoFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                webFragment.setUrl(urls[3]);
                break;
            case R.id.pre_record:
                transaction.hide(tableInfoFragment);
                transaction.hide(historyFragment);
                if (upLoadFragment.auth) {
                    transaction.show(webFragment);
                }else {
                    transaction.show(upLoadFragment);
                }
                webFragment.setUrl(urls[4]);
                break;
//            case R.id.take_photo:
//                transaction.hide(historyFragment);
//                transaction.show(upLoadFragment);
//                break;
            default:
                return super.onMenuItemClick(item);
        }
        transaction.commit();
        return true;
    }*/

    @Override
    public void onBackPressed() {
        if (tableInfoFragment.isVisible() && tableInfoFragment.table){
            showFragment("tableInfoFragment",HistoryFragment.class.getSimpleName());
            return;
        }
        if (webFragment.isVisible()) {
            showFragment(WebFragment.class.getSimpleName(),
                    HistoryFragment.class.getSimpleName());
            return;
        }else if (upLoadFragment.isVisible()) {
            showFragment(UploadFileFragment.class.getSimpleName(),
                    HistoryFragment.class.getSimpleName());
            return;
        }
        super.onBackPressed();
    }
}
