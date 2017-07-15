package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rey.material.widget.ProgressView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import JavaBean.PoliceStationInfo;
import Utils.OKCall;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static JavaBean.UrlNet.POLICE_STATION;
import static JavaBean.UrlNet.REGISTER;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.police_number)
    EditText policeNumber;
    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.password)
    EditText passWord1;
    @BindView(R.id.password2)
    EditText passWord2;
    @BindView(R.id.policid)
    Spinner spinner;
    @BindView(R.id.but_login)
    Button butLogin;
    @BindView(R.id.loadingview)
    ProgressView loadingview;

    private String relName;
    private String userid;
    private String policeid;
    private String tel;
    private String password1;
    private String password2;
    private List<PoliceStationInfo.PoliceStation> result;
    private List<String> policeStationList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initdata();
    }

    private void initdata() {
        policeStationList = new ArrayList<>();
        relName = username.getText().toString();
        userid = policeNumber.getText().toString();
        tel = phoneNumber.getText().toString();
        password1 = passWord1.getText().toString();
        password2 = passWord2.getText().toString();
        getPoliceStation();
        for (PoliceStationInfo.PoliceStation policeStation : result) {
            policeStationList.add(policeStation.getPoliceStation());
        }
        spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,policeStationList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                policeid=spinnerAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getPoliceStation() {
        OkHttpUtils.get()
                .url(POLICE_STATION)
                .build()
                .execute(new OKCall<PoliceStationInfo>(PoliceStationInfo.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logger.d(e.getMessage());
                        Toast.makeText(RegisterActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(PoliceStationInfo response, int id) {
                        result = response.getPoliceStationList();
                    }
                });
    }

    @OnClick({R.id.policid, R.id.but_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.policid:
                break;
            case R.id.but_login:
                if (checkInput(relName, userid, policeid, tel, password1, password2)) {
                    OkHttpUtils.get()
                            .url(REGISTER)
                            .addParams("userid", userid)
                            .addParams("policeid", policeid)
                            .addParams("relname", relName)
                            .addParams("tel", tel)
                            .addParams("password", password1)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Logger.d(e);
                                    Toast.makeText(RegisterActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Intent data = new Intent();
                                    data.putExtra("tel", tel);
                                    data.putExtra("password", password1);
                                    setResult(RESULT_OK, data);
                                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                }
                break;
        }
    }

    /*
     *检查输入
     */
    public boolean checkInput(String username, String policenumber, String phonenumber, String policeid, String password1, String password2) {
        // 账号为空时提示
        if (username == null || username.trim().equals("") ||
                policenumber == null || policenumber.trim().equals("") ||
                phonenumber == null || phonenumber.trim().equals("") ||
                password1 == null || password1.trim().equals("") ||
                password2 == null || password2.trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phonenumber.length() != 11 && phonenumber.charAt(0) != '1') {
            // 账号不匹配手机号格式（11位数字且以1开头）
            Toast.makeText(RegisterActivity.this, "请填写正确手机号码", Toast.LENGTH_SHORT).show();
        } else if (!password2.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不相同", Toast.LENGTH_SHORT).show();

        }

        return false;
    }
}
