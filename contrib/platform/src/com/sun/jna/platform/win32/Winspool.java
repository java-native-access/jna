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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Ported from Winspool.h. Windows SDK 6.0a
 * 
 * @author dblock[at]dblock.org
 */
public interface Winspool extends StdCallLibrary {

    public final static int CCHDEVICENAME = 32;

    public final static int PRINTER_CHANGE_ADD_PRINTER = 0x00000001;
    public final static int PRINTER_CHANGE_SET_PRINTER = 0x00000002;
    public final static int PRINTER_CHANGE_DELETE_PRINTER = 0x00000004;
    public final static int PRINTER_CHANGE_FAILED_CONNECTION_PRINTER = 0x00000008;
    public final static int PRINTER_CHANGE_PRINTER = 0x000000FF;
    public final static int PRINTER_CHANGE_ADD_JOB = 0x00000100;
    public final static int PRINTER_CHANGE_SET_JOB = 0x00000200;
    public final static int PRINTER_CHANGE_DELETE_JOB = 0x00000400;
    public final static int PRINTER_CHANGE_WRITE_JOB = 0x00000800;
    public final static int PRINTER_CHANGE_JOB = 0x0000FF00;
    public final static int PRINTER_CHANGE_ADD_FORM = 0x00010000;
    public final static int PRINTER_CHANGE_SET_FORM = 0x00020000;
    public final static int PRINTER_CHANGE_DELETE_FORM = 0x00040000;
    public final static int PRINTER_CHANGE_FORM = 0x00070000;
    public final static int PRINTER_CHANGE_ADD_PORT = 0x00100000;
    public final static int PRINTER_CHANGE_CONFIGURE_PORT = 0x00200000;
    public final static int PRINTER_CHANGE_DELETE_PORT = 0x00400000;
    public final static int PRINTER_CHANGE_PORT = 0x00700000;
    public final static int PRINTER_CHANGE_ADD_PRINT_PROCESSOR = 0x01000000;
    public final static int PRINTER_CHANGE_DELETE_PRINT_PROCESSOR = 0x04000000;
    public final static int PRINTER_CHANGE_PRINT_PROCESSOR = 0x07000000;
    public final static int PRINTER_CHANGE_SERVER = 0x08000000;
    public final static int PRINTER_CHANGE_ADD_PRINTER_DRIVER = 0x10000000;
    public final static int PRINTER_CHANGE_SET_PRINTER_DRIVER = 0x20000000;
    public final static int PRINTER_CHANGE_DELETE_PRINTER_DRIVER = 0x40000000;
    public final static int PRINTER_CHANGE_PRINTER_DRIVER = 0x70000000;
    public final static int PRINTER_CHANGE_TIMEOUT = 0x80000000;
    public final static int PRINTER_CHANGE_ALL_WIN7 = 0x7F77FFFF;
    public final static int PRINTER_CHANGE_ALL = 0x7777FFFF;

    int PRINTER_ENUM_DEFAULT = 0x00000001;
    int PRINTER_ENUM_LOCAL = 0x00000002;
    int PRINTER_ENUM_CONNECTIONS = 0x00000004;
    int PRINTER_ENUM_FAVORITE = 0x00000004;
    int PRINTER_ENUM_NAME = 0x00000008;
    int PRINTER_ENUM_REMOTE = 0x00000010;
    int PRINTER_ENUM_SHARED = 0x00000020;
    int PRINTER_ENUM_NETWORK = 0x00000040;

    int PRINTER_ENUM_EXPAND = 0x00004000;
    int PRINTER_ENUM_CONTAINER = 0x00008000;

    int PRINTER_ENUM_ICONMASK = 0x00ff0000;
    int PRINTER_ENUM_ICON1 = 0x00010000;
    int PRINTER_ENUM_ICON2 = 0x00020000;
    int PRINTER_ENUM_ICON3 = 0x00040000;
    int PRINTER_ENUM_ICON4 = 0x00080000;
    int PRINTER_ENUM_ICON5 = 0x00100000;
    int PRINTER_ENUM_ICON6 = 0x00200000;
    int PRINTER_ENUM_ICON7 = 0x00400000;
    int PRINTER_ENUM_ICON8 = 0x00800000;
    int PRINTER_ENUM_HIDE = 0x01000000;

    Winspool INSTANCE = (Winspool) Native.loadLibrary("Winspool.drv",
            Winspool.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * The EnumPrinters function enumerates available printers, print servers,
     * domains, or print providers.
     * 
     * @param Flags
     *            The types of print objects that the function should enumerate.
     * @param Name
     *            If Level is 1, Flags contains PRINTER_ENUM_NAME, and Name is
     *            non-NULL, then Name is a pointer to a null-terminated string
     *            that specifies the name of the object to enumerate. This
     *            string can be the name of a server, a domain, or a print
     *            provider. If Level is 1, Flags contains PRINTER_ENUM_NAME, and
     *            Name is NULL, then the function enumerates the available print
     *            providers. If Level is 1, Flags contains PRINTER_ENUM_REMOTE,
     *            and Name is NULL, then the function enumerates the printers in
     *            the user's domain. If Level is 2 or 5,Name is a pointer to a
     *            null-terminated string that specifies the name of a server
     *            whose printers are to be enumerated. If this string is NULL,
     *            then the function enumerates the printers installed on the
     *            local computer. If Level is 4, Name should be NULL. The
     *            function always queries on the local computer. When Name is
     *            NULL, setting Flags to PRINTER_ENUM_LOCAL |
     *            PRINTER_ENUM_CONNECTIONS enumerates printers that are
     *            installed on the local machine. These printers include those
     *            that are physically attached to the local machine as well as
     *            remote printers to which it has a network connection. When
     *            Name is not NULL, setting Flags to PRINTER_ENUM_LOCAL |
     *            PRINTER_ENUM_NAME enumerates the local printers that are
     *            installed on the server Name.
     * @param Level
     *            The type of data structures pointed to by pPrinterEnum. Valid
     *            values are 1, 2, 4, and 5, which correspond to the
     *            PRINTER_INFO_1, PRINTER_INFO_2 , PRINTER_INFO_4, and
     *            PRINTER_INFO_5 data structures.
     * @param pPrinterEnum
     *            A pointer to a buffer that receives an array of
     *            PRINTER_INFO_1, PRINTER_INFO_2, PRINTER_INFO_4, or
     *            PRINTER_INFO_5 structures. Each structure contains data that
     *            describes an available print object. If Level is 1, the array
     *            contains PRINTER_INFO_1 structures. If Level is 2, the array
     *            contains PRINTER_INFO_2 structures. If Level is 4, the array
     *            contains PRINTER_INFO_4 structures. If Level is 5, the array
     *            contains PRINTER_INFO_5 structures. The buffer must be large
     *            enough to receive the array of data structures and any strings
     *            or other data to which the structure members point. If the
     *            buffer is too small, the pcbNeeded parameter returns the
     *            required buffer size.
     * @param cbBuf
     *            The size, in bytes, of the buffer pointed to by pPrinterEnum.
     * @param pcbNeeded
     *            A pointer to a value that receives the number of bytes copied
     *            if the function succeeds or the number of bytes required if
     *            cbBuf is too small.
     * @param pcReturned
     *            A pointer to a value that receives the number of
     *            PRINTER_INFO_1, PRINTER_INFO_2 , PRINTER_INFO_4, or
     *            PRINTER_INFO_5 structures that the function returns in the
     *            array to which pPrinterEnum points.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero.
     */
    boolean EnumPrinters(int Flags, String Name, int Level,
            Pointer pPrinterEnum, int cbBuf, IntByReference pcbNeeded,
            IntByReference pcReturned);

    public static class PRINTER_INFO_1 extends Structure {
        public int Flags;
        public String pDescription;
        public String pName;
        public String pComment;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "Flags", "pDescription",
                    "pName", "pComment" });
        }

        public PRINTER_INFO_1() {
        }

        public PRINTER_INFO_1(int size) {
            super(new Memory(size));
        }
    }

    public static class PRINTER_INFO_4 extends Structure {
        public String pPrinterName;
        public String pServerName;
        public DWORD Attributes;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "pPrinterName", "pServerName",
                    "Attributes" });
        }

        public PRINTER_INFO_4() {
        }

        public PRINTER_INFO_4(int size) {
            super(new Memory(size));
        }
    }

    public class LPPRINTER_DEFAULTS extends Structure {
        public String pDatatype;
        PVOID pDevMode;
        int DesiredAccess;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "pDatatype", "pDevMode",
                    "DesiredAccess" });
        }
    }

    /**
     * The OpenPrinter function retrieves a handle to the specified printer or
     * print server or other types of handles in the print subsystem.
     * 
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd162751(v=vs.85).aspx">MSDN</a>
     * 
     * @param pPrinterName
     *            [in] A pointer to a null-terminated string that specifies the
     *            name of the printer or print server, the printer object, the
     *            XcvMonitor, or the XcvPort. For a printer object use:
     *            PrinterName, Job xxxx. For an XcvMonitor, use: ServerName,
     *            XcvMonitor MonitorName. For an XcvPort, use: ServerName,
     *            XcvPort PortName. If NULL, it indicates the local printer
     *            server.
     * @param phPrinter
     *            [out] A pointer to a variable that receives a handle (not
     *            thread safe) to the open printer or print server object. The
     *            phPrinter parameter can return an Xcv handle for use with the
     *            XcvData function. For more information about XcvData, see the
     *            DDK.
     * @param pDefault
     *            [in] A pointer to a PRINTER_DEFAULTS structure. This value can
     *            be NULL.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero.
     */
    boolean OpenPrinter(
    // _In_
            String pPrinterName,
            // _Out_
            HANDLEByReference phPrinter,
            // _In_
            LPPRINTER_DEFAULTS pDefault);

    /**
     * The FindFirstPrinterChangeNotification function creates a change
     * notification object and returns a handle to the object. You can then use
     * this handle in a call to one of the wait functions to monitor changes to
     * the printer or print server. The FindFirstPrinterChangeNotification call
     * specifies the type of changes to be monitored. You can specify a set of
     * conditions to monitor for changes, a set of printer information fields to
     * monitor, or both. A wait operation on the change notification handle
     * succeeds when one of the specified changes occurs in the specified
     * printer or print server. You then call the
     * FindNextPrinterChangeNotification function to retrieve information about
     * the change, and to reset the change notification object for use in the
     * next wait operation.
     * 
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd162722(v=vs.85).aspx">MSDN</a>
     * 
     * @param hPrinter
     *            [in] A handle to the printer or print server that you want to
     *            monitor. Use the OpenPrinter or AddPrinter function to
     *            retrieve a printer handle.
     * @param fdwFilter
     *            The conditions that will cause the change notification object
     *            to enter a signaled state. A change notification occurs when
     *            one or more of the specified conditions are met. The fdwFilter
     *            parameter can be zero if pPrinterNotifyOptions is non-NULL.
     * 
     * @param fdwOptions
     *            Reserved; must be zero. 
     * @param pPrinterNotifyOptions 
     *            [in, optional] A pointer to a PRINTER_NOTIFY_OPTIONS
     *            structure. The pTypes member of this structure is an array of 
     *            one or more PRINTER_NOTIFY_OPTIONS_TYPE structures, each of which
     *            specifies a printer information field to monitor. A change
     *            notification occurs when one or more of the specified fields
     *            changes. When a change occurs, the
     *            FindNextPrinterChangeNotification function can retrieve the
     *            new printer information. This parameter can be NULL if
     *            fdwFilter is nonzero. For a list of fields that can be
     *            monitored, see PRINTER_NOTIFY_OPTIONS_TYPE.
     * 
     * @return If the function succeeds, the return value is a handle to a
     *         change notification object associated with the specified printer
     *         or print server. If the function fails, the return value is
     *         INVALID_HANDLE_VALUE.
     */
    HANDLE FindFirstPrinterChangeNotification(
    // _In_
            HANDLE hPrinter, int fdwFilter, int fdwOptions,
            // _In_opt_
            LPVOID pPrinterNotifyOptions);

    /**
     * The FindNextPrinterChangeNotification function retrieves information
     * about the most recent change notification for a change notification
     * object associated with a printer or print server. Call this function when
     * a wait operation on the change notification object is satisfied. The
     * function also resets the change notification object to the not-signaled
     * state. You can then use the object in another wait operation to continue
     * monitoring the printer or print server. The operating system will set the
     * object to the signaled state the next time one of a specified set of
     * changes occurs to the printer or print server. The
     * FindFirstPrinterChangeNotification function creates the change
     * notification object and specifies the set of changes to be monitored.
     * 
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd162721(v=vs.85).aspx">MSDN</a>
     * 
     * @param hChange
     *            [in] A handle to a change notification object associated with
     *            a printer or print server. You obtain such a handle by calling
     *            the FindFirstPrinterChangeNotification function. The operating
     *            system sets this change notification object to the signaled
     *            state when it detects one of the changes specified in the
     *            object's change notification filter.
     * @param pdwChange
     *            [out, optional] A pointer to a variable whose bits are set to
     *            indicate the changes that occurred to cause the most recent
     *            notification. The bit flags that might be set correspond to
     *            those specified in the fdwFilter parameter of the
     *            FindFirstPrinterChangeNotification call. The system sets one
     *            or more of the following bit flags.
     * 
     * @param pPrinterNotifyOptions
     *            [in, optional] A pointer to a PRINTER_NOTIFY_OPTIONS
     *            structure. Set the Flags member of this structure to
     *            PRINTER_NOTIFY_OPTIONS_REFRESH, to cause the function to
     *            return the current data for all monitored printer information
     *            fields. The function ignores all other members of the
     *            structure. This parameter can be NULL.
     * 
     * @param ppPrinterNotifyInfo
     *            [out, optional] A pointer to a pointer variable that receives
     *            a pointer to a system-allocated, read-only buffer. Call the
     *            FreePrinterNotifyInfo function to free the buffer when you are
     *            finished with it. This parameter can be NULL if no information
     *            is required. The buffer contains a PRINTER_NOTIFY_INFO
     *            structure, which contains an array of PRINTER_NOTIFY_INFO_DATA
     *            structures. Each element of the array contains information
     *            about one of the fields specified in the pPrinterNotifyOptions
     *            parameter of the FindFirstPrinterChangeNotification call.
     *            Typically, the function provides data only for the fields that
     *            changed to cause the most recent notification. However, if the
     *            structure pointed to by the pPrinterNotifyOptions parameter
     *            specifies PRINTER_NOTIFY_OPTIONS_REFRESH, the function
     *            provides data for all monitored fields. If the
     *            PRINTER_NOTIFY_INFO_DISCARDED bit is set in the Flags member
     *            of the PRINTER_NOTIFY_INFO structure, an overflow or error
     *            occurred, and notifications may have been lost. In this case,
     *            no additional notifications will be sent until you make a
     *            second FindNextPrinterChangeNotification call that specifies
     *            PRINTER_NOTIFY_OPTIONS_REFRESH.
     * 
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero.
     */
    boolean FindNextPrinterChangeNotification(
    // _In_
            HANDLE hChange,
            // _Out_opt_
            DWORDByReference pdwChange,
            // _In_opt_
            LPVOID pPrinterNotifyOptions,
            // _Out_opt_
            LPVOID ppPrinterNotifyInfo);

    /**
     * The FindClosePrinterChangeNotification function closes a change
     * notification object created by calling the
     * FindFirstPrinterChangeNotification function. The printer or print server
     * associated with the change notification object will no longer be
     * monitored by that object.
     * 
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd162721(v=vs.85).aspx">MSDN</a>
     * 
     * @param hChange
     *            [in] A handle to the change notification object to be closed.
     *            This is a handle created by calling the
     *            FindFirstPrinterChangeNotification function.
     * 
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero.
     */
    boolean FindClosePrinterChangeNotification(
    // _In_
            HANDLE hChange);

    boolean EnumJobs(
    // _In_
            HANDLE hPrinter,
            // _In_
            int FirstJob,
            // _In_
            int NoJobs,
            // _In_
            int Level,
            // _Out_
            Pointer pJob,
            // _In_
            int cbBuf,
            // _Out_
            IntByReference pcbNeeded,
            // _Out_
            IntByReference pcReturned);

    public static class JOB_INFO_1 extends Structure {
        public int JobId;
        public String pPrinterName;
        public String pMachineName;
        public String pUserName;
        public String pDocument;
        public String pDatatype;
        public String pStatus;
        public int Status;
        public int Priority;
        public int Position;
        public int TotalPages;
        public int PagesPrinted;
        public SYSTEMTIME Submitted;

        protected List getFieldOrder() {
            return Arrays.asList(new String[] { "JobId", "pPrinterName",
                    "pMachineName", "pUserName", "pDocument", "pDatatype",
                    "pStatus", "Status", "Priority", "Position", "TotalPages",
                    "PagesPrinted", "Submitted" });
        }

        public JOB_INFO_1() {
        }

        public JOB_INFO_1(int size) {
            super(new Memory(size));
        }
    }
}
