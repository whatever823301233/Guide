package com.systek.guide.base;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.systek.guide.R;
import com.systek.guide.util.LogUtil;
import com.systek.guide.iView.MediaBrowserProvider;
import com.systek.guide.service.PlayService;
import com.systek.guide.ui.fragment.PlaybackControlsFragment;

/**
 * Created by qiang on 2016/11/30.
 * 基类Activity,所有activity继承此activity
 */
public abstract  class AppActivity extends BaseActivity implements MediaBrowserProvider {


    private MediaBrowserCompat mMediaBrowser;
    protected PlaybackControlsFragment mControlsFragment;

    //获取第一个fragment
    protected abstract BaseFragment getFirstFragment();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    public String getTag(){
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载音频播放框架
        mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, PlayService.class), mConnectionCallback, null);
        setContentView(getContentViewId());

        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        //避免重复添加Fragment
        if (null == getSupportFragmentManager().getFragments()) {
            BaseFragment firstFragment = getFirstFragment();
            if (null != firstFragment) {
                addFragment(firstFragment);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mControlsFragment = (PlaybackControlsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_playback_controls);
        hidePlaybackControls();
        if(mMediaBrowser != null && ! mMediaBrowser.isConnected() ){
            mMediaBrowser.connect();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            hidePlaybackControls();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (getSupportMediaController() != null) {
            getSupportMediaController().unregisterCallback(mMediaControllerCallback);
        }
        mMediaBrowser.disconnect();
        cancelAllTask();
    }

    /**
     * 子类需要根据情况重写，再销毁activity时取消所有任务
     */
    public abstract void cancelAllTask();

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return mMediaBrowser;
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogUtil.d("", "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogUtil.e("","异常"+e.toString());
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

        if (shouldShowControls()) {
            LogUtil.i("","connectToSession 显示fragment");
            showPlaybackControls();
        } else {
            //LogUtil.d("", "connectionCallback.onConnected: " + "hiding controls because metadata is null");
            LogUtil.i("","connectToSession 隐藏fragment");
            hidePlaybackControls();
        }
        if (mControlsFragment != null) {
            mControlsFragment.onConnected();
        }
        onMediaControllerConnected();
    }

    protected void onMediaControllerConnected() {

    }


    // Callback that ensures that we are showing the controls
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    LogUtil.i("",TAG + " onPlaybackStateChanged");
                    if (shouldShowControls()) {
                        showPlaybackControls();
                    } else {
                        //LogUtil.d(TAG, "mediaControllerCallback.onPlaybackStateChanged: " +"hiding controls because state is ", state.getState());
                        hidePlaybackControls();
                    }

                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    LogUtil.i("",TAG+"onMetadataChanged");
                    if (shouldShowControls()) {
                        showPlaybackControls();
                    } else {
                        //LogUtil.d(TAG, "mediaControllerCallback.onMetadataChanged: " + "hiding controls because metadata is null");
                        hidePlaybackControls();
                    }
                }
            };


    protected boolean shouldShowControls() {
        boolean flag;
        MediaControllerCompat mediaController = getSupportMediaController();
        if (mediaController == null ||
                mediaController.getMetadata() == null ||
                mediaController.getPlaybackState() == null) {
            flag = false;
        }else{
            switch (mediaController.getPlaybackState().getState()) {
                case PlaybackStateCompat.STATE_ERROR:
                case PlaybackStateCompat.STATE_NONE:
                case PlaybackStateCompat.STATE_STOPPED:
                    flag = false;
                    break;
                default:
                    flag = true;
            }
        }
        return flag;
    }
    protected void showPlaybackControls() {
        if(mControlsFragment == null){return;}
        LogUtil.i("","showPlaybackControls 显示fragment");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .show(mControlsFragment)
                            .commitAllowingStateLoss();
                    LogUtil.i("","显示了Fragment");
                }catch (Exception e){
                    LogUtil.e("","显示fragment异常"+e);
                }
            }
        });

    }

    protected void hidePlaybackControls() {
        if(mControlsFragment == null){return;}
        LogUtil.i("","hidePlaybackControls 隐藏fragment");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .hide(mControlsFragment)
                            .commitAllowingStateLoss();
                    LogUtil.i("","隐藏了Fragment");
                }catch (Exception e){
                    LogUtil.e("",e);
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return  R.layout.activity_base;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }

}
