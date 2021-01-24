package com.zednight.config;

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
        ngxEntries.addEntry(ngxParam);
        ngxParam = new NgxParam();
        ngxParam.addValue("experimental_http3");
        NgxBlock onDemand = new NgxBlock();
        onDemand.addValue("on_demand_tls");
        NgxParam ngxParam1 = new NgxParam();
        ngxParam1.addValue("interval 5s");
        onDemand.addEntry(ngxParam1);
        NgxParam ngxParam2 = new NgxParam();
        ngxParam2.addValue("burst 2");
        onDemand.addEntry(ngxParam2);
        ngxEntries.addValue("");
        ngxEntries.addEntry(ngxParam);
        ngxEntries.addEntry(onDemand);
        config.addEntry(ngxEntries);
        System.out.println(new NgxDumper(config).dump().replace(";",""));
    }
}
