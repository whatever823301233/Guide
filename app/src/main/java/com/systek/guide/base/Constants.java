package com.systek.guide.base;

import android.os.Environment;

/**
 * Created by xq823 on 2016/7/30.
 */
public class Constants {

    public static final String SP_IS_AUTO_MUSEUM = "sp_is_auto_museum";//是否在博物馆
    public static final String SP_IS_AUTO_GPS = "SP_IS_AUTO_GPS";//是否在博物馆
    public static final String SP_IS_AUTO_UPDATE = "sp_is_auto_update";//是否在博物馆

    /**本地文件位置*/

    public static final String APP_ROOT = MyApplication.getInstance().getFilesDir().getAbsolutePath();//存储至本地sdcard位置
    public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();//存储至本地sdcard位置
    //String LOCAL_ASSETS_PATH=SDCARD_ROOT + "/Guide/";//sdcard存储图片的位置*/
    public static final String LOCAL_PATH = APP_ROOT+"/";//app内部存储图片的位置*/

    /*关于URL*/
    public static final String BASE_URL = "http://182.92.82.70";

    public static final String URL_CITY_LIST = BASE_URL + "/api/cityService/cityList"; //城市路径

    public static final String URL_MUSEUM_LIST = BASE_URL + "/api/museumService/museumList";// TODO: 2016/12/5  city下博物馆列表

    public static final String URL_EXHIBIT_LIST = "http://182.92.82.70/api/exhibitService/exhibitList?museumId="; //博物馆下展品列表*/

    public static final String URL_BEACON_LIST = "http://182.92.82.70/api/beaconService/beaconList?museumId="; //博物馆下信标列表*/

    public static final String URL_LABEL_LIST = "http://182.92.82.70/api/labelsService/labelsList?museumId=";

    public static final String URL_ASSETS_LIST = "http://182.92.82.70/api/assetsService/assetsList?museumId=";

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "com.example.android.uamp.CURRENT_MEDIA_DESCRIPTION";


}
