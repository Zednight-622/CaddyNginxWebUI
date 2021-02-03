package com.zednight.service;

import cn.craccd.sqlHelper.bean.Page;
import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.utils.ConditionAndWrapper;
import cn.craccd.sqlHelper.utils.ConditionOrWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zednight.ext.SiteExt;
import com.zednight.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SiteService {
    @Autowired
    private SqlHelper sqlHelper;


    public Page search(Page page, String sortColum, String direction, String keywords) {
        ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
        if (StrUtil.isNotEmpty(keywords)) {
            conditionAndWrapper.and(new ConditionOrWrapper().like("redirAddress", keywords).like("name", keywords.trim()).like("port", keywords.trim()));
        }

        Sort sort = null;
        if (StrUtil.isNotEmpty(sortColum)) {
            sort = new Sort(sortColum, "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC);
        }

        page = sqlHelper.findPage(conditionAndWrapper, sort, page, Site.class);
        List<SiteExt> exts = new ArrayList<SiteExt>();
        for (Site site : page.getRecords(Site.class)) {
            SiteExt siteExt = new SiteExt();
            siteExt.setSite(site);
            siteExt.setToStr(buildToStr(site.getId()));
            exts.add(siteExt);
        }
        page.setRecords(exts);
        return page;
    }

    private String buildToStr(String id) {
        List<String> str = new ArrayList<String>();
        List<To> tos = this.getToBySiteId(id);
        for (To to : tos) {
            str.add("<span class='path'>" + to.getLocation() + "</span><span class='value'>" + to.getProxyAddress() + "</span>");
        }
        return StrUtil.join("<br>", str);
    }

    public List<To> getToBySiteId(String id) {
        return sqlHelper.findListByQuery(new ConditionAndWrapper().eq("siteId", id), To.class);
    }

    @Transactional
    public void addOver(Site site, String siteParamJson, List<To> tos) {
        sqlHelper.insertOrUpdate(site);
        List<Param> paramList = new ArrayList<Param>();
        if (StrUtil.isNotEmpty(siteParamJson) && JSONUtil.isJson(siteParamJson)) {
            paramList = JSONUtil.toList(JSONUtil.parseArray(siteParamJson), Param.class);
        }
        Collections.reverse(paramList);
        for (Param param : paramList) {
            param.setSiteId(site.getId());
            sqlHelper.insert(param);
        }

        sqlHelper.deleteByQuery(new ConditionAndWrapper().eq("siteId", site.getId()), To.class);

        if (tos != null) {
            // 反向插入,保证列表与输入框对应
            Collections.reverse(tos);

            for (To to : tos) {
                to.setSiteId(site.getId());

                sqlHelper.insert(to);

                paramList = new ArrayList<Param>();
                if (StrUtil.isNotEmpty(to.getToParamJson()) && JSONUtil.isJson(to.getToParamJson())) {
                    paramList = JSONUtil.toList(JSONUtil.parseArray(to.getToParamJson()), Param.class);
                }

                // 反向插入,保证列表与输入框对应
                Collections.reverse(paramList);
                for (Param param : paramList) {
                    param.setToId(to.getId());
                    sqlHelper.insert(param);
                }
            }
        }
    }

    public void deleteById(String id) {
        sqlHelper.deleteById(id, Site.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper().eq("site", id), To.class);
    }
}
