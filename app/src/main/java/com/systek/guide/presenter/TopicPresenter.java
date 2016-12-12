package com.systek.guide.presenter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.systek.guide.base.Constants;
import com.systek.guide.base.GlobalConfig;
import com.systek.guide.base.util.FileUtil;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.bean.BaseBean;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.biz.bizImpl.TopicBiz;
import com.systek.guide.biz.iBiz.ITopicBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.iView.ITopicView;
import com.systek.guide.ui.widget.channel.ChannelItem;
import com.systek.okhttp_library.OkHttpUtils;
import com.systek.okhttp_library.callback.FileCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by qiang on 2016/12/2.
 */

public class TopicPresenter {

    private static final int MSG_WHAT_SHOW_ALL_EXHIBITS =9527;
    private static final int MSG_WHAT_UPDATE_DATA_FAIL=9528;


    private ITopicView topicView;
    private ITopicBiz topicBiz;
    private Handler handler;

    public TopicPresenter(ITopicView topicView){
        this.topicView=topicView;
        topicBiz=new TopicBiz();
        handler=new MyHandler(this.topicView);
    }

    public void onViewCreated(){
        topicView.showLoading();
        topicView.setTitle("专题");
        String museumId = topicView.getMuseumId();
        topicBiz.initAllExhibitByMuseumId(museumId, new OnInitBeanListener() {
            @Override
            public void onSuccess(List<? extends BaseBean> beans) {
                List<Exhibit> exhibitList= (List<Exhibit>) beans;
                topicView.setAllExhibitList(exhibitList);
                handler.sendEmptyMessage(MSG_WHAT_SHOW_ALL_EXHIBITS);
            }

            @Override
            public void onFailed() {
                handler.sendEmptyMessage(MSG_WHAT_UPDATE_DATA_FAIL);
            }
        },topicView.getTag());
    }


    public void onExhibitChoose() {
        Exhibit exhibit = topicView.getChooseExhibit();
        String url = exhibit.getAudiourl();
        String name = FileUtil.changeUrl2Name(url);
        boolean fileExists = FileUtil.checkFileExists(url,exhibit.getMuseumId());
        if(!fileExists){
            topicView.showLoading();
            OkHttpUtils
                    .get().url(Constants.BASE_URL+exhibit.getAudiourl())
                    .build()
                    .execute(new FileCallBack(Constants.LOCAL_PATH+exhibit.getMuseumId(),name) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            LogUtil.e("",e.toString());
                            topicView.showFailedError();
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            topicView.hideLoading();
                            topicView.toPlay();
                        }
                    });
        }else{
            LogUtil.i("File is Exists");
            topicView.toPlay();
        }
    }

    public void checkChannelList() {
        List<ChannelItem> channelItems= GlobalConfig.getInstance(topicView.getContext()).getUserChannelList();
        if(channelItems==null||channelItems.size()<=2){
            channelItems=new ArrayList<>();
            channelItems.add(new ChannelItem(1, "全部", 1, 1));
            channelItems.add(new ChannelItem(2, "筛选", 2, 1));
            topicView.setUserChannelList(channelItems);
            topicView.showAllExhibits();
            return;
        }
        topicView.setUserChannelList(channelItems);

        List<Exhibit> exhibitList=getExhibitByChannel(channelItems);
        if(exhibitList==null||exhibitList.size()==0){
            topicView.onNoData();
        }else{
            topicView.setAllExhibitList(exhibitList);
            topicView.refreshExhibitList();
        }
    }





    private List<Exhibit> getExhibitByChannel(List<ChannelItem> channelItemList){
        if(channelItemList==null||channelItemList.size()==0){return null;}
        topicBiz.getExhibit(channelItemList);
        return topicBiz.getExhibit(channelItemList);

    }
    private List<Exhibit> getExhibitByChannel(ChannelItem channelItem){
        if(channelItem==null){return null;}
        if(channelItem.getName().equals("全部")){
            return topicBiz.getExhibit(topicView.getUserChannelList());
        }else if(channelItem.getName().equals("筛选")){
            return topicBiz.getExhibit(topicView.getUserChannelList());
        }
        return topicBiz.getExhibit(channelItem);

    }

    public void onChannelChoose() {
        ChannelItem channelItem=topicView.getChooseChannel();
        if(channelItem==null){return;}
        new AsyncTask<ChannelItem,Integer,List<Exhibit>>(){
            @Override
            protected List<Exhibit> doInBackground(ChannelItem... params) {
                ChannelItem item=params[0];
                if(item==null){return null;}
                return getExhibitByChannel(item);
            }
            @Override
            protected void onPostExecute(List<Exhibit> exhibits) {
                if(exhibits==null||exhibits.size()==0){
                    topicView.onNoData();
                }
                topicView.setAllExhibitList(exhibits);
                topicView.refreshExhibitList();
            }
        }.execute(channelItem);


    }


    private static class MyHandler extends Handler {

        WeakReference<ITopicView> activityWeakReference;
        MyHandler(ITopicView activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference==null){return;}
            ITopicView activity=activityWeakReference.get();
            if(activity==null){return;}
            switch (msg.what){
                case MSG_WHAT_SHOW_ALL_EXHIBITS:
                    activity.showAllExhibits();
                    break;
                case MSG_WHAT_UPDATE_DATA_FAIL:
                    activity.showFailedError();
                    break;
                default:break;
            }
            activity.hideLoading();
        }
    }

}
