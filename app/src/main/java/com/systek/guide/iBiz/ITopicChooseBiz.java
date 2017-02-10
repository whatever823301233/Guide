package com.systek.guide.iBiz;


import com.systek.guide.bean.Label;

import java.util.List;

/**
 * Created by Qiang on 2016/8/12.
 */
public interface ITopicChooseBiz {
    List<Label> getLabels(String museumId);
}
