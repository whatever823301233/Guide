package com.systek.okhttp.builder;

import com.systek.okhttp.request.OtherRequest;
import com.systek.okhttp.request.RequestCall;

import okhttp3.RequestBody;

/**
 * Created by xq823 on 2016/8/1.
 */
public class OtherRequestBuilder extends OkHttpRequestBuilder<OtherRequestBuilder> {


    private RequestBody requestBody;
    private String method;
    private String content;

    public OtherRequestBuilder(String method)
    {
        this.method = method;
    }

    @Override
    public RequestCall build()
    {
        return new OtherRequest(requestBody, content, method, url, tag, params, headers,id).build();
    }

    public OtherRequestBuilder requestBody(RequestBody requestBody)
    {
        this.requestBody = requestBody;
        return this;
    }

    public OtherRequestBuilder requestBody(String content)
    {
        this.content = content;
        return this;
    }

}
