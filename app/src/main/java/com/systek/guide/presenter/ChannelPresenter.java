package com.systek.guide.presenter;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.systek.guide.R;
import com.systek.guide.base.GlobalConfig;
import com.systek.guide.bean.Label;
import com.systek.guide.biz.TopicChooseBiz;
import com.systek.guide.iBiz.ITopicChooseBiz;
import com.systek.guide.iView.IChannelChooseView;
import com.systek.guide.bean.ChannelItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by qiang on 2016/12/2.
 */

public class ChannelPresenter {

    private IChannelChooseView topicChooseView;

    private ITopicChooseBiz topicChooseBiz;

    private Handler handler;


    public ChannelPresenter(IChannelChooseView topicChooseView){
        this.topicChooseView=topicChooseView;
        topicChooseBiz=new TopicChooseBiz();

    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if(topicChooseView.isMove()){return;}
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0 && position != 1) {
                    topicChooseView.hideUserAddOtherItem(parent,view,position,id);
                }
                break;
            case R.id.otherGridView:
                topicChooseView.hideOtherAddUserItem(parent,view,position,id);
                break;
            default:
                break;
        }
    }


    public void initChannels() {
        topicChooseView.showLoading();
        String museumId =   topicChooseView.getMuseumId();

        new AsyncTask<String,Integer,List<String>>(){

            @Override
            protected List<String> doInBackground(String... params) {
                String museumId = params[0];
                List<Label> labels = topicChooseBiz.getLabels(museumId);
                if(labels == null || labels.size() == 0 ){return null;}
                List<String> labelStrList = new ArrayList<>();
                for(Label label:labels){
                    String str = label.getLables();
                    if(TextUtils.isEmpty(str)){continue;}
                    String[] innerLabels = str.split(",");
                    if(innerLabels.length == 0){continue;}
                    Collections.addAll(labelStrList, innerLabels);
                }
                return labelStrList;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                if(strings == null || strings.size() == 0 ){
                    topicChooseView.showFailedError();
                    return;
                }
                /** 初始化数据*/
                ArrayList<ChannelItem> userChannelList = new ArrayList<>();
                ArrayList<ChannelItem> otherChannelList = new ArrayList<>();
                userChannelList.add(new ChannelItem(1, "全部", 1, 1));
                userChannelList.add(new ChannelItem(2, "筛选", 2, 1));
                int i=1,j=1;
                for(String s:strings){
                    ChannelItem item=new ChannelItem(i,s,j,0);
                    otherChannelList.add(item);
                    i++;
                    j++;
                }
                topicChooseView.setUserChannel(userChannelList);
                topicChooseView.setOtherChannel(otherChannelList);
                topicChooseView.updateUserChannel();
                topicChooseView.updateOtherChannel();

            }
        }.execute(museumId);
    }

    public void saveLabels() {
        List<ChannelItem> channelItems = topicChooseView.getUserChannelList();
        if(channelItems != null && channelItems.size() > 2){
            GlobalConfig.getInstance(topicChooseView.getContext()).saveUserChannelList(channelItems);
        }
    }

}
