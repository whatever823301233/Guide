package com.systek.guide.biz.bizImpl;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.systek.guide.biz.iBiz.ICollectionBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.db.handler.ExhibitHandler;


/**
 * Created by Qiang on 2016/8/9.
 */
public class CollectionBiz implements ICollectionBiz {


    @Override
    public void initExhibitByMuseumId(String museumId, final OnInitBeanListener listener, String tag) {

        // TODO: 2016/8/9 QVolley 中加入普通异步任务
        new AsyncTask<String,Integer,Void>(){
            @Override
            protected Void doInBackground(String... params) {
                String id = params[0];
                if(TextUtils.isEmpty(id)){
                    listener.onFailed();
                }else{
                    listener.onSuccess(ExhibitHandler.queryFavoriteExhibitListByMuseumId(id));
                }
                return null;
            }
        }.execute(museumId);
    }
}
