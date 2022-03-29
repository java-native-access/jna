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

import static com.sun.jna.platform.win32.WinError.ERROR_INSUFFICIENT_BUFFER;
import static com.sun.jna.platform.win32.WinError.ERROR_SUCCESS;

import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winspool.JOB_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_2;
import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_4;
import com.sun.jna.ptr.IntByReference;

/**
 * Winspool Utility API.
 *
 * @author dblock[at]dblock.org, Ivan Ridao Freitas, Padrus, Artem Vozhdayenko
 * @author Tres Finocchiaro, tres.finocchiaro@gmail.com
 */
public abstract class WinspoolUtil {

    /**
     * Helper for getting printer info struct by number, e.g. PRINTER_INFO_1 = 1, etc.
     */
    static Structure getPrinterInfoByStruct(String printerName, int structType) {
        IntByReference pcbNeeded = new IntByReference();
        IntByReference pcReturned = new IntByReference();
        HANDLEByReference pHandle = new HANDLEByReference();

        // Get printer handle
        if (!Winspool.INSTANCE.OpenPrinter(printerName, pHandle, null)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        Win32Exception we = null;
        Structure struct = null;

        try {
            // First pass: Get page size
            Winspool.INSTANCE.GetPrinter(pHandle.getValue(), structType, null, 0, pcbNeeded);

            struct = initStructByType(structType, pcbNeeded.getValue());
            if (pcbNeeded.getValue() <= 0) {
                return struct;
            }

            // Second pass: Get printer information
            if (!Winspool.INSTANCE.GetPrinter(pHandle.getValue(), structType, struct.getPointer(), pcbNeeded.getValue(), pcReturned)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            struct.read();
        }
        catch(Win32Exception e) {
            we = e;
        }
        finally {
            if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
                Win32Exception ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                if (we != null) {
                    ex.addSuppressedReflected(we);
                }
            }
        }

        if (we != null) {
            throw we;
        }

        return struct;
    }

    /**
     * Helper for getting array of printer info
     * PRINTER_ENUM_LOCAL | PRINTER_ENUM_CONNECTIONS
     */
    static Structure[] getPrinterInfoByStruct(int flags, int structType) {
        IntByReference pcbNeeded = new IntByReference();
        IntByReference pcReturned = new IntByReference();
        // When Name is NULL, setting Flags to PRINTER_ENUM_LOCAL | PRINTER_ENUM_CONNECTIONS
        // enumerates printers that are installed on the local machine.
        // These printers include those that are physically attached to the local machine
        // as well as remote printers to which it has a network connection.
        // See https://msdn.microsoft.com/en-us/library/windows/desktop/dd162692(v=vs.85).aspx
        Winspool.INSTANCE.EnumPrinters(flags, null, structType, null, 0, pcbNeeded, pcReturned);
        Structure struct = initStructByType(structType, pcbNeeded.getValue());
        if (pcbNeeded.getValue() <= 0) {
            return emptyStructArrayByType(structType);
        }
        if (!Winspool.INSTANCE.EnumPrinters(flags, null, structType, struct.getPointer(), pcbNeeded.getValue(), pcbNeeded,
                pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        struct.read();
        return struct.toArray(pcReturned.getValue());
    }

    private static Structure[] emptyStructArrayByType(int structType) {
        switch(structType) {
            case 1:
                return new PRINTER_INFO_1[0];
            case 2:
                return new PRINTER_INFO_2[0];
            case 3:
                return new PRINTER_INFO_3[0];
            case 4:
                return new PRINTER_INFO_4[0];
            case 5:
                return new PRINTER_INFO_5[0];
            case 6:
                return new PRINTER_INFO_6[0];
            case 7:
                return new PRINTER_INFO_7[0];
            case 8:
                return new PRINTER_INFO_8[0];
            case 9:
                return new PRINTER_INFO_9[0];
            default:
                throw new UnsupportedOperationException("PRINTER_INFO_" + structType + " doesn't exist or has not been implemented");
        }
    }

    private static Structure initStructByType(int structType, int size) {
        switch(structType) {
            case 1:
                return size <= 0 ? new PRINTER_INFO_1() : new PRINTER_INFO_1(size);
            case 2:
                return size <= 0 ? new PRINTER_INFO_2() : new PRINTER_INFO_2(size);
            case 3:
                return size <= 0 ? new PRINTER_INFO_3() : new PRINTER_INFO_3(size);
            case 4:
                return size <= 0 ? new PRINTER_INFO_4() : new PRINTER_INFO_4(size);
            case 5:
                return size <= 0 ? new PRINTER_INFO_5() : new PRINTER_INFO_5(size);
            case 6:
                return size <= 0 ? new PRINTER_INFO_6() : new PRINTER_INFO_6(size);
            case 7:
                return size <= 0 ? new PRINTER_INFO_7() : new PRINTER_INFO_7(size);
            case 8:
                return size <= 0 ? new PRINTER_INFO_8() : new PRINTER_INFO_8(size);
            case 9:
                return size <= 0 ? new PRINTER_INFO_9() : new PRINTER_INFO_9(size);
            default:
                throw new UnsupportedOperationException("PRINTER_INFO_" + structType + " doesn't exist or has not been implemented");
        }
    }

    public static PRINTER_INFO_1[] getAllPrinterInfo1() {
        return getPrinterInfo1(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_2[] getAllPrinterInfo2() {
        return getPrinterInfo2(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_3[] getAllPrinterInfo3() {
        return getPrinterInfo3(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_4[] getAllPrinterInfo4() {
        return getPrinterInfo4(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_5[] getAllPrinterInfo5() {
        return getPrinterInfo5(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_6[] getAllPrinterInfo6() {
        return getPrinterInfo6(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_7[] getAllPrinterInfo7() {
        return getPrinterInfo7(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_8[] getAllPrinterInfo8() {
        return getPrinterInfo8(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    public static PRINTER_INFO_9[] getAllPrinterInfo9() {
        return getPrinterInfo9(Winspool.PRINTER_ENUM_LOCAL | Winspool.PRINTER_ENUM_CONNECTIONS);
    }

    /**
     * The PRINTER_INFO_1 structure specifies general printer information.
     */
    public static PRINTER_INFO_1[] getPrinterInfo1() {
        return getPrinterInfo1(Winspool.PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_1 structure specifies general printer information.
     */
    public static PRINTER_INFO_1 getPrinterInfo1(String printerName) {
        return (PRINTER_INFO_1)getPrinterInfoByStruct(printerName, 1);
    }

    /**
     * The PRINTER_INFO_1 structure specifies general printer information.
     */
    public static PRINTER_INFO_1[] getPrinterInfo1(int flags) {
        return (PRINTER_INFO_1[])getPrinterInfoByStruct(flags, 1);
    }

    /**
     * The PRINTER_INFO_2 structure specifies general printer information.
     */
    public static PRINTER_INFO_2 getPrinterInfo2(String printerName) {
        return (PRINTER_INFO_2)getPrinterInfoByStruct(printerName, 2);
    }

    /**
     * The PRINTER_INFO_2 structure specifies general printer information.
     */
    public static PRINTER_INFO_2[] getPrinterInfo2(int flags) {
        return (PRINTER_INFO_2[])getPrinterInfoByStruct(flags, 2);
    }

    /**
     * The PRINTER_INFO_2 structure specifies general printer information.
     */
    public static PRINTER_INFO_2[] getPrinterInfo2() {
        return getPrinterInfo2(Winspool.PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_3 structure specifies printer security information.
     */
    public static PRINTER_INFO_3 getPrinterInfo3(String printerName) {
        return (PRINTER_INFO_3)getPrinterInfoByStruct(printerName, 3);
    }

    /**
     * The PRINTER_INFO_3 structure specifies printer security information.
     */
    public static PRINTER_INFO_3[] getPrinterInfo3(int flags) {
        return (PRINTER_INFO_3[])getPrinterInfoByStruct(flags, 3);
    }

    /**
     * The PRINTER_INFO_3 structure specifies printer security information.
     */
    public static PRINTER_INFO_3[] getPrinterInfo3() {
        return getPrinterInfo3(Winspool.PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_4 structure specifies general printer information.
     */
    public static PRINTER_INFO_4 getPrinterInfo4(String printerName) {
        return (PRINTER_INFO_4)getPrinterInfoByStruct(printerName, 4);
    }

    /**
     * The PRINTER_INFO_4 structure specifies general printer information.
     */
    public static PRINTER_INFO_4[] getPrinterInfo4(int flags) {
        return (PRINTER_INFO_4[])getPrinterInfoByStruct(flags, 4);
    }

    /**
     * The PRINTER_INFO_4 structure specifies general printer information.
     */
    public static PRINTER_INFO_4[] getPrinterInfo4() {
        return getPrinterInfo4(PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_5 structure specifies detailed printer information.
     */
    public static PRINTER_INFO_5 getPrinterInfo5(String printerName) {
        return (PRINTER_INFO_5)getPrinterInfoByStruct(printerName, 5);
    }

    /**
     * The PRINTER_INFO_5 structure specifies detailed printer information.
     */
    public static PRINTER_INFO_5[] getPrinterInfo5(int flags) {
        return (PRINTER_INFO_5[])getPrinterInfoByStruct(flags, 5);
    }

    /**
     * The PRINTER_INFO_5 structure specifies detailed printer information.
     */
    public static PRINTER_INFO_5[] getPrinterInfo5() {
        return getPrinterInfo5(PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_6 specifies the status value of a printer.
     */
    public static PRINTER_INFO_6 getPrinterInfo6(String printerName) {
        return (PRINTER_INFO_6)getPrinterInfoByStruct(printerName, 6);
    }

    /**
     * The PRINTER_INFO_6 specifies the status value of a printer.
     */
    public static PRINTER_INFO_6[] getPrinterInfo6(int flags) {
        return (PRINTER_INFO_6[])getPrinterInfoByStruct(flags, 6);
    }

    /**
     * The PRINTER_INFO_6 specifies the status value of a printer.
     */
    public static PRINTER_INFO_6[] getPrinterInfo6() {
        return getPrinterInfo6(PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_7 structure specifies directory services printer information.
     */
    public static PRINTER_INFO_7 getPrinterInfo7(String printerName) {
        return (PRINTER_INFO_7)getPrinterInfoByStruct(printerName, 7);
    }

    /**
     * The PRINTER_INFO_7 structure specifies directory services printer information.
     */
    public static PRINTER_INFO_7[] getPrinterInfo7(int flags) {
        return (PRINTER_INFO_7[])getPrinterInfoByStruct(flags, 7);
    }

    /**
     * The PRINTER_INFO_7 structure specifies directory services printer information.
     */
    public static PRINTER_INFO_7[] getPrinterInfo7() {
        return getPrinterInfo7(PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_8 structure specifies the global default printer settings.
     */
    public static PRINTER_INFO_8 getPrinterInfo8(String printerName) {
        return (PRINTER_INFO_8)getPrinterInfoByStruct(printerName, 8);
    }

    /**
     * The PRINTER_INFO_8 structure specifies the global default printer settings.
     */
    public static PRINTER_INFO_8[] getPrinterInfo8(int flags) {
        return (PRINTER_INFO_8[])getPrinterInfoByStruct(flags, 8);
    }

    /**
     * The PRINTER_INFO_8 structure specifies the global default printer settings.
     */
    public static PRINTER_INFO_8[] getPrinterInfo8() {
        return getPrinterInfo8(PRINTER_ENUM_LOCAL);
    }

    /**
     * The PRINTER_INFO_9 Structure specifies the per-user default printer settings.\
     */
    public static PRINTER_INFO_9 getPrinterInfo9(String printerName) {
        return (PRINTER_INFO_9)getPrinterInfoByStruct(printerName, 9);
    }

    /**
     * The PRINTER_INFO_9 Structure specifies the per-user default printer settings.\
     */
    public static PRINTER_INFO_9[] getPrinterInfo9(int flags) {
        return (PRINTER_INFO_9[])getPrinterInfoByStruct(flags, 9);
    }

    /**
     * The PRINTER_INFO_9 Structure specifies the per-user default printer settings.\
     */
    public static PRINTER_INFO_9[] getPrinterInfo9() {
        return getPrinterInfo9(PRINTER_ENUM_LOCAL);
    }
}