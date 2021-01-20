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
package com.sun.jna.platform.win32;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winspool.*;
import com.sun.jna.ptr.PointerByReference;

import static com.sun.jna.platform.win32.Winspool.*;

public class Win32SpoolMonitor {

    private static final int TWO_DIMENSIONAL_PRINTERS = 0;

    private static final Charset UTF_16LE = Charset.forName("UTF-16LE");

    public void monitorPrinter(String pPrinterName) {
        HANDLEByReference phPrinter = new HANDLEByReference();
        Winspool.INSTANCE.OpenPrinter(pPrinterName, phPrinter, null);

        // Get change notification handle for the printer
        HANDLE chgObject = Winspool.INSTANCE.FindFirstPrinterChangeNotification(
                    phPrinter.getValue(),
                    Winspool.PRINTER_CHANGE_JOB,
                    0,
                    (PRINTER_NOTIFY_OPTIONS) null);

        if (chgObject != null) {
            while (true) {
                // Wait for the change notification
                Kernel32.INSTANCE.WaitForSingleObject(chgObject,
                        WinBase.INFINITE);

                DWORDByReference pdwChange = new DWORDByReference();
                boolean fcnreturn =
                    Winspool.INSTANCE.FindNextPrinterChangeNotification(
                        chgObject,
                        pdwChange,
                        (PRINTER_NOTIFY_OPTIONS) null,
                        (PointerByReference) null);

                if (fcnreturn) {
                    JOB_INFO_1[] jobInfo1 = WinspoolUtil.getJobInfo1(phPrinter);
                    for (int i = 0; i < jobInfo1.length; i++) {
                        this.printJobInfo(jobInfo1[i]);
                    }
                    break;
                }
            }
            // Close Printer Change Notification handle when finished.
            Winspool.INSTANCE.FindClosePrinterChangeNotification(chgObject);
        } else {
            // Unable to open printer change notification handle
            getLastError();
        }
    }

    public int getLastError() {
        int rc = Kernel32.INSTANCE.GetLastError();

        if (rc != 0)
            System.out.println("error: " + rc);

        return rc;
    }

    public void monitorAllPrinters() {
        System.out.println("Monitoring all printers, press Ctrl + C to stop");
        HANDLEByReference printServerHandle = new HANDLEByReference();
        boolean success =
            Winspool.INSTANCE.OpenPrinter(null, printServerHandle, null);
        if (!success) {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            throw new RuntimeException("Failed to access the print server - " +
                errorCode);
        }

        try {
            PRINTER_NOTIFY_OPTIONS options = new PRINTER_NOTIFY_OPTIONS();
            options.Count = 1;
            PRINTER_NOTIFY_OPTIONS_TYPE.ByReference optionsType =
                new PRINTER_NOTIFY_OPTIONS_TYPE.ByReference();
            optionsType.Type = JOB_NOTIFY_TYPE;
            optionsType.setFields(new short[] {
                JOB_NOTIFY_FIELD_PRINTER_NAME,
                JOB_NOTIFY_FIELD_STATUS,
                JOB_NOTIFY_FIELD_DOCUMENT
            });
            optionsType.toArray(1);
            options.pTypes = optionsType;
            HANDLE changeNotificationsHandle =
                Winspool.INSTANCE.FindFirstPrinterChangeNotification(
                    printServerHandle.getValue(),
                    PRINTER_CHANGE_ADD_JOB |
                        PRINTER_CHANGE_SET_JOB |
                        PRINTER_CHANGE_DELETE_JOB,
                    TWO_DIMENSIONAL_PRINTERS,
                    options);
            if (!isValidHandle(changeNotificationsHandle)) {
                int errorCode = Kernel32.INSTANCE.GetLastError();
                throw new RuntimeException("Failed to get a change handle - " +
                    errorCode);
            }

            try {
                while (true) {
                    Kernel32.INSTANCE.WaitForSingleObject(
                        changeNotificationsHandle,
                        WinBase.INFINITE);

                    DWORDByReference change =
                        new DWORDByReference();
                    PointerByReference infoPointer = new PointerByReference();
                    success =
                        Winspool.INSTANCE.FindNextPrinterChangeNotification(
                            changeNotificationsHandle,
                            change,
                            options,
                            infoPointer);
                    if (!success) {
                        int errorCode = Kernel32.INSTANCE.GetLastError();
                        throw new RuntimeException("Failed to get printer " +
                            "change notification - " + errorCode);
                    }

                    System.out.println("Change - " +
                        String.format("0x%08X", change.getValue().longValue()));

                    if (infoPointer.getValue() != null) {
                        PRINTER_NOTIFY_INFO info =
                            Structure.newInstance(PRINTER_NOTIFY_INFO.class,
                                infoPointer.getValue());
                        info.read();

                        try {
                            if ((info.Flags & PRINTER_NOTIFY_INFO_DISCARDED) > 0) {
                                System.out.println("Some information was " +
                                    "discarded");
                            }

                            for (PRINTER_NOTIFY_INFO_DATA data : info.aData) {
                                System.out.println("Job ID - " + data.Id);
                                if (data.Field == JOB_NOTIFY_FIELD_PRINTER_NAME) {
                                    String printerName = new String(
                                        data.NotifyData.Data.pBuf.getByteArray(
                                            0,
                                            data.NotifyData.Data.cbBuf),
                                        UTF_16LE);
                                    System.out.println("Printer - " + printerName);
                                } else if (data.Field == JOB_NOTIFY_FIELD_STATUS) {
                                    System.out.println("Status - " +
                                        String.format("0x%08X", data.NotifyData.adwData[0]));
                                } else {
                                    String jobName = new String(
                                        data.NotifyData.Data.pBuf.getByteArray(
                                            0,
                                            data.NotifyData.Data.cbBuf),
                                        UTF_16LE);
                                    System.out.println("Job Name - " + jobName);
                                }
                            }
                        } finally {
                            Winspool.INSTANCE.FreePrinterNotifyInfo(info.getPointer());
                        }
                    }
                    System.out.println("==================================================");
                }
            } finally {
                Winspool.INSTANCE.FindClosePrinterChangeNotification(
                    changeNotificationsHandle);
            }
        } finally {
            Winspool.INSTANCE.ClosePrinter(printServerHandle.getValue());
        }
    }

    private boolean isValidHandle(HANDLE handle) {
        return handle != null && !handle.equals(Kernel32.INVALID_HANDLE_VALUE);
    }

    private void printJobInfo(JOB_INFO_1 jobInfo1) {
        FILETIME lpFileTime = new FILETIME();
        Kernel32.INSTANCE.SystemTimeToFileTime(jobInfo1.Submitted, lpFileTime);

        String info = "JobId: " + jobInfo1.JobId + "\n" + "pDatatype: "
                + jobInfo1.pDatatype + "\n" + "PagesPrinted: "
                + jobInfo1.PagesPrinted + "\n" + "pDocument: "
                + jobInfo1.pDocument + "\n" + "pMachineName: "
                + jobInfo1.pMachineName + "\n" + "Position: "
                + jobInfo1.Position + "\n" + "pPrinterName: "
                + jobInfo1.pPrinterName + "\n" + "Priority: "
                + jobInfo1.Priority + "\n" + "pStatus: " + jobInfo1.pStatus
                + "\n" + "pUserName: " + jobInfo1.pUserName + "\n" + "Status: "
                + jobInfo1.Status + "\n" + "TotalPages: " + jobInfo1.TotalPages
                + "\n" + "Submitted: " + DateFormat.getDateTimeInstance().format(lpFileTime.toDate());

        System.out.println(info);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.print("Please enter the name of a printer to monitor, " +
            "or press enter to monitor all printers: ");
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        String printer = reader.readLine();
        Win32SpoolMonitor monitor = new Win32SpoolMonitor();
        if (printer.isEmpty()) {
            monitor.monitorAllPrinters();
        } else {
            monitor.monitorPrinter(printer);
        }
    }
}
