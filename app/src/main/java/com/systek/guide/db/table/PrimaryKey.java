package com.systek.guide.db.table;

/**
 * Created by qiang on 2016/11/30.
 */

public @interface PrimaryKey {

    String column() default "primary_key";
}
