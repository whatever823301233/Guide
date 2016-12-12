package com.systek.okhttp_library.builder;

import com.systek.okhttp_library.OkHttpUtils;
import com.systek.okhttp_library.request.OtherRequest;
import com.systek.okhttp_library.request.RequestCall;

/**
 * Created by xq823 on 2016/8/1.
 */
public class HeadBuilder extends GetBuilder {

    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }

}
