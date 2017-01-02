package com.systek.guide.biz.bizImpl;

import com.alibaba.fastjson.JSON;
import com.systek.guide.base.Constants;
import com.systek.guide.util.LogUtil;
import com.systek.guide.bean.City;
import com.systek.guide.biz.iBiz.ICityBiz;
import com.systek.guide.biz.iBiz.OnInitBeanListener;
import com.systek.guide.db.handler.CityHandler;
import com.systek.okhttp_library.OkHttpUtils;
import com.systek.okhttp_library.callback.StringCallback;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 *
 */
public class CityBiz implements ICityBiz {


    public static final String TAG = CityBiz.class.getSimpleName();
    private List<City> cities;
    @Override
    public List<City> fixCityName(String cityName, List<City> cities) {
        return null;
    }

    @Override
    public void initCitiesByNet( final OnInitBeanListener cityListener) {

        OkHttpUtils
                .get()
                .url( Constants.URL_CITY_LIST )
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        LogUtil.e(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("",response);
                        List<City> cities= JSON.parseArray(response,City.class);
                        if(cities!=null&&cities.size()>0){
                            cityListener.onSuccess(cities);
                        }else{
                            cityListener.onFailed();
                        }
                    }
                });
    }

      /*  AsyncPost post=new AsyncPost(Constants.URL_CITY_LIST,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                LogUtil.i("",response);
                List<City> cities= JSON.parseArray(response,City.class);
                if(cities!=null&&cities.size()>0){
                    cityListener.onSuccess(cities);
                }else{
                    cityListener.onFailed();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("",error);
            }
        });
        QVolley.getInstance(null).addToAsyncQueue(post,tag);
}*/

    @Override
    public List<City> initCitiesBySQL(OnInitBeanListener cityListener) {



        cities= CityHandler.queryAllCities();
        if(cityListener==null){return cities;}
        if(cities==null||cities.size()==0){
            cityListener.onFailed();
        }else{
            cityListener.onSuccess(cities);
        }
        return cities;
    }

    @Override
    public void saveCitiesBySQL(List<City> cities) {
        CityHandler.addCities(cities);
    }


}
