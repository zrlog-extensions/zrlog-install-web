package com.zrlog.install.business.service;

import com.zrlog.install.web.config.InstallConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InstallPreflightService {

    public void assertReady(InstallConfig installConfig) throws IOException {
        assertWritableTarget(installConfig.getDbPropertiesFile(), "db.properties");
        assertWritableTarget(installConfig.getAction().getLockFile(), "install.lock");
        try (InputStream inputStream = InstallPreflightService.class.getResourceAsStream("/init-table-structure.sql")) {
            if (inputStream == null) {
                throw new IOException("Missing init-table-structure.sql");
            }
        }
    }

    private void assertWritableTarget(File targetFile, String label) throws IOException {
        File parent = targetFile.getParentFile();
        if (parent == null) {
            throw new IOException("Missing parent directory for " + label);
        }
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Can not create parent directory for " + label);
        }
        if (!parent.isDirectory() || !parent.canWrite()) {
            throw new IOException("Parent directory is not writable for " + label);
        }
        if (targetFile.exists() && !targetFile.canWrite()) {
            throw new IOException(label + " is not writable");
        }
        File tempFile = File.createTempFile(".zrlog-install-", ".tmp", parent);
        if (!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }
}
