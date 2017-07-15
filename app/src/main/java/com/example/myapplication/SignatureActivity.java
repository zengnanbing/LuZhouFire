package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.base.BaseActivity;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.orhanobut.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;
import com.rey.material.widget.ProgressView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import JavaBean.UrlNet;
import Utils.FileUtils;
import Utils.NetUtils;
import Utils.SPUtils;
import Utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SignatureActivity extends BaseActivity {

    @BindView(R.id.loadingview)
    ProgressView loadingview;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_layout);
        ButterKnife.bind(this);
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                // 获取SD卡路径
                path = FileUtils.getPath();
                // 文件名
                String firetableid=(String) SPUtils.get(getApplicationContext(),"formId","-1");
                String pictureName=firetableid+"_"+"SignPic.jpeg";
                path=path + "/" + pictureName;
                FileUtils.CompressBitmap(signatureBitmap, path);
//                loadingview.setVisibility(View.VISIBLE);
                if (NetUtils.isNetworkAvailable(getApplicationContext())) {

                    final ProgressDialog dialog = new ProgressDialog(SignatureActivity.this);
                    dialog.setMessage("上传中...");
                    dialog.show();
                    Map<String, String> params = new HashMap<>(2);
                    String id = (String) SPUtils.get(getApplicationContext(), "formId", "-1");
                    params.put("firetableid", id);
                    params.put("PicType", "SignPic");
                    Logger.d(params);
                    File file = new File(path);
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
                                    toastUtil.Short(getApplicationContext(), "失败").
                                            setToastColor(Color.WHITE, 0xffffc107).show();
                                    PgyCrashManager.reportCaughtException(getApplicationContext(), e);
                                    Logger.e(e.getMessage());
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    dialog.dismiss();
                                    String info;
                                    if (response.contains("success")) {
                                        info = "上传成功";
                                        FileUtils.deleteFile(path);
                                    } else {
                                        info = "上传失败";
                                    }
                                    ToastUtil toastUtil = new ToastUtil();
                                    toastUtil.Short(getApplicationContext(), info).
//                              setToastBackground(Color.WHITE, R.drawable.toast).show();
        setToastColor(Color.WHITE, 0xffffc107).show();
                                    finish();
                                    Logger.d(response);
                                }
                            });
                }else {
                    File file = new File(path);
                    ToastUtil toastUtil = new ToastUtil();
                    toastUtil.Short(getApplicationContext(), "保存成功").
//                              setToastBackground(Color.WHITE, R.drawable.toast).show();
        setToastColor(Color.WHITE, 0xffffc107).show();
                    finish();

                }
            }
        });
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
//        File file = new File(Environment.getExternalStorageDirectory(), "sign");

//        try {
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }

        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", 1));

            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        SignatureActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));

            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
