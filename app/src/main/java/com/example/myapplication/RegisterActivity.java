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
import okhttp3.Call;
import static JavaBean.UrlNet.REGISTER;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText policeNumber;
    EditText phoneNumber;
    EditText passWord1;
    EditText passWord2;
    Spinner spinner;
    Button butRegiet;
    ProgressView loadingview;

    private String relName;
    private String userid;
    private int policeid;
    private String tel;
    private String password1;
    private String password2;
    //    private List<PoliceStationInfo.PoliceStation> result =new ArrayList<>();
    //    private List<String> policeStationList =new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getPoliceStation();
        initView();
        initEvent();
    }


    private void initView() {
        username = (EditText) findViewById(R.id.username);
        policeNumber = (EditText) findViewById(R.id.police_number);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        passWord1 = (EditText) findViewById(R.id.password);
        passWord2 = (EditText) findViewById(R.id.password2);
        spinner = (Spinner) findViewById(R.id.policid);
        butRegiet = (Button) findViewById(R.id.but_register);

        String[] policeStationList = getResources().getStringArray(R.array.policeStation);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, policeStationList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }

    private void initdata() {
        relName = username.getText().toString();
        userid = policeNumber.getText().toString();
        tel = phoneNumber.getText().toString();
        password1 = passWord1.getText().toString();
        password2 = passWord2.getText().toString();

//        for (PoliceStationInfo.PoliceStation policeStation : result) {
//            policeStationList.add(policeStation.getPoliceStation());
//        }


    }

    private void initEvent() {
//        派出所选择
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                policeid = i;
                Logger.d(policeid + "onItemSelected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        注册
        butRegiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initdata();
                Logger.d(relName + "|" + userid + "|" + policeid + "|" + tel + "|" + password1 + "|" + password2);
                if (checkInput(relName, userid, tel, password1, password2, policeid)) {
                    OkHttpUtils.get()
                            .url(REGISTER)
                            .addParams("userid", userid)
                            .addParams("policeid", String.valueOf(policeid))
                            .addParams("relname", relName)
                            .addParams("tel", tel)
                            .addParams("password", password1)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Logger.d(e);
                                    Toast.makeText(RegisterActivity.this, "失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if (response.length() > 13) {
                                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent data = new Intent();
                                        data.putExtra("tel", tel);
                                        data.putExtra("password", password1);
                                        setResult(RESULT_OK, data);
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });

                }

            }
        });

    }


//    private void getPoliceStation() {
//        OkHttpUtils.get()
//                .url(POLICE_STATION)
//                .build()
//                .execute(new OKCall<PoliceStationInfo>(PoliceStationInfo.class) {
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Logger.d(e.getMessage());
//                        Toast.makeText(RegisterActivity.this, "失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(PoliceStationInfo response, int id) {
//                    }
//                });
//    }

    /*
     *检查输入
     */
    public boolean checkInput(String username, String policenumber, String phonenumber, String password1, String password2, int policeid) {
        // 账号为空时提示
        if (username == null || username.trim().equals("") ||
                policenumber == null || policenumber.trim().equals("") ||
                phonenumber == null || phonenumber.trim().equals("") ||
                password1 == null || password1.trim().equals("") ||
                password2 == null || password2.trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phonenumber.length() != 11 || phonenumber.charAt(0) != '1') {
            // 账号不匹配手机号格式（11位数字且以1开头）
            Toast.makeText(RegisterActivity.this, "请填写正确手机号码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password1.length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码长度小于6位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password1.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不相同", Toast.LENGTH_SHORT).show();
            return false;

        } else if (policeid == 0) {
            Toast.makeText(RegisterActivity.this, "请选择所在派出所", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
