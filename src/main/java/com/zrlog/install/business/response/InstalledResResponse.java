package com.zrlog.install.business.response;

public class InstalledResResponse {

    private Boolean installed;
    private String installedTitle;
    private String installedTips;
    private String missingConfigTips;
    private String lang;
    private Boolean askConfig;
    private String askConfigTips;
    private String installSuccessContent;

    public Boolean getInstalled() {
        return installed;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
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

    public String getMissingConfigTips() {
        return missingConfigTips;
    }

    public void setMissingConfigTips(String missingConfigTips) {
        this.missingConfigTips = missingConfigTips;
    }

    public Boolean getAskConfig() {
        return askConfig;
    }

    public void setAskConfig(Boolean askConfig) {
        this.askConfig = askConfig;
    }

    public String getInstallSuccessContent() {
        return installSuccessContent;
    }

    public void setInstallSuccessContent(String installSuccessContent) {
        this.installSuccessContent = installSuccessContent;
    }

    public String getAskConfigTips() {
        return askConfigTips;
    }

    public void setAskConfigTips(String askConfigTips) {
        this.askConfigTips = askConfigTips;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
