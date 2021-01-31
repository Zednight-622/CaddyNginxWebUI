package com.zednight.ext;

import com.zednight.model.Location;
import com.zednight.model.Server;
import com.zednight.model.Site;
import com.zednight.model.To;

import java.util.List;

public class SiteExt {
    Site site;
    List<To> toList;
    String locationStr;
    String paramJson;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<To> getToList() {
        return toList;
    }

    public void setToList(List<To> toList) {
        this.toList = toList;
    }

    public String getLocationStr() {
        return locationStr;
    }

    public void setLocationStr(String locationStr) {
        this.locationStr = locationStr;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
