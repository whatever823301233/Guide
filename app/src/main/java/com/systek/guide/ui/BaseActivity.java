package com.systek.guide.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.systek.guide.base.AppManager;

/**
 * Created by qiang on 2016/11/30.
 */

public abstract class BaseActivity extends AppCompatActivity{

    /**
     * 类唯一标记
     */
    public String TAG = getClass().getSimpleName();

    public BaseActivity getActivity(){
        return this;
    }
    public Context getContext(){
        return this;
    }

    //布局文件ID
    protected abstract int getContentViewId();

    //布局中Fragment的ID
    protected abstract int getFragmentContentId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance( getApplicationContext() ).addActivity( this );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance( getApplicationContext() ).removeActivity( this );
    }

    //添加fragment
    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public void hideFragment(String tag){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction().hide(fragment)
                    .commitAllowingStateLoss();
        }
    }

    public void showFragment(BaseFragment fragment){
        Fragment mFragment = getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getSimpleName());
        if(mFragment == null){
            addFragment(fragment);
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(fragment)
                    .commitAllowingStateLoss();
        }
    }



    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
