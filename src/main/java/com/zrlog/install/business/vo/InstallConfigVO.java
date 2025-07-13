package com.zrlog.install.business.vo;

import java.util.Map;

public class InstallConfigVO {

    private Map<String,String> dbConfig;
    private Map<String,String> configMsg;

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
}
