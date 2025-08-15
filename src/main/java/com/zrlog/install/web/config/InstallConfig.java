package com.zrlog.install.web.config;

import com.hibegin.http.server.api.HttpErrorHandle;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.web.InstallAction;

import java.io.File;

public interface InstallConfig {

    InstallAction getAction();

    boolean isWarMode();

    String getAcceptLanguage();

    String encryptPassword(String password);

    String defaultTemplatePath();

    String getZrLogSqlVersion();

    File getDbPropertiesFile();

    LastVersionInfo getLastVersionInfo();

    String getBuildVersion();

    String getJdbcUrlQueryStr(String dbType);

    HttpErrorHandle getErrorHandler();
}
