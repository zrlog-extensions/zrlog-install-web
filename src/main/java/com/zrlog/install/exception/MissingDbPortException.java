package com.zrlog.install.exception;

public class MissingDbPortException extends AbstractInstallException {
    @Override
    public int getError() {
        return 9022;
    }

    @Override
    public String getMessage() {
        return "数据库端口不能为空";
    }
}
