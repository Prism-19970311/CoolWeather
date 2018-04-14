package com.prism.coolweather.db;

import org.litepal.crud.DataSupport;


public class Province extends DataSupport {
    private int id;
    private String provineceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvineceName() {
        return provineceName;
    }

    public void setProvineceName(String provineceName) {
        this.provineceName = provineceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
