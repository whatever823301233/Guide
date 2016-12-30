package com.systek.guide.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.systek.guide.R;
import com.systek.guide.base.AppManager;
import com.systek.guide.bean.Museum;
import com.systek.guide.ui.iView.MainActivityView;
import com.systek.guide.presenter.MainActivityPresenter;
import com.systek.guide.service.MediaIDHelper;
import com.systek.guide.ui.AppActivity;
import com.systek.guide.ui.BaseFragment;
import com.systek.guide.ui.fragment.ChannelFragment;
import com.systek.guide.ui.fragment.CityFragment;
import com.systek.guide.ui.fragment.CollectionFragment;
import com.systek.guide.ui.fragment.EmptyFragment;
import com.systek.guide.ui.fragment.ExhibitListFragment;
import com.systek.guide.ui.fragment.MapFragment;
import com.systek.guide.ui.fragment.MuseumHomeFragment;
import com.systek.guide.ui.fragment.MuseumListFragment;
import com.systek.guide.ui.fragment.TopicFragment;

import java.util.List;

public class MainActivity extends AppActivity implements MainActivityView,
        MuseumListFragment.OnFragmentInteractionListener,
        MuseumHomeFragment.OnMuseumHomeFragmentInteractionListener,
        ExhibitListFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        EmptyFragment.OnFragmentInteractionListener,
        TopicFragment.OnFragmentInteractionListener,
        CollectionFragment.OnFragmentInteractionListener,
        ChannelFragment.OnFragmentInteractionListener,
        CityFragment.OnCityFragmentInteractionListener {

    public static final String INTENT_FRAGMENT = "intent_fragment";
    public static final String City_Fragment_FLAG = "city_fragment_flag";

    private MainActivityPresenter presenter;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    private RadioGroup radioGroupTitle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mMediaId;
    private String currentMuseumId;
    private String currentCity;
    private Museum currentMuseum;
    private RadioButton radioBtnMap;
    private RadioButton radioBtnNearExhibit;

    @Override
    protected BaseFragment getFirstFragment() {
        BaseFragment fragment = null;
        if(currentMuseum != null){
            fragment = MuseumHomeFragment.newInstance(currentMuseum);
        }else if(currentCity != null){
            fragment = MuseumListFragment.newInstance(currentCity);
        }else{
            fragment = CityFragment.newInstance();
        }
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainActivityPresenter(this);
        findView();
        presenter.onCreate();
    }

    private void findView() {
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id 'toolbar'");
        }
        //mToolbar.inflateMenu(R.menu.search_menu);
        toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        radioGroupTitle = (RadioGroup) findViewById(R.id.radioGroupTitle);
        radioBtnMap = (RadioButton) findViewById(R.id.radioBtnMap);
        radioBtnNearExhibit = (RadioButton) findViewById(R.id.radioBtnNearExhibit);
        radioGroupTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                presenter.onCheckedChanged(radioGroup,i);
            }
        });
        /*mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                *//*Intent intent = new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);*//*
                return true;
            }
        });*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView == null) {
                throw new IllegalStateException("Layout requires a NavigationView with id 'nav_view'");
            }

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    Intent intent = null;
                    switch (item.getItemId()){
                       /* case R.id.menu_1:
                            intent = new Intent(getActivity(),DownloadManagerActivity.class);
                            break;
                        *//*case R.id.menu_2:
                            intent = new Intent(getActivity(),CollectionActivity.class);
                            break;*//*
                        case R.id.menu_3:
                            intent = new Intent(getActivity(),CityChooseActivity.class);
                            break;
                        case R.id.menu_4:
                            intent = new Intent(getActivity(),MuseumChooseActivity.class);
                            break;
                        case R.id.menu_5:
                            intent = new Intent(getActivity(),SettingActivity.class);
                            break;*/
                    }
                    if(intent != null){
                        startActivity(intent);
                    }
                    closeDrawer();
                    return true;
                }
            });
            // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,R.string.app_name, R.string.app_name);
            mDrawerLayout.addDrawerListener(mDrawerListener);
            //populateDrawerItems(navigationView);
            //setSupportActionBar(mToolbar);
            updateDrawerToggle();
        } else {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }


    public final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener(){

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    protected void onMediaControllerConnected() {
        super.onMediaControllerConnected();
        if (mMediaId == null) {
            mMediaId = MediaIDHelper.createBrowseCategoryMediaID(MediaIDHelper.MEDIA_ID_MUSEUM_ID,currentMuseumId);
        }
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
    public void closeDrawer() {
        if(mDrawerLayout == null || navigationView == null){return;}
        if(mDrawerLayout.isDrawerOpen(navigationView)){
            mDrawerLayout.closeDrawer(navigationView);
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen( navigationView );
    }

    protected void updateDrawerToggle() {
        if (mDrawerToggle == null) {
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }


    @Override
    public void cancelAllTask() {

    }

    @Override
    public void setTitle(String title) {
        if(toolbarTitle == null || TextUtils.isEmpty(title)){return;}
        if( toolbarTitle.getVisibility() != View.VISIBLE ){
            toolbarTitle.setVisibility(View.VISIBLE);
        }if(radioGroupTitle != null && radioGroupTitle.getVisibility() == View.VISIBLE){
            radioGroupTitle.setVisibility(View.GONE);
        }
        toolbarTitle.setText(title);
    }

    @Override
    public void showFragment(String tag) {
        if(TextUtils.isEmpty(tag)){
            return;
        }
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(mFragment == null){
            BaseFragment fragment = null;
            if(tag.equals(ExhibitListFragment.class.getSimpleName())){
                fragment = ExhibitListFragment.newInstance(currentMuseumId);
            }else if(tag.equals(MapFragment.class.getSimpleName())){
                fragment = MapFragment.newInstance(currentMuseumId);
            }else if(tag.equals(TopicFragment.class.getSimpleName())){
                fragment = TopicFragment.newInstance(currentMuseumId);
            }else if(tag.equals(CollectionFragment.class.getSimpleName())){
                fragment = CollectionFragment.newInstance(currentMuseumId);
            }
            if(fragment != null){
                addFragment(fragment);
            }
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(mFragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void setNavigationMenu() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED );
    }

    @Override
    public void setCurrentMuseum(Museum museum) {
        if(museum == null){
            currentMuseum = null;
            currentMuseumId = null;
        }else {
            currentMuseum = museum;
            currentMuseumId = museum.getId();
        }

    }

    @Override
    public Museum getCurrentMuseum() {
        return currentMuseum;
    }

    @Override
    public void skip2MuseumListFragment(String cityName) {
        MuseumListFragment fragment = MuseumListFragment.newInstance(cityName);
        showFragment(fragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public void setNavigationNone() {
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED );
    }

    @Override
    public void setCurrentCity(String cityName) {
        currentCity = cityName;
    }

    @Override
    public MediaControllerCompat.TransportControls getControls() {
        MediaControllerCompat mediaControllerCompat = getSupportMediaController();
        return mediaControllerCompat == null ? null : mediaControllerCompat.getTransportControls();
    }
    /*用于计算点击返回键时间*/
    private long mExitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isDrawerOpen()){
                closeDrawer();
                return true;
            }
            if(isLastFragment()){
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    AppManager.getInstance(this).exitApp();
                }
                return true;
            }else{
                return super.onKeyDown(keyCode,event);
            }
        } else {//拦截MENU按钮点击事件，让他无任何操作
            return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
        }
    }

    private boolean isLastFragment(){
        return  getSupportFragmentManager().getBackStackEntryCount() == 1;
    }

    @Override
    public void setMapTitle(){
        if( toolbarTitle != null && null != radioGroupTitle){
            toolbarTitle.setVisibility( View.GONE );
            radioGroupTitle.setVisibility( View.VISIBLE );
            radioBtnMap.setChecked(true);
            radioBtnNearExhibit.setChecked(false);
        }
    }

    @Override
    public void setNearExhibitTitle() {
        if( toolbarTitle != null && null != radioGroupTitle){
            toolbarTitle.setVisibility( View.GONE );
            radioGroupTitle.setVisibility( View.VISIBLE );
            radioBtnMap.setChecked(false);
            radioBtnNearExhibit.setChecked(true);
        }
    }

    @Override
    public String getMuseumId() {
        return currentMuseumId;
    }

    @Override
    public void showErrorView() {

    }
}
