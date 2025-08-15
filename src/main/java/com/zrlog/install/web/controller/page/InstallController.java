package com.zrlog.install.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.web.Controller;
import com.zrlog.install.business.response.InstalledResResponse;
import com.zrlog.install.business.service.InstallResourceService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class InstallController extends Controller {

    private void fillToRealLink(HttpRequest request, Element link) {
        String newUrl = link.attr("href").replaceFirst("/", request.getContextPath() + "/");
        link.attr("href", newUrl);
    }

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
        Object stringObjectMap = new InstallResourceService().installResourceInfo(getRequest());
        Objects.requireNonNull(document.getElementById("resourceInfo")).text(new Gson().toJson(stringObjectMap));
        if (stringObjectMap instanceof InstalledResResponse) {
            document.title(((InstalledResResponse) stringObjectMap).getInstalledTitle());
        } else {
            document.title(String.valueOf(((Map<String, Object>) stringObjectMap).get("installWizard")));
        }
        Elements favicon = document.head().select("link[rel=shortcut icon]");
        if (!favicon.isEmpty()) {
            favicon.forEach(link -> fillToRealLink(request, link));
        }
        String html = document.html();
        response.renderHtmlStr(html);
    }

}
