package com.zednight.config;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxParam;

public class CaddyConf {
    public static void main(String[] args) {
        NgxConfig config = new NgxConfig();
        NgxBlock site = new NgxBlock();
        site.addValue("localhost");
        NgxParam ngxParam = new NgxParam();
        ngxParam.addValue("response \"Hello World!\"");
        site.addEntry(ngxParam);
        config.addEntry(site);
        System.out.println(new NgxDumper(config).dump().replace(";",""));
    }
}
