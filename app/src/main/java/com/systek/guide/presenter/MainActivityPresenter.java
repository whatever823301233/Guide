package com.systek.guide.presenter;

import android.content.Intent;
import android.util.Log;
import android.widget.RadioGroup;

import com.systek.guide.R;
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
        if(i == R.id.radioBtnMap){
            Log.i(MapFragment.class.getSimpleName() ,"radioBtnMap");
            mainActivityView.setMapTitle();
            mainActivityView.showFragment(MapFragment.class.getSimpleName());
            //mainActivityView.hideFragment(ExhibitListFragment.class.getSimpleName());
        }else{
            Log.i(MapFragment.class.getSimpleName() ,"radioBtnExhibitList");
            mainActivityView.setNearExhibitTitle();
            mainActivityView.showFragment(ExhibitListFragment.class.getSimpleName());
            //mainActivityView.hideFragment(EmptyFragment.class.getSimpleName());
        }
    }
}
