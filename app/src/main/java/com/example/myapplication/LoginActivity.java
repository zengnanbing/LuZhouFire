package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.base.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.ProgressView;
import com.zhy.http.okhttp.OkHttpUtils;

import JavaBean.UrlNet;
import JavaBean.UserInfo;
import Utils.NetUtils;
import Utils.OKCall;
import Utils.SPUtils;
import Utils.mIntent;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by 澄鱼 on 2016/4/30.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.register)
    TextView register;
    private Button but_login;
    @ViewInject(R.id.loadingview)
    private ProgressView loadingView;
    @ViewInject(R.id.username)
    private EditText usercounter;  //账号输入框
    @ViewInject(R.id.username2)
    private EditText password;  //密码输入框
    @ViewInject(R.id.login_checbox)
    private CheckBox checbox;   //记住密码
    public final static int REQUEST_REGISTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏沉浸栏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ViewUtils.inject(this); //注入view和事件

        initUI();
        initEvent();

    }

    private void initUI() {

        but_login = (Button) findViewById(R.id.but_login);

        /******************读取保存的密码到输入框*********************/
        if (!(SPUtils.get(LoginActivity.this, "usercount", "没有").equals("没有"))) {

            usercounter.setText((String) SPUtils.get(LoginActivity.this, "usercount", ""));
            password.setText((String) SPUtils.get(LoginActivity.this, "password", ""));

        }

    }


    private void initEvent() {
        but_login.setOnClickListener(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.register:   //注册跳转
                Logger.d("注册");

                break;
            case R.id.but_login:   //登录跳转
                if (!NetUtils.isNetworkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                } else {
                    loadingView.setVisibility(View.VISIBLE);
                    if (checbox.isChecked()) {
                        savePaw(true);
                    } else {
                        savePaw(false);
                    }

                    OkHttpUtils.post()
                            .url(UrlNet.loginUri)
                            .addParams("tel", usercounter.getText().toString())
                            .addParams("password", password.getText().toString())
                            .build()
                            .execute(new OKCall<UserInfo>(UserInfo.class) {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Logger.d(e.getMessage());
                                    loadingView.setVisibility(View.GONE);
                                    SPUtils.put(LoginActivity.this, "IsFirst", true);
                                    Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(UserInfo response, int id) {
                                    String state = response.result.state;
                                    Logger.d(state);
                                    if ("success".equals(state)) {
                                        mIntent.intent(LoginActivity.this, MainActivity.class);
                                        SPUtils.put(LoginActivity.this, "IsFirst", false);
                                        SPUtils.put(LoginActivity.this, "IsLogout", false);
                                        SPUtils.put(LoginActivity.this, "id", response.result.userid);
                                        SPUtils.put(LoginActivity.this, "tel", response.result.tel);
                                        SPUtils.put(LoginActivity.this, "location",
                                                response.result.policeStation);
                                        SPUtils.put(LoginActivity.this, "coverageRate",
                                                response.result.coverageRate);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), state, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingView.setVisibility(View.GONE);
                                }
                            });
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_REGISTER:
                if (resultCode == RESULT_OK) {
                    String tel = data.getStringExtra("tel");
                    String passwd = data.getStringExtra("password");
                    usercounter.setText(tel);
                    password.setText(passwd);
                }
                break;

        }
    }

    /*********************
     * 记住密码保存和清除功能
     *******************************/
    private void savePaw(boolean s) {

        if (s) {
            SPUtils.put(LoginActivity.this, "usercount", usercounter.getText().toString());
            SPUtils.put(LoginActivity.this, "password", password.getText().toString());
        } else {
            SPUtils.remove(LoginActivity.this, "usercount");
            SPUtils.remove(LoginActivity.this, "password");
        }

    }
}
