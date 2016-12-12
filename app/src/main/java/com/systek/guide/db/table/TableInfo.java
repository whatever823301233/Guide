package com.systek.guide.db.table;

import com.systek.guide.base.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by qiang on 2016/11/30.
 */

public class TableInfo {

    private Class<?> clazz;
    private TableColumn primaryKey;
    private String tableName;
    private List<TableColumn> columns;

    public TableInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.setInit();
    }

    private <T> void setInit() {
        this.columns = new ArrayList<>();
        this.setTableName();
        this.setColumns(this.clazz);
        if(this.primaryKey == null) {
            throw new RuntimeException("类 " + this.clazz.getSimpleName() + " 没有设置主键，请使用标注主键");
        } else {
            if(LogUtil.mIsPrintLog) {
                LogUtil.v("SqliteUtility", String.format("类 %s 的主键是 %s", new Object[]{this.clazz.getSimpleName(), this.primaryKey.getColumn()}));
                Iterator i$ = this.columns.iterator();

                while(i$.hasNext()) {
                    TableColumn column = (TableColumn)i$.next();
                    LogUtil.v("SqliteUtility", String.format("[column = %s, datatype = %s]", new Object[]{column.getColumn(), column.getDataType()}));
                }
            }

        }
    }

    private void setTableName() {
        this.tableName = TableInfoUtils.getTableName(this.clazz);
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public TableColumn getPrimaryKey() {
        return this.primaryKey;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<TableColumn> getColumns() {
        return this.columns;
    }

    public void setColumns(Class<?> c) {
        if(c != null && !"Object".equalsIgnoreCase(c.getSimpleName())) {
            Field[] fields = c.getDeclaredFields();
            Field[] arr$ = fields;
            int len$ = fields.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Field field = arr$[i$];
                if(this.primaryKey == null) {
                    PrimaryKey column = (PrimaryKey)field.getAnnotation(PrimaryKey.class);
                    if(column != null) {
                        this.primaryKey = new TableColumn();
                        this.primaryKey.setColumn(column.column());
                        this.setColumn(field, this.primaryKey);
                        continue;
                    }

                    AutoIncrementPrimaryKey autoIncrementPrimaryKey = (AutoIncrementPrimaryKey)field.getAnnotation(AutoIncrementPrimaryKey.class);
                    if(autoIncrementPrimaryKey != null) {
                        this.primaryKey = new AutoIncrementTableColumn();
                        this.primaryKey.setColumn(autoIncrementPrimaryKey.column());
                        this.setColumn(field, this.primaryKey);
                        continue;
                    }
                }

                if(!"serialVersionUID".equals(field.getName()) && !"$change".equals(field.getName())) {
                    TableColumn var9 = new TableColumn();
                    var9.setColumn(field.getName());
                    this.setColumn(field, var9);
                    this.columns.add(var9);
                }
            }

            this.setColumns(c.getSuperclass());
        }
    }

    private void setColumn(Field field, TableColumn column) {
        column.setField(field);
        if(!field.getType().getName().equals("int") && !field.getType().getName().equals("java.lang.Integer")) {
            if(!field.getType().getName().equals("long") && !field.getType().getName().equals("java.lang.Long")) {
                if(!field.getType().getName().equals("float") && !field.getType().getName().equals("java.lang.Float")) {
                    if(!field.getType().getName().equals("double") && !field.getType().getName().equals("java.lang.Double")) {
                        if(!field.getType().getName().equals("boolean") && !field.getType().getName().equals("java.lang.Boolean")) {
                            if(!field.getType().getName().equals("char") && !field.getType().getName().equals("java.lang.Character")) {
                                if(!field.getType().getName().equals("byte") && !field.getType().getName().equals("java.lang.Byte")) {
                                    if(!field.getType().getName().equals("short") && !field.getType().getName().equals("java.lang.Short")) {
                                        if(field.getType().getName().equals("java.lang.String")) {
                                            column.setDataType("string");
                                            column.setColumnType("TEXT");
                                        } else if(field.getType().getName().equals("[B")) {
                                            column.setDataType("blob");
                                            column.setColumnType("BLOB");
                                        } else {
                                            column.setDataType("object");
                                            column.setColumnType("TEXT");
                                        }
                                    } else {
                                        column.setDataType("short");
                                        column.setColumnType("TEXT");
                                    }
                                } else {
                                    column.setDataType("byte");
                                    column.setColumnType("INTEGER");
                                }
                            } else {
                                column.setDataType("char");
                                column.setColumnType("TEXT");
                            }
                        } else {
                            column.setDataType("boolean");
                            column.setColumnType("TEXT");
                        }
                    } else {
                        column.setDataType("double");
                        column.setColumnType("REAL");
                    }
                } else {
                    column.setDataType("float");
                    column.setColumnType("REAL");
                }
            } else {
                column.setDataType("long");
                column.setColumnType("INTEGER");
            }
        } else {
            column.setDataType("int");
            column.setColumnType("INTEGER");
        }

    }

}
