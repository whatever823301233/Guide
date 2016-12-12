package com.systek.guide.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.systek.guide.base.AppManager;
import com.systek.guide.base.util.AndroidUtil;
import com.systek.guide.bean.BaseBean;
import com.systek.guide.bean.Museum;
import com.systek.guide.biz.bizImpl.MuseumChooseBiz;
import com.systek.guide.biz.iBiz.IMuseumChooseBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.iView.IMuseumChooseView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by qiang on 2016/12/1.
 *
 *  博物馆选择控制
 */

public class MuseumChoosePresenter {

    private static final int MSG_WHAT_REFRESH_MUSEUM_LIST   = 9527;
    private static final int MSG_WHAT_REFRESH_TITLE         = 9529;
    private static final int MSG_WHAT_UPDATE_DATA_FAIL      = 9530;
    private static final int MSG_WHAT_HIDE_ERROR_VIEW       = 9531;


    private IMuseumChooseView museumChooseView;
    private IMuseumChooseBiz museumChooseBiz;
    private Handler handler;

    public MuseumChoosePresenter(IMuseumChooseView museumChooseView){
        this.museumChooseView = museumChooseView;
        museumChooseBiz = new MuseumChooseBiz();
        handler = new MyHandler(museumChooseView);
    }

    public void onViewCreated() {
        setTitle();
        initMuseumList();

    }

    private void setTitle() {
        String cityName = museumChooseView.getCityName();
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_REFRESH_TITLE;
        msg.obj = cityName;
        handler.sendMessage(msg);
    }


    public synchronized void initMuseumList(){
        museumChooseView.showLoading();
        final String city = museumChooseView.getCityName();
        if(TextUtils.isEmpty(city)){
            museumChooseView.showFailedError();
            return;
        }
        final List<Museum> localMuseumList = museumChooseBiz.initMuseumListBySQL(city);
        boolean isNetConn = AndroidUtil.isNetworkConnected(museumChooseView.getContext());
        if(!isNetConn){
            if(localMuseumList == null || localMuseumList.size() == 0){
                museumChooseView.onNoData();
            }else{
                museumChooseView.setMuseumList(localMuseumList);
                museumChooseView.refreshMuseumList();
            }
        }else{
            museumChooseBiz.initMuseumListByNet(city, new OnInitBeanListener() {
                @Override
                public void onSuccess(List<? extends BaseBean> beans) {
                    List<Museum> netMuseumList= (List<Museum>) beans;
                    if(netMuseumList == null || netMuseumList.size() == 0){
                        if(localMuseumList == null || localMuseumList.size() == 0){
                            museumChooseView.onNoData();
                        }else{
                            museumChooseView.setMuseumList(localMuseumList);
                        }
                    }else{
                        if(localMuseumList == null || localMuseumList.size() == 0){
                            museumChooseBiz.saveMuseumBySQL(netMuseumList);
                            museumChooseView.setMuseumList(netMuseumList);
                        }else{
                            netMuseumList.removeAll(localMuseumList);
                            museumChooseBiz.saveMuseumBySQL(netMuseumList);
                            netMuseumList.addAll(localMuseumList);
                            museumChooseView.setMuseumList(netMuseumList);
                        }
                    }
                    museumChooseView.refreshMuseumList();
                }

                @Override
                public void onFailed() {
                    if(localMuseumList == null || localMuseumList.size() == 0){
                        museumChooseView.showFailedError();
                    }else{
                        museumChooseView.setMuseumList(localMuseumList);
                        museumChooseView.refreshMuseumList();
                    }
                }
            });
        }

    }

    public void onErrorFresh(){
        handler.sendEmptyMessage(MSG_WHAT_HIDE_ERROR_VIEW);
        initMuseumList();
    }

    public void onGetCity() {
        String city = museumChooseView.getCityName();
        if(TextUtils.isEmpty(city)){
            museumChooseView.showFailedError();
            return;
        }
        handler.sendEmptyMessage(MSG_WHAT_REFRESH_TITLE);
        initMuseumList();
    }

    public void onMuseumChoose(Museum museum) {
        museumChooseView.setChooseMuseum(museum);
        //存储博物馆id
        AppManager.getInstance(museumChooseView.getContext()).setMuseumId(museum.getId());
        if(museum.getDownloadState() == Museum.DOWNLOAD_STATE_FINISH){
            museumChooseView.skit2MuseumHome(museum);
        }else{
            museumChooseView.showDownloadTipDialog();
        }

    }

    public void onDownloadMuseum() {
        Museum museum = museumChooseView.getChooseMuseum();
        museumChooseView.showProgressDialog();
        museumChooseBiz.downloadMuseum(museum,progressListener);// TODO: 2016/9/2

    }
    private IMuseumChooseBiz.DownloadProgressListener progressListener = new IMuseumChooseBiz.DownloadProgressListener(){

        @Override
        public void onStart() {
            AndroidUtil.closeBluetooth();
        }

        @Override
        public void onProgress(int progress, int totalSize) {
            museumChooseView.updateProgress(progress,totalSize);
            if(progress == totalSize){
                Museum museum = museumChooseView.getChooseMuseum();
                museum.setDownloadState(Museum.DOWNLOAD_STATE_FINISH);
                museumChooseBiz.updateDownloadState(museum);
                museumChooseView.hideProgressDialog();
            }
        }

        @Override
        public void onEnd() {
            AndroidUtil.openBluetooth();
        }

        @Override
        public void onError() {

        }
    };


    private static class MyHandler extends Handler {

        WeakReference<IMuseumChooseView> activityWeakReference;
        MyHandler(IMuseumChooseView activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference == null){return;}
            IMuseumChooseView activity=activityWeakReference.get();
            if(activity == null){return;}
            switch (msg.what){
                case MSG_WHAT_REFRESH_MUSEUM_LIST:
                    activity.refreshMuseumList();
                    break;
                case MSG_WHAT_REFRESH_TITLE:
                    String name = (String) msg.obj;
                    activity.setTitle(name);
                    break;
                case MSG_WHAT_UPDATE_DATA_FAIL:
                    activity.showFailedError();
                    break;
                case MSG_WHAT_HIDE_ERROR_VIEW:
                    activity.hideErrorView();
                    break;

            }
            activity.hideLoading();
        }
    }

}
