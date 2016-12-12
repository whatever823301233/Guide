package com.systek.guide.biz.bizImpl;


import com.systek.guide.bean.Label;
import com.systek.guide.biz.iBiz.ITopicChooseBiz;
import com.systek.guide.db.handler.LabelHandler;

import java.util.List;

/**
 * Created by Qiang on 2016/8/12.
 */
public class TopicChooseBiz implements ITopicChooseBiz {
    @Override
    public List<Label> getLabels(String museumId) {
        return LabelHandler.queryLabels(museumId);
    }
}
