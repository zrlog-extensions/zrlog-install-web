package com.zrlog.install.exception;

import com.zrlog.install.business.type.TestConnectDbResult;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.DefaultInstallConfig;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstallExceptionTest {

    @After
    public void tearDown() {
        InstallConstants.installConfig = new DefaultInstallConfig();
    }

    @Test
    public void shouldExposeDatabaseErrorCodeAndMessage() {
        InstallConstants.installConfig = new DefaultInstallConfig();
        InstallException exception = new InstallException(TestConnectDbResult.DB_NOT_EXISTS);

        assertEquals(9000, exception.getError());
        assertEquals("DB_NOT_EXISTS", exception.getCode());
        assertTrue(exception.getMessage().contains("[Error-DB_NOT_EXISTS]"));
        assertTrue(exception.getMessage().length() > "[Error-DB_NOT_EXISTS] - ".length());
    }

    @Test
    public void shouldExposeInstalledErrorCodeAndMessage() {
        InstallConstants.installConfig = new DefaultInstallConfig();
        InstalledException exception = new InstalledException();

        assertEquals(9020, exception.getError());
        assertTrue(exception.getMessage().length() > 0);
    }

    @Test
    public void shouldUseWarInstalledMessageWhenWarModeIsEnabled() {
        InstallConstants.installConfig = new DefaultInstallConfig() {
            @Override
            public boolean isWarMode() {
                return true;
            }
        };
        InstalledException exception = new InstalledException();

        assertEquals(9020, exception.getError());
        assertTrue(exception.getMessage().length() > 0);
    }

    @Test
    public void shouldExposeMissingFieldErrors() {
        assertError(new MissingDbHostException(), 9021);
        assertError(new MissingDbPortException(), 9022);
        assertError(new MissingDbNameException(), 9023);
        assertError(new MissingDbUserNameException(), 9023);
    }

    private static void assertError(AbstractInstallException exception, int error) {
        assertEquals(error, exception.getError());
        assertTrue(exception.getMessage().length() > 0);
    }
}
