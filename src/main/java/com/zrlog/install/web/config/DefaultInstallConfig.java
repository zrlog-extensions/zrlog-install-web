package com.zrlog.install.web.config;

import com.hibegin.http.server.api.HttpErrorHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.exception.AbstractInstallException;
import com.zrlog.install.web.InstallAction;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        return md5(password);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            // 将 byte[] 转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
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
        LastVersionInfo lastVersionInfo = new LastVersionInfo();
        lastVersionInfo.setLatestVersion(false);
        lastVersionInfo.setNewVersion("3.2.0");
        lastVersionInfo.setDownloadUrl("https://dl.zrlog.com/release/zrlog.zip");
        return lastVersionInfo;
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
