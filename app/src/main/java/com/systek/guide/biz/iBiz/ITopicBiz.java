package com.systek.guide.biz.iBiz;


import com.systek.guide.bean.Exhibit;
import com.systek.guide.ui.widget.channel.ChannelItem;

import java.util.List;

/**
 * Created by Qiang on 2016/8/4.
 */
public interface ITopicBiz {

    void initAllExhibitByMuseumId(String museumId, OnInitBeanListener listener, String tag);

    List<Exhibit> getExhibit(List<ChannelItem> channelItemList);

    List<Exhibit> getExhibit(ChannelItem channelItem);
}
