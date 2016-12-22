package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;


/**
 * Created by Qiang on 2016/8/10.
 */
public interface IMainGuideView {

    void showLoading();

    void hideLoading();

    void toNextActivity(Intent intent);

    void showFailedError();

    void hideErrorView();

    String getMuseumId();

    String getTag();

    Context getContext();

    String getFragmentFlag();

    void showFragment(String simpleName);
}
