package com.zrlog.install.exception;


public class MissingDbNameException extends AbstractInstallException {
    @Override
    public int getError() {
        return 9023;
    }

    @Override
    public String getMessage() {
        return "数据库名不能为空";
    }
}
