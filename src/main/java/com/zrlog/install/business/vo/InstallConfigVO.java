package com.zrlog.install.business.vo;

import java.util.Map;

public class InstallConfigVO {

    private Map<String,String> dbConfig;
    private Map<String,String> configMsg;
    private Map<String,String> appendWebsite;
    private String contextPath;

    public Map<String, String> getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(Map<String, String> dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Map<String, String> getConfigMsg() {
        return configMsg;
    }

    public void setConfigMsg(Map<String, String> configMsg) {
        this.configMsg = configMsg;
    }

    public Map<String, String> getAppendWebsite() {
        return appendWebsite;
    }

    public void setAppendWebsite(Map<String, String> appendWebsite) {
        this.appendWebsite = appendWebsite;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
