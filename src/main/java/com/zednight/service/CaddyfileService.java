package com.zednight.service;

import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.utils.ConditionAndWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.zednight.config.InitConfig;
import com.zednight.ext.AsycPack;
import com.zednight.ext.ConfExt;
import com.zednight.ext.ConfFile;
import com.zednight.model.*;
import com.github.odiszapc.nginxparser.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CaddyfileService {
    final UpstreamService upstreamService;
    final SettingService settingService;
    final ServerService serverService;
    final LocationService locationService;
    final ParamService paramService;
    final SqlHelper sqlHelper;
    final TemplateService templateService;

    public CaddyfileService(TemplateService templateService, UpstreamService upstreamService, SettingService settingService, ServerService serverService, LocationService locationService,
                       ParamService paramService, SqlHelper sqlHelper) {
        this.upstreamService = upstreamService;
        this.settingService = settingService;
        this.serverService = serverService;
        this.locationService = locationService;
        this.paramService = paramService;
        this.sqlHelper = sqlHelper;
        this.templateService = templateService;
    }

    public synchronized ConfExt buildCaddyfile() {
        return buildConf(false, false);
    }

    public synchronized ConfExt buildConf(Boolean decompose, Boolean check) {
        ConfExt confExt = new ConfExt();
        confExt.setFileList(new ArrayList<>());

        String caddyPath = settingService.get("caddyPath");
        if (check) {
            caddyPath = InitConfig.home + "temp/caddyfile";
        }
        try {

            NgxConfig ngxConfig = new NgxConfig();
            NgxBlock globalBlock = new NgxBlock();
            globalBlock.addValue("");
            // 获取基本参数
            List<Global> globals = sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Global.class);
            for (Global g : globals) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(g.getName().trim() + " " + g.getValue().trim());
                globalBlock.addEntry(ngxParam);
            }
            ngxConfig.addEntry(globalBlock);

            // 获取Site参数
            List<Site> siteList = sqlHelper.findAll(Site.class);
            for (Site site : siteList) {
                NgxBlock siteBlock = new NgxBlock();
                siteBlock.addValue(site.getName());
                if (site.getIsGzip().equals("1") || site.getIsZstd().equals("1")) {
                    String encode = "encode ";
                    if(site.getIsGzip().equals("1")) encode+="gzip ";
                    if(site.getIsZstd().equals("1")) encode+="zstd ";
                    NgxParam encodeParam = new NgxParam();
                    encodeParam.addValue(encode);
                    siteBlock.addEntry(encodeParam);
                }
                List<To> toList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("siteId", site.getId()), To.class);
                for (To to : toList) {
                    NgxBlock reverse = new NgxBlock();
                    reverse.addValue("reverse_proxy " + to.getLocation() + " " + to.getProxyAddress());
                    if (to.getLbPolicy() != null) {
                        NgxParam lbProxyParam = new NgxParam();
                        lbProxyParam.addValue("lb_policy " + to.getLbPolicy());
                        reverse.addEntry(lbProxyParam);
                    }
                    if (to.getLbTryDuration() != null) {
                        NgxParam lbTryDurationParam = new NgxParam();
                        lbTryDurationParam.addValue("lb_try_duration " + to.getLbTryDuration());
                        reverse.addEntry(lbTryDurationParam);
                    }
                    List<Param> paramList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("toId", to.getId()), Param.class);
                    for (Param param : paramList) {
                        NgxParam ngxParam = new NgxParam();
                        ngxParam.addValue(param.getName() + " " + param.getValue());
                        reverse.addEntry(ngxParam);
                    }
                    siteBlock.addEntry(reverse);
                }
                List<Param> paramList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("siteId", site.getId()), Param.class);
                for (Param param : paramList) {
                    NgxParam ngxParam = new NgxParam();
                    ngxParam.addValue(param.getName() + " " + param.getValue());
                    siteBlock.addEntry(ngxParam);
                }
                ngxConfig.addEntry(siteBlock);
            }

            String conf = new NgxDumper(ngxConfig).dump().replace(";", "");
            confExt.setConf(conf);

            return confExt;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public NgxBlock buildBlockUpstream(Upstream upstream) {
        NgxParam ngxParam = null;

        NgxBlock ngxBlockServer = new NgxBlock();

        ngxBlockServer.addValue("upstream " + upstream.getName());

        if (StrUtil.isNotEmpty(upstream.getTactics())) {
            ngxParam = new NgxParam();
            ngxParam.addValue(upstream.getTactics());
            ngxBlockServer.addEntry(ngxParam);
        }

        List<UpstreamServer> upstreamServers = upstreamService.getUpstreamServers(upstream.getId());
        for (UpstreamServer upstreamServer : upstreamServers) {
            ngxParam = new NgxParam();
            ngxParam.addValue("server " + buildNodeStr(upstreamServer));
            ngxBlockServer.addEntry(ngxParam);
        }

        // 自定义参数
        List<Param> paramList = paramService.getListByTypeId(upstream.getId(), "upstream");
        for (Param param : paramList) {
            setSameParam(param, ngxBlockServer);
        }

        return ngxBlockServer;
    }

    public NgxBlock buildBlockSite(Site site) {
        NgxBlock siteBlock = new NgxBlock();
        siteBlock.addValue(site.getName());
        if (site.getIsGzip().equals("1") || site.getIsZstd().equals("1")) {
            String encode = "encode ";
            if(site.getIsGzip().equals("1")) encode+="gzip ";
            if(site.getIsZstd().equals("1")) encode+="zstd ";
            NgxParam encodeParam = new NgxParam();
            encodeParam.addValue(encode);
            siteBlock.addEntry(encodeParam);
        }
        List<To> toList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("siteId", site.getId()), To.class);
        for (To to : toList) {
            NgxBlock reverse = new NgxBlock();
            reverse.addValue("reverse_proxy " + to.getLocation() + " " + to.getProxyAddress());
            if (to.getLbPolicy() != null) {
                NgxParam lbProxyParam = new NgxParam();
                lbProxyParam.addValue("lb_policy " + to.getLbPolicy());
                reverse.addEntry(lbProxyParam);
            }
            if (to.getLbTryDuration() != null) {
                NgxParam lbTryDurationParam = new NgxParam();
                lbTryDurationParam.addValue("lb_try_duration " + to.getLbTryDuration());
                reverse.addEntry(lbTryDurationParam);
            }
            List<Param> paramList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("toId", to.getId()), Param.class);
            for (Param param : paramList) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(param.getName() + " " + param.getValue());
                reverse.addEntry(ngxParam);
            }
            siteBlock.addEntry(reverse);
        }
        List<Param> paramList = sqlHelper.findListByQuery(new ConditionAndWrapper().eq("siteId", site.getId()), Param.class);
        for (Param param : paramList) {
            NgxParam ngxParam = new NgxParam();
            ngxParam.addValue(param.getName() + " " + param.getValue());
            siteBlock.addEntry(ngxParam);
        }


        return siteBlock;
    }

    /**
     * include防止重复
     *
     * @param ngxBlockHttp
     * @param ngxParam
     * @return
     */
    private boolean noContain(NgxBlock ngxBlockHttp, NgxParam ngxParam) {
        for (NgxEntry ngxEntry : ngxBlockHttp.getEntries()) {
            if (ngxEntry.toString().equals(ngxParam.toString())) {
                return false;
            }
        }

        return true;
    }

    public String buildNodeStr(UpstreamServer upstreamServer) {
        String status = "";
        if (!"none".equals(upstreamServer.getStatus())) {
            status = upstreamServer.getStatus();
        }

        if (upstreamServer.getServer().contains(":")) {
            upstreamServer.setServer("[" + upstreamServer.getServer() + "]");
        }

        return upstreamServer.getServer() + ":" + upstreamServer.getPort() //
                + " weight=" + upstreamServer.getWeight() //
                + " fail_timeout=" + upstreamServer.getFailTimeout() + "s"//
                + " max_fails=" + upstreamServer.getMaxFails() //
                + " " + status;

    }

    private void setSameParam(Param param, NgxBlock ngxBlock) {
        if (StrUtil.isEmpty(param.getTemplateValue())) {
            NgxParam ngxParam = new NgxParam();
            ngxParam.addValue(param.getName().trim() + " " + param.getValue().trim());
            ngxBlock.addEntry(ngxParam);
        } else {
            List<Param> params = templateService.getParamList(param.getTemplateValue());
            for (Param paramSub : params) {
                NgxParam ngxParam = new NgxParam();
                ngxParam.addValue(paramSub.getName().trim() + " " + paramSub.getValue().trim());
                ngxBlock.addEntry(ngxParam);
            }
        }
    }

    private void addConfFile(ConfExt confExt, String name, NgxBlock ngxBlockServer) {
        boolean hasSameName = false;
        for (ConfFile confFile : confExt.getFileList()) {
            if (confFile.getName().equals(name)) {
                confFile.setConf(confFile.getConf() + "\n" + buildStr(ngxBlockServer));
                hasSameName = true;
            }
        }

        if (!hasSameName) {
            ConfFile confFile = new ConfFile();
            confFile.setName(name);
            confFile.setConf(buildStr(ngxBlockServer));
            confExt.getFileList().add(confFile);
        }
    }

    private String buildStr(NgxBlock ngxBlockServer) {

        NgxConfig ngxConfig = new NgxConfig();
        ngxConfig.addEntry(ngxBlockServer);

        return new NgxDumper(ngxConfig).dump().replace("};", "  }");
    }

    public void replace(String caddyPath, String caddyContent, List<String> subContent, List<String> subName) {
        String date = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
        // 备份主文件
        if (FileUtil.exist(caddyPath)) {
            FileUtil.mkdir(InitConfig.home + "bak");
            FileUtil.copy(caddyPath, InitConfig.home + "bak/caddyfile." + date + ".bak", true);
        }

        // 备份conf.d文件夹
        String confd = caddyPath.replace("caddyfile.txt", "conf.d/");
        if (!FileUtil.exist(confd)) {
            FileUtil.mkdir(confd);
        } else {
            ZipUtil.zip(confd, InitConfig.home + "bak/nginx.conf." + date + ".zip");
        }

        // 删除conf.d下全部文件
        FileUtil.del(confd);
        FileUtil.mkdir(confd);

        // 写入主文件
        FileUtil.writeString(caddyContent, caddyPath.replace(" ", "_"), StandardCharsets.UTF_8);
        String decompose = settingService.get("decompose");

        if ("true".equals(decompose)) {
            // 写入conf.d文件
            if (subContent != null) {
                for (int i = 0; i < subContent.size(); i++) {
                    String tagert = caddyPath.replace("nginx.conf", "conf.d/" + subName.get(i)).replace(" ", "_");
                    FileUtil.writeString(subContent.get(i), tagert, StandardCharsets.UTF_8); // 清空
                }
            }
        }

    }

    public AsycPack getAsycPack() {
        AsycPack asycPack = new AsycPack();
        asycPack.setBasicList(sqlHelper.findAll(Basic.class));

        asycPack.setHttpList(sqlHelper.findAll(Http.class));
        List<Server> serverList = sqlHelper.findAll(Server.class);
        for (Server server : serverList) {
            if (StrUtil.isNotEmpty(server.getPem()) && FileUtil.exist(server.getPem())) {
                server.setPemStr(FileUtil.readString(server.getPem(), StandardCharsets.UTF_8));
            }

            if (StrUtil.isNotEmpty(server.getKey()) && FileUtil.exist(server.getKey())) {
                server.setKeyStr(FileUtil.readString(server.getKey(), StandardCharsets.UTF_8));
            }
        }
        asycPack.setServerList(serverList);

        List<Password> passwordList = sqlHelper.findAll(Password.class);
        for (Password password : passwordList) {
            if (StrUtil.isNotEmpty(password.getPath()) && FileUtil.exist(password.getPath())) {
                password.setPathStr(FileUtil.readString(password.getPath(), StandardCharsets.UTF_8));
            }

        }
        asycPack.setPasswordList(passwordList);

        asycPack.setLocationList(sqlHelper.findAll(Location.class));
        asycPack.setUpstreamList(sqlHelper.findAll(Upstream.class));
        asycPack.setUpstreamServerList(sqlHelper.findAll(UpstreamServer.class));
        asycPack.setStreamList(sqlHelper.findAll(Stream.class));

        asycPack.setParamList(sqlHelper.findAll(Param.class));

        String nginxPath = settingService.get("nginxPath");
        String decompose = settingService.get("decompose");

        ConfExt confExt = buildConf(StrUtil.isNotEmpty(decompose) && decompose.equals("true"), false);

        if (FileUtil.exist(nginxPath)) {
            String orgStr = FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
            confExt.setConf(orgStr);

            for (ConfFile confFile : confExt.getFileList()) {
                confFile.setConf("");

                String filePath = nginxPath.replace("nginx.conf", "conf.d/" + confFile.getName());
                if (FileUtil.exist(filePath)) {
                    confFile.setConf(FileUtil.readString(filePath, StandardCharsets.UTF_8));
                }
            }
        }

        asycPack.setDecompose(decompose);
        asycPack.setConfExt(confExt);
        return asycPack;
    }

    @Transactional
    public void setAsycPack(AsycPack asycPack) {
        // 不要同步Cert表
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Password.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Basic.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Http.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Server.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Location.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Upstream.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), UpstreamServer.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Stream.class);
        sqlHelper.deleteByQuery(new ConditionAndWrapper(), Param.class);

        sqlHelper.insertAll(asycPack.getBasicList());
        sqlHelper.insertAll(asycPack.getHttpList());
        sqlHelper.insertAll(asycPack.getServerList());
        sqlHelper.insertAll(asycPack.getLocationList());
        sqlHelper.insertAll(asycPack.getUpstreamList());
        sqlHelper.insertAll(asycPack.getUpstreamServerList());
        sqlHelper.insertAll(asycPack.getStreamList());
        sqlHelper.insertAll(asycPack.getParamList());
        sqlHelper.insertAll(asycPack.getPasswordList());

        for (Server server : asycPack.getServerList()) {
            if (StrUtil.isNotEmpty(server.getPem()) && StrUtil.isNotEmpty(server.getPemStr())) {
                FileUtil.writeString(server.getPemStr(), server.getPem(), StandardCharsets.UTF_8);
            }
            if (StrUtil.isNotEmpty(server.getKey()) && StrUtil.isNotEmpty(server.getKeyStr())) {
                FileUtil.writeString(server.getKeyStr(), server.getKey(), StandardCharsets.UTF_8);
            }
        }

        for (Password password : asycPack.getPasswordList()) {
            if (StrUtil.isNotEmpty(password.getPath()) && StrUtil.isNotEmpty(password.getPathStr())) {
                FileUtil.writeString(password.getPathStr(), password.getPath(), StandardCharsets.UTF_8);
            }
        }

        settingService.set("decompose", asycPack.getDecompose());

        ConfExt confExt = asycPack.getConfExt();

        String nginxPath = settingService.get("nginxPath");

        if (FileUtil.exist(nginxPath)) {

            List<String> subContent = new ArrayList<>();
            List<String> subName = new ArrayList<>();

            for (ConfFile confFile : confExt.getFileList()) {
                subContent.add(confFile.getConf());
                subName.add(confFile.getName());
            }

            replace(nginxPath, confExt.getConf(), subContent, subName);
        }
    }

}
