package com.systek.guide.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.RadioGroup;

import com.systek.guide.R;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.iView.MainActivityView;
import com.systek.guide.ui.fragment.CityFragment;
import com.systek.guide.ui.fragment.ExhibitListFragment;
import com.systek.guide.ui.fragment.MapFragment;

/**
 * Created by qiang on 2016/12/1.
 */

public class MainActivityPresenter {

    private static final String TAG = MainActivityPresenter.class.getSimpleName();
    public static final String INTENT_FRAGMENT = "intent_fragment";
    public static final String City_Fragment_FLAG = "city_fragment_flag";
    private MainActivityView mainActivityView;


    public MainActivityPresenter(MainActivityView mainActivityView){
        this.mainActivityView = mainActivityView;
    }

    public void onCreate() {
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        Intent intent = mainActivityView.getIntent();
        String flag = intent.getStringExtra(INTENT_FRAGMENT);
        if(flag.equals(City_Fragment_FLAG)){
            mainActivityView.showFragment(CityFragment.newInstance());
        }
    }

    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        String museumId = mainActivityView.getMuseumId();
        if(TextUtils.isEmpty(museumId)){
            mainActivityView.showErrorView();
        }
        if(i == R.id.radioBtnMap){
            LogUtil.i(TAG ,"radioBtnMap");
            mainActivityView.setMapTitle();
            mainActivityView.showFragment(MapFragment.newInstance(museumId));
            mainActivityView.hideFragment(ExhibitListFragment.class.getSimpleName());
        }else{
            LogUtil.i(TAG ,"radioBtnExhibitList");
            mainActivityView.setNearExhibitTitle();
            mainActivityView.showFragment(ExhibitListFragment.newInstance(museumId));
            mainActivityView.hideFragment(MapFragment.class.getSimpleName());
        }
    }
}
