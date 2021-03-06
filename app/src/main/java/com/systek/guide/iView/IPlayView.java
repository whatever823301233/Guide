package com.systek.guide.iView;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.systek.guide.bean.Exhibit;
import com.systek.guide.bean.MultiAngleImg;

import java.util.ArrayList;

/**
 * Created by Qiang on 2016/8/19.
 */
public interface IPlayView {

    void updateFromParams(Intent intent);

    void initMediaBrowser();

    void registerMediaControllerCallback();

    void unregisterMediaControllerCallback();

    void updateDuration(MediaMetadataCompat metadata);

    void onMetadataChanged(MediaMetadataCompat metadata);

    void subscribeExhibitList();

    void setToolbarTitle(String title);

    void scheduleSeekbarUpdate();

    void stopSeekbarUpdate();

    void updateProgress();

    void updatePlaybackState(PlaybackStateCompat state);

    Context getContext();


    void connectSession();

    Intent getIntent();

    MediaControllerCompat getSupportMediaController();

    String getTag();

    void setPlayTime(String time);

    void finish();

    void setSupportMediaController(MediaControllerCompat mediaController);

    void updateMediaMetadataCompat(MediaMetadataCompat description);

    void setMuseumId(String museumId);

    void showIcon();

    void setLyricUrl(String lyricUrl);

    void refreshLyricContent();

    void setExhibitContent(String content);

    void onSwitchLyric();

    void setExhibit(Exhibit exhibit);

    Exhibit getExhibit();

    void setImgsAndTimes(ArrayList<MultiAngleImg> multiAngleImgs, ArrayList<Integer> imgsTimeList);

    void refreshMultiImgs();

    void setIconUrl(String iconUrl);

    void skipTo(int time);

    boolean getIsBlur();
    void setIsBlur(boolean isBlur);

    String getMuseumId();

    void autoPlayExhibit(Exhibit exhibit);

    void disconnectMediaBrowser();

    void shutdownExecutorService();
}
