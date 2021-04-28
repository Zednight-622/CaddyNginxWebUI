package com.zednight.utils;

import cn.hutool.core.util.RuntimeUtil;

public class CaddyUtils {
    public static boolean isRun() {
        boolean isRun = false;
        if (SystemTool.isWindows()) {
            String[] command = { "tasklist" };
            String rs = RuntimeUtil.execForStr(command);
            isRun = rs.toLowerCase().contains("caddy");
        } else {
            String[] command = { "/bin/sh", "-c", "ps -ef|grep caddy" };
            String rs = RuntimeUtil.execForStr(command);
            isRun = rs.contains("caddy run");
        }
        return isRun;
    }
}
