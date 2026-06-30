package com.zrlog.install.util;

import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.DefaultInstallConfig;
import com.zrlog.install.web.config.InstallConfig;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstallI18nUtilTest {

    @After
    public void tearDown() {
        InstallConstants.installConfig = new DefaultInstallConfig();
    }

    @Test
    public void shouldReturnSortedLanguageResourceMap() {
        InstallConstants.installConfig = installConfig("zh_CN");

        Map<String, Object> installMap = InstallI18nUtil.getInstallMap();

        assertTrue(installMap.containsKey("helloWorld"));
        assertEquals(installMap, InstallI18nUtil.getInstallMap());
        assertEquals(InstallI18nUtil.getInstallStringFromRes("helloWorld"), installMap.get("helloWorld"));
    }

    @Test
    public void shouldReturnEmptyValuesForUnknownLanguageOrKey() {
        InstallConstants.installConfig = installConfig("missing");

        assertTrue(InstallI18nUtil.getInstallMap().isEmpty());
        assertEquals("", InstallI18nUtil.getInstallStringFromRes("helloWorld"));
    }

    private static InstallConfig installConfig(String acceptLanguage) {
        return (InstallConfig) Proxy.newProxyInstance(
                InstallI18nUtilTest.class.getClassLoader(),
                new Class[]{InstallConfig.class},
                (proxy, method, args) -> {
                    if ("getAcceptLanguage".equals(method.getName())) {
                        return acceptLanguage;
                    }
                    if ("isWarMode".equals(method.getName())) {
                        return false;
                    }
                    if ("toString".equals(method.getName())) {
                        return "InstallConfigProxy";
                    }
                    return null;
                });
    }
}
