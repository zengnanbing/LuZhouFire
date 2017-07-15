package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.myapplication.base.BaseActivity;

import Fragment.ClockFragment;

/**
 * Created by 澄鱼 on 2016/5/24.
 * 图案解锁密码设置和验证
 */
public class PictureClockActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictureclock);

        //取回传递的标志位区别是密码设置还是验证
        int result = getIntent().getIntExtra("flag", 0);


        if (result == 1) {

            //设置图案解锁页面
            getSupportFragmentManager().beginTransaction().add(R.id.frame_clock, new ClockFragment(2)).commit();

        } else {
            //验证图案解锁页面
            getSupportFragmentManager().beginTransaction().add(R.id.frame_clock, new ClockFragment(3)).commit();
        }


    }
}
