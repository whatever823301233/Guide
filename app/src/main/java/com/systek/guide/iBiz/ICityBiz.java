package com.systek.guide.iBiz;


import com.systek.guide.bean.City;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 */
public interface ICityBiz {

    List<City> fixCityName(String cityName, List<City> cities);

    /**
     * 联网加载城市
     * @param cityListener 监听回调（成功或失败）
     */
    void initCitiesByNet( OnInitBeanListener cityListener);

    /**
     * 从数据库中加载城市
     * @param cityListener 监听回调（成功或失败）
     * @return
     */
    List<City> initCitiesBySQL(OnInitBeanListener cityListener);

    /**
     * 将城市数据保存至数据库
     * @param cities
     */
    void saveCitiesBySQL(List<City> cities);


}
