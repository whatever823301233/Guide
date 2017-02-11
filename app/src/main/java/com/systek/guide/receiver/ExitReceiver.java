package com.systek.guide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.systek.guide.manager.AppManager;


/**
 * Created by Qiang on 2016/7/13.
 */
public class ExitReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        AppManager.getInstance( context ).exitApp();
    }
}
