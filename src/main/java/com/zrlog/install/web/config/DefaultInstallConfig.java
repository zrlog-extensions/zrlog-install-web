package com.zrlog.install.web.config;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.web.InstallAction;

import java.io.File;

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
        return "1";
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
    public String getContextPath() {
        return "/";
    }
}
