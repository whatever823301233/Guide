package com.systek.guide.db.tableInfoImpl;

import android.database.sqlite.SQLiteDatabase;

import com.systek.guide.bean.City;
import com.systek.guide.bean.Label;

import java.util.ArrayList;

/**
 * Created by Qiang on 2016/8/12.
 */
public class LabelInfo extends TableInfo {

    private static final String CREATE_INFO     = "create table if not exists "
            + Label.TABLE_NAME                  + " (_id integer primary key autoincrement , "
            + Label.ID                          + " varchar,"
            + Label.MUSEUM_ID                   + " varchar,"
            + Label.NAME                        + " varchar,"
            + Label.LABELS                      + " varchar )";

    private static final ArrayList<String> tableInfo;

    static {
        tableInfo=new ArrayList<>();
        tableInfo.add(CREATE_INFO);
    }

    public LabelInfo() {
        super(City.TABLE_NAME, tableInfo);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 2016/7/30
    }


}
