package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.fragment.WebFragment;
import com.orhanobut.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JavaBean.UrlNet;
import Utils.FileUtils;
import Utils.SPUtils;
import Utils.ToastUtil;
import okhttp3.Call;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

//@RuntimePermissions
public class SubmitActivity extends BaseActivity {

    private WebView mWebView;
    protected WebSettings webViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        mWebView = (WebView) findViewById(R.id.submit_webView);
        mWebView.setWebChromeClient(new WebChromeClient());
        webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        webViewSettings.setUseWideViewPort(true);
        //设置默认加载的可视范围是大视野范围
        webViewSettings.setLoadWithOverviewMode(true);
        mWebView.addJavascriptInterface(this, "android");
        mWebView.loadUrl("file:///android_asset/html/Cache.html");

    }
    @JavascriptInterface
    public String getKey() {
        String key="";
        Map<String, ?> map = SPUtils.getAll(SubmitActivity.this);
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getKey().contains("+")) {
                key=key+entry.getValue()+",";
            }
        }
        Logger.d(key);
        return key;
    }

    @JavascriptInterface
   public void uploadPicture() {
        // 文件名
        List<String> path = getPictures(FileUtils.getPath());
        for (String s : path) {
//            SubmitActivityPermissionsDispatcher.uploadWithCheck(SubmitActivity.this, s);
            upload(s);
            Logger.d(s);
        }
    }

    @JavascriptInterface
    public void ClearCache(String key){
        SPUtils.remove(SubmitActivity.this,key);
        SPUtils.put(this,"cache",false);
    }

//    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void upload(final String mFilePath) {
        final ProgressDialog dialog = new ProgressDialog(SubmitActivity.this);
        dialog.setMessage("上传中...");
        dialog.show();
        Map<String, String> params = new HashMap<>(2);
        String id = mFilePath.split("_")[0];
        params.put("firetableid", id);
        if (mFilePath.contains("CheckerPic")) {
            params.put("PicType", "CheckerPic");
        } else if (mFilePath.contains("SignPic")) {
            params.put("PicType", "SignPic");
        } else {
            params.put("PicType", "UnitPic");
        }
        Logger.d(params);
        File file = new File(mFilePath);
        OkHttpUtils.post()
                .addFile("file", "img.jpeg", file)
                .url(UrlNet.UPLOAD_IMG)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        ToastUtil toastUtil = new ToastUtil();
                        toastUtil.Short(SubmitActivity.this, "失败").
                                setToastColor(Color.WHITE, 0xffffc107).show();
                        PgyCrashManager.reportCaughtException(SubmitActivity.this, e);
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialog.dismiss();
                        String info;
                        if (response.contains("success")) {
                            info = "上传成功";
                            FileUtils.deleteFile(mFilePath);
                        } else {
                            info = "上传失败";
                        }
                        ToastUtil toastUtil = new ToastUtil();
                        toastUtil.Short(SubmitActivity.this, info).
//                      setToastBackground(Color.WHITE, R.drawable.toast).show();
        setToastColor(Color.WHITE, 0xffffc107).show();
                    }
                });

    }

    public List<String> getPictures(final String path) {
        List<String> list = new ArrayList<String>();
        File file = new File(path);
        File[] allFiles = file.listFiles();
        if (allFiles == null) {
            return null;
        }
        for (int k = 0; k < allFiles.length; k++) {
            final File fi = allFiles[k];
            if (fi.isFile()) {
                int idx = fi.getPath().lastIndexOf(".");
                if (idx <= 0) {
                    continue;
                }
                String suffix = fi.getPath().substring(idx);
                if (suffix.toLowerCase().equals(".jpeg")) {
                    list.add(fi.getPath());
                }
            }
        }
        return list;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        SubmitActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
}