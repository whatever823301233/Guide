package com.systek.guide.biz;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.systek.guide.base.Constants;
import com.systek.guide.util.FileUtil;
import com.systek.guide.util.LogUtil;
import com.systek.guide.bean.Exhibit;
import com.systek.guide.bean.Label;
import com.systek.guide.bean.Museum;
import com.systek.guide.bean.MyBeacon;
import com.systek.guide.iBiz.IMuseumHomeBiz;
import com.systek.guide.iBiz.OnInitBeanListener;
import com.systek.guide.iBiz.OnResponseListener;
import com.systek.guide.db.handler.BeaconHandler;
import com.systek.guide.db.handler.ExhibitHandler;
import com.systek.guide.db.handler.LabelHandler;
import com.systek.okhttp.OkHttpUtils;
import com.systek.okhttp.callback.FileCallBack;
import com.systek.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Qiang on 2016/8/1.
 */
public class MuseumHomeBiz implements IMuseumHomeBiz {

    @Override
    public List<String> getImgUrls(Museum museum) {
        List<String> imgUrls=new ArrayList<>();
        if(museum==null){return imgUrls;}
        String imgStr=museum.getImgurl();
        String[] imgs = imgStr.split(",");
        Collections.addAll(imgUrls,imgs);
        return imgUrls;
    }


    @Override
    public void getExhibitListByMuseumId(String museumId,final OnInitBeanListener listener) {

        new AsyncTask<String,Integer,List<Exhibit>>(){

            @Override
            protected List<Exhibit> doInBackground(String... params) {
                String id=params[0];
                return ExhibitHandler.queryAllExhibitListByMuseumId(id);
            }

            @Override
            protected void onPostExecute(List<Exhibit> exhibitList) {
                if(exhibitList!=null&&exhibitList.size()>0){
                    listener.onSuccess(exhibitList);
                }else{
                    listener.onFailed();
                }
            }
        }.execute(museumId);

    }

    @Override
    public void getExhibitListByMuseumIdNet(String museumId,final String tag,final OnInitBeanListener listener) {
        String url= Constants.URL_EXHIBIT_LIST + museumId;
        OkHttpUtils.get()
                .url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.e("",e);
                listener.onFailed();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    listener.onSuccess(JSON.parseArray(response,Exhibit.class));
                }catch (Exception e){
                    LogUtil.e("",e);
                    listener.onFailed();
                }
            }
        });
    }

    @Override
    public void getLabelListByNet(String museumId,final OnInitBeanListener onInitBeanListener, String tag) {
        String url = Constants.URL_LABEL_LIST + museumId;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onInitBeanListener.onFailed();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onInitBeanListener.onSuccess(JSON.parseArray(response,Label.class));
                    }
                });
    }

    @Override
    public List<Label> getLabels(String museumId) {
        return LabelHandler.queryLabels(museumId);
    }

    @Override
    public void saveLabels(List<Label> labelList) {
        LabelHandler.addLabels(labelList);
        LogUtil.i("","addLabels 保存成功 ");
    }

    @Override
    public List<MyBeacon> getMyBeacons(String museumId) {
        return  BeaconHandler.queryBeacons(museumId);
    }

    @Override
    public void saveBeacons(List<MyBeacon> beacons) {
        BeaconHandler.addBeacons(beacons);
    }

    @Override
    public void saveExhibit(List<Exhibit> exhibitList) {
        ExhibitHandler.addExhibitList(exhibitList);
        LogUtil.i(MuseumHomeBiz.class.getSimpleName(),"saveExhibit(List<Exhibit> exhibitList)");
    }


    @Override
    public void downloadMuseumAudio(String audioUrl,final String id,final OnResponseListener listener) {
        String dir = Constants.LOCAL_PATH + id;
        String name = FileUtil.changeUrl2Name(audioUrl);
        OkHttpUtils
                .get()
                .url(Constants.BASE_URL + audioUrl)
                .build()
                .execute(new FileCallBack(dir,name ) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        listener.onFail(e.toString());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        listener.onSuccess(response.getAbsolutePath());
                    }
                });
    }

}
