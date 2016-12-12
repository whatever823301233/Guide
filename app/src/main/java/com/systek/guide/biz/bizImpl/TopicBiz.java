package com.systek.guide.biz.bizImpl;

import android.os.AsyncTask;

import com.systek.guide.base.util.LogUtil;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.biz.iBiz.ITopicBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.db.handler.ExhibitHandler;
import com.systek.guide.ui.widget.channel.ChannelItem;

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
                String id = params[0];
                List<Exhibit> exhibitList = null;
                try{
                    exhibitList = ExhibitHandler.queryAllExhibitListByMuseumId(id);
                }catch (Exception e){
                    LogUtil.e(TopicBiz.class.getClass().getSimpleName(),e);
                }
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
}
