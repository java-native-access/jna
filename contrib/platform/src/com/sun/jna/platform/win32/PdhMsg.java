/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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

/**
 * PDH specific error codes
 * @author Lyor Goldstein
 * @see <A HREF="https:/**msdn.microsoft.com/en-us/library/windows/desktop/aa373046(v=vs.85).aspx">Performance Data Helper Error Codes</A>
 */
public interface PdhMsg {
    /** Returned data is valid. */
    public static final int PDH_CSTATUS_VALID_DATA = 0x00000000;
    /** Return data value is valid and different from the last sample. */
    public static final int PDH_CSTATUS_NEW_DATA = 0x00000001;
    /** Unable to connect to the specified computer, or the computer is offline. */
    public static final int PDH_CSTATUS_NO_MACHINE = 0x800007D0;
    /** The specified instance is not present. */
    public static final int PDH_CSTATUS_NO_INSTANCE = 0x800007D1;
    /** There is more data to return than would fit in the supplied buffer. Allocate a larger buffer and call the function again. */
    public static final int PDH_MORE_DATA = 0x800007D2;
    /** The data item has been added to the query but has not been validated nor accessed. No other status information on this data item is available. */
    public static final int PDH_CSTATUS_ITEM_NOT_VALIDATED = 0x800007D3;
    /** The selected operation should be retried. */
    public static final int PDH_RETRY = 0x800007D4;
    /** No data to return. */
    public static final int PDH_NO_DATA = 0x800007D5;
    /** A counter with a negative denominator value was detected. */
    public static final int PDH_CALC_NEGATIVE_DENOMINATOR = 0x800007D6;
    /** A counter with a negative time base value was detected. */
    public static final int PDH_CALC_NEGATIVE_TIMEBASE = 0x800007D7;
    /** A counter with a negative value was detected. */
    public static final int PDH_CALC_NEGATIVE_VALUE = 0x800007D8;
    /** The user canceled the dialog box. */
    public static final int PDH_DIALOG_CANCELLED = 0x800007D9;
    /** The end of the log file was reached. */
    public static final int PDH_END_OF_LOG_FILE = 0x800007DA;
    /** A time-out occurred while waiting for the asynchronous counter collection thread to end. */
    public static final int PDH_ASYNC_QUERY_TIMEOUT = 0x800007DB;
    /** Cannot change set default real-time data source. */
    public static final int PDH_CANNOT_SET_DEFAULT_REALTIME_DATASOURCE = 0x800007DC;
    /** The specified object is not found on the system. */
    public static final int PDH_CSTATUS_NO_OBJECT = 0xC0000BB8;
    /** The specified counter could not be found. */
    public static final int PDH_CSTATUS_NO_COUNTER = 0xC0000BB9;
    /** The returned data is not valid. */
    public static final int PDH_CSTATUS_INVALID_DATA= 0xC0000BBA;
    /** A PDH function could not allocate enough temporary memory to complete the operation. */
    public static final int PDH_MEMORY_ALLOCATION_FAILURE = 0xC0000BBB;
    /** The handle is not a valid PDH object. */
    public static final int PDH_INVALID_HANDLE = 0xC0000BBC;
    /** A required argument is missing or incorrect. */
    public static final int PDH_INVALID_ARGUMENT = 0xC0000BBD;
    /** Unable to find the specified function. */
    public static final int PDH_FUNCTION_NOT_FOUND = 0xC0000BBE;
    /** No counter was specified. */
    public static final int PDH_CSTATUS_NO_COUNTERNAME = 0xC0000BBF;
    /** Unable to parse the counter path. Check the format and syntax of the specified path. */
    public static final int PDH_CSTATUS_BAD_COUNTERNAME = 0xC0000BC0;
    /** The buffer passed by the caller is not valid. */
    public static final int PDH_INVALID_BUFFER = 0xC0000BC1;
    /** The requested data is larger than the buffer supplied. */
    public static final int PDH_INSUFFICIENT_BUFFER = 0xC0000BC2;
    /** Unable to connect to the requested computer. */
    public static final int PDH_CANNOT_CONNECT_MACHINE = 0xC0000BC3;
    /** The specified counter path could not be interpreted. */
    public static final int PDH_INVALID_PATH = 0xC0000BC4;
    /** The instance name could not be read from the specified counter path. */
    public static final int PDH_INVALID_INSTANCE = 0xC0000BC5;
    /** The data is not valid. */
    public static final int PDH_INVALID_DATA = 0xC0000BC6;
    /** The dialog box data block was missing or not valid. */
    public static final int PDH_NO_DIALOG_DATA = 0xC0000BC7;
    /** Unable to read the counter and/or help text from the specified computer. */
    public static final int PDH_CANNOT_READ_NAME_STRINGS = 0xC0000BC8;
    /** Unable to create the specified log file. */
    public static final int PDH_LOG_FILE_CREATE_ERROR = 0xC0000BC9;
    /** Unable to open the specified log file. */
    public static final int PDH_LOG_FILE_OPEN_ERROR = 0xC0000BCA;
    /** The specified log file type has not been installed on this system. */
    public static final int PDH_LOG_TYPE_NOT_FOUND = 0xC0000BCB;
    /** No more data is available. */
    public static final int PDH_NO_MORE_DATA = 0xC0000BCC;
    /** The specified record was not found in the log file. */
    public static final int PDH_ENTRY_NOT_IN_LOG_FILE = 0xC0000BCD;
    /** The specified data source is a log file. */
    public static final int PDH_DATA_SOURCE_IS_LOG_FILE = 0xC0000BCE;
    /** The specified data source is the current activity. */
    public static final int PDH_DATA_SOURCE_IS_REAL_TIME = 0xC0000BCF;
    /** The log file header could not be read. */
    public static final int PDH_UNABLE_READ_LOG_HEADER = 0xC0000BD0;
    /** Unable to find the specified file. */
    public static final int PDH_FILE_NOT_FOUND = 0xC0000BD1;
    /** There is already a file with the specified file name. */
    public static final int PDH_FILE_ALREADY_EXISTS = 0xC0000BD2;
    /** The function referenced has not been implemented. */
    public static final int PDH_NOT_IMPLEMENTED = 0xC0000BD3;
    /** Unable to find the specified string in the list of performance name and help text strings. */
    public static final int PDH_STRING_NOT_FOUND = 0xC0000BD4;
    /** Unable to map to the performance counter name data files. */
    public static final int PDH_UNABLE_MAP_NAME_FILES = 0x80000BD5;
    /** The format of the specified log file is not recognized by the PDH DLL. */
    public static final int PDH_UNKNOWN_LOG_FORMAT = 0xC0000BD6;
    /** The specified Log Service command value is not recognized. */
    public static final int PDH_UNKNOWN_LOGSVC_COMMAND = 0xC0000BD7;
    /** The specified query from the Log Service could not be found or could not be opened. */
    public static final int PDH_LOGSVC_QUERY_NOT_FOUND = 0xC0000BD8;
    /** The Performance Data Log Service key could not be opened. */
    public static final int PDH_LOGSVC_NOT_OPENED = 0xC0000BD9;
    /** An error occurred while accessing the WBEM data store. */
    public static final int PDH_WBEM_ERROR = 0xC0000BDA;
    /** Unable to access the desired computer or service. */
    public static final int PDH_ACCESS_DENIED = 0xC0000BDB;
    /** The maximum log file size specified is too small to log the selected counters. */
    public static final int PDH_LOG_FILE_TOO_SMALL = 0xC0000BDC;
    /** Cannot connect to ODBC DataSource Name. */
    public static final int PDH_INVALID_DATASOURCE = 0xC0000BDD;
    /** SQL Data base does not contain a valid set of tables for Perfmon. */
    public static final int PDH_INVALID_SQLDB = 0xC0000BDE;
    /** No counters were found for this Perfmon SQL Log Set. */
    public static final int PDH_NO_COUNTERS = 0xC0000BDF;
    /** Call to SQLAllocStmt failed with %1. */
    public static final int PDH_SQL_ALLOC_FAILED = 0xC0000BE0;
    /** Call to SQLAllocConnect failed with %1. */
    public static final int PDH_SQL_ALLOCCON_FAILED = 0xC0000BE1;
    /** Call to SQLExecDirect failed with %1. */
    public static final int PDH_SQL_EXEC_DIRECT_FAILED = 0xC0000BE2;
    /** Call to SQLFetch failed with %1. */
    public static final int PDH_SQL_FETCH_FAILED = 0xC0000BE3;
    /** Call to SQLRowCount failed with %1. */
    public static final int PDH_SQL_ROWCOUNT_FAILED = 0xC0000BE4;
    /** Call to SQLMoreResults failed with %1.
    public static final int PDH_SQL_MORE_RESULTS_FAILED = 0xC0000BE5;
    /** Call to SQLConnect failed with %1. */
    public static final int PDH_SQL_CONNECT_FAILED = 0xC0000BE6;
    /** Call to SQLBindCol failed with %1. */
    public static final int PDH_SQL_BIND_FAILED = 0xC0000BE7;
    /** Unable to connect to the WMI server on requested computer. */
    public static final int PDH_CANNOT_CONNECT_WMI_SERVER = 0xC0000BE8;
    /** Collection &quot;%1!s!&quot; is already running. */
    public static final int PDH_PLA_COLLECTION_ALREADY_RUNNING = 0xC0000BE9;
    /** The specified start time is after the end time. */
    public static final int PDH_PLA_ERROR_SCHEDULE_OVERLAP = 0xC0000BEA;
    /** Collection &quot;%1!s!&quot; does not exist. */
    public static final int PDH_PLA_COLLECTION_NOT_FOUND = 0xC0000BEB;
    /** The specified end time has already elapsed. */
    public static final int PDH_PLA_ERROR_SCHEDULE_ELAPSED = 0xC0000BEC;
    /** Collection &quot;%1!s!&quot; did not start; check the application event log for any errors. */
    public static final int PDH_PLA_ERROR_NOSTART = 0xC0000BED;
    /** Collection &quot;%1!s!&quot; already exists. */
    public static final int PDH_PLA_ERROR_ALREADY_EXISTS = 0xC0000BEE;
    /** There is a mismatch in the settings type. */
    public static final int PDH_PLA_ERROR_TYPE_MISMATCH = 0xC0000BEF;
    /** The information specified does not resolve to a valid path name. */
    public static final int PDH_PLA_ERROR_FILEPATH = 0xC0000BF0;
    /** The &quot;Performance Logs &amp; Alerts&quot; service did not respond. */
    public static final int PDH_PLA_SERVICE_ERROR = 0xC0000BF1;
    /** The information passed is not valid. */
    public static final int PDH_PLA_VALIDATION_ERROR = 0xC0000BF2;
    /** The information passed is not valid. */
    public static final int PDH_PLA_VALIDATION_WARNING = 0x80000BF3;
    /** The name supplied is too long. */
    public static final int PDH_PLA_ERROR_NAME_TOO_LONG = 0xC0000BF4;
    /** SQL log format is incorrect. Correct format is &quot;SQL:&lt;DSN-name&gt;!&lt;LogSet-Name&gt;&quot;. */
    public static final int PDH_INVALID_SQL_LOG_FORMAT = 0xC0000BF5;
    /** Performance counter in PdhAddCounter call has already been added in the performance query. */
    public static final int PDH_COUNTER_ALREADY_IN_QUERY = 0xC0000BF6;
    /** Unable to read counter information and data from input binary log files. */
    public static final int PDH_BINARY_LOG_CORRUPT = 0xC0000BF7;
    /** At least one of the input binary log files contain fewer than two data samples. */
    public static final int PDH_LOG_SAMPLE_TOO_SMALL = 0xC0000BF8;
    /** The version of the operating system on the computer named %1 is later than that on the local computer. */
    public static final int PDH_OS_LATER_VERSION = 0xC0000BF9;
    /** %1 supports %2 or later. Check the operating system version on the computer named %3. */
    public static final int PDH_OS_EARLIER_VERSION = 0xC0000BFA;
    /** The output file must contain earlier data than the file to be appended. */
    public static final int PDH_INCORRECT_APPEND_TIME = 0xC0000BFB;
    /** Both files must have identical counters in order to append. */
    public static final int PDH_UNMATCHED_APPEND_COUNTER = 0xC0000BFC;
    /** Cannot alter CounterDetail table layout in SQL database. */
    public static final int PDH_SQL_ALTER_DETAIL_FAILED = 0xC0000BFD;
    /** System is busy - a time-out occurred when collecting counter data */
    public static final int PDH_QUERY_PERF_DATA_TIMEOUT = 0xC0000BFE; 
}
