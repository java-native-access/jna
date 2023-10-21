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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


/**
 * @author dblock[at]dblock[dot]org
 */
public class WinspoolDummyTest {

    private static final String EVERYONE = "S-1-1-0";

    @Test
    public void testNoDuplicateMethodsNames() {
        // Get a printer name from Java to use
        //String printer = PrintServiceLookup.lookupDefaultPrintService().getName();
        String printer = "Microsoft Print to PDF";

        // Get custom Winspool2 instance
        Winspool winspool = Winspool.INSTANCE;

        // Get printer handle from Winspool
        WinNT.HANDLEByReference hPrinter = new WinNT.HANDLEByReference();
        boolean success = winspool.OpenPrinter(printer, hPrinter, null);
        System.out.println("OpenPrinter status: " + success);
        if (getLastError() || !success) {
            System.err.println("Can't open printer, stopping");
            return;
        }

        // Get DEVMODE size
        int bufferSize = winspool.DocumentProperties(null, hPrinter.getValue(), printer, null, null, 0);

        if (getLastError() || bufferSize == 0) {
            System.err.println("Problem calling DocumentProperties");
        } else {
            System.out.println("The DEVMODE size must be " + bufferSize);
            Memory devmodeBuffer = new Memory(bufferSize);
            int status = winspool.DocumentProperties(null, hPrinter.getValue(), printer, devmodeBuffer, null, 6 /* DM_OUT_BUFFER | DM_PROMPT */);

            WinGDI.DEVMODE dm = Structure.newInstance(WinGDI.DEVMODE.class, devmodeBuffer);
            dm.read();

            System.out.println(dm);
            System.out.printf("Device:           %s%n", dm.getDmDeviceName());
            System.out.printf("Formname:         %s%n", dm.getDmFormName());
            if ((dm.dmFields & WinGDI.DM_ORIENTATION) == WinGDI.DM_ORIENTATION
                    || (dm.dmFields & WinGDI.DM_PAPERSIZE) == WinGDI.DM_PAPERSIZE
                    || (dm.dmFields & WinGDI.DM_PAPERLENGTH) == WinGDI.DM_PAPERLENGTH
                    || (dm.dmFields & WinGDI.DM_PAPERWIDTH) == WinGDI.DM_PAPERWIDTH
                    || (dm.dmFields & WinGDI.DM_COPIES) == WinGDI.DM_COPIES) {
                dm.dmUnion1.setType("dummystructname");
                dm.dmUnion1.read();
                if((dm.dmFields & WinGDI.DM_ORIENTATION) == WinGDI.DM_ORIENTATION) {
                    System.out.printf("Orientientation:  %d%n", dm.dmUnion1.dummystructname.dmOrientation);
                }
                if((dm.dmFields & WinGDI.DM_PAPERSIZE) == WinGDI.DM_PAPERSIZE) {
                    System.out.printf("Paper size:       %d%n", dm.dmUnion1.dummystructname.dmPaperSize);
                }
                if((dm.dmFields & WinGDI.DM_PAPERLENGTH) == WinGDI.DM_PAPERLENGTH) {
                    System.out.printf("Paper length:     %d%n", dm.dmUnion1.dummystructname.dmPaperLength);
                }
                if((dm.dmFields & WinGDI.DM_PAPERWIDTH) == WinGDI.DM_PAPERWIDTH) {
                    System.out.printf("Paper width:      %d%n", dm.dmUnion1.dummystructname.dmPaperWidth);
                }
                if((dm.dmFields & WinGDI.DM_COPIES) == WinGDI.DM_COPIES) {
                    System.out.printf("Copies:           %d%n", dm.dmUnion1.dummystructname.dmCopies);
                }
            }

            // TODO: This may throw false error
            long IDOK = 1;
            if (getLastError()) {
                System.err.println("Problem calling DocumentProperties");
            }

            if (status == IDOK) {
                System.out.println("The call to DocumentProperties returned IDOK");
            } else {
                // API states a negative value is equivalent to an error
                System.err.println("The call to DocumentProperties returned " + status);
            }
        }

        // Close printer handle
        winspool.INSTANCE.ClosePrinter(hPrinter.getValue());
    }

    private static boolean getLastError() {
        int error = Native.getLastError();
        if (error == 0) {
            System.out.println("  OK");
            return false;
        } else {
            System.out.println("  ERROR: " + error);
        }
        return true;
    }
}
