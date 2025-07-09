package com.zrlog.install.exception;

import com.zrlog.install.business.type.TestConnectDbResult;
import com.zrlog.install.util.InstallI18nUtil;

public class InstallException extends AbstractInstallException {

    private final TestConnectDbResult result;

    public InstallException(TestConnectDbResult result) {
        this.result = result;
    }

    @Override
    public int getError() {
        return 9000;
    }

    @Override
    public String getMessage() {
        return "[Error-" + result + "] - " + InstallI18nUtil.getInstallStringFromRes("connectDbError_" + result.getError());
    }
}
