/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
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
