package com.zrlog.install;

import com.google.gson.Gson;
import com.hibegin.common.util.EnvKit;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.install.business.service.InstallService;
import com.zrlog.install.business.vo.InstallConfigVO;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.InstallServerConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Application {

    public static void main(String[] args) throws FileNotFoundException {
        PathUtil.setRootPath(System.getProperty("user.dir"));
        if (args.length >= 1) {
            File configFile = new File(args[0]);
            if (configFile.exists()) {
                InstallConfigVO config = new Gson().fromJson(new FileReader(configFile), InstallConfigVO.class);
                InstallService installService = new InstallService(InstallConstants.installConfig, config.getDbConfig(), config.getConfigMsg());
                boolean install = installService.install();
                System.out.println("installed = " + install);
                return;
            }
        }
        WebServerBuilder builder = new WebServerBuilder.Builder().config(new InstallServerConfig()).build();
        builder.start();
    }
}
