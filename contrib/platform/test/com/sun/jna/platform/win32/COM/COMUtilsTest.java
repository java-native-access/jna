package com.sun.jna.platform.win32.COM;

import junit.framework.TestCase;

public class COMUtilsTest extends TestCase {

    public void testSUCCEEDED() throws Exception {
        assertTrue(COMUtils.SUCCEEDED(COMUtils.S_OK));
        assertTrue(COMUtils.SUCCEEDED(COMUtils.S_FALSE));
        assertFalse(COMUtils.SUCCEEDED(COMUtils.E_UNEXPECTED));
    }

    public void testFAILED() throws Exception {
        assertFalse(COMUtils.FAILED(COMUtils.S_OK));
        assertFalse(COMUtils.FAILED(COMUtils.S_FALSE));
        assertTrue(COMUtils.FAILED(COMUtils.E_UNEXPECTED));
    }
}