package com.cym.config;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxDumper;
import com.github.odiszapc.nginxparser.NgxParam;

public class CaddyConf {
    public static void main(String[] args) {
        NgxConfig config = new NgxConfig();
        NgxBlock ngxEntries = new NgxBlock();
        NgxParam ngxParam = new NgxParam();
        ngxParam.addValue("http3 on");
        ngxEntries.addValue("");
        ngxEntries.addEntry(ngxParam);
        config.addEntry(ngxEntries);
        System.out.println(new NgxDumper(config).dump().replace(";",""));
    }
}
