package com.zrlog.install.exception;

public class MissingDbUserNameException extends AbstractInstallException {
    @Override
    public int getError() {
        return 9023;
    }

    @Override
    public String getMessage() {
        return "数据库用户名不能为空";
    }
}
