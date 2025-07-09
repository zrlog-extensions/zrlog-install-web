package com.zrlog.install.web.config;

import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.web.Router;
import com.zrlog.install.web.controller.api.ApiInstallController;
import com.zrlog.install.web.controller.page.InstallController;
import com.zrlog.install.web.controller.page.MigrateController;

public class InstallRouters {

    public static void configRouter(ServerConfig serverConfig) {
        Router router = serverConfig.getRouter();
        router.addMapper("/migrate", MigrateController.class);
        router.addMapper("/install", InstallController.class);
        router.addMapper("/api/install"  , ApiInstallController.class);
        serverConfig.addStaticResourceMapper("/install/static", "/install/static", InstallRouters.class::getResourceAsStream);
    }
}
