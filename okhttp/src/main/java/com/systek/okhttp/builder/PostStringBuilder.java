package com.systek.okhttp.builder;

import com.systek.okhttp.request.PostStringRequest;
import com.systek.okhttp.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by xq823 on 2016/8/1.
 */
public class PostStringBuilder  extends OkHttpRequestBuilder<PostStringBuilder> {

    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content)
    {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build()
    {
        return new PostStringRequest(url, tag, params, headers, content, mediaType,id).build();
    }

}
