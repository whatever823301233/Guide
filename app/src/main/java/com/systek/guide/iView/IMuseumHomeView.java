package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.systek.guide.bean.Museum;
import com.systek.guide.ui.BaseFragment;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 */
public interface IMuseumHomeView {

    void refreshView();

    void showLoading();

    void hideLoading();

    void toNextActivity(Intent intent);

    void setCurrentMuseum(Museum museum);

    Museum getCurrentMuseum();

    void setTitle(String title);

    void refreshIntroduce(String text);

    void setIconUrls(List<String> iconUrls);

    void refreshIcons();

    void refreshMedia();

    void showFailedError();

    void setAdapterMuseumId(String museumId);

    void setMediaPath(String s);

    String getTag();

    void setOnClickView(View view);

    View getOnClickView();

    Context getContext();


    boolean isDrawerOpen();

    void closeDrawer();

    void addShowFragment(BaseFragment fragment);

    void addShowFragment(String tag);

    void showGuideActivity(String simpleName);
}
