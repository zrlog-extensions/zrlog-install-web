package com.zrlog.install.web;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.business.vo.InstallSuccessData;

import java.io.File;

public interface InstallAction {

    void installSuccess();

    default File getLockFile() {
        return PathUtil.getConfFile("/install.lock");
    }

    default boolean isInstalled() {
        return getLockFile().exists();
    }
}
