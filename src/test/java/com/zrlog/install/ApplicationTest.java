package com.zrlog.install;

import com.google.gson.Gson;
import com.hibegin.common.dao.InMemoryDatabase;
import com.zrlog.install.business.vo.InstallConfigVO;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.DefaultInstallConfig;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ApplicationTest {

    @After
    public void tearDown() {
        InstallConstants.installConfig = new DefaultInstallConfig();
    }

    @Test
    public void shouldSkipConfigInstallWhenConfigFileIsMissing() throws Exception {
        assertNull(Application.installFromConfigFile(new String[0]));
        assertNull(Application.installFromConfigFile(new String[]{"/tmp/missing-zrlog-install-config.json"}));
    }

    @Test
    public void shouldInstallFromConfigFileUsingInMemoryDatabase() throws Exception {
        File root = Files.createTempDirectory("zrlog-application-install").toFile();
        File configFile = new File(root, "install.json");
        TestInstallConfig installConfig = new TestInstallConfig(
                new File(root, "conf/db.properties"),
                new File(root, "conf/install.lock"));
        InstallConstants.installConfig = installConfig;
        InstallConfigVO configVO = new InstallConfigVO();
        Map<String, String> configMsg = new LinkedHashMap<>();
        configMsg.put("title", "Application Install");
        configMsg.put("second_title", "Config file");
        configMsg.put("username", "admin");
        configMsg.put("password", "${github_pat}");
        configMsg.put("email", "admin@example.com");
        configMsg.put("installDate", "2026-06-29 10:20:30 +0800");
        configVO.setConfigMsg(configMsg);
        configVO.setDbConfig(h2DbConfig());
        configVO.setContextPath("/blog");
        Files.writeString(configFile.toPath(), new Gson().toJson(configVO));

        Integer exitCode = Application.installFromConfigFile(new String[]{configFile.getAbsolutePath(), "password"});

        assertEquals(Integer.valueOf(0), exitCode);
        assertTrue(installConfig.dbPropertiesFile.exists());
        assertTrue(installConfig.lockFile.exists());
        assertTrue(installConfig.installSuccessCalled);
    }

    private static Map<String, String> h2DbConfig() {
        Map<String, String> dbConfig = new LinkedHashMap<>();
        dbConfig.put("driverClass", InMemoryDatabase.H2_DRIVER_CLASS);
        dbConfig.put("jdbcUrl", InMemoryDatabase.h2JdbcUrl("zrlog_application_install_" + UUID.randomUUID()));
        dbConfig.put("user", "sa");
        dbConfig.put("password", "");
        dbConfig.put("dbType", "h2");
        dbConfig.put("dbName", "zrlog");
        dbConfig.put("dbHost", "localhost");
        dbConfig.put("dbPort", "0");
        return dbConfig;
    }

    private static class TestInstallConfig extends DefaultInstallConfig {

        private final File dbPropertiesFile;
        private final File lockFile;
        private boolean installSuccessCalled;

        TestInstallConfig(File dbPropertiesFile, File lockFile) {
            this.dbPropertiesFile = dbPropertiesFile;
            this.lockFile = lockFile;
        }

        @Override
        public File getDbPropertiesFile() {
            return dbPropertiesFile;
        }

        @Override
        public InstallAction getAction() {
            return new InstallAction() {
                @Override
                public void installSuccess() {
                    installSuccessCalled = true;
                }

                @Override
                public File getLockFile() {
                    return lockFile;
                }
            };
        }
    }
}
