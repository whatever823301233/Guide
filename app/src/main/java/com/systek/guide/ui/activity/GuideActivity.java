package com.systek.guide.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.systek.guide.R;
import com.systek.guide.iView.IMainGuideView;
import com.systek.guide.presenter.GuidePresenter;
import com.systek.guide.service.MediaIDHelper;
import com.systek.guide.ui.AppActivity;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.fragment.ExhibitListFragment;
import com.systek.guide.ui.fragment.MapFragment;

import java.util.List;


public class GuideActivity extends AppActivity implements IMainGuideView,
        MapFragment.OnFragmentInteractionListener,
        ExhibitListFragment.OnFragmentInteractionListener{
    public static final String INTENT_FRAGMENT_FLAG     = "intent_fragment_flag";
    public static final String INTENT_MUSEUM_ID         = "intent_museum_id";
    private GuidePresenter presenter;
    private String museumId;
    private ExhibitListFragment exhibitListFragment;
    private MapFragment mapFragment;
    private RadioButton radioButtonMap;
    private RadioButton radioButtonList;
    private RadioGroup radioGroupTitle;
    private TextView tvToast;
    private String mMediaId;
    private  TextView toolbarTitle;

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new GuidePresenter(this);
        museumId = getIntent().getStringExtra(INTENT_MUSEUM_ID);
        findView();
        addListener();
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getTag(),"onDestroy");
    }

    private void addListener() {
        radioGroupTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                presenter.onCheckedRadioButton(checkedId);
            }
        });
    }

    @Override
    public void cancelAllTask() {

    }


    private void findView() {
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        radioGroupTitle = (RadioGroup)findViewById(R.id.radioGroupTitle);
        radioButtonMap = (RadioButton)findViewById(R.id.radioBtnMap);
        radioButtonList = (RadioButton)findViewById(R.id.radioBtnNearExhibit);
        tvToast = (TextView)findViewById(R.id.tvToast);
    }


    public void onConnected() {
        if (mMediaId == null) {
            mMediaId = MediaIDHelper.createBrowseCategoryMediaID(MediaIDHelper.MEDIA_ID_MUSEUM_ID,museumId);
        }
        //updateTitle();

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unsubscribe first.
        getMediaBrowser().unsubscribe(mMediaId);
        getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

    }

    private MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
        }

        @Override
        public void onError(@NonNull String parentId) {
            super.onError(parentId);
        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
        }
    };

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toNextActivity(Intent intent) {

    }

    @Override
    public void showFailedError() {

    }

    @Override
    public void hideErrorView() {

    }

    @Override
    public String getMuseumId() {
        return museumId;
    }




    @Override
    public String getFragmentFlag() {
        return getIntent().getStringExtra(INTENT_FRAGMENT_FLAG);
    }


    @Override
    public void setNearExhibitTitle() {
        if( toolbarTitle != null && null != radioGroupTitle){
            toolbarTitle.setVisibility( View.GONE );
            radioGroupTitle.setVisibility( View.VISIBLE );
            radioButtonList.setChecked(true);
        }
    }

    @Override
    public void setMapTitle() {
        if( toolbarTitle != null && null != radioGroupTitle){
            toolbarTitle.setVisibility( View.GONE );
            radioGroupTitle.setVisibility( View.VISIBLE );
            radioButtonMap.setChecked(true);
        }
    }

    @Override
    public void showFragment(String simpleName) {
        if(TextUtils.isEmpty(simpleName)){
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (simpleName.equals(ExhibitListFragment.class.getSimpleName())) {
            if(exhibitListFragment == null){
                exhibitListFragment = ExhibitListFragment.newInstance(museumId);
            }
            // 使用当前Fragment的布局替代id_content的控件
            transaction.replace(R.id.fragment_container, exhibitListFragment);
            transaction.commit();
        }else{
            if(mapFragment == null){
                mapFragment = MapFragment.newInstance(museumId);
            }
            transaction.replace(R.id.fragment_container, mapFragment);
            transaction.commit();
        }

    }
}
