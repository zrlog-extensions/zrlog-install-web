package com.zrlog.install.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.install.business.service.InstallResourceService;
import com.zrlog.install.web.InstallConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class InstallController extends Controller {

    /**
     * 加载安装向导第一个页面数据
     */
    public void index() {
        InputStream file = InstallController.class.getResourceAsStream("/install/index.html");
        if (Objects.isNull(file)) {
            response.renderCode(404);
            return;
        }
        Document document = Jsoup.parse(IOUtil.getStringInputStream(file));
        //clean history
        document.body().removeClass("dark");
        document.body().removeClass("light");
        Objects.requireNonNull(document.selectFirst("base")).attr("href", request.getContextPath() + "/");
        Map<String, Object> stringObjectMap = new InstallResourceService().installResourceInfo(getRequest());
        Objects.requireNonNull(document.getElementById("resourceInfo")).text(new Gson().toJson(stringObjectMap));
        if (InstallConstants.installConfig.getAction().isInstalled()) {
            document.title(String.valueOf(stringObjectMap.get("installedTitle")));
        } else {
            document.title(String.valueOf(stringObjectMap.get("installWizard")));
        }
        String html = document.html();
        response.renderHtmlStr(html);
    }

}
