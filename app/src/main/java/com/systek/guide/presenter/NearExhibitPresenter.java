package com.systek.guide.presenter;

import com.systek.guide.biz.bizImpl.MainGuideBiz;
import com.systek.guide.biz.iBiz.IMainGuideBiz;
import com.systek.guide.iView.IExhibitListView;

/**
 * Created by qiang on 2016/12/9.
 */

public class NearExhibitPresenter {

    private IMainGuideBiz biz;
    private IExhibitListView exhibitListView;

    public NearExhibitPresenter(IExhibitListView exhibitListView){
        this.exhibitListView = exhibitListView;
        biz = new MainGuideBiz();
    }


    public void onResume() {
        exhibitListView.setNearExhibitTitle();
    }
}
