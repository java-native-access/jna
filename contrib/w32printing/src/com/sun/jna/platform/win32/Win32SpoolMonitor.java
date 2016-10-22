/**
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

import java.text.DateFormat;

import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.Winspool.JOB_INFO_1;

public class Win32SpoolMonitor {

    public Win32SpoolMonitor() {

        String pPrinterName = "HP Color LaserJet CM4730 MFP PCL 6";
        HANDLEByReference phPrinter = new HANDLEByReference();
        Winspool.INSTANCE.OpenPrinter(pPrinterName, phPrinter, null);

        // Get change notification handle for the printer
        HANDLE chgObject = Winspool.INSTANCE
                .FindFirstPrinterChangeNotification(phPrinter.getValue(),
                        Winspool.PRINTER_CHANGE_JOB, 0, null);

        if (chgObject != null) {
            while (true) {
                // Wait for the change notification
                Kernel32.INSTANCE.WaitForSingleObject(chgObject,
                        WinBase.INFINITE);

                DWORDByReference pdwChange = new DWORDByReference();
                boolean fcnreturn = Winspool.INSTANCE
                        .FindNextPrinterChangeNotification(chgObject,
                                pdwChange, null, null);

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
    public static void main(String[] args) {
        new Win32SpoolMonitor();
    }
}
