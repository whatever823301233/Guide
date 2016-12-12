package com.systek.guide.db.table;

/**
 * Created by qiang on 2016/11/30.
 */

public class Extra {
    private String owner;
    private String key;

    public Extra() {
    }

    public Extra(String owner, String key) {
        this.key = key;
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
