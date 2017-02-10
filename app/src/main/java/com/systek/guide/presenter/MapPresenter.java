package com.systek.guide.presenter;

import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.systek.guide.biz.MapBiz;
import com.systek.guide.iBiz.IMapBiz;
import com.systek.guide.iView.IMapView;

import java.lang.ref.WeakReference;

/**
 * Created by qiang on 2016/12/2.
 */

public class MapPresenter {


    private static final int MSG_WHAT_SET_USER_LOCATION = 9527;
    private static final int MSG_WHAT_SET_ROUTE = 9528;
    private static final int MSG_WHAT_SET_ROUTE_NULL = 9529;

    private IMapBiz mapBiz;
    private IMapView mapView;
    private Handler handler;

    public MapPresenter(IMapView mapView){
        this.mapView = mapView;
        mapBiz = new MapBiz();
        handler = new MyHandler(mapView);
    }


    public void onLocationChanged(Location location) {
        boolean useRoutedLocation = mapView.isUseRoutedLocation();
        if(!useRoutedLocation && !Double.isNaN(location.getLatitude())) {
            //Switch to ui thread
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_SET_USER_LOCATION;
            msg.obj = location;
            handler.sendMessage(msg);
        }
    }

    public void onLocationOnRouteChanged(Location location) {
        boolean useRoutedLocation = mapView.isUseRoutedLocation();

        if( useRoutedLocation &&!Double.isNaN(location.getLatitude())) {
            //Switch to ui thread
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_SET_USER_LOCATION;
            msg.obj = location;
            handler.sendMessage(msg);
        }
    }

    public void onRouteChange(double[][] triplets) {
        mapView.setUseRoutedLocation(true);
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_SET_ROUTE;
        msg.obj = triplets;
        handler.sendMessage(msg);
    }

    public void onRouteCalculationFailure(int i, String msg) {
        mapView.setUseRoutedLocation(false);
        handler.sendEmptyMessage(MSG_WHAT_SET_ROUTE_NULL);
    }

    public void onResume() {
        mapView.setTitle();
    }

    private static class MyHandler extends Handler {

        WeakReference<IMapView> activityWeakReference;
        MyHandler(IMapView activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference == null){return;}
            IMapView mapView = activityWeakReference.get();
            if(mapView == null){return;}
            switch (msg.what){
                case MSG_WHAT_SET_USER_LOCATION:
                    Location location = (Location) msg.obj;
                    mapView.setUserLocation(location);
                    break;
                case MSG_WHAT_SET_ROUTE:
                    double[][] triplets = (double[][]) msg.obj;
                    mapView.setRoute(triplets);

            }
        }
    }


}
