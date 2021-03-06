package com.systek.guide.presenter;

import android.os.Handler;
import android.os.Message;

import com.systek.guide.base.Constants;
import com.systek.guide.util.FileUtil;
import com.systek.guide.util.LogUtil;
import com.systek.guide.bean.BaseBean;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.biz.CollectionBiz;
import com.systek.guide.iBiz.ICollectionBiz;
import com.systek.guide.iBiz.OnInitBeanListener;
import com.systek.guide.iView.ICollectionView;
import com.systek.okhttp.OkHttpUtils;
import com.systek.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;

/**
 * Created by qiang on 2016/12/2.
 */

public class CollectionPresenter {

    public static final String TAG = CollectionPresenter.class.getSimpleName();

    private static final int MSG_WHAT_SHOW_ALL_EXHIBITS = 9527;
    private static final int MSG_WHAT_UPDATE_DATA_FAIL = 9528;
    private static final int MSG_WHAT_NO_DATA = 9529;


    private ICollectionView collectionView;
    private ICollectionBiz collectionBiz;
    private Handler handler;


    public CollectionPresenter(ICollectionView collectionView){
        this.collectionView=collectionView;
        collectionBiz=new CollectionBiz();
        handler=new MyHandler(collectionView);
    }


    public void onViewCreated() {
        collectionView.showLoading();
        collectionView.setTitle("收藏");

        String museumId = collectionView.getMuseumId();
        collectionBiz.initExhibitByMuseumId(museumId, new OnInitBeanListener() {
            @Override
            public void onSuccess(List<? extends BaseBean> beans) {
                List<Exhibit> exhibitList= (List<Exhibit>) beans;
                if(exhibitList==null||exhibitList.size()==0){
                    handler.sendEmptyMessage(MSG_WHAT_NO_DATA);
                }else{
                    collectionView.setFavoriteExhibitList(exhibitList);
                    handler.sendEmptyMessage(MSG_WHAT_SHOW_ALL_EXHIBITS);
                }
            }

            @Override
            public void onFailed() {
                handler.sendEmptyMessage(MSG_WHAT_UPDATE_DATA_FAIL);
            }

        },collectionView.getTag());
    }

    public void onExhibitChoose(Exhibit exhibit) {
        collectionView.showLoading();
        collectionView.setChooseExhibit(exhibit);
        String url = exhibit.getAudiourl();
        String name = FileUtil.changeUrl2Name(url);
        boolean fileExists = FileUtil.checkFileExists(url,exhibit.getMuseumId());
        if(!fileExists){
            collectionView.showLoading();
            OkHttpUtils
                    .get().url(Constants.BASE_URL+exhibit.getAudiourl())
                    .build()
                    .execute(new FileCallBack(Constants.LOCAL_PATH+exhibit.getMuseumId(),name) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            LogUtil.e("",e.toString());
                            collectionView.hideLoading();
                            collectionView.showFailedError();
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            collectionView.hideLoading();
                            collectionView.toPlay();
                        }
                    });
        }else{
            LogUtil.i(TAG,"File is Exists");
            collectionView.toPlay();
        }

    }


    private static class MyHandler extends Handler {

        WeakReference<ICollectionView> activityWeakReference;
        MyHandler(ICollectionView activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference == null){return;}
            ICollectionView activity = activityWeakReference.get();
            if(activity == null){return;}
            switch (msg.what){
                case MSG_WHAT_SHOW_ALL_EXHIBITS:
                    activity.showFavoriteExhibits();
                    break;
                case MSG_WHAT_UPDATE_DATA_FAIL:
                    activity.showFailedError();
                    break;
                case MSG_WHAT_NO_DATA:
                    activity.onNoData();
                    break;
                default:break;
            }
            activity.hideLoading();
        }
    }



}



