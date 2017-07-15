package com.example.myapplication.fragment;


import android.Manifest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseFragment;
import com.orhanobut.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import JavaBean.UrlNet;
import Utils.FileUtils;
import Utils.NetUtils;
import Utils.SPUtils;
import Utils.ScreenUtil;
import Utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
//import permissions.dispatcher.RuntimePermissions;

/**
 * @Description: 上传图片
 * @author: cyq7on
 * @date: 2016/8/20 18:25
 * @version: V1.0
 */

//@RuntimePermissions
public class UploadFileFragment extends BaseFragment {


    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.loadingview)
    ProgressView loadingview;
    @BindView(R.id.btnUpload)
    Button btnUpload;
    //自拍照片
    private Bitmap bitmap;
    private static final int TAKE_PHOTO = 1;
    public static final int SIGNATURE = 2;
    //是否拍照认证过
    public boolean auth = false;
    private String mFilePath;
    private String mPictureName;
    private String URI;
    private Uri uri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uplod, container, false);
        if (savedInstanceState != null) {
            uri = Uri.parse(savedInstanceState.getString("uri"));
        }
//        btnUpload = (Button) view.findViewById(R.id.btnUpload);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoad();
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            return;
        }
        if (auth) {
            openTakePhoto();
        } else {
            //网络状态不好时，Android js交互还没完成，故这里采用延时
            btnUpload.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog();
                }
            }, 1000);

        }
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    private void showDialog() {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("拍照确认")
                .setMessage("如果确认填表，必须先拍照确认身份和地点！")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        UploadFileFragmentPermissionsDispatcher.openTakePhotoWithCheck(UploadFileFragment.this);
                        openTakePhoto();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showFragment(HistoryFragment.class.getSimpleName());
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

//    @NeedsPermission(Manifest.permission.CAMERA)
    void openTakePhoto() {
        /**
         * 在启动拍照之前最好先判断一下sdcard是否可用
         */
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) {   //如果可用
            // 获取SD卡路径
            mFilePath = FileUtils.getPath();
            // 文件名
            String firetableid = (String) SPUtils.get(context, "formId", "-1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            Date currentData = new Date(System.currentTimeMillis());
            if (!auth) {
                mPictureName = firetableid + "_CheckerPic.jpeg";
            } else {
                mPictureName = firetableid + "_" + sdf.format(currentData) + ".jpeg";
            }
            Logger.d(mPictureName);
//            FileUtils.createFolder(mFilePath);
            mFilePath = mFilePath + "/" + mPictureName;
//            File outImage = new File(mFilePath,mPictureName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 加载路径
//            uri = Uri.fromFile(new File(mFilePath));
            uri = Uri.fromFile(new File(mFilePath));
            Logger.d(uri);
            URI = uri.toString();
            // 指定存储路径，这样就可以保存原图了
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            showToastShort("SD卡不可用");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SIGNATURE:
                break;
            case TAKE_PHOTO:
                int width = ScreenUtil.dip2px(context, 400f);
//                bitmap = FileUtils.decodeBitmapFromFile(mFilePath, width, width);
//                FileUtils.CompressBitmap(bitmap, mFilePath);
                if (resultCode == 0) {
                    if (auth) {
                        showFragment(WebFragment.class.getSimpleName());
                    } else {
                        showFragment(HistoryFragment.class.getSimpleName());
                    }
                    break;
                } else {
                    bitmap = FileUtils.decodeBitmapFromFile(mFilePath, width, width);
                    FileUtils.CompressBitmap(bitmap, mFilePath);

                    img.setVisibility(View.VISIBLE);
                    btnUpload.setVisibility(View.VISIBLE);
                    img.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri", URI);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        UploadFileFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }

//    @OnShowRationale(Manifest.permission.CAMERA)
//    void ratio(PermissionRequest request) {
//        request.proceed();
//    }
//
//    @OnPermissionDenied(Manifest.permission.CAMERA)
//    void deny() {
//        Toast.makeText(this.getActivity(), "未授权", Toast.LENGTH_SHORT).show();
//    }
//
//    @OnNeverAskAgain(Manifest.permission.CAMERA)
//    void askAgain() {
//        Toast.makeText(this.getActivity(), "不再询问", Toast.LENGTH_SHORT).show();
//    }

    void upLoad() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("上传中...");
        dialog.show();
        Map<String, String> params = new HashMap<>(2);
        String id = (String) SPUtils.get(context, "formId", "-1");
        params.put("firetableid", id);
        if (auth) {
            params.put("PicType", "UnitPic");
        } else {
            params.put("PicType", "CheckerPic");
        }
        Logger.d(params);
        File file = new File(mFilePath);
//        有网络
        if (NetUtils.isNetworkAvailable(getActivity())) {
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
                            toastUtil.Short(context, "失败").
                                    setToastColor(Color.WHITE, 0xffffc107).show();
                            PgyCrashManager.reportCaughtException(context, e);
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dialog.dismiss();
                            auth = true;
                            String info;
                            if (response.contains("success")) {
                                info = "上传成功";
                                FileUtils.deleteFile(mFilePath);
                            } else {
                                info = "上传失败";
                            }
                            ToastUtil toastUtil = new ToastUtil();
                            toastUtil.Short(context, info).
//                      setToastBackground(Color.WHITE, R.drawable.toast).show();
        setToastColor(Color.WHITE, 0xffffc107).show();
                            img.setVisibility(View.INVISIBLE);
                            btnUpload.setVisibility(View.INVISIBLE);
                            auth = true;
                            showFragment(WebFragment.class.getSimpleName());
                            Logger.d(response);
                        }
                    });

        } else {
//            无网络
            dialog.dismiss();
            auth = true;
            String info;
            info = "无法连接网络，已保存";
            ToastUtil toastUtil = new ToastUtil();
            toastUtil.Short(context, info).
//                      setToastBackground(Color.WHITE, R.drawable.toast).show();
        setToastColor(Color.WHITE, 0xffffc107).show();
            img.setVisibility(View.INVISIBLE);
            btnUpload.setVisibility(View.INVISIBLE);
            auth = true;
            showFragment(WebFragment.class.getSimpleName());

        }
    }

}
