package com.zrlog.install.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void shouldDetectEmptyStrings() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("zrlog"));
    }

    @Test
    public void shouldDetectNonEmptyStrings() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty("zrlog"));
    }
}
