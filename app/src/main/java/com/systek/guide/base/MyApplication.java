package com.systek.guide.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.squareup.leakcanary.LeakCanary;
import com.systek.guide.db.DBHandler;
import com.systek.guide.manager.AppManager;

import java.util.Iterator;

/**
 * Created by qiang on 2016/11/28.
 */

public class MyApplication extends Application {

    private static Context instance;

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        if(!isSameAppName()){
            return;
        }
        instance = this;
        AppManager.getInstance(this).initApp("com.systek.guide",iAppListener);
        DBHandler.init(this);

    }


    private IAppListener iAppListener=new IAppListener() {
        @Override
        public void destroy() {
            DBHandler.getInstance().destroy();
        }
    };


    /**
     * 判断是否为相同app名
     *
     * @return
     */
    private boolean isSameAppName() {
        int pid = android.os.Process.myPid();
        String processAppName = getProcessAppName(pid);
        String packageName=getPackageName();
        return !(TextUtils.isEmpty(processAppName) || !processAppName.equalsIgnoreCase(packageName));
    }

    /**
     * 获取processAppName
     *
     * @param pid
     * @return
     */
    private String getProcessAppName(int pid) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = activityManager.getRunningAppProcesses().iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = iterator.next();
            try {
                if (runningAppProcessInfo.pid == pid) {
                    return runningAppProcessInfo.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
