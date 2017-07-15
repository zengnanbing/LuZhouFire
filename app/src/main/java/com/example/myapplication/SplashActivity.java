package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.example.myapplication.base.BaseActivity;
import com.orhanobut.logger.Logger;

import Utils.SPUtils;
import Utils.mIntent;

/**
 * Created by 澄鱼 on 2016/4/30.
 */
public class SplashActivity extends Activity {

    private LinearLayout splash;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            jump();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏沉浸栏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        splash = (LinearLayout) findViewById(R.id.splash);

        AnimationSplash();

    }

    /*********
     * 跳转到主页面或导航页面或者登录界面
     **********/
    private void jump() {

        //引导页面
        if ((boolean) SPUtils.get(this, "IsFirst", true)) {
            Logger.d(SPUtils.get(this, "IsFirst", true));
            mIntent.intent(this, GuideActivity.class);
            finish();
        }else if ((boolean) SPUtils.get(this,"IsLogout",true)){
            mIntent.intent(this, LoginActivity.class);
            finish();
        }

            else
         {

            //图案密码设置页面
            if ((((String) SPUtils.get(this,"pic_password","没有"))).equals("没有")){

                Intent intent = new Intent(this,PictureClockActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }else {
                //图案密码验证页面
                Intent intent = new Intent(this,PictureClockActivity.class);
                intent.putExtra("flag",2);
                startActivity(intent);
               // mIntent.intent(this, LoginActivity.class);
            }

            finish();
        }

    }

    /**********
     * 闪屏页动画
     **********/
    private void AnimationSplash() {

        //透明
        AlphaAnimation alpha = new AlphaAnimation(0.9f, 1);
        alpha.setDuration(800);

        //3.缩放
        ScaleAnimation scale = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(800);
        scale.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator); //加速插补器*/

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alpha);
      //  animationSet.addAnimation(scale);
        animationSet.setFillAfter(true);

        splash.startAnimation(animationSet);

        /*************发送延时跳转***************/
        handler.sendEmptyMessageDelayed(0, 3000);

    }


}