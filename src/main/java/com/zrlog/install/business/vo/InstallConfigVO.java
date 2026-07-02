package com.zrlog.install.business.vo;

import java.util.Map;

public class InstallConfigVO {

    private InstallDatabaseConfig dbConfig;
    private InstallSiteConfig configMsg;
    private Map<String, String> appendWebsite;
    private String contextPath;

    public InstallDatabaseConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(InstallDatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void setDbConfig(Map<String, String> dbConfig) {
        this.dbConfig = InstallDatabaseConfig.from(dbConfig);
    }

    public InstallSiteConfig getConfigMsg() {
        return configMsg;
    }

    public void setConfigMsg(InstallSiteConfig configMsg) {
        this.configMsg = configMsg;
    }

    public void setConfigMsg(Map<String, String> configMsg) {
        this.configMsg = InstallSiteConfig.from(configMsg);
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
