package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.WinNT;
import org.junit.Test;

import static org.junit.Assert.*;

public class COMUtilsTest {

    @Test
    public void testSUCCEEDED() throws Exception {
        assertTrue(COMUtils.SUCCEEDED(COMUtils.S_OK));
        assertTrue(COMUtils.SUCCEEDED(COMUtils.S_FALSE));
        assertFalse(COMUtils.SUCCEEDED(COMUtils.E_UNEXPECTED));
    }

    @Test
    public void testFAILED() throws Exception {
        assertFalse(COMUtils.FAILED(COMUtils.S_OK));
        assertFalse(COMUtils.FAILED(COMUtils.S_FALSE));
        assertTrue(COMUtils.FAILED(COMUtils.E_UNEXPECTED));
    }
    
    @Test(expected = COMException.class)
    public void testCreateCOMExceptionFromCustomHRESULT() {
        // This resulted in a LastErrorException instead of COMException
        COMUtils.checkRC(new WinNT.HRESULT(0x80040200));
    }
}