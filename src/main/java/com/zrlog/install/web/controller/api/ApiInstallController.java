package com.zrlog.install.web.controller.api;

import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.web.Controller;
import com.zrlog.install.business.response.InstallResourceResponse;
import com.zrlog.install.business.response.InstallResultResponse;
import com.zrlog.install.business.response.TestConnectResponse;
import com.zrlog.install.business.service.InstallResourceService;
import com.zrlog.install.business.service.InstallService;
import com.zrlog.install.business.type.TestConnectDbResult;
import com.zrlog.install.exception.*;
import com.zrlog.install.util.StringUtils;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.InstallConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class ApiInstallController extends Controller {

    private final InstallConfig installConfig;

    public ApiInstallController() {
        this.installConfig = InstallConstants.installConfig;
    }

    /**
     * 检查数据库是否可以正常连接使用，无法连接时给出相应的提示
     */
    @ResponseBody
    public TestConnectResponse testDbConn() {
        TestConnectDbResult testConnectDbResult = new InstallService(installConfig, getDbConn()).testDbConn();
        if (testConnectDbResult.getError() != 0) {
            throw new InstallException(testConnectDbResult);
        }
        return new TestConnectResponse();
    }

    private Map<String, String> getDbConn() {
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbHost"))) {
            throw new MissingDbHostException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbPort"))) {
            throw new MissingDbPortException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbUserName"))) {
            throw new MissingDbUserNameException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbName"))) {
            throw new MissingDbNameException();
        }
        Map<String, String> dbConn = new HashMap<>();
        dbConn.put("user", getRequest().getParaToStr("dbUserName", ""));
        dbConn.put("password", getRequest().getParaToStr("dbPassword", ""));
        String dbType = getRequest().getParaToStr("dbType", "mysql");
        String jdbcUrl = "jdbc:" + dbType + "://" + getRequest().getParaToStr("dbHost") + ":" + getRequest().getParaToStr("dbPort") + "/" + getRequest().getParaToStr("dbName");
        if (Objects.equals(dbType, "mysql")) {
            jdbcUrl += "?" + InstallConstants.installConfig.getJdbcUrlQueryStr();
            dbConn.put("driverClass", "com.mysql.cj.jdbc.Driver");
        }
        dbConn.put("jdbcUrl", jdbcUrl);
        return dbConn;
    }

    /**
     * 数据库检查通过后，根据填写信息，执行数据表，表数据的初始化
     */
    @ResponseBody
    public InstallResultResponse startInstall() {
        Map<String, String> configMsg = new HashMap<>();
        configMsg.put("title", getRequest().getParaToStr("title", ""));
        configMsg.put("second_title", getRequest().getParaToStr("second_title", ""));
        configMsg.put("username", getRequest().getParaToStr("username", ""));
        configMsg.put("password", getRequest().getParaToStr("password", ""));
        configMsg.put("email", getRequest().getParaToStr("email", ""));
        if (!new InstallService(installConfig, getDbConn(), configMsg).install()) {
            throw new InstallException(TestConnectDbResult.UNKNOWN);
        }
        return new InstallResultResponse();
    }

    @ResponseBody
    public InstallResourceResponse installResource() {
        return new InstallResourceResponse(new InstallResourceService().installResourceInfo(getRequest()));
    }
}
