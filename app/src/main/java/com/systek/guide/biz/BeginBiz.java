package com.systek.guide.biz;


import android.os.AsyncTask;

import com.systek.guide.iBiz.IBeginBiz;
import com.systek.guide.iBiz.OnResponseListener;

/**
 * Created by Qiang on 2016/9/22.
 */
public class BeginBiz implements IBeginBiz {

    //private ArrayList<AsyncTask<String,Void,Void>> taskList;

    @Override
    public void waitForSomeTime(final int time, final OnResponseListener listener) {

        AsyncTask<String,Void,Void> task = new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... voids) {

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(null);
                return null;
            }
        };
        task.execute();
        /*if(taskList == null){
            taskList = new ArrayList<>();
        }
        taskList.add(task);*/


    }

  /*  @Override
    public void cancelAllTask() {
        if(taskList == null){
            return;
        }
        for(AsyncTask task: taskList){
            if(task != null && !task.isCancelled()){
                try{
                    task.cancel(true);
                }catch (Exception e){
                    LogUtil.e("",e);
                }
            }
        }
    }*/
}
