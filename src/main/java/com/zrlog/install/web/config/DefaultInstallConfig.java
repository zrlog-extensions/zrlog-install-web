package com.zrlog.install.web.config;

import com.hibegin.http.server.api.HttpErrorHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.exception.AbstractInstallException;
import com.zrlog.install.web.InstallAction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DefaultInstallConfig implements InstallConfig {
    @Override
    public InstallAction getAction() {
        return new DefaultInstallAction();
    }

    @Override
    public boolean isWarMode() {
        return false;
    }

    @Override
    public String getAcceptLanguage() {
        return "zh_CN";
    }

    @Override
    public String encryptPassword(String password) {
        return password;
    }

    @Override
    public String defaultTemplatePath() {
        return "/include/templates/default";
    }

    @Override
    public String getZrLogSqlVersion() {
        return "19";
    }

    @Override
    public File getDbPropertiesFile() {
        return PathUtil.getConfFile("db.properties");
    }

    @Override
    public LastVersionInfo getLastVersionInfo() {
        return new LastVersionInfo();
    }

    @Override
    public String getBuildVersion() {
        return "1.0.0-SNAPSHOT";
    }

    @Override
    public String getJdbcUrlQueryStr() {
        return "characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=GMT";
    }

    @Override
    public HttpErrorHandle getErrorHandler() {
        return (request, response, e) -> {
            if (e instanceof AbstractInstallException) {
                AbstractInstallException ee = (AbstractInstallException) e;
                Map<String, Object> error = new HashMap<>();
                error.put("error", ee.getError());
                error.put("message", ee.getMessage());
                response.renderJson(error);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", 9999);
                error.put("message", e.getMessage());
                response.renderJson(error);
                response.renderJson(error);
            }
        };
    }
}
