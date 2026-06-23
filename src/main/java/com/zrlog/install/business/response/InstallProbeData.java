package com.zrlog.install.business.response;

import java.util.ArrayList;
import java.util.List;

public class InstallProbeData {

    private String status;
    private String runtimeMode;
    private String charset;
    private String dbPropertiesPath;
    private String lockFilePath;
    private List<InstallProbeItem> items = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDbPropertiesPath() {
        return dbPropertiesPath;
    }

    public void setDbPropertiesPath(String dbPropertiesPath) {
        this.dbPropertiesPath = dbPropertiesPath;
    }

    public String getLockFilePath() {
        return lockFilePath;
    }

    public void setLockFilePath(String lockFilePath) {
        this.lockFilePath = lockFilePath;
    }

    public List<InstallProbeItem> getItems() {
        return items;
    }

    public void setItems(List<InstallProbeItem> items) {
        this.items = items;
    }
}
