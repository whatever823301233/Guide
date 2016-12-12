package com.systek.guide.db.table;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.systek.guide.base.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by qiang on 2016/11/30.
 */

public class SqliteUtility {

    public static final String TAG = "SqliteUtility";
    private static Hashtable<String, SqliteUtility> dbCache = new Hashtable<>();
    private String dbName;
    private SQLiteDatabase db;

    SqliteUtility(String dbName, SQLiteDatabase db) {
        this.db = db;
        this.dbName = dbName;
        dbCache.put(dbName, this);
        LogUtil.d("SqliteUtility", "将库 %s 放到缓存中", new Object[]{dbName});
    }

    public static SqliteUtility getInstance() {
        return getInstance("com_m_default_db");
    }

    public static SqliteUtility getInstance(String dbName) {
        return (SqliteUtility)dbCache.get(dbName);
    }

    public <T> T selectById(Extra extra, Class<T> clazz, Object id) {
        try {
            TableInfo e = checkTable(clazz);
            String selection = String.format(" %s = ? ", e.getPrimaryKey().getColumn());
            String extraSelection = SqlUtils.appendExtraWhereClause(extra);
            if(!TextUtils.isEmpty(extraSelection)) {
                selection = String.format("%s and %s", selection, extraSelection);
            }

            ArrayList selectionArgList = new ArrayList();
            selectionArgList.add(String.valueOf(id));
            String[] extraSelectionArgs = SqlUtils.appendExtraWhereArgs(extra);
            if(extraSelectionArgs != null && extraSelectionArgs.length > 0) {
                selectionArgList.addAll(Arrays.asList(extraSelectionArgs));
            }

            String[] selectionArgs = (String[])selectionArgList.toArray(new String[0]);
            List<T> list = this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
            if(list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return null;
    }

    public <T> List<T> select(Extra extra, Class<T> clazz) {
        String selection = SqlUtils.appendExtraWhereClause(extra);
        String[] selectionArgs = SqlUtils.appendExtraWhereArgs(extra);
        return this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
    }

    public <T> List<T> select(Class<T> clazz, String selection, String[] selectionArgs) {
        return this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
    }

    public <T> List<T> select(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        TableInfo tableInfo = this.checkTable(clazz);
        ArrayList list = new ArrayList();
        if(LogUtil.mIsPrintLog) {
            LogUtil.d("SqliteUtility", " method[select], table[%s], selection[%s], selectionArgs%s, groupBy[%s], having[%s], orderBy[%s], limit[%s] ", new Object[]{tableInfo.getTableName(), selection, JSON.toJSON(selectionArgs), String.valueOf(groupBy), String.valueOf(having), String.valueOf(orderBy), String.valueOf(limit)});
        }

        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(tableInfo.getPrimaryKey().getColumn());
        Iterator start = tableInfo.getColumns().iterator();

        while(start.hasNext()) {
            TableColumn tableColumn = (TableColumn)start.next();
            columnList.add(tableColumn.getColumn());
        }

        long start1 = System.currentTimeMillis();
        Cursor cursor = this.db.query(tableInfo.getTableName(), (String[])columnList.toArray(new String[0]), selection, selectionArgs, groupBy, having, orderBy, limit);
        LogUtil.d("SqliteUtility", "table[%s] 查询数据结束，耗时 %s ms", new Object[]{tableInfo.getTableName(), String.valueOf(System.currentTimeMillis() - start1)});
        start1 = System.currentTimeMillis();

        try {
            if(cursor.moveToFirst()) {
                do {
                    try {
                        Object e = clazz.newInstance();
                        this.bindSelectValue(e, cursor, tableInfo.getPrimaryKey());
                        Iterator i$ = tableInfo.getColumns().iterator();

                        while(i$.hasNext()) {
                            TableColumn column = (TableColumn)i$.next();
                            this.bindSelectValue(e, cursor, column);
                        }

                        list.add(e);
                    } catch (Exception var20) {
                        var20.printStackTrace();
                    }
                } while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        LogUtil.d("SqliteUtility", "table[%s], 设置数据结束，耗时 %s ms", new Object[]{tableInfo.getTableName(), String.valueOf(System.currentTimeMillis() - start1)});
        LogUtil.d("SqliteUtility", "查询到数据 %d 条", new Object[]{Integer.valueOf(list.size())});
        return list;
    }

    public <T> void insert(Extra extra, T... entities) {
        try {
            if(entities != null && entities.length > 0) {
                this.insert(extra, Arrays.asList(entities));
            } else {
                LogUtil.d("SqliteUtility", "method[insert(Extra extra, T... entities)], entities is null or empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void insertOrReplace(Extra extra, T... entities) {
        try {
            if(entities != null && entities.length > 0) {
                this.insert(extra, Arrays.asList(entities), "INSERT OR REPLACE INTO ");
            } else {
                LogUtil.d("SqliteUtility", "method[insertOrReplace(Extra extra, T... entities)], entities is null or empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void insert(Extra extra, List<T> entityList) {
        try {
            this.insert(extra, entityList, "INSERT OR IGNORE INTO ");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void insertOrReplace(Extra extra, List<T> entityList) {
        try {
            this.insert(extra, entityList, "INSERT OR REPLACE INTO ");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    private <T> void insert(Extra extra, List<T> entityList, String insertInto) {
        if(entityList != null && entityList.size() != 0) {
            TableInfo tableInfo = this.checkTable(entityList.get(0).getClass());
            long start = System.currentTimeMillis();
            this.db.beginTransaction();

            try {
                String sql = SqlUtils.createSqlInsert(insertInto, tableInfo);
                LogUtil.v("SqliteUtility", insertInto + " sql = %s", new Object[]{sql});
                SQLiteStatement insertStatement = this.db.compileStatement(sql);
                long bindTime = 0L;
                long startTime = System.currentTimeMillis();
                Iterator i$ = entityList.iterator();

                while(true) {
                    if(!i$.hasNext()) {
                        LogUtil.d("SqliteUtility", "bindvalues 耗时 %s ms", new Object[]{bindTime + ""});
                        this.db.setTransactionSuccessful();
                        break;
                    }

                    Object entity = i$.next();
                    this.bindInsertValues(extra, insertStatement, tableInfo, entity);
                    bindTime += System.currentTimeMillis() - startTime;
                    startTime = System.currentTimeMillis();
                    insertStatement.execute();
                }
            } finally {
                this.db.endTransaction();
            }

            LogUtil.d("SqliteUtility", "表 %s %s 数据 %d 条， 执行时间 %s ms", new Object[]{tableInfo.getTableName(), insertInto, Integer.valueOf(entityList.size()), String.valueOf(System.currentTimeMillis() - start)});
        } else {
            LogUtil.d("SqliteUtility", "method[insert(Extra extra, List<T> entityList)], entityList is null or empty");
        }
    }

    public <T> void update(Extra extra, T... entities) {
        try {
            if(entities != null && entities.length > 0) {
                this.insertOrReplace(extra, entities);
            } else {
                LogUtil.d("SqliteUtility", "method[update(Extra extra, T... entities)], entities is null or empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void update(Extra extra, List<T> entityList) {
        try {
            if(entityList != null && entityList.size() > 0) {
                this.insertOrReplace(extra, entityList);
            } else {
                LogUtil.d("SqliteUtility", "method[update(Extra extra, T... entities)], entities is null or empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> int update(Class<?> clazz, ContentValues values, String whereClause, String[] whereArgs) {
        try {
            TableInfo e = this.checkTable(clazz);
            return this.db.update(e.getTableName(), values, whereClause, whereArgs);
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0;
        }
    }

    public <T> void deleteAll(Extra extra, Class<T> clazz) {
        try {
            TableInfo e = this.checkTable(clazz);
            String where = SqlUtils.appendExtraWhereClauseSql(extra);
            if(!TextUtils.isEmpty(where)) {
                where = " where " + where;
            }

            String sql = "DELETE FROM \'" + e.getTableName() + "\' " + where;
            LogUtil.d("SqliteUtility", "method[delete] table[%s], sql[%s]", new Object[]{e.getTableName(), sql});
            long start = System.currentTimeMillis();
            this.db.execSQL(sql);
            LogUtil.d("SqliteUtility", "表 %s 清空数据, 耗时 %s ms", new Object[]{e.getTableName(), String.valueOf(System.currentTimeMillis() - start)});
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public <T> void deleteById(Extra extra, Class<T> clazz, Object id) {
        try {
            TableInfo e = this.checkTable(clazz);
            String whereClause = String.format(" %s = ? ", e.getPrimaryKey().getColumn());
            String extraWhereClause = SqlUtils.appendExtraWhereClause(extra);
            if(!TextUtils.isEmpty(extraWhereClause)) {
                whereClause = String.format("%s and %s", whereClause, extraWhereClause);
            }

            ArrayList whereArgList = new ArrayList();
            whereArgList.add(String.valueOf(id));
            String[] extraWhereArgs = SqlUtils.appendExtraWhereArgs(extra);
            if(extraWhereArgs != null && extraWhereArgs.length > 0) {
                whereArgList.addAll(Arrays.asList(extraWhereArgs));
            }

            String[] whereArgs = (String[])whereArgList.toArray(new String[0]);
            if(LogUtil.mIsPrintLog) {
                LogUtil.d("SqliteUtility", " method[deleteById], table[%s], id[%s], whereClause[%s], whereArgs%s ", new Object[]{e.getTableName(), String.valueOf(id), whereClause, JSON.toJSON(whereArgs)});
            }

            long start = System.currentTimeMillis();
            int rowCount = this.db.delete(e.getTableName(), whereClause, whereArgs);
            LogUtil.d("SqliteUtility", "表 %s 删除数据 %d 条, 耗时 %s ms", new Object[]{e.getTableName(), Integer.valueOf(rowCount), String.valueOf(System.currentTimeMillis() - start)});
        } catch (Exception var13) {
            var13.printStackTrace();
        }

    }

    public <T> void delete(Class<T> clazz, String whereClause, String[] whereArgs) {
        try {
            TableInfo e = this.checkTable(clazz);
            long start = System.currentTimeMillis();
            int rowCount = this.db.delete(e.getTableName(), whereClause, whereArgs);
            if(LogUtil.mIsPrintLog) {
                LogUtil.d("SqliteUtility", "method[delete], table[%s], whereClause[%s], whereArgs%s ", new Object[]{e.getTableName(), whereClause, JSON.toJSON(whereArgs)});
            }

            LogUtil.d("SqliteUtility", "表 %s 删除数据 %d 条，耗时 %s ms", new Object[]{e.getTableName(), Integer.valueOf(rowCount), String.valueOf(System.currentTimeMillis() - start)});
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public long sum(Class<?> clazz, String column, String whereClause, String[] whereArgs) {
        TableInfo tableInfo = this.checkTable(clazz);
        if(TextUtils.isEmpty(column)) {
            return 0L;
        } else {
            String sql = null;
            if(TextUtils.isEmpty(whereClause)) {
                whereArgs = null;
                sql = String.format(" select sum(%s) as _sum_ from %s ", column, tableInfo.getTableName());
            } else {
                sql = String.format(" select sum(%s) as _sum_ from %s where %s ", column, tableInfo.getTableName(), whereClause);
            }

            //LogUtil.d("SqliteUtility", "sum() --- > " + sql);
           // LogUtil.d("SqliteUtility", whereArgs);

            try {
                long e = System.currentTimeMillis();
                Cursor cursor = this.db.rawQuery(sql, whereArgs);
                if(cursor.moveToFirst()) {
                    long sum = cursor.getLong(cursor.getColumnIndex("_sum_"));
                    LogUtil.d("SqliteUtility", "sum = %s 耗时%sms", new Object[]{String.valueOf(sum), String.valueOf(System.currentTimeMillis() - e)});
                    cursor.close();
                    return sum;
                }
            } catch (Exception var12) {
                LogUtil.e(SqliteUtility.class.getSimpleName(), var12);
            }

            return 0L;
        }
    }

    public long count(Class<?> clazz, String whereClause, String[] whereArgs) {
        TableInfo tableInfo = this.checkTable(clazz);
        String sql = null;
        if(TextUtils.isEmpty(whereClause)) {
            whereArgs = null;
            sql = String.format(" select count(*) as _count_ from %s ", tableInfo.getTableName());
        } else {
            sql = String.format(" select count(*) as _count_ from %s where %s ", tableInfo.getTableName(), whereClause);
        }

        //LogUtil.d("SqliteUtility", "count --- > " + sql);
        //LogUtil.d("SqliteUtility", whereArgs);

        try {
            long e = System.currentTimeMillis();
            Cursor cursor = this.db.rawQuery(sql, whereArgs);
            if(cursor.moveToFirst()) {
                long count = cursor.getLong(cursor.getColumnIndex("_count_"));
                LogUtil.d("SqliteUtility", "count = %s 耗时%sms", new Object[]{String.valueOf(count), String.valueOf(System.currentTimeMillis() - e)});
                cursor.close();
                return count;
            }
        } catch (Exception var11) {
            LogUtil.e(SqliteUtility.class.getSimpleName(), var11);
        }

        return 0L;
    }

    private <T> void bindInsertValues(Extra extra, SQLiteStatement insertStatement, TableInfo tableInfo, T entity) {
        int index = 1;
        if(!(tableInfo.getPrimaryKey() instanceof AutoIncrementTableColumn)) {
            this.bindInsertValue(insertStatement, index++, tableInfo.getPrimaryKey(), entity);
        }

        for(int owner = 0; owner < tableInfo.getColumns().size(); ++owner) {
            TableColumn key = (TableColumn)tableInfo.getColumns().get(owner);
            this.bindInsertValue(insertStatement, index++, key, entity);
        }

        String var10 = extra != null && !TextUtils.isEmpty(extra.getOwner())?extra.getOwner():"";
        insertStatement.bindString(index++, var10);
        String var11 = extra != null && !TextUtils.isEmpty(extra.getKey())?extra.getKey():"";
        insertStatement.bindString(index++, var11);
        long createAt = System.currentTimeMillis();
        insertStatement.bindLong(index, createAt);
    }

    private <T> void bindInsertValue(SQLiteStatement insertStatement, int index, TableColumn column, T entity) {
        try {
            column.getField().setAccessible(true);
            Object e = column.getField().get(entity);
            if(e == null) {
                insertStatement.bindNull(index);
                return;
            }

            if("object".equalsIgnoreCase(column.getDataType())) {
                insertStatement.bindString(index, JSON.toJSONString(e));
            } else if("INTEGER".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindLong(index, Long.parseLong(e.toString()));
            } else if("REAL".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindDouble(index, Double.parseDouble(e.toString()));
            } else if("BLOB".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindBlob(index, (byte[])((byte[])e));
            } else if("TEXT".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindString(index, e.toString());
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            LogUtil.w("SqliteUtility", "属性 %s bindvalue 异常", new Object[]{column.getField().getName()});
        }

    }

    private <T> void bindSelectValue(T entity, Cursor cursor, TableColumn column) {
        Field field = column.getField();
        field.setAccessible(true);

        try {
            if(!field.getType().getName().equals("int") && !field.getType().getName().equals("java.lang.Integer")) {
                if(!field.getType().getName().equals("long") && !field.getType().getName().equals("java.lang.Long")) {
                    if(!field.getType().getName().equals("float") && !field.getType().getName().equals("java.lang.Float")) {
                        if(!field.getType().getName().equals("double") && !field.getType().getName().equals("java.lang.Double")) {
                            if(!field.getType().getName().equals("boolean") && !field.getType().getName().equals("java.lang.Boolean")) {
                                if(!field.getType().getName().equals("char") && !field.getType().getName().equals("java.lang.Character")) {
                                    if(!field.getType().getName().equals("byte") && !field.getType().getName().equals("java.lang.Byte")) {
                                        if(!field.getType().getName().equals("short") && !field.getType().getName().equals("java.lang.Short")) {
                                            if(field.getType().getName().equals("java.lang.String")) {
                                                field.set(entity, cursor.getString(cursor.getColumnIndex(column.getColumn())));
                                            } else if(field.getType().getName().equals("[B")) {
                                                field.set(entity, cursor.getBlob(cursor.getColumnIndex(column.getColumn())));
                                            } else {
                                                String e = cursor.getString(cursor.getColumnIndex(column.getColumn()));
                                                field.set(entity, JSON.parseObject(e, field.getGenericType()));
                                            }
                                        } else {
                                            field.set(entity, cursor.getShort(cursor.getColumnIndex(column.getColumn())));
                                        }
                                    } else {
                                        field.set(entity, (byte) cursor.getInt(cursor.getColumnIndex(column.getColumn())));
                                    }
                                } else {
                                    field.set(entity, cursor.getString(cursor.getColumnIndex(column.getColumn())).toCharArray()[0]);
                                }
                            } else {
                                field.set(entity, Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(column.getColumn()))));
                            }
                        } else {
                            field.set(entity, cursor.getDouble(cursor.getColumnIndex(column.getColumn())));
                        }
                    } else {
                        field.set(entity, cursor.getFloat(cursor.getColumnIndex(column.getColumn())));
                    }
                } else {
                    field.set(entity, cursor.getLong(cursor.getColumnIndex(column.getColumn())));
                }
            } else {
                field.set(entity, cursor.getInt(cursor.getColumnIndex(column.getColumn())));
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private <T> TableInfo checkTable(Class<T> clazz) {
        TableInfo tableInfo = TableInfoUtils.exist(this.dbName, clazz);
        if(tableInfo == null) {
            tableInfo = TableInfoUtils.newTable(this.dbName, this.db, clazz);
        }

        return tableInfo;
    }

}
