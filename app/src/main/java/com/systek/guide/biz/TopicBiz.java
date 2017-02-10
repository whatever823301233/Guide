package com.systek.guide.biz;

import android.os.AsyncTask;

import com.systek.guide.bean.ChannelItem;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.db.handler.ExhibitHandler;
import com.systek.guide.iBiz.ITopicBiz;
import com.systek.guide.iBiz.OnInitBeanListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Qiang on 2016/8/4.
 */
public class TopicBiz implements ITopicBiz {

    @Override
    public void initAllExhibitByMuseumId(String museumId, final OnInitBeanListener listener, String tag) {

        new AsyncTask<String,Integer,Void>(){
            @Override
            protected Void doInBackground(String... params) {
                List<Exhibit> exhibitList = null;
                String id = params[0];
                exhibitList = ExhibitHandler.queryAllExhibitListByMuseumId(id);
                if(exhibitList == null || exhibitList.size() == 0 ){
                    listener.onFailed();
                }else{
                    listener.onSuccess(exhibitList);
                }
                return null;
            }
        }.execute(museumId);

    }

    @Override
    public List<Exhibit> getExhibit(List<ChannelItem> channelItemList) {
        return ExhibitHandler.queryExhibit(channelItemList);
    }

    @Override
    public List<Exhibit> getExhibit(ChannelItem channelItem) {
        return ExhibitHandler.queryExhibit(channelItem);
    }

    @Override
    public List<Exhibit> getFilterExhibit(final List<ChannelItem> userChannelList) {
        if(userChannelList == null || userChannelList.size() == 0){ return null; }
        List<ChannelItem> channelItems = new ArrayList<>(userChannelList);
        channelItems.remove(0);
        channelItems.remove(0);
        List<Exhibit> filterList =  ExhibitHandler.queryExhibit(channelItems.get(0));
        if(filterList == null || filterList.size() == 0){return null;}
        for(int i = 1;i<channelItems.size();i++){
            ChannelItem item = channelItems.get(i);
            List<Exhibit> singleList =  ExhibitHandler.queryExhibit(item);
            filterList.retainAll(singleList);
        }
        return filterList;
    }
}
