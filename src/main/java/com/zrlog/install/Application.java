package com.zrlog.install;

import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.config.InstallServerConfig;

public class Application {

    public static void main(String[] args) {
        PathUtil.setRootPath(System.getProperty("user.dir"));
        WebServerBuilder builder = new WebServerBuilder.Builder().config(new InstallServerConfig()).build();
        builder.start();
    }
}
