package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;

import com.systek.guide.ui.BaseFragment;

/**
 * Created by qiang on 2016/12/1.
 */

public interface MainActivityView {

    Intent getIntent();

    void addFragment(BaseFragment fragment);

    void showFragment(BaseFragment fragment);

    void showFragment(String tag);

    boolean isDrawerOpen();

    void closeDrawer();

    Context getContext();

    void setMapTitle();

    void setNearExhibitTitle();

    String getMuseumId();

    void showErrorView();

    void hideFragment(String tag);
}
