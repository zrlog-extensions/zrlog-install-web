package com.zrlog.install.business.response;

public class InstalledResResponse {

    private Boolean installed;
    private Boolean missingConfig;
    private String installedTitle;
    private String installedTips;
    private String missingConfigTips;
    private Boolean containerMode;

    public Boolean getInstalled() {
        return installed;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
    }

    public Boolean getMissingConfig() {
        return missingConfig;
    }

    public void setMissingConfig(Boolean missingConfig) {
        this.missingConfig = missingConfig;
    }

    public String getInstalledTitle() {
        return installedTitle;
    }

    public void setInstalledTitle(String installedTitle) {
        this.installedTitle = installedTitle;
    }

    public String getInstalledTips() {
        return installedTips;
    }

    public void setInstalledTips(String installedTips) {
        this.installedTips = installedTips;
    }

    public Boolean getContainerMode() {
        return containerMode;
    }

    public String getMissingConfigTips() {
        return missingConfigTips;
    }

    public void setMissingConfigTips(String missingConfigTips) {
        this.missingConfigTips = missingConfigTips;
    }

    public void setContainerMode(Boolean containerMode) {
        this.containerMode = containerMode;
    }
}
