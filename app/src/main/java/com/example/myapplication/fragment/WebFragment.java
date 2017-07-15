package com.example.myapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.myapplication.DealActivity;
import com.example.myapplication.R;
import com.example.myapplication.SignatureActivity;
import com.example.myapplication.base.BaseFragment;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.ProgressView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import JavaBean.CheckInfo;
import Utils.SPUtils;
import Utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * @Description: webView加载网页
 * @author: cyq7on
 * @date: 2016/8/1 9:48
 * @version: V1.0
 */
public class WebFragment extends BaseFragment {
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.loadingview)
    ProgressView loadingview;
    protected WebSettings webViewSettings;
    @BindView(R.id.cbPhoto)
    CheckBox cbPhoto;
    @BindView(R.id.cbSign)
    CheckBox cbSign;
    //是否是表单详情页
    public boolean table = false;
    @BindView(R.id.ll)
    LinearLayout ll;
    private CheckInfo.ResultBean.ChildrenBean bean;
    private String property;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_base, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        loadingview.setVisibility(View.GONE);
        webView.setWebChromeClient(new WebChromeClient());
        webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        webViewSettings.setUseWideViewPort(true);
        //设置默认加载的可视范围是大视野范围
        webViewSettings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String policeStation = (String) SPUtils.get(context, "location", "");
                int userId = (int) SPUtils.get(context, "id", 0);
                Logger.d(bean.getTitle());
                Logger.d(policeStation);
                Logger.d(bean.getAddress());

                webView.loadUrl("javascript:getUnit(" +
                        "'" + policeStation + "'" + "," +
                        "'" + property + "'" + "," +
                        "'" + bean.getId() + "'" + "," +
                        "'" + bean.getTitle() + "'" + "," +
                        "'" + bean.getAddress() + "'" + "," +
                        "'" + bean.getMaster() + "'" + "," +
                        "'" + userId + "'" +
                        ")");
            }
        });
        webView.addJavascriptInterface(this, "android");
        if (table) {
            ll.setVisibility(View.GONE);
            return;
        }
        cbSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbSign.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, SignatureActivity.class);
                            startActivityForResult(intent, UploadFileFragment.SIGNATURE);
                        }
                    }, 1000);
                    webView.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" + ")");
                }
            }
        });
        cbPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPhoto.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFragment(UploadFileFragment.class.getSimpleName());
                        }
                    }, 1000);
                }
            }
        });
    }

    @JavascriptInterface
    public void getFormId(final String id) {
        SPUtils.put(context, "formId", id);
        Logger.d(id);
    }

    @JavascriptInterface
    public void commitSuccess() {
        showFragment(HistoryFragment.class.getSimpleName());
        webView.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil toastUtil = new ToastUtil();
                toastUtil.Short(context, "已提交").
                        setToastColor(Color.WHITE, 0xffffc107).show();
            }
        });
    }

    @JavascriptInterface
    public void commitFailed() {
        showFragment(HistoryFragment.class.getSimpleName());
        webView.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil toastUtil = new ToastUtil();
                toastUtil.Short(context, "提交失败").
                        setToastColor(Color.WHITE, 0xffffc107).show();
            }
        });
    }

    @JavascriptInterface
    public void save(String s) {
        SPUtils.put(getActivity(),s,s);
        SPUtils.put(getActivity(),"cache",true);
        String out= (String) SPUtils.get(getActivity(),s,"");
        ToastUtil toastUtil = new ToastUtil();
        toastUtil.Short(context, "已保存").
                setToastColor(Color.WHITE, 0xffffc107).show();

    }

    @Override
    protected void initData() {
//        webView.loadUrl("https://www.baidu.com/");
        bean = ((DealActivity) context).bean;
        property = ((DealActivity) context).type;
    }

    public void setUrl(String url) {
        Logger.d(url);
        webView.loadUrl(url);
    }
}
