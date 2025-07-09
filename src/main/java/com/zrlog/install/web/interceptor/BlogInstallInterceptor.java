package com.zrlog.install.web.interceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.install.exception.InstalledException;
import com.zrlog.install.web.InstallConstants;

import java.util.Objects;

public class BlogInstallInterceptor implements HandleAbleInterceptor {
    @Override
    public boolean isHandleAble(HttpRequest request) {
        if (request.getUri().startsWith("/install/static")) {
            return false;
        }
        return Objects.equals(request.getUri(), "/install") ||
                Objects.equals(request.getUri(), "/api/public/installResource") ||
                request.getUri().startsWith("/api/install/");
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String target = request.getUri();
        if (target.startsWith("/api/install/") && InstallConstants.installConfig.getAction().isInstalled()) {
            throw new InstalledException();
        }
        new MethodInterceptor().doInterceptor(request, response);
        return false;
    }
}
