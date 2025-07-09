package com.zrlog.install.exception;

import com.zrlog.install.util.InstallI18nUtil;
import com.zrlog.install.web.InstallConstants;

public class InstalledException extends AbstractInstallException {
    @Override
    public int getError() {
        return 9020;
    }

    @Override
    public String getMessage() {
        return InstallI18nUtil.getInstallStringFromRes(InstallConstants.installConfig.isWarMode() ? "installedWarTips" : "installedTips");
    }
}
