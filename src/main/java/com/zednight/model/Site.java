package com.zednight.model;

import cn.craccd.sqlHelper.bean.BaseModel;
import cn.craccd.sqlHelper.config.Table;

@Table
public class Site extends BaseModel {
    private String name;
    private String port;
    private String isGzip;
    private String isZstd;
    private String isRedir;
    private String redirAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIsGzip() {
        return isGzip;
    }

    public void setIsGzip(String isGzip) {
        this.isGzip = isGzip;
    }

    public String getIsZstd() {
        return isZstd;
    }

    public void setIsZstd(String isZstd) {
        this.isZstd = isZstd;
    }

    public String getIsRedir() {
        return isRedir;
    }

    public void setIsRedir(String isRedir) {
        this.isRedir = isRedir;
    }

    public String getRedirAddress() {
        return redirAddress;
    }

    public void setRedirAddress(String redirAddress) {
        this.redirAddress = redirAddress;
    }
}
