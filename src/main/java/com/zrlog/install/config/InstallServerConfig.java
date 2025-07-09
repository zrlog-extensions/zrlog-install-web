package com.zrlog.install.config;

import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.install.web.config.InstallRouters;
import com.zrlog.install.web.interceptor.BlogInstallInterceptor;

public class InstallServerConfig extends AbstractServerConfig {

    private final ServerConfig serverConfig = new ServerConfig();

    public InstallServerConfig() {
        serverConfig.addInterceptor(BlogInstallInterceptor.class);
        serverConfig.addInterceptor(MethodInterceptor.class);
        InstallRouters.configRouter(serverConfig);
    }

    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return new RequestConfig();
    }

    @Override
    public ResponseConfig getResponseConfig() {
        return new ResponseConfig();
    }
}
