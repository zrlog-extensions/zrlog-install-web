package com.zrlog.install.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.util.InstallI18nUtil;
import com.zrlog.install.util.MarkdownUtil;
import com.zrlog.install.web.InstallConstants;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InstallResourceService {

    public Map<String, Object> installResourceInfo(HttpRequest request) {
        String lang = InstallConstants.installConfig.getAcceptLanguage();
        Map<String, Object> installMap = new TreeMap<>(InstallI18nUtil.getInstallMap());
        if (InstallConstants.installConfig.getAction().isInstalled()) {
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("installed", true);
            stringObjectMap.put("installedTitle", installMap.get("installedTitle"));
            if (InstallConstants.installConfig.isWarMode()) {
                stringObjectMap.put("installedTips", installMap.get("installedWarTips"));
            } else {
                stringObjectMap.put("installedTips", installMap.get("installedTips"));
            }
            return stringObjectMap;
        }
        installMap.put("currentVersion", InstallConstants.installConfig.getBuildVersion());
        //encoding ok, remove utfTips
        if (Charset.defaultCharset().displayName().toLowerCase().contains("utf")) {
            installMap.put("utfTips", "");
        }
        installMap.put("installed", false);
        //这个是不需要的
        installMap.remove("installedWarTips");
        installMap.remove("installedTips");
        try (InputStream inputStream = InstallResourceService.class.getResourceAsStream("/i18n/disclaimer-agreement/" + lang + ".md")) {
            if (Objects.nonNull(inputStream)) {
                installMap.put("disclaimerAgreement", MarkdownUtil.renderMd(new String(inputStream.readAllBytes())));
            } else {
                installMap.put("disclaimerAgreement", "");
            }
            LastVersionInfo lastVersionInfo = InstallConstants.installConfig.getLastVersionInfo();
            if (Objects.nonNull(lastVersionInfo) && Objects.equals(lastVersionInfo.getLatestVersion(), false)) {
                installMap.put("upgradeTips", MarkdownUtil.renderMd(installMap.get("newVersion") + " v" + lastVersionInfo.getNewVersion()));
                installMap.put("upgradeChangeLog", lastVersionInfo.getChangeLog());
                installMap.put("upgradeDownloadUrl", lastVersionInfo.getDownloadUrl());
            } else {
                installMap.put("upgradeTips", "");
            }
        } catch (Exception e) {
            //ignore
        }
        return installMap;
    }
}
