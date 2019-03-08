/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 *
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
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolUtilTest {

    public static void main(String[] args) {
        for (PRINTER_INFO_1 printerInfo : WinspoolUtil.getPrinterInfo1()) {
            System.out.println(printerInfo.pName + ": " + printerInfo.pDescription);
        }
        for (PRINTER_INFO_4 printerInfo : WinspoolUtil.getPrinterInfo4()) {
            System.out.println(printerInfo.pPrinterName + " on " + printerInfo.pServerName);
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        WinNT.HANDLEByReference hbr = new WinNT.HANDLEByReference();
        boolean result = Winspool.INSTANCE.OpenPrinter("Will not be found", hbr, null);
        Assume.assumeFalse(result);
        int error = Native.getLastError();
        Assume.assumeTrue("Print service not available", error != WinError.RPC_S_SERVER_UNAVAILABLE);
    }

    @Test
    public void testGetPrinterInfo1() {
        assertTrue(WinspoolUtil.getPrinterInfo1().length >= 0);
    }

    @Test
    public void testGetPrinterInfo2() {
        assertTrue(WinspoolUtil.getPrinterInfo2().length >= 0);
    }

    @Test
    public void testGetPrinterInfo2Specific() {
        try {
            WinspoolUtil.getPrinterInfo2("1234567890A123");
            fail("A Win32Exception with ERROR_INVALID_PRINTER_NAME should have been thrown instead of hitting this.");
        } catch (Win32Exception e) {
            Assume.assumeTrue("Print service not available", WinError.RPC_S_SERVER_UNAVAILABLE != e.getHR().intValue());
            assertEquals("A Win32Exception with ERROR_INVALID_PRINTER_NAME message should have been thrown.",
                    Kernel32Util.formatMessage(W32Errors.HRESULT_FROM_WIN32(WinError.ERROR_INVALID_PRINTER_NAME)), e.getMessage());
        }
    }

    @Test
    public void testGetPrinterInfo4() {
        assertTrue(WinspoolUtil.getPrinterInfo4().length >= 0);
    }
}
