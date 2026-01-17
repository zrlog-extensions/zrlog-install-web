package com.zrlog.install.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.ServerConfig;
import com.zrlog.install.business.response.InstalledResResponse;
import com.zrlog.install.business.response.LastVersionInfo;
import com.zrlog.install.util.InstallI18nUtil;
import com.zrlog.install.util.InstallSuccessContentUtils;
import com.zrlog.install.web.InstallConstants;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InstallResourceService {

    private InstalledResResponse getInstalledResResponse(Map<String, Object> installMap, ServerConfig serverConfig) {
        InstalledResResponse installedResResponse = new InstalledResResponse();
        installedResResponse.setInstalled(true);
        installedResResponse.setLang((String) installMap.get("lang"));
        if (InstallConstants.installConfig.isContainerMode() && InstallConstants.installConfig.isMissingConfig()) {
            installedResResponse.setAskConfig(true);
            installedResResponse.setInstallSuccessContent(InstallSuccessContentUtils.getContent(InstallConstants.installConfig.getDbPropertiesFile(), true, serverConfig));
            installedResResponse.setMissingConfigTips((String) installMap.get("missingConfigTips"));
            installedResResponse.setAskConfigTips((String) installMap.get("askConfigTips"));
        } else {
            installedResResponse.setAskConfig(false);
        }
        installedResResponse.setInstalledTitle((String) installMap.get("installedTitle"));
        if (InstallConstants.installConfig.isWarMode()) {
            installedResResponse.setInstalledTips((String) installMap.get("installedWarTips"));
        } else {
            installedResResponse.setInstalledTips((String) installMap.get("installedTips"));
        }
        return installedResResponse;
    }

    private Map<String, Object> fillFeedbackInfo(Map<String, Object> installMap, ServerConfig serverConfig) {
        Object feedbackRes = installMap.get("installFeedback");
        if (Objects.isNull(feedbackRes)) {
            return installMap;
        }
        String feedbackUrl = "https://blog.zrlog.com/feedback.html?v=" + Objects.requireNonNullElse(serverConfig.getApplicationVersion(), "3")
                + "&os=" + System.getProperty("os.name");
        feedbackRes = feedbackRes.toString()
                .replace("${feedbackInfo}", "<a target=\"_blank\" href=\"" + feedbackUrl + "\">https://blog.zrlog.com/feedback.html</a>");
        installMap.put("installFeedback", feedbackRes);
        return installMap;
    }

    public Object installResourceInfo(HttpRequest request) {
        String lang = InstallConstants.installConfig.getAcceptLanguage();
        Map<String, Object> installMap = new TreeMap<>(InstallI18nUtil.getInstallMap());
        installMap.put("lang", lang);
        if (InstallConstants.installConfig.getAction().isInstalled()) {
            return getInstalledResResponse(installMap, request.getServerConfig());
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
        installMap.put("askConfig", InstallConstants.installConfig.isContainerMode());
        try (InputStream inputStream = InstallResourceService.class.getResourceAsStream("/i18n/disclaimer-agreement/" + lang + ".md")) {
            if (Objects.nonNull(inputStream)) {
                installMap.put("disclaimerAgreement", new String(inputStream.readAllBytes()));
            } else {
                installMap.put("disclaimerAgreement", "");
            }
            LastVersionInfo lastVersionInfo = InstallConstants.installConfig.getLastVersionInfo();
            if (Objects.nonNull(lastVersionInfo) && Objects.equals(lastVersionInfo.getLatestVersion(), false)) {
                installMap.put("upgradeTips", installMap.get("newVersion") + " v" + lastVersionInfo.getNewVersion());
                installMap.put("upgradeChangeLog", lastVersionInfo.getChangeLog());
                installMap.put("upgradeDownloadUrl", lastVersionInfo.getDownloadUrl());
            } else {
                installMap.put("upgradeTips", "");
            }
        } catch (Exception e) {
            //ignore
        }
        return fillFeedbackInfo(installMap, request.getServerConfig());
    }
}
