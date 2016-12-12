package com.systek.guide.db.tableInfoImpl;

import android.database.sqlite.SQLiteDatabase;

import com.systek.guide.bean.City;

import java.util.ArrayList;

/**
 * Created by xq823 on 2016/7/30.
 */
public class CityInfo extends TableInfo {

    private static final String CREATE_INFO     = "create table if not exists "
            + City.TABLE_NAME                   + " (_id integer primary key autoincrement , "
            + City.ALPHA                        + " varchar,"
            + City.NAME                         + " varchar )";

    private static final ArrayList<String> tableInfo;

    static {
        tableInfo=new ArrayList<>();
        tableInfo.add(CREATE_INFO);
    }

    public CityInfo() {
        super(City.TABLE_NAME, tableInfo);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 2016/7/30
    }
}
