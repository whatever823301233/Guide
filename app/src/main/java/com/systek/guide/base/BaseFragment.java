package com.systek.guide.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qiang on 2016/11/28.
 */

public abstract class BaseFragment extends Fragment {

    protected BaseActivity mActivity;
    private View contentView;

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取布局文件ID
    protected abstract int getLayoutId();

    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }

    //添加fragment
    protected void showFragment(BaseFragment fragment) {
        if (null != fragment) {
            getHoldingActivity().showFragment(fragment);
        }
    }

    //移除fragment
    protected void removeFragment() {
        getHoldingActivity().removeFragment();
    }


    public View findViewById(int id){
        if(contentView == null){
            throw new IllegalStateException("contentView = null !");
        }
        return contentView.findViewById(id);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(getLayoutId(), container, false);
        }
        if(savedInstanceState == null){
            initView(contentView, savedInstanceState);
        }
        return contentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
