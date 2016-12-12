package com.systek.guide.db.table;

import android.text.TextUtils;

import com.systek.guide.base.util.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by qiang on 2016/11/30.
 */

public class SqlUtils {

    public SqlUtils() {
    }

    public static String getTableSql(TableInfo table) {
        TableColumn primaryKey = table.getPrimaryKey();
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("CREATE TABLE IF NOT EXISTS ");
        strSQL.append(table.getTableName());
        strSQL.append(" ( ");
        if(primaryKey instanceof AutoIncrementTableColumn) {
            strSQL.append(" ").append(primaryKey.getColumn()).append(" ").append(" INTEGER PRIMARY KEY AUTOINCREMENT ,");
        } else {
            strSQL.append(" ").append(primaryKey.getColumn()).append(" ").append(primaryKey.getColumnType()).append(" NOT NULL ,");
        }

        Iterator tableStr = table.getColumns().iterator();

        while(tableStr.hasNext()) {
            TableColumn column = (TableColumn)tableStr.next();
            strSQL.append(" ").append(column.getColumn());
            strSQL.append(" ").append(column.getColumnType());
            strSQL.append(" ,");
        }

        strSQL.append(" ").append("com_m_common_owner").append(" text NOT NULL, ");
        strSQL.append(" ").append("com_m_common_key").append(" text NOT NULL, ");
        strSQL.append(" ").append("com_m_common_createat").append(" INTEGER NOT NULL ");
        if(!(primaryKey instanceof AutoIncrementTableColumn)) {
            strSQL.append(", PRIMARY KEY ( ").append(primaryKey.getColumn()).append(" , ").append("com_m_common_key").append(" , ").append("com_m_common_owner").append(" )");
        }

        strSQL.append(" )");
        String tableStr1 = strSQL.toString();
        LogUtil.d("SqliteUtility", "create table = " + tableStr1);
        return tableStr1;
    }

    public static String createSqlInsert(String insertInto, TableInfo tableInfo) {
        ArrayList columns = new ArrayList();
        if(!(tableInfo.getPrimaryKey() instanceof AutoIncrementTableColumn)) {
            columns.add(tableInfo.getPrimaryKey());
        }

        columns.addAll(tableInfo.getColumns());
        columns.add(FieldUtils.getOwnerColumn());
        columns.add(FieldUtils.getKeyColumn());
        columns.add(FieldUtils.getCreateAtColumn());
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tableInfo.getTableName()).append(" (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");
        appendPlaceholders(builder, columns.size());
        builder.append(')');
        return builder.toString();
    }

    public static StringBuilder appendColumns(StringBuilder builder, List<TableColumn> columns) {
        int length = columns.size();

        for(int i = 0; i < length; ++i) {
            builder.append('\'').append(((TableColumn)columns.get(i)).getColumn()).append('\'');
            if(i < length - 1) {
                builder.append(',');
            }
        }

        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for(int i = 0; i < count; ++i) {
            if(i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }

        return builder;
    }

    public static String appendExtraWhereClauseSql(Extra extra) {
        StringBuffer sb = new StringBuffer();
        if(extra != null && (!TextUtils.isEmpty(extra.getKey()) || !TextUtils.isEmpty(extra.getOwner()))) {
            if(!TextUtils.isEmpty(extra.getKey()) && !TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = \'").append(extra.getOwner()).append("\' ").append(" and ").append("com_m_common_key").append(" = \'").append(extra.getKey()).append("\' ");
            } else if(!TextUtils.isEmpty(extra.getKey())) {
                sb.append("com_m_common_key").append(" = \'").append(extra.getKey()).append("\' ");
            } else if(!TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = \'").append(extra.getOwner()).append("\' ");
            }
        } else {
            sb.append("");
        }

        return sb.toString();
    }

    public static String appendExtraWhereClause(Extra extra) {
        StringBuffer sb = new StringBuffer();
        if(extra != null && (!TextUtils.isEmpty(extra.getKey()) || !TextUtils.isEmpty(extra.getOwner()))) {
            if(!TextUtils.isEmpty(extra.getKey()) && !TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_key").append(" = ? ").append(" and ").append("com_m_common_owner").append(" = ? ");
            } else if(!TextUtils.isEmpty(extra.getKey())) {
                sb.append("com_m_common_key").append(" = ? ");
            } else if(!TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = ? ");
            }
        } else {
            sb.append("");
        }

        return sb.toString();
    }

    public static String[] appendExtraWhereArgs(Extra extra) {
        ArrayList argList = new ArrayList();
        if(extra != null) {
            if(!TextUtils.isEmpty(extra.getKey())) {
                argList.add(extra.getKey());
            }

            if(!TextUtils.isEmpty(extra.getOwner())) {
                argList.add(extra.getOwner());
            }
        }

        return (String[])argList.toArray(new String[0]);
    }

}
