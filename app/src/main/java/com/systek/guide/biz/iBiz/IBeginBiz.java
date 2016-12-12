package com.systek.guide.biz.iBiz;


/**
 * Created by Qiang on 2016/9/22.
 */
public interface IBeginBiz  {

    /**
     * 开始界面等待一段时间
     * @param time 等待时长
     */
    void waitForSomeTime(int time,OnResponseListener listener);


}
