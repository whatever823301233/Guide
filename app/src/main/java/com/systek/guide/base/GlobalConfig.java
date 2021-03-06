package com.systek.guide.base;

import android.content.Context;

import com.systek.guide.bean.ChannelItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Qiang on 2016/7/13.
 *
 * 应用级的pref缓存
 */
public class GlobalConfig extends Config {

    private static final String CONFIG_FILE_NAME = "global_config";

    private static volatile GlobalConfig mInstance;
    private String currentAreaId;

    private HashMap<String,Integer> visitAreaCount = new HashMap<>();
    private List<ChannelItem> channelList;


    private GlobalConfig( Context context, String configFileName ) {

        super( context, configFileName );
    }

    private GlobalConfig() {
    }


    /**
     * 获取单例对象
     *
     * @param context
     * @return GlobalConfig
     * @since 1.0.0
     */
    public static GlobalConfig getInstance( Context context ) {

        if( mInstance == null ) {
            synchronized( GlobalConfig.class ) {
                if( mInstance == null ) {
                    mInstance = new GlobalConfig( context, CONFIG_FILE_NAME );
                }
            }
        }
        return mInstance;
    }



    /*
     * 销毁单例，建议在应用退出的时候调用
     */
    @Override
    public void destroy() {

        super.destroy();
        mInstance = null;
    }


    public void saveUserChannelList(List<ChannelItem> channelItems){
        this.channelList=channelItems;
    }

    public List<ChannelItem> getUserChannelList(){
        return channelList;
    }




}
