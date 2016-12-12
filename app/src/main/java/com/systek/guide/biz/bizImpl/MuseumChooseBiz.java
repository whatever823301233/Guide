package com.systek.guide.biz.bizImpl;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.systek.okhttp_library.OkHttpUtils;
import com.systek.okhttp_library.callback.FileCallBack;
import com.systek.okhttp_library.callback.StringCallback;
import com.systek.guide.base.Constants;
import com.systek.guide.base.util.FileUtil;
import com.systek.guide.base.util.LogUtil;
import com.systek.guide.bean.Museum;
import com.systek.guide.biz.iBiz.IMuseumChooseBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.db.handler.MuseumHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Created by xq823 on 2016/7/31.
 */
public class MuseumChooseBiz implements IMuseumChooseBiz {

    public static final String TAG = MuseumChooseBiz.class.getSimpleName();


    @Override
    public void initMuseumListByNet(final String city, final OnInitBeanListener onInitBeanListener) {

        OkHttpUtils
                .get()
                .url( Constants.URL_MUSEUM_LIST )
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        LogUtil.e(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<Museum> museumList = JSON.parseArray(response, Museum.class);
                        if(museumList != null && museumList.size() > 0){
                            List<Museum> cityMuseumList = new ArrayList<>();
                            for(Museum museum : museumList){
                                if(museum.getCity().equals(city)){
                                    cityMuseumList.add(museum);
                                }
                            }
                            onInitBeanListener.onSuccess(cityMuseumList);
                        }else{
                            onInitBeanListener.onFailed();
                        }
                    }
                });
    }

    @Override
    public List<Museum> initMuseumListBySQL(String city) {
        return  MuseumHandler.queryAllMuseumByCity(city);
    }

    @Override
    public void saveMuseumBySQL(List<Museum> museumList) {
        MuseumHandler.addMuseumList(museumList);
    }

    @Override
    public void downloadMuseum(Museum museum,final DownloadProgressListener listener) {
        if(museum == null){return;}
        final String museumId = museum.getId();
        String url = Constants.URL_ASSETS_LIST + museumId;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        LogUtil.e("",e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = (JSONArray) jsonObject.get("url");
                            LogUtil.i("",array.toString());
                            List<String> urlList = JSON.parseArray(array.toString(),String.class);
                            totalSize = urlList.size();
                            LogUtil.i("",urlList.size());
                            downloadFiles(urlList,museumId,listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void updateDownloadState(Museum museum) {
        if(museum == null){return;}
        MuseumHandler.updateMuseum(museum);
    }

    private void downloadFiles(List<String> urlList,String museumId,DownloadProgressListener listener) {
        if(urlList == null){return;}
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(String str:urlList){
            if(TextUtils.isEmpty(str)){continue;}
            DownloadTask task=new DownloadTask(listener);
            task.executeOnExecutor(executor,str,museumId);
        }

    }
    private int totalSize;
    private int progress;

    private class DownloadTask extends AsyncTask<String,Integer,String>{


        DownloadProgressListener listener;

        DownloadTask(DownloadProgressListener listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... params) {
            listener.onStart();
            String mUrl = params[0];
            String name = FileUtil.changeUrl2Name(mUrl);
            String url = Constants.BASE_URL + mUrl;
            String savePath = Constants.LOCAL_PATH + params[1];
            boolean flag = FileUtil.checkFileExists(savePath + "/" + name);
            if(flag){
                progress ++;
                if(listener != null){
                    listener.onProgress(progress,totalSize);
                }
                if(progress == totalSize){
                    listener.onEnd();
                }
            }else{
                OkHttpUtils
                        .get()
                        .url(url)
                        .build()
                        .execute(new FileCallBack(savePath,name) {

                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                LogUtil.e("",e);
                            }

                            @Override
                            public void onResponse(File response, int id) {
                                LogUtil.i("",response.getAbsolutePath());
                                progress ++;
                                if(listener != null){
                                    listener.onProgress(progress,totalSize);
                                }
                                if(progress == totalSize){
                                    listener.onEnd();
                                }
                            }
                        });
            }
            return null;
        }

    }

}
