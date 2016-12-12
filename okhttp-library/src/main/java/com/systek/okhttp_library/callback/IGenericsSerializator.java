package com.systek.okhttp_library.callback;

/**
 * Created by xq823 on 2016/8/1.
 */
public interface IGenericsSerializator {

    <T> T transform(String response, Class<T> classOfT);

}
