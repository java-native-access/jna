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

import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolUtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WinspoolUtilTest.class);
        for(PRINTER_INFO_1 printerInfo : WinspoolUtil.getPrinterInfo1()) {
            System.out.println(printerInfo.pName + ": " + printerInfo.pDescription);        	
        }
        for(PRINTER_INFO_4 printerInfo : WinspoolUtil.getPrinterInfo4()) {
            System.out.println(printerInfo.pPrinterName + " on " + printerInfo.pServerName);        	
        }
    }
    
	public void testGetPrinterInfo1() {
		assertTrue(WinspoolUtil.getPrinterInfo1().length >= 0);
	}
	
	public void testGetPrinterInfo2() {
		assertTrue(WinspoolUtil.getPrinterInfo2().length >= 0);
	}
	
    public void testGetPrinterInfo2Specific() {
        try {
            WinspoolUtil.getPrinterInfo2("1234567890A123");
            fail("A Win32Exception with ERROR_INVALID_PRINTER_NAME should have been thrown instead of hitting this.");
        } catch (Win32Exception e) {
            assertEquals("A Win32Exception with ERROR_INVALID_PRINTER_NAME message should have been thrown.",
                    Kernel32Util.formatMessage(W32Errors.HRESULT_FROM_WIN32(WinError.ERROR_INVALID_PRINTER_NAME)), e.getMessage());
        }
    }

	public void testGetPrinterInfo4() {
        assertTrue(WinspoolUtil.getPrinterInfo4().length >= 0);
    }
}
