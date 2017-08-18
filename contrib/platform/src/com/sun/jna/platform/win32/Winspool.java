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

import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.LPVOID;
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

    Winspool INSTANCE = Native.loadLibrary("Winspool.drv", Winspool.class, W32APIOptions.DEFAULT_OPTIONS);

	public static final int CCHDEVICENAME = 32;

	public static final int PRINTER_STATUS_PAUSED = 0x00000001;
	public static final int PRINTER_STATUS_ERROR = 0x00000002;
	public static final int PRINTER_STATUS_PENDING_DELETION = 0x00000004;
	public static final int PRINTER_STATUS_PAPER_JAM = 0x00000008;
	public static final int PRINTER_STATUS_PAPER_OUT = 0x00000010;
	public static final int PRINTER_STATUS_MANUAL_FEED = 0x00000020;
	public static final int PRINTER_STATUS_PAPER_PROBLEM = 0x00000040;
	public static final int PRINTER_STATUS_OFFLINE = 0x00000080;
	public static final int PRINTER_STATUS_IO_ACTIVE = 0x00000100;
	public static final int PRINTER_STATUS_BUSY = 0x00000200;
	public static final int PRINTER_STATUS_PRINTING = 0x00000400;
	public static final int PRINTER_STATUS_OUTPUT_BIN_FULL = 0x00000800;
	public static final int PRINTER_STATUS_NOT_AVAILABLE = 0x00001000;
	public static final int PRINTER_STATUS_WAITING = 0x00002000;
	public static final int PRINTER_STATUS_PROCESSING = 0x00004000;
	public static final int PRINTER_STATUS_INITIALIZING = 0x00008000;
	public static final int PRINTER_STATUS_WARMING_UP = 0x00010000;
	public static final int PRINTER_STATUS_TONER_LOW = 0x00020000;
	public static final int PRINTER_STATUS_NO_TONER = 0x00040000;
	public static final int PRINTER_STATUS_PAGE_PUNT = 0x00080000;
	public static final int PRINTER_STATUS_USER_INTERVENTION = 0x00100000;
	public static final int PRINTER_STATUS_OUT_OF_MEMORY = 0x00200000;
	public static final int PRINTER_STATUS_DOOR_OPEN = 0x00400000;
	public static final int PRINTER_STATUS_SERVER_UNKNOWN = 0x00800000;
	public static final int PRINTER_STATUS_POWER_SAVE = 0x01000000;

	public static final int PRINTER_ATTRIBUTE_QUEUED = 0x00000001;
	public static final int PRINTER_ATTRIBUTE_DIRECT = 0x00000002;
	public static final int PRINTER_ATTRIBUTE_DEFAULT = 0x00000004;
	public static final int PRINTER_ATTRIBUTE_SHARED = 0x00000008;
	public static final int PRINTER_ATTRIBUTE_NETWORK = 0x00000010;
	public static final int PRINTER_ATTRIBUTE_HIDDEN = 0x00000020;
	public static final int PRINTER_ATTRIBUTE_LOCAL = 0x00000040;
	public static final int PRINTER_ATTRIBUTE_ENABLE_DEVQ = 0x00000080;
	public static final int PRINTER_ATTRIBUTE_KEEPPRINTEDJOBS = 0x00000100;
	public static final int PRINTER_ATTRIBUTE_DO_COMPLETE_FIRST = 0x00000200;
	public static final int PRINTER_ATTRIBUTE_WORK_OFFLINE = 0x00000400;
	public static final int PRINTER_ATTRIBUTE_ENABLE_BIDI = 0x00000800;
	public static final int PRINTER_ATTRIBUTE_RAW_ONLY = 0x00001000;
	public static final int PRINTER_ATTRIBUTE_PUBLISHED = 0x00002000;
	public static final int PRINTER_ATTRIBUTE_FAX = 0x00004000;
	public static final int PRINTER_ATTRIBUTE_TS = 0x00008000;
	public static final int PRINTER_ATTRIBUTE_PUSHED_USER = 0x00020000;
	public static final int PRINTER_ATTRIBUTE_PUSHED_MACHINE = 0x00040000;
	public static final int PRINTER_ATTRIBUTE_MACHINE = 0x00080000;
	public static final int PRINTER_ATTRIBUTE_FRIENDLY_NAME = 0x00100000;
	public static final int PRINTER_ATTRIBUTE_TS_GENERIC_DRIVER = 0x00200000;

	public static final int PRINTER_CHANGE_ADD_PRINTER = 0x00000001;
	public static final int PRINTER_CHANGE_SET_PRINTER = 0x00000002;
	public static final int PRINTER_CHANGE_DELETE_PRINTER = 0x00000004;
	public static final int PRINTER_CHANGE_FAILED_CONNECTION_PRINTER = 0x00000008;
	public static final int PRINTER_CHANGE_PRINTER = 0x000000FF;
	public static final int PRINTER_CHANGE_ADD_JOB = 0x00000100;
	public static final int PRINTER_CHANGE_SET_JOB = 0x00000200;
	public static final int PRINTER_CHANGE_DELETE_JOB = 0x00000400;
	public static final int PRINTER_CHANGE_WRITE_JOB = 0x00000800;
	public static final int PRINTER_CHANGE_JOB = 0x0000FF00;
	public static final int PRINTER_CHANGE_ADD_FORM = 0x00010000;
	public static final int PRINTER_CHANGE_SET_FORM = 0x00020000;
	public static final int PRINTER_CHANGE_DELETE_FORM = 0x00040000;
	public static final int PRINTER_CHANGE_FORM = 0x00070000;
	public static final int PRINTER_CHANGE_ADD_PORT = 0x00100000;
	public static final int PRINTER_CHANGE_CONFIGURE_PORT = 0x00200000;
	public static final int PRINTER_CHANGE_DELETE_PORT = 0x00400000;
	public static final int PRINTER_CHANGE_PORT = 0x00700000;
	public static final int PRINTER_CHANGE_ADD_PRINT_PROCESSOR = 0x01000000;
	public static final int PRINTER_CHANGE_DELETE_PRINT_PROCESSOR = 0x04000000;
	public static final int PRINTER_CHANGE_PRINT_PROCESSOR = 0x07000000;
	public static final int PRINTER_CHANGE_SERVER = 0x08000000;
	public static final int PRINTER_CHANGE_ADD_PRINTER_DRIVER = 0x10000000;
	public static final int PRINTER_CHANGE_SET_PRINTER_DRIVER = 0x20000000;
	public static final int PRINTER_CHANGE_DELETE_PRINTER_DRIVER = 0x40000000;
	public static final int PRINTER_CHANGE_PRINTER_DRIVER = 0x70000000;
	public static final int PRINTER_CHANGE_TIMEOUT = 0x80000000;
	public static final int PRINTER_CHANGE_ALL_WIN7 = 0x7F77FFFF;
	public static final int PRINTER_CHANGE_ALL = 0x7777FFFF;

	public static final int PRINTER_ENUM_DEFAULT = 0x00000001;
	public static final int PRINTER_ENUM_LOCAL = 0x00000002;
	public static final int PRINTER_ENUM_CONNECTIONS = 0x00000004;
	public static final int PRINTER_ENUM_FAVORITE = 0x00000004;
	public static final int PRINTER_ENUM_NAME = 0x00000008;
	public static final int PRINTER_ENUM_REMOTE = 0x00000010;
	public static final int PRINTER_ENUM_SHARED = 0x00000020;
	public static final int PRINTER_ENUM_NETWORK = 0x00000040;

	public static final int PRINTER_ENUM_EXPAND = 0x00004000;
	public static final int PRINTER_ENUM_CONTAINER = 0x00008000;

	public static final int PRINTER_ENUM_ICONMASK = 0x00ff0000;
	public static final int PRINTER_ENUM_ICON1 = 0x00010000;
	public static final int PRINTER_ENUM_ICON2 = 0x00020000;
	public static final int PRINTER_ENUM_ICON3 = 0x00040000;
	public static final int PRINTER_ENUM_ICON4 = 0x00080000;
	public static final int PRINTER_ENUM_ICON5 = 0x00100000;
	public static final int PRINTER_ENUM_ICON6 = 0x00200000;
	public static final int PRINTER_ENUM_ICON7 = 0x00400000;
	public static final int PRINTER_ENUM_ICON8 = 0x00800000;
	public static final int PRINTER_ENUM_HIDE = 0x01000000;

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
	 * 
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd162692(v=vs.85).aspx">
	 *      EnumPrinters function</a>
	 */
    boolean EnumPrinters(int Flags, String Name, int Level,
            Pointer pPrinterEnum, int cbBuf, IntByReference pcbNeeded,
            IntByReference pcReturned);
    
    /**
	 * The GetPrinter function retrieves information about a specified printer.
	 * 
	 * @param hPrinter
	 *            A handle to the printer for which the function retrieves
	 *            information. Use the OpenPrinter or AddPrinter function to
	 *            retrieve a printer handle.
	 * @param Level
	 *            The level or type of structure that the function stores into
	 *            the buffer pointed to by pPrinter. This value can be 1, 2, 3,
	 *            4, 5, 6, 7, 8 or 9.
	 * @param pPrinter
	 *            A pointer to a buffer that receives a structure containing
	 *            information about the specified printer. The buffer must be
	 *            large enough to receive the structure and any strings or other
	 *            data to which the structure members point. If the buffer is
	 *            too small, the pcbNeeded parameter returns the required buffer
	 *            size. The type of structure is determined by the value of
	 *            Level.
	 * @param cbBuf
	 *            The size, in bytes, of the buffer pointed to by pPrinter.
	 * @param pcbNeeded
	 *            A pointer to a variable that the function sets to the size, in
	 *            bytes, of the printer information. If cbBuf is smaller than
	 *            this value, GetPrinter fails, and the value represents the
	 *            required buffer size. If cbBuf is equal to or greater than
	 *            this value, GetPrinter succeeds, and the value represents the
	 *            number of bytes stored in the buffer.
	 * @return If the function succeeds, the return value is a nonzero value. If
	 *         the function fails, the return value is zero.
	 * 
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd144911(v=vs.85).aspx">
	 *      GetPrinter function</a>
	 */
	boolean GetPrinter(HANDLE hPrinter, int Level, Pointer pPrinter, int cbBuf, IntByReference pcbNeeded);

	/**
	 * The PRINTER_INFO_1 structure specifies general printer information.
	 *
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd162844(v=vs.85).aspx">
	 *      PRINTER_INFO_1 structure</a>
	 */
	public static class PRINTER_INFO_1 extends Structure {
		
		public static final List<String> FIELDS = createFieldsOrder("Flags", "pDescription", "pName", "pComment");
		
		/**
		 * Specifies information about the returned data. Following are the
		 * values for this member.
		 */
		public int Flags;
		/**
		 * Pointer to a null-terminated string that describes the contents of
		 * the structure.
		 */
		public String pDescription;
		/**
		 * Pointer to a null-terminated string that names the contents of the
		 * structure.
		 */
		public String pName;
		/**
		 * Pointer to a null-terminated string that contains additional data
		 * describing the structure.
		 */
		public String pComment;

		public PRINTER_INFO_1() {
            super();
        }

        public PRINTER_INFO_1(int size) {
            super(new Memory(size));
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
	
	/**
	 * The PRINTER_INFO_2 structure specifies detailed printer information.
	 * 
	 * @author Ivan Ridao Freitas, Padrus
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd162845(v=vs.85).aspx">
	 *      PRINTER_INFO_2 structure</a>
	 */
	public static class PRINTER_INFO_2 extends Structure {
		
		public static final List<String> FIELDS = createFieldsOrder("pServerName", "pPrinterName", "pShareName",
				"pPortName", "pDriverName", "pComment", "pLocation", "pDevMode", "pSepFile", "pPrintProcessor",
				"pDatatype", "pParameters", "pSecurityDescriptor", "Attributes", "Priority", "DefaultPriority",
				"StartTime", "UntilTime", "Status", "cJobs", "AveragePPM");
		
		/**
		 * A pointer to a null-terminated string identifying the server that
		 * controls the printer. If this string is NULL, the printer is
		 * controlled locally.
		 */
		public String pServerName;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * printer.
		 */
		public String pPrinterName;
		/**
		 * A pointer to a null-terminated string that identifies the share point
		 * for the printer. (This string is used only if the
		 * PRINTER_ATTRIBUTE_SHARED constant was set for the Attributes member.)
		 */
		public String pShareName;
		/**
		 * A pointer to a null-terminated string that identifies the port(s)
		 * used to transmit data to the printer. If a printer is connected to
		 * more than one port, the names of each port must be separated by
		 * commas (for example, "LPT1:,LPT2:,LPT3:").
		 */
		public String pPortName;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * printer driver.
		 */
		public String pDriverName;
		/**
		 * A pointer to a null-terminated string that provides a brief
		 * description of the printer.
		 */
		public String pComment;
		/**
		 * A pointer to a null-terminated string that specifies the physical
		 * location of the printer (for example, "Bldg. 38, Room 1164").
		 */
		public String pLocation;
		/**
		 * A pointer to a DEVMODE structure that defines default printer data
		 * such as the paper orientation and the resolution.
		 */
		public INT_PTR pDevMode;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * file used to create the separator page. This page is used to separate
		 * print jobs sent to the printer.
		 */
		public String pSepFile;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * print processor used by the printer. You can use the
		 * EnumPrintProcessors function to obtain a list of print processors
		 * installed on a server.
		 */
		public String pPrintProcessor;
		/**
		 * A pointer to a null-terminated string that specifies the data type
		 * used to record the print job. You can use the
		 * EnumPrintProcessorDatatypes function to obtain a list of data types
		 * supported by a specific print processor.
		 */
		public String pDatatype;
		/**
		 * A pointer to a null-terminated string that specifies the default
		 * print-processor parameters.
		 */
		public String pParameters;
		/**
		 * A pointer to a SECURITY_DESCRIPTOR structure for the printer. This
		 * member may be NULL.
		 */
		public INT_PTR pSecurityDescriptor;
		/**
		 * The printer attributes. This member can be any reasonable combination
		 * of the values PRINTER_ATTRIBUTE_XXX.
		 */
		public int Attributes;
		/**
		 * A priority value that the spooler uses to route print jobs.
		 */
		public int Priority;
		/**
		 * The default priority value assigned to each print job.
		 */
		public int DefaultPriority;
		/**
		 * The earliest time at which the printer will print a job. This value
		 * is expressed as minutes elapsed since 12:00 AM GMT (Greenwich Mean
		 * Time).
		 */
		public int StartTime;
		/**
		 * The latest time at which the printer will print a job. This value is
		 * expressed as minutes elapsed since 12:00 AM GMT (Greenwich Mean
		 * Time).
		 */
		public int UntilTime;
		/**
		 * The printer status. This member can be any reasonable combination of
		 * the values PRINTER_STATUS_XXX.
		 */
		public int Status;
		/**
		 * The number of print jobs that have been queued for the printer.
		 */
		public int cJobs;
		/**
		 * The average number of pages per minute that have been printed on the
		 * printer.
		 */
		public int AveragePPM;

		public PRINTER_INFO_2() {
			super();
		}

		public PRINTER_INFO_2(int size) {
			super(new Memory(size));
		}
		
		@Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

		/**
		 * Checks if the printer attributes have one of the values PRINTER_ATTRIBUTE_XXX.
		 */
		public boolean hasAttribute(int value) {
			return (Attributes & value) == value;
		}
	}

	/**
	 * The PRINTER_INFO_4 structure specifies general printer information.
	 * <p>
	 * The structure can be used to retrieve minimal printer information on a
	 * call to EnumPrinters. Such a call is a fast and easy way to retrieve the
	 * names and attributes of all locally installed printers on a system and
	 * all remote printer connections that a user has established.
	 * 
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd162847(v=vs.85).aspx">
	 *      PRINTER_INFO_4 structure</a>
	 */
	public static class PRINTER_INFO_4 extends Structure {
		
		public static final List<String> FIELDS = createFieldsOrder("pPrinterName", "pServerName", "Attributes");
		
		/**
		 * Pointer to a null-terminated string that specifies the name of the
		 * printer (local or remote).
		 */
		public String pPrinterName;
		/**
		 * Pointer to a null-terminated string that is the name of the server.
		 */
		public String pServerName;
		/**
		 * Specifies information about the returned data.
		 */
		public DWORD Attributes;

		public PRINTER_INFO_4() {
            super();
        }

        public PRINTER_INFO_4(int size) {
            super(new Memory(size));
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The PRINTER_DEFAULTS structure specifies the default data type,
     * environment, initialization data, and access rights for a printer.
     *
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd162839(v=vs.85).aspx">PRINTER_DEFAULTS structure</a>
     */
    public class LPPRINTER_DEFAULTS extends Structure {

        public static final List<String> FIELDS = createFieldsOrder("pDatatype", "pDevMode", "DesiredAccess");

        /**
         * Pointer to a null-terminated string that specifies the default data
         * type for a printer.
         */
        public String pDatatype;
        /**
         * Pointer to a DEVMODE structure that identifies the default
         * environment and initialization data for a printer.
         */
        public Pointer pDevMode;
        /**
         * Specifies desired access rights for a printer. The OpenPrinter
         * function uses this member to set access rights to the printer. These
         * rights can affect the operation of the SetPrinter and DeletePrinter
         * functions.
         */
        public int DesiredAccess;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The OpenPrinter function retrieves a handle to the specified printer or
     * print server or other types of handles in the print subsystem.
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
     * 
     * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd162751(v=vs.85).aspx">OpenPrinter function</a>
     */
    boolean OpenPrinter(
    	    // _In_
            String pPrinterName,
            // _Out_
            HANDLEByReference phPrinter,
            // _In_
            LPPRINTER_DEFAULTS pDefault);

	/**
	 * The ClosePrinter function closes the specified printer object.<br>
	 * Note This is a blocking or synchronous function and might not return
	 * immediately.<br>
	 * How quickly this function returns depends on run-time factors such as
	 * network status, print server configuration, and printer driver
	 * implementation-factors that are difficult to predict when writing an
	 * application. Calling this function from a thread that manages interaction
	 * with the user interface could make the application appear to be
	 * unresponsive.
	 * 
	 * When the ClosePrinter function returns, the handle hPrinter is invalid,
	 * regardless of whether the function has succeeded or failed.
	 * 
	 * @param hPrinter
	 *            A handle to the printer object to be closed. This handle is
	 *            returned by the OpenPrinter or AddPrinter function.
	 * @return If the function succeeds, the return value is a nonzero value. If
	 *         the function fails, the return value is zero.
	 * 
	 * @see <a href=
	 *      "http://msdn.microsoft.com/en-us/library/windows/desktop/dd162751(v=vs.85).aspx">
	 *      ClosePrinter function</a>
	 */
	boolean ClosePrinter(HANDLE hPrinter);

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
     * 
	 * @see <a href=
	 *      "http://msdn.microsoft.com/en-us/library/windows/desktop/dd162722(v=vs.85).aspx">
	 *      FindFirstPrinterChangeNotification function</a>
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
     * 
	 * @see <a href=
	 *      "http://msdn.microsoft.com/en-us/library/windows/desktop/dd162721(v=vs.85).aspx">
	 *      FindClosePrinterChangeNotification function</a>
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
     * @param hChange
     *            [in] A handle to the change notification object to be closed.
     *            This is a handle created by calling the
     *            FindFirstPrinterChangeNotification function.
     * 
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero.
     * 
	 * @see <a href=
	 *      "http://msdn.microsoft.com/en-us/library/windows/desktop/dd162721(v=vs.85).aspx">
	 *      FindClosePrinterChangeNotification function</a>
	 */
    boolean FindClosePrinterChangeNotification(
    	    // _In_
            HANDLE hChange);

	/**
	 * The EnumJobs function retrieves information about a specified set of
	 * print jobs for a specified printer.
	 * 
	 * @param hPrinter
	 *            A handle to the printer object whose print jobs the function
	 *            enumerates. Use the OpenPrinter or AddPrinter function to
	 *            retrieve a printer handle.
	 * @param FirstJob
	 *            The zero-based position within the print queue of the first
	 *            print job to enumerate. For example, a value of 0 specifies
	 *            that enumeration should begin at the first print job in the
	 *            print queue; a value of 9 specifies that enumeration should
	 *            begin at the tenth print job in the print queue.
	 * @param NoJobs
	 *            The total number of print jobs to enumerate.
	 * @param Level
	 *            The type of information returned in the pJob buffer.
	 * @param pJob
	 *            A pointer to a buffer that receives an array of JOB_INFO_1,
	 *            JOB_INFO_2, or JOB_INFO_3 structures. The buffer must be large
	 *            enough to receive the array of structures and any strings or
	 *            other data to which the structure members point.
	 * @param cbBuf
	 *            The size, in bytes, of the pJob buffer.
	 * @param pcbNeeded
	 *            A pointer to a variable that receives the number of bytes
	 *            copied if the function succeeds. If the function fails, the
	 *            variable receives the number of bytes required.
	 * @param pcReturned
	 *            A pointer to a variable that receives the number of
	 *            JOB_INFO_1, JOB_INFO_2, or JOB_INFO_3 structures returned in
	 *            the pJob buffer.
	 * @return If the function succeeds, the return value is a nonzero value. If
	 *         the function fails, the return value is zero.
	 * 
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd162625(v=vs.85).aspx">
	 *      EnumJobs function</a>
	 */
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

	/**
	 * The JOB_INFO_1 structure specifies print-job information such as the
	 * job-identifier value, the name of the printer for which the job is
	 * spooled, the name of the machine that created the print job, the name of
	 * the user that owns the print job, and so on.
	 *
	 * @see <a href=
	 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/dd145019(v=vs.85).aspx">
	 *      JOB_INFO_1 structure</a>
	 */
	public static class JOB_INFO_1 extends Structure {
		
		public static final List<String> FIELDS = createFieldsOrder(
                "JobId", "pPrinterName",
                "pMachineName", "pUserName", "pDocument", "pDatatype",
                "pStatus", "Status", "Priority", "Position", "TotalPages",
                "PagesPrinted", "Submitted");
		
		/**
		 * A job identifier.
		 */
		public int JobId;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * printer for which the job is spooled.
		 */
		public String pPrinterName;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * machine that created the print job.
		 */
		public String pMachineName;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * user that owns the print job.
		 */
		public String pUserName;
		/**
		 * A pointer to a null-terminated string that specifies the name of the
		 * print job (for example, "MS-WORD: Review.doc").
		 */
		public String pDocument;
		/**
		 * A pointer to a null-terminated string that specifies the type of data
		 * used to record the print job.
		 */
		public String pDatatype;
		/**
		 * A pointer to a null-terminated string that specifies the status of
		 * the print job. This member should be checked prior to Status and, if
		 * pStatus is NULL, the status is defined by the contents of the Status
		 * member.
		 */
		public String pStatus;
		/**
		 * The job status. The value of this member can be zero or a combination
		 * of one or more of the following values. A value of zero indicates
		 * that the print queue was paused after the document finished spooling.
		 */
		public int Status;
		/**
		 * The job priority. This member can be one of the following values or
		 * in the range between 1 through 99 (MIN_PRIORITY through
		 * MAX_PRIORITY).
		 */
		public int Priority;
		/**
		 * The job's position in the print queue.
		 */
		public int Position;
		/**
		 * The total number of pages that the document contains. This value may
		 * be zero if the print job does not contain page delimiting
		 * information.
		 */
		public int TotalPages;
		/**
		 * The number of pages that have printed. This value may be zero if the
		 * print job does not contain page delimiting information.
		 */
		public int PagesPrinted;
		/**
		 * A SYSTEMTIME structure that specifies the time that this document was
		 * spooled.
		 * <p>
		 * This time value is in Universal Time Coordinate (UTC) format. You
		 * should convert it to a local time value before displaying it. You can
		 * use the FileTimeToLocalFileTime function to perform the conversion.
		 */
		public SYSTEMTIME Submitted;

        public JOB_INFO_1() {
            super();
        }

        public JOB_INFO_1(int size) {
            super(new Memory(size));
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
