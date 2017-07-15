package com.example.myapplication.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;


/**
 * Created by Long
 * on 2016/7/16.
 */
public abstract class BaseToolbarActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {


    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;
    protected final static String MODE_NORMAL = "MODE_NORMAL";
    protected final static String MODE_BACK = "MODE_BACK";
    protected final static String MODE_MENU = "MODE_MENU";
    protected final static String MODE_ALL = "MODE_ALL";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected abstract void initData() ;

    protected abstract void initView() ;


    // 普通的setContentView
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    // 带有title的setContentView
    protected void setContentView(@LayoutRes int layoutResID, @StringRes int titleResID) {
        setContentView(layoutResID, titleResID, -1, MODE_NORMAL);
    }

    // 带有title和返回的setContentView
    protected void setContentView(@LayoutRes int layoutResID, int titleResID, String mode) {
        if(mode == MODE_BACK) {
            setContentView(layoutResID,titleResID,-1,MODE_BACK);
        }
    }

    // 带有title和menu的setContentView
    protected void setContentView(@LayoutRes int layoutResID, @StringRes int titleResID, int mendID) {
        setContentView(layoutResID, titleResID, mendID, MODE_MENU);
    }

    protected void setContentView(@LayoutRes int layoutResID, @StringRes int titleResID, int menuID, String mode) {
        super.setContentView(layoutResID);
        setUpToolbar(titleResID,menuID,mode);
    }

    protected void setUpToolbar(@StringRes int titleResID, int menuID, String mode) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setUpToolbarTitle(titleResID);
        if(mode.equals(MODE_NORMAL)) {
        } else if(mode.equals(MODE_BACK)) {
            mToolbar.setNavigationIcon(R.mipmap.icon_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else if(mode.equals(MODE_MENU)) {
            setUpMenu(menuID);
        } else if(mode.equals(MODE_ALL)) {
            mToolbar.setNavigationIcon(R.mipmap.icon_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            setUpMenu(menuID);
        }
    }
    protected void setUpToolbar(String title, int menuID, String mode) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setUpToolbarTitle(title);
        if(mode.equals(MODE_NORMAL)) {
        } else if(mode.equals(MODE_BACK)) {
            mToolbar.setNavigationIcon(R.mipmap.icon_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else if(mode.equals(MODE_MENU)) {
            setUpMenu(menuID);
        } else if(mode.equals(MODE_ALL)) {
            mToolbar.setNavigationIcon(R.mipmap.icon_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            setUpMenu(menuID);
        }
    }

    protected void setUpToolbarTitle(@StringRes int titleResID) {
        if(mToolbarTitle != null && titleResID > 0) {
            mToolbarTitle.setText(titleResID);
        }
    }

    protected void setUpToolbarTitle(String title) {
        if(mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }
    }

    protected void setUpMenu(int menuId) {
        if(mToolbar != null) {
            mToolbar.getMenu().clear();
            if(menuId > 0) {
                mToolbar.inflateMenu(menuId);
                mToolbar.setOnMenuItemClickListener(this);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    protected void addFragmentToStack(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void showFragment(String hideTag,String showTag) {
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction();
        Fragment hideFragment = getSupportFragmentManager().findFragmentByTag(hideTag);
        Fragment showFragment = getSupportFragmentManager().findFragmentByTag(showTag);
        transaction.hide(hideFragment);
        transaction.show(showFragment);
        transaction.commit();
    }

    protected void showToastShort(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    protected void showToastLong(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(1000);
        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        FeedbackActivity.setBarImmersive(true);
        PgyFeedbackShakeManager.register(this, false);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }
}

