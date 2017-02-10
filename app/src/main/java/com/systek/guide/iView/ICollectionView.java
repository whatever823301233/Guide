package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;

import com.systek.guide.bean.Exhibit;

import java.util.List;


/**
 * Created by Qiang on 2016/7/29.
 */
public interface ICollectionView {

    void refreshView();

    void showLoading();

    void hideLoading();

    Exhibit getChooseExhibit();

    void setChooseExhibit(Exhibit exhibit);

    void toNextActivity(Intent intent);

    void showFailedError();

    void onNoData();

    void hideErrorView();

    void showFavoriteExhibits();

    String getMuseumId();

    String getTag();

    void setFavoriteExhibitList(List<Exhibit> exhibitList);

    Context getContext();

    void toPlay();

    void setTitle(String title);
}
