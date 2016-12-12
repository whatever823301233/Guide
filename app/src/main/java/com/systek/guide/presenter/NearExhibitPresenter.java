package com.systek.guide.presenter;

import com.systek.guide.biz.bizImpl.MainGuideBiz;
import com.systek.guide.biz.iBiz.IMainGuideBiz;
import com.systek.guide.iView.IMainGuideView;

/**
 * Created by qiang on 2016/12/9.
 */

public class NearExhibitPresenter {

    private IMainGuideBiz biz;
    private IMainGuideView guideView;

    public NearExhibitPresenter(IMainGuideView guideView){
        this.guideView = guideView;
        biz = new MainGuideBiz();
    }


    public void onResume() {
        guideView.setNearExhibitTitle();
    }
}
