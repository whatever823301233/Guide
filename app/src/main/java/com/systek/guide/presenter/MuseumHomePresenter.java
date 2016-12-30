package com.systek.guide.presenter;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.systek.guide.R;
import com.systek.guide.base.Constants;
import com.systek.guide.base.util.FileUtil;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.bean.BaseBean;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.bean.Label;
import com.systek.guide.bean.Museum;
import com.systek.guide.biz.bizImpl.MuseumHomeBiz;
import com.systek.guide.biz.iBiz.IMuseumHomeBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.biz.iBiz.OnResponseListener;
import com.systek.guide.ui.iView.IMuseumHomeView;
import com.systek.guide.ui.fragment.CollectionFragment;
import com.systek.guide.ui.fragment.ExhibitListFragment;
import com.systek.guide.ui.fragment.MapFragment;
import com.systek.guide.ui.fragment.TopicFragment;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by qiang on 2016/12/2.
 */

public class MuseumHomePresenter {

    private static final  int  MSG_WHAT_REFRESH_VIEW = 9527;
    private static final  int  MSG_WHAT_SET_TITLE = 9528;
    private static final  int  MSG_WHAT_REFRESH_ICONS = 9529;
    private static final  int  MSG_WHAT_REFRESH_INTRODUCE = 9530;
    private static final  int  MSG_WHAT_REFRESH_MEDIA = 9531;
    private static final  int  MSG_WHAT_SHOW_ERROR = 9532;

    private IMuseumHomeView museumHomeView;
    private IMuseumHomeBiz museumHomeBiz;
    private Handler handler;


    public MuseumHomePresenter ( IMuseumHomeView  museumHomeView ){
        this.museumHomeView = museumHomeView;
        museumHomeBiz = new MuseumHomeBiz();
        handler = new MyHandler ( museumHomeView );
    }


    public void onViewCreated(){

        setAdapterMuseumId();
        //initTitle();
        initIcons();
        initIntroduce();
        initAudio();
        initExhibits();
        initLabels();
    }

    private void setAdapterMuseumId() {
        Museum museum = museumHomeView.getCurrentMuseum();
        museumHomeView.setAdapterMuseumId(museum.getId());
    }

    private void initLabels() {
        museumHomeView.showLoading();
        Museum museum = museumHomeView.getCurrentMuseum();
        final String museumId = museum.getId();
        List<Label> labels = museumHomeBiz.getLabels(museumId);
        if(labels != null && labels.size()>0){
            museumHomeView.hideLoading();
            return;
        }
        museumHomeBiz.getLabelListByNet(museumId,new OnInitBeanListener(){
            @Override
            public void onSuccess(List<? extends BaseBean> beans) {
                List<Label> labelList = (List<Label>) beans;
                museumHomeBiz.saveLabels(labelList);
            }

            @Override
            public void onFailed() {
                museumHomeView.showFailedError();
            }
        },museumHomeView.getTag());


    }


    private void initExhibits() {
        museumHomeView.showLoading();
        Museum museum = museumHomeView.getCurrentMuseum();
        final String museumId = museum.getId();
        museumHomeBiz.getExhibitListByMuseumId ( museumId, new OnInitBeanListener(){
            @Override
            public void onSuccess(List<? extends BaseBean> beans) {
                LogUtil.i("","getExhibitListByMuseumId onSuccess");
            }

            @Override
            public void onFailed() {
                LogUtil.i("","getExhibitListByMuseumId onFailed");
                museumHomeBiz.getExhibitListByMuseumIdNet ( museumId, museumHomeView.getTag(), new OnInitBeanListener() {
                    @Override
                    public void onSuccess ( List <? extends BaseBean> beans ) {
                        LogUtil.i("","getExhibitListByMuseumIdNet onSuccess");
                        List<Exhibit> exhibitList = (List<Exhibit>) beans;
                        museumHomeBiz.saveExhibit(exhibitList);
                        handler.sendEmptyMessage(MSG_WHAT_REFRESH_VIEW);
                    }

                    @Override
                    public void onFailed() {
                        LogUtil.i("","getExhibitListByMuseumIdNet onFailed");
                        handler.sendEmptyMessage(MSG_WHAT_SHOW_ERROR);
                    }
                });
            }
        });
    }

    private void initIntroduce() {
        Museum museum = museumHomeView.getCurrentMuseum();
        String introduce = museum.getTexturl();
        museumHomeView.refreshIntroduce(introduce);
    }

    private void initAudio() {
        museumHomeView.showLoading();
        final Museum museum = museumHomeView.getCurrentMuseum();
        String audioUrl = museum.getAudiourl();
        boolean isAudioExists =  FileUtil.checkFileExists(audioUrl,museum.getId());
        if(isAudioExists){
            String name = FileUtil.changeUrl2Name(audioUrl);//audioUrl.replaceAll("/","_");
            museumHomeView.setMediaPath( Constants.LOCAL_PATH + museum.getId()+"/"+name);
            museumHomeView.refreshMedia();
        }else{
            museumHomeBiz.downloadMuseumAudio(audioUrl,museum.getId(),new OnResponseListener(){
                @Override
                public void onSuccess(String path) {
                    museumHomeView.setMediaPath(path);
                    museumHomeView.refreshMedia();
                }

                @Override
                public void onFail(String error) {
                    handler.sendEmptyMessage(MSG_WHAT_SHOW_ERROR);
                }
            });
        }

    }

    private void initIcons() {
        Museum museum = museumHomeView.getCurrentMuseum();
        List<String> imgUrls = museumHomeBiz.getImgUrls(museum);
        museumHomeView.setIconUrls(imgUrls);
        museumHomeView.refreshIcons();
    }

    public void onImageButtonClick() {
        View view = museumHomeView.getOnClickView();
        String flag = null;
        switch (view.getId()){
            case R.id.rlTopicHome:
                flag = TopicFragment.class.getSimpleName();
                break;
            case R.id.rlCollectionHome:
                flag = CollectionFragment.class.getSimpleName();
                break;
        }
        if(flag != null){
            museumHomeView.addShowFragment(flag);
            return;
        }
        if(view.getId() == R.id.rlMapHome){
            museumHomeView.showGuideActivity(MapFragment.class.getSimpleName());
        }else{
            museumHomeView.showGuideActivity(ExhibitListFragment.class.getSimpleName());
        }
    }

    public void onResume() {
        refreshTitle();
    }

    private void refreshTitle() {
        Museum museum = museumHomeView.getCurrentMuseum();
        if(museum == null){ return; }
        museumHomeView.setTitle(museum.getName());
    }

    private static class MyHandler extends Handler {

        WeakReference<IMuseumHomeView> activityWeakReference;
        MyHandler(IMuseumHomeView activity){
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference == null){return;}
            IMuseumHomeView activity = activityWeakReference.get();
            if(activity == null){return;}
            switch (msg.what){
                case MSG_WHAT_REFRESH_VIEW:
                    activity.refreshView();
                    activity.hideLoading();
                    break;
               /* case MSG_WHAT_SET_TITLE:
                    String museumName = (String) msg.obj;
                    activity.setTitle(museumName);
                    break;*/
                /*case MSG_WHAT_REFRESH_ICONS:
                    activity.refreshIcons();
                    break;
                case MSG_WHAT_REFRESH_INTRODUCE:
                    activity.refreshIntroduce();
                    break;
                case MSG_WHAT_REFRESH_MEDIA:
                    activity.refreshMedia();
                    break;*/
                case MSG_WHAT_SHOW_ERROR:
                    activity.hideLoading();
                    activity.showFailedError();
                    break;
                default:break;
            }
        }
    }


}
