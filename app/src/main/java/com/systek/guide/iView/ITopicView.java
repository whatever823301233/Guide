package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;

import com.systek.guide.bean.Exhibit;
import com.systek.guide.bean.ChannelItem;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 */
public interface ITopicView {

    void showLoading();

    void hideLoading();

    String getMuseumId();

    void refreshExhibitList();

    List<String> getChooseLabels();

    void addChooseLabels(String label);

    void removeLabel();

    void removeLabels(List<String> labels);

    void toMapView(List<Exhibit> exhibits);

    void toNextActivity(Intent intent);

    void showFailedError();
    void hideErrorView();
    void showAllExhibits();

    String getTag();

    void setAllExhibitList(List<Exhibit> exhibitList);

    void setChooseExhibit(Exhibit exhibit);
    Exhibit getChooseExhibit();

    Context getContext();

    void toPlay();

    void onNoData();

    void setUserChannelList(List<ChannelItem> channelItems);

    List<ChannelItem> getUserChannelList();

    void setChooseChannel(ChannelItem channel);

    ChannelItem getChooseChannel();

    void setTitle(String title);

    List<Exhibit> getChooseAllExhibits();

}
