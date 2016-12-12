package com.systek.guide.db.table;

import java.lang.reflect.Field;

/**
 * Created by qiang on 2016/11/30.
 */

public class TableColumn {

    private String dataType;
    private Field field;
    private String column;
    private String columnType;

    public TableColumn() {
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

}
