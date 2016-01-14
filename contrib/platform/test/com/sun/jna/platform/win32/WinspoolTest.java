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
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_2;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4;
import com.sun.jna.ptr.IntByReference;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WinspoolTest.class);
    }

    public void testEnumPrinters_4() {
    	IntByReference pcbNeeded = new IntByReference();
    	IntByReference pcReturned = new IntByReference();
    	// if there are no printers installed, EnumPrinters will succeed with zero items returned 
    	Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 4, null, 0, pcbNeeded, pcReturned);
    	assertTrue(pcReturned.getValue() == 0);
    	if (pcbNeeded.getValue() > 0) {
    		PRINTER_INFO_4 pPrinterEnum = new PRINTER_INFO_4(pcbNeeded.getValue());	    	
    		assertTrue(Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 4, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned));
	    	assertTrue(pcReturned.getValue() >= 0);	    	
	    	PRINTER_INFO_4[] printerInfo = (PRINTER_INFO_4[]) pPrinterEnum.toArray(pcReturned.getValue());
	    	for(PRINTER_INFO_4 pi : printerInfo) {
	    		assertTrue(pi.pPrinterName == null || pi.pPrinterName.length() >= 0);
	    	}
    	}
    }
    
    public void testEnumPrinters_2() {
        IntByReference pcbNeeded = new IntByReference();
        IntByReference pcReturned = new IntByReference();
        // if there are no printers installed, EnumPrinters will succeed with zero items returned 
        Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 2, null, 0, pcbNeeded, pcReturned);
        assertTrue(pcReturned.getValue() == 0);
        if (pcbNeeded.getValue() > 0) {
            PRINTER_INFO_2 pPrinterEnum = new PRINTER_INFO_2(pcbNeeded.getValue());         
            assertTrue(Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 2, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned));
            assertTrue(pcReturned.getValue() >= 0);         
            PRINTER_INFO_2[] printerInfo = (PRINTER_INFO_2[]) pPrinterEnum.toArray(pcReturned.getValue());
            for(PRINTER_INFO_2 pi : printerInfo) {
                assertTrue(pi.pPrinterName == null || pi.pPrinterName.length() >= 0);
            }
        }
    }
    
    public void testEnumPrinters_1() {
    	IntByReference pcbNeeded = new IntByReference();
    	IntByReference pcReturned = new IntByReference();
    	// if there are no printers installed, EnumPrinters will succeed with zero items returned
    	Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 1, null, 0, pcbNeeded, pcReturned);
    	assertTrue(pcReturned.getValue() == 0);
    	if (pcbNeeded.getValue() > 0) {
	    	PRINTER_INFO_1 pPrinterEnum = new PRINTER_INFO_1(pcbNeeded.getValue());
	    	assertTrue(Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, null, 1, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned));
	    	assertTrue(pcReturned.getValue() >= 0);
	    	PRINTER_INFO_1[] printerInfo = (PRINTER_INFO_1[]) pPrinterEnum.toArray(pcReturned.getValue());
	    	for(PRINTER_INFO_1 pi : printerInfo) {
	    		assertTrue(pi.pName == null || pi.pName.length() >= 0);
	    	}
    	}
    }
    
    public void testOpenPrinter() {
        HANDLEByReference hbr = new HANDLEByReference();
        boolean result = Winspool.INSTANCE.OpenPrinter("1234567890A123", hbr, null);
        assertFalse("OpenPrinter should return false on failure.", result);
        assertNull("The pointer-to-a-printer-handle should be null on failure.", hbr.getValue());
        assertEquals("GetLastError() should return ERROR_INVALID_PRINTER_NAME", WinError.ERROR_INVALID_PRINTER_NAME, Native.getLastError());
    }
    
    public void testClosePrinter() {
        boolean result = Winspool.INSTANCE.ClosePrinter(null);
        assertFalse("ClosePrinter should return false on failure.", result);
        assertEquals("GetLastError() should return ERROR_INVALID_HANDLE", WinError.ERROR_INVALID_HANDLE, Native.getLastError());
    }
}
