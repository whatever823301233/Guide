package com.systek.guide.base;

/**
 * Created by qiang on 2016/11/28.
 *
 * 监听app的生命状态
 */

public interface IAppListener {
    /**
     *  app退出时，清除数据及状态使用
     */
    void destroy();

}
