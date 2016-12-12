package com.systek.guide.db.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.systek.guide.base.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.systek.guide.base.util.LogUtil.d;

/**
 * Created by qiang on 2016/11/30.
 */

public class TableInfoUtils {

    public static final String TAG = "SqliteUtility";
    private static final HashMap<String, TableInfo> tableInfoMap = new HashMap<>();

    public TableInfoUtils() {
    }

    public static <T> TableInfo exist(String dbName, Class<T> clazz) {
        return (TableInfo)tableInfoMap.get(dbName + "-" + getTableName(clazz));
    }

    public static String getTableName(Class<?> clazz) {
        TableName table = clazz.getAnnotation(TableName.class);
        return table != null && table.table().trim().length() != 0?table.table():clazz.getName().replace('.', '_');
    }

    public static <T> TableInfo newTable(String dbName, SQLiteDatabase db, Class<T> clazz) {
        Cursor cursor = null;
        TableInfo tableInfo = new TableInfo(clazz);
        tableInfoMap.put(dbName + "-" + getTableName(clazz), tableInfo);

        try {
            String e = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type =\'table\' AND name =\'" + tableInfo.getTableName() + "\' ";
            cursor = db.rawQuery(e, (String[])null);
            if(cursor != null && cursor.moveToNext()) {
                int createSql = cursor.getInt(0);
                if(createSql > 0) {
                    cursor.close();
                    LogUtil.d("SqliteUtility", "表 %s 已存在", new Object[]{tableInfo.getTableName()});
                    cursor = db.rawQuery("PRAGMA table_info(" + tableInfo.getTableName() + ")", (String[])null);
                    ArrayList tableColumns = new ArrayList();
                    if(cursor != null && cursor.moveToNext()) {
                        do {
                            tableColumns.add(cursor.getString(cursor.getColumnIndex("name")));
                        } while(cursor.moveToNext());
                    }

                    cursor.close();
                    ArrayList properList = new ArrayList();
                    Iterator newFieldList = tableInfo.getColumns().iterator();

                    while(newFieldList.hasNext()) {
                        TableColumn i$ = (TableColumn)newFieldList.next();
                        properList.add(i$.getColumn());
                    }

                    ArrayList newFieldList1 = new ArrayList();
                    Iterator i$2 = properList.iterator();

                    String newField;
                    while(i$2.hasNext()) {
                        newField = (String)i$2.next();
                        if(!tableInfo.getPrimaryKey().equals(newField)) {
                            boolean isNew = true;
                            Iterator i$1 = tableColumns.iterator();

                            while(i$1.hasNext()) {
                                String tableColumn = (String)i$1.next();
                                if(tableColumn.equals(newField)) {
                                    isNew = false;
                                    break;
                                }
                            }

                            if(isNew) {
                                newFieldList1.add(newField);
                            }
                        }
                    }

                    i$2 = newFieldList1.iterator();

                    while(i$2.hasNext()) {
                        newField = (String)i$2.next();
                        db.execSQL(String.format("ALTER TABLE %s ADD %s TEXT", new Object[]{tableInfo.getTableName(), newField}));
                        d("SqliteUtility", "表 %s 新增字段 %s", new Object[]{tableInfo.getTableName(), newField});
                    }

                    TableInfo i$3 = tableInfo;
                    return i$3;
                }
            }

            String createSql1 = SqlUtils.getTableSql(tableInfo);
            db.execSQL(createSql1);
            LogUtil.d("SqliteUtility", "创建一张新表 %s", new Object[]{tableInfo.getTableName()});
        } catch (Exception var18) {
            var18.printStackTrace();
            LogUtil.d("SqliteUtility", var18.getMessage() + "");
        } finally {
            if(cursor != null) {
                cursor.close();
            }

            cursor = null;
        }

        return tableInfo;
    }

}
