package com.zrlog.install.exception;


public class MissingDbHostException extends AbstractInstallException {
    @Override
    public int getError() {
        return 9021;
    }

    @Override
    public String getMessage() {
        return "数据库地址不能为空";
    }
}
