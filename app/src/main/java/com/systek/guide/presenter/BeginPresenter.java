package com.systek.guide.presenter;

import android.content.Intent;

import com.systek.guide.biz.BeginBiz;
import com.systek.guide.iBiz.IBeginBiz;
import com.systek.guide.iBiz.OnResponseListener;
import com.systek.guide.iView.IBeginView;
import com.systek.guide.ui.activity.MainActivity;

/**
 * Created by qiang on 2016/11/30.
 *
 * 欢迎界面控制
 */

public class BeginPresenter {

    private IBeginBiz beginBiz;
    private IBeginView beginView;

    public BeginPresenter(IBeginView beginView){
        this.beginView=beginView;
        beginBiz=new BeginBiz();
    }


    public void onCreate() {

        beginBiz.waitForSomeTime(3000, new OnResponseListener() {
            @Override
            public void onSuccess(String path) {
                Intent intent = new Intent(beginView.getContext(), MainActivity.class);
                intent.putExtra(MainActivityPresenter.INTENT_FRAGMENT, MainActivityPresenter.City_Fragment_FLAG);
                beginView.goToNextActivity(intent);
            }

            @Override
            public void onFail(String error) {

            }
        });
    }


}
