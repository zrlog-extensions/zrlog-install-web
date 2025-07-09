package com.zrlog.install.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.install.util.InstallI18nUtil;
import com.zrlog.install.util.MarkdownUtil;
import com.zrlog.install.web.InstallConstants;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstallResourceService {

    public Map<String, Object> installResourceInfo(HttpRequest request) {
        String lang = InstallConstants.installConfig.getAcceptLanguage();
        Map<String, Object> installMap = InstallI18nUtil.getInstallMap();
        if (InstallConstants.installConfig.getAction().isInstalled()) {
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("installed", true);
            stringObjectMap.put("installedTips", installMap.get("installedTips"));
            stringObjectMap.put("installedTitle", installMap.get("installedTitle"));
            if (InstallConstants.installConfig.isWarMode()) {
                installMap.put("installedTips", installMap.get("installedWarTips"));
            } else {
                installMap.put("installedTips", installMap.get("installedTips"));
            }
            return stringObjectMap;
        }
        //installMap.put("currentVersion", BlogBuildInfoUtil.getVersion());
        //encoding ok, remove utfTips
        if (Charset.defaultCharset().displayName().toLowerCase().contains("utf")) {
            installMap.put("utfTips", "");
        }
        installMap.put("installed", false);
        //这个是不需要的
        installMap.remove("installedWarTips");
        installMap.remove("installedTips");
        //UpdateVersionInfoPlugin updateVersionInfoPlugin = new UpdateVersionInfoPlugin();
        try (InputStream inputStream = InstallResourceService.class.getResourceAsStream("/i18n/disclaimer-agreement/" + lang + ".md")) {
            if (Objects.nonNull(inputStream)) {
                installMap.put("disclaimerAgreement", MarkdownUtil.renderMd(new String(inputStream.readAllBytes())));
            } else {
                installMap.put("disclaimerAgreement", "");
            }
            installMap.put("lastVersionInfo", InstallConstants.installConfig.getLastVersionInfo());
        } catch (Exception e) {
            //ignore
        } finally {
            //updateVersionInfoPlugin.stop();
        }
        return installMap;
    }
}
