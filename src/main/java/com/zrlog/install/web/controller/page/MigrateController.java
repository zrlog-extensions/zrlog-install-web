package com.zrlog.install.web.controller.page;

import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.install.util.SqlConvertUtils;
import com.zrlog.install.web.InstallConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

public class MigrateController extends Controller {

    public void convertToSqliteSqlFile() throws FileNotFoundException {
        if (InstallConstants.installConfig.getAction().isInstalled()) {
            response.renderCode(403);
            return;
        }
        String sqlPath = getRequest().getParaToStr("sqlPath", "mysql.sql");
        if (sqlPath.isEmpty()) {
            response.renderCode(404);
        }
        File sqlFile = PathUtil.getConfFile(sqlPath);
        List<String> strings = SqlConvertUtils.doMySQLToSqliteBySqlText(IOUtil.getStringInputStream(new FileInputStream(sqlFile)));
        StringJoiner stringJoiner = new StringJoiner(";\n");
        strings.forEach(stringJoiner::add);
        IOUtil.writeStrToFile(stringJoiner.toString(), PathUtil.getConfFile("sqlite.sql"));
        response.renderHtmlStr("convert success");
    }


    public void doImportSqlite() throws Exception {
        convertToSqliteSqlFile();
        String sqlPath = getRequest().getParaToStr("sqlPath", "sqlite.sql");
        File sqlFile = PathUtil.getConfFile(sqlPath);
        Properties properties = new Properties();
        properties.load(new FileInputStream(PathUtil.getConfFile("sqlite-db.properties")));
        try (DataSourceWrapper dataSourceWrapper = new DataSourceWrapperImpl(properties, false)) {
            DAO dao = new DAO(dataSourceWrapper);
            for (String sql : SqlConvertUtils.extractExecutableSqlByInputStream(new FileInputStream(sqlFile))) {
                try {
                    if (sql.startsWith("INSERT INTO")) {
                        List<Object> values = SqlConvertUtils.extractValues(sql);
                        // 构造带 ? 占位符的 SQL
                        String realSql = sql.replaceFirst("\\((.+)\\)", "(" + values.stream().map(v -> "?").reduce((a, b) -> a + ", " + b).orElse("") + ")");
                        dao.execute(realSql, values.toArray());
                    } else {
                        dao.execute(sql);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        response.renderHtmlStr("doImportSqlite success");
    }
}
