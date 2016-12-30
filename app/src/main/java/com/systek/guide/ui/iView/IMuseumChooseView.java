package com.systek.guide.ui.iView;

import android.content.Context;
import android.content.Intent;

import com.systek.guide.bean.Museum;

import java.util.List;


/**
 * Created by Qiang on 2016/7/29.
 */
public interface IMuseumChooseView {

    String getTag();

    void refreshMuseumList();

    void showLoading();

    void hideLoading();

    void toNextActivity(Intent intent);

    void setTitle(String tile);

    String getCityName();

    void setMuseumList(List<Museum> museumList);


    void showFailedError();

    void hideErrorView();

    Context getContext();

    void showToast(String content);

    void closeDrawer();

    Museum getChooseMuseum();

    void showDownloadTipDialog();

    void updateProgress(int progress, int totalSize);

    void showProgressDialog();

    void hideProgressDialog();

    void skit2MuseumHome(Museum museum);

    void setChooseMuseum(Museum museum);

    void onNoData();
}
