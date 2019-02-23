/*
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
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