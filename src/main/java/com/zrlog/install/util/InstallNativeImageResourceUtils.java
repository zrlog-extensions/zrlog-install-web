package com.zrlog.install.util;

import com.google.gson.Gson;
import com.hibegin.http.server.util.NativeImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InstallNativeImageResourceUtils {

    public static List<String> getResourceNameList() {
        List<String> resourceNameList = new ArrayList<>();
        resourceNameList.add("/i18n/disclaimer-agreement/en_US.md");
        resourceNameList.add("/i18n/disclaimer-agreement/zh_CN.md");
        resourceNameList.add("/i18n/init-blog/en_US.md");
        resourceNameList.add("/i18n/init-blog/zh_CN.md");
        resourceNameList.add("/i18n/install_en_US.properties");
        resourceNameList.add("/i18n/install_zh_CN.properties");
        try (InputStream assetJson = InstallNativeImageResourceUtils.class.getResourceAsStream("/install/asset-manifest.json")) {
            Map<String, Object> assetMap = new Gson().fromJson(new String(assetJson.readAllBytes()), Map.class);
            if (assetMap != null) {
                resourceNameList.addAll(((Map<String, String>) assetMap.get("files")).values().stream().map(e -> {
                    return e.replaceFirst("\\./", "/");
                }).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resourceNameList.add("/init-table-structure.sql");
        NativeImageUtils.doResourceLoadByResourceNames(resourceNameList);
        return resourceNameList;
    }

    public static void main(String[] args) {
        List<String> resourceNameList = getResourceNameList();
        System.out.println("resourceNameList = " + resourceNameList);
    }
}
