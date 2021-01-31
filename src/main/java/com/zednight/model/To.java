package com.zednight.model;

import cn.craccd.sqlHelper.bean.BaseModel;
import cn.craccd.sqlHelper.config.Table;

@Table
public class To extends BaseModel {
    private String siteId;
    private String lbPolicy;
    private String lbTryDuration;
    private String location;
    private String proxyAddress;
    private String toParamJson;

    public String getToParamJson() {
        return toParamJson;
    }

    public void setToParamJson(String toParamJson) {
        this.toParamJson = toParamJson;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProxyAddress() {
        return proxyAddress;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLbPolicy() {
        return lbPolicy;
    }

    public void setLbPolicy(String lbPolicy) {
        this.lbPolicy = lbPolicy;
    }

    public String getLbTryDuration() {
        return lbTryDuration;
    }

    public void setLbTryDuration(String lbTryDuration) {
        this.lbTryDuration = lbTryDuration;
    }
}
