package com.systek.guide.presenter;

import android.util.Log;

import com.systek.guide.R;
import com.systek.guide.biz.bizImpl.MainGuideBiz;
import com.systek.guide.biz.iBiz.IMainGuideBiz;
import com.systek.guide.iView.IMainGuideView;
import com.systek.guide.ui.fragment.ExhibitListFragment;
import com.systek.guide.ui.fragment.MapFragment;

/**
 * Created by UPC on 2016/12/22.
 */

public class GuidePresenter {

    private IMainGuideView guideView;
    private IMainGuideBiz mainGuideBiz;


    public GuidePresenter(IMainGuideView guideView){
        this.guideView = guideView;
        mainGuideBiz = new MainGuideBiz();
    }


    public void onCheckedRadioButton(int checkedId){
        if(checkedId == R.id.radioBtnMap){
            Log.i(MapFragment.class.getSimpleName() ,"radioBtnMap");
            guideView.showFragment(MapFragment.class.getSimpleName());
        }else{
            Log.i(MapFragment.class.getSimpleName() ,"radioBtnExhibitList");
            guideView.showFragment(ExhibitListFragment.class.getSimpleName());
        }
    }

    public void onCreate() {
        setFragment();
    }


    private void setFragment() {
        String flag = guideView.getFragmentFlag();
        if(flag.equals(ExhibitListFragment.class.getSimpleName())){
            guideView.showFragment(ExhibitListFragment.class.getSimpleName());
        }else{
            guideView.showFragment(MapFragment.class.getSimpleName());
        }
    }
}
