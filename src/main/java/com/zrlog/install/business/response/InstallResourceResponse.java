package com.zrlog.install.business.response;

import java.util.Map;

public class InstallResourceResponse extends InstallApiStandardResponse {

    private Map<String, Object> data;

    public InstallResourceResponse() {
    }

    public InstallResourceResponse(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
