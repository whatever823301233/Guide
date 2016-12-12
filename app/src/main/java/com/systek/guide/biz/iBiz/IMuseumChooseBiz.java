package com.systek.guide.biz.iBiz;


import com.systek.guide.bean.Museum;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 */
public interface IMuseumChooseBiz {

    void initMuseumListByNet(String city, OnInitBeanListener onInitBeanListener);

    List<Museum> initMuseumListBySQL(String city);

    void saveMuseumBySQL(List<Museum> museumList);

    void downloadMuseum(Museum museum, DownloadProgressListener listener);

    void updateDownloadState(Museum museum);


    interface DownloadProgressListener{

        void onStart();
        void onProgress(int progress, int totalSize);
        void onEnd();
        void onError();
    }

}
