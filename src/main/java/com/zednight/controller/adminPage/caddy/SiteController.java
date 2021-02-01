package com.zednight.controller.adminPage.caddy;

import cn.craccd.sqlHelper.bean.Page;
import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.bean.Sort.Direction;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxParam;
import com.zednight.ext.ServerExt;
import com.zednight.ext.SiteExt;
import com.zednight.model.*;
import com.zednight.service.*;
import com.zednight.utils.BaseController;
import com.zednight.utils.JsonResult;
import com.zednight.utils.TelnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/adminPage/site")
public class SiteController extends BaseController {
    @Autowired
    private ServerService serverService;
    @Autowired
    private UpstreamService upstreamService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private ConfService confService;
    @Autowired
    private SiteService siteService;

    @RequestMapping("")
    public ModelAndView index(HttpSession httpSession, ModelAndView modelAndView, Page page, String sort, String direction, String keywords) {
        Page pages = siteService.search(page, sort, direction, keywords);
        modelAndView.addObject("page", pages);
        modelAndView.setViewName("/adminPage/caddySite/index");
        return modelAndView;
    }



    @RequestMapping("addOver")
    @ResponseBody
    public JsonResult addOver(String siteJson, String siteParamJson, String toJson) {
        if(siteJson==null) return renderError("site为空");
        Site site = JSONUtil.toBean(siteJson, Site.class);
        List<To> to = JSONUtil.toList(JSONUtil.parseArray(toJson), To.class);
        try {
            siteService.addOver(site, siteParamJson, to);
        } catch (Exception e) {
            return renderError(e.getMessage());
        }
        return renderSuccess();
    }

    @RequestMapping("setEnable")
    @ResponseBody
    public JsonResult setEnable(Server server) {
        sqlHelper.updateById(server);
        return renderSuccess();
    }

    @RequestMapping("detail")
    @ResponseBody
    public JsonResult detail(String id) {
        Server server = sqlHelper.findById(id, Server.class);

        ServerExt serverExt = new ServerExt();
        serverExt.setServer(server);
        List<Location> list = serverService.getLocationByServerId(id);
        for (Location location : list) {
            String json = paramService.getJsonByTypeId(location.getId(), "location");
            location.setLocationParamJson(json);
        }
        serverExt.setLocationList(list);
        String json = paramService.getJsonByTypeId(server.getId(), "server");
        serverExt.setParamJson(json);

        return renderSuccess(serverExt);
    }

    @RequestMapping("del")
    @ResponseBody
    public JsonResult del(String id) {
        serverService.deleteById(id);

        return renderSuccess();
    }

    @RequestMapping("clone")
    @ResponseBody
    public JsonResult clone(String id) {
        serverService.clone(id);

        return renderSuccess();
    }

    @RequestMapping("importServer")
    @ResponseBody
    public JsonResult importServer(String nginxPath) {

        if (StrUtil.isEmpty(nginxPath) || !FileUtil.exist(nginxPath)) {
            return renderError(m.get("serverStr.fileNotExist"));
        }

        try {
            serverService.importServer(nginxPath);
            return renderSuccess(m.get("serverStr.importSuccess"));
        } catch (Exception e) {
            e.printStackTrace();

            return renderError(m.get("serverStr.importFail"));
        }
    }

    @RequestMapping("testPort")
    @ResponseBody
    public JsonResult testPort() {
        List<Server> servers = sqlHelper.findAll(Server.class);

        List<String> ips = new ArrayList<>();
        for (Server server : servers) {
            String ip = "";
            String port = "";
            if (server.getListen().contains(":")) {
                ip = server.getListen().split(":")[0];
                port = server.getListen().split(":")[1];
            } else {
                ip = "127.0.0.1";
                port = server.getListen();
            }

            if (TelnetUtils.isRunning(ip, Integer.parseInt(port)) && !ips.contains(server.getListen())) {
                ips.add(server.getListen());
            }
        }

        if (ips.size() == 0) {
            return renderSuccess();
        } else {
            return renderError(m.get("serverStr.portUserdList") + ": " + StrUtil.join(" , ", ips));
        }

    }

    @RequestMapping("editDescr")
    @ResponseBody
    public JsonResult editDescr(String id, String descr) {
        Server server = new Server();
        server.setId(id);
        server.setDescr(descr);
        sqlHelper.updateById(server);

        return renderSuccess();
    }

    @RequestMapping("preview")
    @ResponseBody
    public JsonResult preview(String id, String type) {
        NgxBlock ngxBlock = null;
        if (type.equals("server")) {
            Server server = sqlHelper.findById(id, Server.class);
            ngxBlock = confService.bulidBlockServer(server);
        } else if (type.equals("upstream")) {
            Upstream upstream = sqlHelper.findById(id, Upstream.class);
            ngxBlock = confService.buildBlockUpstream(upstream);
        } else if (type.equals("http")) {
            List<Http> httpList = sqlHelper.findAll(new Sort("seq", Direction.ASC), Http.class);
            ngxBlock = new NgxBlock();
            ngxBlock.addValue("http");
            for (Http http : httpList) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(http.getName().trim() + " " + http.getValue().trim());
                ngxBlock.addEntry(ngxParam);
            }
        } else if (type.equals("stream")) {
            List<Stream> streamList = sqlHelper.findAll(new Sort("seq", Direction.ASC), Stream.class);
            ngxBlock = new NgxBlock();
            ngxBlock.addValue("stream");
            for (Stream stream : streamList) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(stream.getName() + " " + stream.getValue());
                ngxBlock.addEntry(ngxParam);
            }
        }
        NgxConfig ngxConfig = new NgxConfig();
        ngxConfig.addEntry(ngxBlock);

        String conf = new NgxDumper(ngxConfig).dump().replace("};", "  }");

        return renderSuccess(conf);
    }

}
