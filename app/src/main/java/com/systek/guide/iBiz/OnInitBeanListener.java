package com.systek.guide.iBiz;


import com.systek.guide.bean.BaseBean;

import java.util.List;

/**
 * Created by Qiang on 2016/7/29.
 */
public interface OnInitBeanListener {

    void onSuccess(List<? extends BaseBean> beans);
    void onFailed();

}
