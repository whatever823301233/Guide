package com.systek.guide.bean;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by Qiang on 2016/7/12.
 */
public abstract class BaseBean implements Serializable{

    public abstract ContentValues toContentValues();

}
