package com.example.myapplication.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import butterknife.Unbinder;


/**
 * Created by Long
 * on 2016/7/16.
 */
public abstract class BaseFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    protected Unbinder unbinder;
    protected Context context;
    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        initView();
        initData();
    }

    protected void setUpToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
    }


    protected void setUpToolbarTitle(@StringRes int titleResID) {
        if(mToolbarTitle != null && titleResID > 0) {
            mToolbarTitle.setText(titleResID);
        }
    }

    protected void setUpToolbarMenu(int menuId) {
        if(mToolbar != null) {
            mToolbar.getMenu().clear();
            if(menuId > 0) {
                mToolbar.inflateMenu(menuId);
                mToolbar.setOnMenuItemClickListener(this);
            }
        }
    }

    protected abstract void initView() ;
    protected abstract void initData() ;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void showFragment(String tag) {
        FragmentTransaction transaction = getFragmentManager().
                beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        transaction.hide(this);
        transaction.show(fragment);
        transaction.commit();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    protected void openActivity(Class<?> cls) {
        Intent intent = new Intent(getContext(), cls);
        startActivity(intent);
    }

    protected void showToastShort(String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    protected void showToastLong(String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
