package com.systek.guide.biz.iBiz;

/**
 * Created by Qiang on 2016/8/2.
 */
public interface OnResponseListener {

    void onSuccess(String path);

    void onFail(String error);

}
