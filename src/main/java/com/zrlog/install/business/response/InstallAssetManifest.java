package com.zrlog.install.business.response;

import java.util.Map;

public class InstallAssetManifest {

    private Map<String, String> files;

    public Map<String, String> getFiles() {
        return files;
    }

    public void setFiles(Map<String, String> files) {
        this.files = files;
    }
}
