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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_2;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4;
import com.sun.jna.ptr.IntByReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolTest {

    @BeforeClass
    public static void setUp() throws Exception {
        HANDLEByReference hbr = new HANDLEByReference();
        boolean result = Winspool.INSTANCE.OpenPrinter("Will not be found", hbr, null);
        Assume.assumeFalse(result);
        int error = Native.getLastError();
        Assume.assumeTrue("Print service not available", error != WinError.RPC_S_SERVER_UNAVAILABLE);
    }

    @Test
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
            for (PRINTER_INFO_4 pi : printerInfo) {
                assertTrue(pi.pPrinterName == null || pi.pPrinterName.length() >= 0);
            }
        }
    }

    @Test
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

    @Test
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
            for (PRINTER_INFO_1 pi : printerInfo) {
                assertTrue(pi.pName == null || pi.pName.length() >= 0);
            }
        }
    }

    @Test
    public void testOpenPrinter() {
        HANDLEByReference hbr = new HANDLEByReference();
        boolean result = Winspool.INSTANCE.OpenPrinter("1234567890A123", hbr, null);
        assertFalse("OpenPrinter should return false on failure.", result);
        assertNull("The pointer-to-a-printer-handle should be null on failure.", hbr.getValue());
        assertEquals("GetLastError() should return ERROR_INVALID_PRINTER_NAME", WinError.ERROR_INVALID_PRINTER_NAME, Native.getLastError());
    }

    @Test
    public void testClosePrinter() {
        boolean result = Winspool.INSTANCE.ClosePrinter(null);
        assertFalse("ClosePrinter should return false on failure.", result);
        assertEquals("GetLastError() should return ERROR_INVALID_HANDLE", WinError.ERROR_INVALID_HANDLE, Native.getLastError());
    }

    @Test
    public void testCorrectDeclarationOfMembers() throws InstantiationException, IllegalAccessException {
        for(Class klass: Winspool.class.getDeclaredClasses()) {
            if(Structure.class.isAssignableFrom(klass)) {
                boolean writeWorked = false;
                try {
                    Structure struct = Structure.newInstance((Class<? extends Structure>) klass);
                    struct.write();
                    writeWorked = true;
                } catch (java.lang.Throwable ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                }
                assertTrue("Failed to write structure: " + klass.getName(), writeWorked);
            }
        }
    }
}
