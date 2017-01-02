package com.systek.guide.presenter;

import android.os.Handler;
import android.os.Message;

import com.systek.guide.util.AndroidUtil;
import com.systek.guide.util.LogUtil;
import com.systek.guide.bean.BaseBean;
import com.systek.guide.bean.City;
import com.systek.guide.biz.bizImpl.CityBiz;
import com.systek.guide.biz.iBiz.ICityBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.ui.iView.ICityChooseView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 *
 */
public class CityChoosePresenter {

    private static final int MSG_WHAT_REFRESH_VIEW = 9527;
    private static final int MSG_WHAT_UPDATE_DATA_FAIL = 9528;
    private static final int MSG_WHAT_SKIP_FRAGMENT = 9529;

    private ICityBiz cityBiz;
    private ICityChooseView cityChooseView;
    private Handler handler;

    public CityChoosePresenter(ICityChooseView cityChooseView){
        this.cityChooseView=cityChooseView;
        cityBiz = new CityBiz();
        handler = new MyHandler(cityChooseView);
    }

    public void onErrorFresh(){
        cityChooseView.hideErrorView();
        initCity();
    }

    public void initCity(){
        cityChooseView.showLoading();
        cityChooseView.hideKeyboard();
        List<City> cities = cityBiz.initCitiesBySQL(null);
        if(cities!=null&&cities.size()>0){
            LogUtil.i("","加载城市by sql");
            cityChooseView.setListCities(cities);
            handler.sendEmptyMessage(MSG_WHAT_REFRESH_VIEW);
            return;
        }
        boolean isNetConn= AndroidUtil.isNetworkConnected(cityChooseView.getContext());
        if(!isNetConn){
            cityChooseView.showFailedError();
            return;
        }
        cityBiz.initCitiesByNet(new OnInitBeanListener() {
            @Override
            public void onSuccess(List<? extends BaseBean> beans) {
                if( beans == null || beans.size() == 0 ){return;}
                if( beans.get(0) instanceof City){
                    List<City> cities = (List<City>)beans;
                    cityChooseView.setListCities(cities);
                    handler.sendEmptyMessage(MSG_WHAT_REFRESH_VIEW);
                    cityBiz.saveCitiesBySQL(cities);
                }
            }

            @Override
            public void onFailed() {
                handler.sendEmptyMessage(MSG_WHAT_UPDATE_DATA_FAIL);
            }
        });
    }

    public void clearInputCity(){
        cityChooseView.clearInputCity();
    }


    public void onCityChoose(City city){
        cityChooseView.setCurrentCity(city.getName());
        cityChooseView.toNextFragment(city.getName());
       /* Message msg = Message.obtain();
        msg.what = MSG_WHAT_SKIP_FRAGMENT;
        msg.obj = city.getName();
        handler.sendMessage(msg);*/
    }


    public void fixCityName(){
        String input = cityChooseView.getCurrentInput();
        List<City> cities = cityChooseView.getListCities();
        List<City> tipCities = cityBiz.fixCityName(input,cities);
        cityChooseView.updateTipCities(tipCities);
    }

    public void onCreate() {
        setToolbar();
    }

    private void setToolbar() {
        cityChooseView.setTitle("城市选择");
    }


    private static class MyHandler extends Handler {

        WeakReference<ICityChooseView> activityWeakReference;
        MyHandler(ICityChooseView activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if(activityWeakReference == null){return;}
            ICityChooseView view = activityWeakReference.get();
            if(view == null){return;}
            switch (msg.what){
                case MSG_WHAT_REFRESH_VIEW:
                    view.refreshView();
                    break;
                case MSG_WHAT_UPDATE_DATA_FAIL:
                    view.showFailedError();
                case MSG_WHAT_SKIP_FRAGMENT:
                    String city = (String) msg.obj;
                    view.toNextFragment(city);
                default:
                    break;
            }
            view.hideLoading();
        }
    }

}
