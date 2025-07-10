package com.zrlog.install.business.service;

import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.template.BasicTemplateRender;
import com.zrlog.install.business.type.TestConnectDbResult;
import com.zrlog.install.util.InstallI18nUtil;
import com.zrlog.install.util.MarkdownUtil;
import com.zrlog.install.util.SqlConvertUtils;
import com.zrlog.install.util.StringUtils;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.config.InstallConfig;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 与安装向导相关的业务代码
 */
public class InstallService {

    private static final Logger LOGGER = LoggerUtil.getLogger(InstallService.class);
    private final Map<String, String> dbConn;
    private final Map<String, String> configMsg;
    private final InstallAction installAction;
    private final InstallConfig installConfig;

    public InstallService(InstallConfig installConfig, Map<String, String> dbConn, Map<String, String> configMsg) {
        this.dbConn = dbConn;
        this.configMsg = configMsg;
        this.installAction = installConfig.getAction();
        this.installConfig = installConfig;
    }

    public InstallService(InstallConfig installConfig, Map<String, String> dbConn) {
        this(installConfig, dbConn, null);
    }

    /**
     * 通过执行数据库的sql文件，完成对数据库表，基础表数据的初始化，达到安装的效果
     *
     * @return false 表示安装没有正常执行，true 表示初始化数据库成功。
     */
    public boolean install() {
        if (installAction.isInstalled()) {
            return false;
        }
        return startInstall(dbConn, configMsg);
    }

    /**
     * 封装网站设置的数据数据，返回Map形式方便调用者进行遍历
     *
     * @param webSite
     * @return
     */
    private Map<String, Object> getDefaultWebSiteSettingMap(Map<String, String> webSite) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("appId", UUID.randomUUID().toString());
        map.put("title", Objects.requireNonNullElse(webSite.get("title"), ""));
        map.put("second_title", Objects.requireNonNullElse(webSite.get("second_title"), ""));
        map.put("language", installConfig.getAcceptLanguage());
        map.put("rows", 10);
        map.put("template", installConfig.defaultTemplatePath());
        map.put("autoUpgradeVersion", 86400);
        map.put("zrlogSqlVersion", installConfig.getZrLogSqlVersion());
        return map;
    }

    static DataSourceWrapperImpl buildDataSource(Properties dbProperties, boolean dev) {
        DataSourceWrapperImpl dataSource = new DataSourceWrapperImpl(dbProperties, dev);
        if (!dataSource.isWebApi()) {
            dataSource.setDriverClassName(dbProperties.getProperty("driverClass"));
            dataSource.setJdbcUrl(dbProperties.getProperty("jdbcUrl"));
        }
        dataSource.setUsername(dbProperties.getProperty("user"));
        dataSource.setPassword(dbProperties.getProperty("password"));

        return dataSource;
        /*}*/
    }

    /**
     * 尝试使用填写的数据库信息连接数据库
     */
    public TestConnectDbResult testDbConn() {
        Properties properties = new Properties();
        properties.putAll(dbConn);
        try (DataSourceWrapperImpl ds = buildDataSource(properties, EnvKit.isDevMode())) {
            ds.testConnection();
            return TestConnectDbResult.SUCCESS;
        } catch (SQLRecoverableException e) {
            LOGGER.log(Level.SEVERE, "", e);
            return TestConnectDbResult.CREATE_CONNECT_ERROR;
        } catch (SQLSyntaxErrorException e) {
            LOGGER.log(Level.SEVERE, "", e);
            return TestConnectDbResult.DB_NOT_EXISTS;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
            if (e.getMessage().contains("Access denied for user") && e.getMessage().contains("using password")) {
                return TestConnectDbResult.USERNAME_OR_PASSWORD_ERROR;
            } else {
                if (e.getCause() instanceof IOException) {
                    return TestConnectDbResult.CREATE_CONNECT_ERROR;
                }
                return TestConnectDbResult.SQL_EXCEPTION_UNKNOWN;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        return TestConnectDbResult.UNKNOWN;
    }


    /**
     * 保存程序的数据库链接信息
     *
     * @throws IOException
     */
    private void installSuccess() throws IOException {
        File dbFile = installConfig.getDbPropertiesFile();
        if (dbFile.exists()) {
            dbFile.delete();
        }
        dbFile.getParentFile().mkdirs();
        dbFile.createNewFile();
        Properties prop = new Properties();
        prop.putAll(dbConn);
        prop.store(new FileOutputStream(dbFile), "This is a database configuration dbFile");
        File lockFile = installConfig.getAction().getLockFile();
        lockFile.getParentFile().mkdirs();
        lockFile.createNewFile();
        //install success
        installAction.installSuccess();
    }

    private boolean startInstall(Map<String, String> dbConn, Map<String, String> blogMsg) {
        Properties properties = new Properties();
        properties.putAll(dbConn);
        //
        try (DataSourceWrapperImpl ds = buildDataSource(properties, EnvKit.isDevMode())) {
            DAO dao = new DAO(ds);
            String sql = IOUtil.getStringInputStream(InstallService.class.getResourceAsStream("/init-table-structure.sql"));
            List<String> sqlList;
            if (ds.isWebApi()) {
                sqlList = SqlConvertUtils.doMySQLToSqliteBySqlText(sql);
            } else {
                sqlList = SqlConvertUtils.extractExecutableSql(sql);
            }
            for (String sqlSt : sqlList) {
                dao.execute(sqlSt);
            }
            List<Boolean> results = new ArrayList<>();
            //初始数据
            results.add(initWebSite(dao));
            results.add(initUser(blogMsg, dao));
            results.add(insertNav(dao));
            results.add(initPlugin(dao));
            results.add(insertType(dao));
            results.add(insertTag(dao));
            results.add(insertFirstArticle(dao));
            if (results.stream().allMatch(e -> Objects.equals(e, true))) {
                installSuccess();
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "install error ", e);
        }
        return false;
    }

    private static String getPlainSearchText(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return Jsoup.parse(content).body().text();
    }

    private boolean insertFirstArticle(DAO dao) throws Exception {
        int logId = 1;
        String insetLog = "INSERT INTO `log`(`logId`,`canComment`,`keywords`,`alias`,`typeId`,`userId`,`title`,`content`,`plain_content`,`markdown`,`digest`,`releaseTime`,`last_update_date`,`rubbish`,`privacy`) VALUES (" + logId + ",?,?,?,1,1,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        try (InputStream in = InstallService.class.getResourceAsStream("/i18n/init-blog/" + installConfig.getAcceptLanguage() + ".md")) {
            Map<String, Object> data = new HashMap<>();
            data.put("editUrl", "/admin/article-edit?id=" + logId);
            String markdown = new BasicTemplateRender(data, InstallService.class).render(in);
            String content = MarkdownUtil.renderMd(markdown);
            params.add(true);
            params.add(InstallI18nUtil.getInstallStringFromRes("defaultType"));
            params.add("hello-world");
            params.add(InstallI18nUtil.getInstallStringFromRes("helloWorld"));
            params.add(content);
            params.add(getPlainSearchText(content));
            params.add(markdown);
            params.add(content);
            params.add(new Date());
            params.add(new Date());
            params.add(false);
            params.add(false);
        }
        return dao.execute(insetLog, params.toArray());
    }

    private boolean insertType(DAO dao) throws SQLException {
        String insertLogType = "INSERT INTO `type`(`typeId`, `typeName`, `remark`, `alias`) VALUES (1,'" + InstallI18nUtil.getInstallStringFromRes("defaultType") + "','','note')";
        return dao.execute(insertLogType);
    }

    private boolean insertTag(DAO dao) throws SQLException {
        String insertTag = "INSERT INTO `tag`(`tagId`,`text`,`count`) VALUES (1,'" + InstallI18nUtil.getInstallStringFromRes("defaultType") + "',1)";
        return dao.execute(insertTag);
    }

    private boolean initPlugin(DAO dao) throws SQLException {
        String insertPluginSql = "INSERT INTO `plugin` VALUES (1,NULL,true,'" + InstallI18nUtil.getInstallStringFromRes("category") + "',NULL,'types',3)," +
                "(2,NULL,true,'" + InstallI18nUtil.getInstallStringFromRes("tag") + "',NULL,'tags',3)," +
                "(3,NULL,true,'" + InstallI18nUtil.getInstallStringFromRes("link") + "',NULL,'links',2)," +
                "(4,NULL,true,'" + InstallI18nUtil.getInstallStringFromRes("archive") + "',NULL,'archives',3)";
        return dao.execute(insertPluginSql);
    }

    private boolean insertNav(DAO dao) throws SQLException {
        String insertLogNavSql = "INSERT INTO `lognav`( `navId`,`url`, `navName`, `sort`) VALUES (?,?,?,?)";
        return dao.execute(insertLogNavSql, 1, "/", InstallI18nUtil.getInstallStringFromRes("home"), 1) && dao.execute(insertLogNavSql, 2, "/admin/login", InstallI18nUtil.getInstallStringFromRes("manage"), 2);
    }

    private boolean initUser(Map<String, String> blogMsg, DAO dao) throws SQLException {
        String insertUserSql = "INSERT INTO `user`( `userId`,`userName`, `password`, `email`,`secretKey`) VALUES (1,?,?,?,?)";
        return dao.execute(insertUserSql, blogMsg.get("username"), installConfig.encryptPassword(blogMsg.get("password")), configMsg.get("email"), UUID.randomUUID().toString());
    }

    private boolean initWebSite(DAO dao) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `website` (`siteId`,`name`, `value`, `remark`) VALUES ");
        Map<String, Object> defaultMap = getDefaultWebSiteSettingMap(configMsg);
        for (int i = 0; i < defaultMap.size(); i++) {
            sb.append("(").append("?").append(",").append("?").append(",").append("?").append(",NULL),");
        }
        List<Object> params = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Object> e : defaultMap.entrySet()) {
            params.add(i += 1);
            params.add(e.getKey());
            params.add(e.getValue());
        }
        String insertWebSql = sb.substring(0, sb.toString().length() - 1);
        return dao.execute(insertWebSql, params.toArray());
    }
}
