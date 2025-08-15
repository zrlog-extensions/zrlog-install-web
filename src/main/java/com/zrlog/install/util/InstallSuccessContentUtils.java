package com.zrlog.install.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.template.BasicTemplateRender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class InstallSuccessContentUtils {

    private static Map<String, Object> getInstallInfo(File dbProperties, ServerConfig serverConfig) throws IOException {
        Map<String, Object> data = new HashMap<>();
        Properties dataSourceProperties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(dbProperties)) {
            dataSourceProperties.load(fileInputStream);
            //db
            data.put("dbUserName", dataSourceProperties.getProperty("user"));
            data.put("dbPassword", dataSourceProperties.getProperty("password"));
            String jdbcUrl = dataSourceProperties.getProperty("jdbcUrl");
            URI uri = URI.create(jdbcUrl.replaceFirst("jdbc:", ""));
            data.put("dbHost", uri.getHost());
            data.put("dbPort", uri.getPort() + "");
            data.put("dbName", uri.getPath().substring(1));
            data.put("dbType", uri.getScheme());
            data.put("dbProperties", Arrays.stream(IOUtil.getStringInputStream(new FileInputStream(dbProperties)).split("\n")).filter(e -> !e.startsWith("#")).collect(Collectors.joining("<br/>")));
            //app
            data.put("appName", serverConfig.getApplicationName());
            data.put("serverPort", serverConfig.getPort());
            data.put("contextPath", serverConfig.getContextPath());
            return data;
        }
    }

    private static String getMdFilePath() {
        if (EnvKit.isFaaSMode()) {
            return "/i18n/installed/faas_zh_CN.md";
        }
        return "/i18n/installed/docker_zh_CN.md";
    }

    public static String getContent(File dbProperties, boolean containerMode, ServerConfig serverConfig) {
        if (containerMode) {
            BasicTemplateRender basicTemplateRender;
            try {
                basicTemplateRender = new BasicTemplateRender(getInstallInfo(dbProperties, serverConfig), InstallSuccessContentUtils.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return basicTemplateRender.render(InstallSuccessContentUtils.class.getResourceAsStream(getMdFilePath()));
        }
        return "";
    }
}
