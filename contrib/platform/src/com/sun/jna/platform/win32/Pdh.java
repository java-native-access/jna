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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Windows Performance Data Helper (a.k.a. PDH).
 * @author Lyor Goldstein
 * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373083(v=vs.85).aspx">Performance Counters</A>
 */
public interface Pdh extends StdCallLibrary {
    Pdh INSTANCE = Native.load("Pdh", Pdh.class, W32APIOptions.DEFAULT_OPTIONS);

    /** Maximum counter name length. */
    int PDH_MAX_COUNTER_NAME = 1024;
    /** Maximum counter instance name length. */
    int PDH_MAX_INSTANCE_NAME = 1024;
    /** Maximum full counter path length. */
    int PDH_MAX_COUNTER_PATH = 2048;
    /** Maximum full counter log name length. */
    int PDH_MAX_DATASOURCE_PATH = 1024;

    int PDH_MORE_DATA = 0x800007D2;
    int PDH_INVALID_ARGUMENT =  0xC0000BBD;
    int PDH_MEMORY_ALLOCATION_FAILURE = 0xC0000BBB;
    int PDH_CSTATUS_NO_MACHINE = 0x800007D0;
    int PDH_CSTATUS_NO_OBJECT = 0xC0000BB8;

    /* TODO
     * LPVOID CALLBACK AllocateMemory(_In_ SIZE_T AllocSize,_In_ LPVOID pContext)
     * void CALLBACK FreeMemory(LPVOID pBuffer,LPVOID pContext)
     */

    /**
     * Connects to the specified computer.
     * @param szMachineName The name of the computer to connect to. If
     * {@code null}, PDH connects to the local computer.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372578(v=vs.85).aspx">PdhConnectMachine</A>
     */
    int PdhConnectMachine(String szMachineName);

    // Known values for the PdhGetDllVersion result
    int PDH_CVERSION_WIN40 = 0x0400;
    int PDH_CVERSION_WIN50 = 0x0500;
    // v1.1 revision of PDH -- basic log functions
    // v1.2 of the PDH -- adds variable instance counters
    // v1.3 of the PDH -- adds log service control & stubs for NT5/PDH v2 fn's
    // v2.0 of the PDH -- is the NT v 5.0 B2 version
    int PDH_VERSION = PDH_CVERSION_WIN50 + 0x0003;

    /**
     * Returns the version of the currently installed Pdh.dll file.
     * @param lpdwVersion A variable that receives the version of Pdh.dll.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372630(v=vs.85).aspx">PdhGetDllVersion</A>
     */
    int PdhGetDllVersion(DWORDByReference lpdwVersion);

    /**
     * Creates a new query that is used to manage the collection of performance data.
     * @param szDataSource The name of the log file from which to retrieve performance data.
     * If {@code null}, performance data is collected from a real-time data source.
     * @param dwUserData User-defined value to associate with this query.
     * @param phQuery (Out) Handle to the query. You use this handle in subsequent calls.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372652(v=vs.85).aspx">PdhOpenQuery</A>
     */
    int PdhOpenQuery(String szDataSource, DWORD_PTR dwUserData, HANDLEByReference phQuery);

    /**
     * Closes all counters contained in the specified query, closes all
     * handles related to the query, and frees all memory associated with
     * the query.
     * @param hQuery Handle to the query to close.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372558(v=vs.85).aspx">PdhCloseQuery</A>
     */
    int PdhCloseQuery(HANDLE hQuery);

    /**
     * Components of a counter path
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373041(v=vs.85).aspx">PDH_COUNTER_PATH_ELEMENTS</A>
     * @see <A HREF="https://technet.microsoft.com/en-us/library/cc776490(v=ws.10).aspx">Windows Server 2003 Performance Counters Reference</A>
     */
    @FieldOrder({"szMachineName", "szObjectName", "szInstanceName",
                "szParentInstance", "dwInstanceIndex", "szCounterName"})
    public class PDH_COUNTER_PATH_ELEMENTS extends Structure {
        public String szMachineName;
        public String szObjectName;
        public String szInstanceName;
        public String szParentInstance;
        public int  dwInstanceIndex;
        public String szCounterName;
    }

    // flags for the PdhMakeCounterPath
    int PDH_PATH_WBEM_RESULT = 0x00000001;
    int PDH_PATH_WBEM_INPUT  = 0x00000002;

    /**
     * Creates a full counter path using the members specified in the
     * {@link Pdh.PDH_COUNTER_PATH_ELEMENTS} structure.
     * @param pCounterPathElements Structure that contains the members
     * used to make up the path
     * @param szFullPathBuffer Caller-allocated buffer that receives a null-terminated
     * counter path. The maximum length of a counter path is PDH_MAX_COUNTER_PATH.
     * Set to {@code null} if <tt>pcchBufferSize</tt> is zero.
     * @param pcchBufferSize Size of the <tt>szFullPathBuffer</tt> buffer. If
     * zero on input, the function returns PDH_MORE_DATA and sets this parameter
     * to the required buffer size. If the buffer is larger than the required
     * size, the function sets this parameter to the actual size of the buffer
     * that was used.
     * @param dwFlags Format of the input and output counter values.
     * @return ERROR_SUCCESS (or PDH_MORE_DATA)
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372649(v=vs.85).aspx">PdhMakeCounterPath</A>
     */
    int PdhMakeCounterPath(PDH_COUNTER_PATH_ELEMENTS pCounterPathElements, char[] szFullPathBuffer, DWORDByReference pcchBufferSize, int dwFlags);

    /**
     * Adds the specified counter to the query.
     * @param hQuery Handle to the query to which you want to add the counter.
     * @param szFullCounterPath String that contains the counter path.
     * The maximum length of a counter path is {@link #PDH_MAX_COUNTER_PATH}.
     * @param dwUserData User-defined value.
     * @param phCounter (Out) Handle to the counter that was added to the query.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372204(v=vs.85).aspx">PdhAddCounter</A>
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373193(v=vs.85).aspx">Specifying a Counter Path</A>
     */
    int PdhAddCounter(HANDLE hQuery, String szFullCounterPath, DWORD_PTR dwUserData, HANDLEByReference phCounter);
    int PdhAddEnglishCounter(HANDLE hQuery, String szFullCounterPath, DWORD_PTR dwUserData, HANDLEByReference phCounter);

    /**
     * Removes a counter from a query.
     * @param hCounter Handle of the counter to remove from its query.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372665(v=vs.85).aspx">PdhRemoveCounter</A>
     */
    int PdhRemoveCounter(HANDLE hCounter);

    /**
     * The data as it was collected from the counter provider. No translation,
     * formatting, or other interpretation is performed on the data.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373060(v=vs.85).aspx">PDH_RAW_COUNTER</A>
     */
    @FieldOrder({"CStatus", "TimeStamp", "FirstValue", "SecondValue", "MultiCount"})
    public class PDH_RAW_COUNTER extends Structure {
        /** Counter status that indicates if the counter value is valid. */
        public int CStatus;
        /** Local time for when the data was collected */
        public FILETIME TimeStamp = new FILETIME();
        /** First raw counter value. */
        public long FirstValue;
        /** Second raw counter value. */
        public long SecondValue;
        /**
         * If the counter type contains the PERF_MULTI_COUNTER flag,
         * this member contains the additional counter data used in the
         * calculation
         */
        public int MultiCount;
    }

    /**
     * @param hCounter Handle of the counter from which to retrieve the current raw value.
     * @param lpdwType Receives the counter type - this parameter is optional
     * @param pValue The {@link PDH_RAW_COUNTER} structure to receive the data
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372644(v=vs.85).aspx">PdhGetRawCounterValue</A>
     */
    int PdhGetRawCounterValue(HANDLE hCounter, DWORDByReference lpdwType, PDH_RAW_COUNTER pValue);

    // counter value types
    int PDH_FMT_RAW          = 0x00000010;
    int PDH_FMT_ANSI         = 0x00000020;
    int PDH_FMT_UNICODE      = 0x00000040;
    int PDH_FMT_LONG         = 0x00000100;
    int PDH_FMT_DOUBLE       = 0x00000200;
    int PDH_FMT_LARGE        = 0x00000400;
    int PDH_FMT_NOSCALE      = 0x00001000;
    int PDH_FMT_1000         = 0x00002000;
    int PDH_FMT_NODATA       = 0x00004000;
    int PDH_FMT_NOCAP100     = 0x00008000;
    int PERF_DETAIL_COSTLY   = 0x00010000;
    int PERF_DETAIL_STANDARD = 0x0000FFFF;

    /**
     * Validates that the counter is present on the computer specified in the counter path.
     * @param szFullCounterPath The counter path to validate
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372986(v=vs.85).aspx">PdhValidatePath</A>
     */
    int PdhValidatePath(String szFullCounterPath);

    /**
     * Collects the current raw data value for all counters in the specified
     * query and updates the status code of each counter.
     * @param hQuery Handle to the query
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372563(v=vs.85).aspx">PdhCollectQueryData</A>
     */
    int PdhCollectQueryData(HANDLE hQuery);

    /**
     * Uses a separate thread to collect the current raw data value for all counters
     * in the specified query. The function then signals the application-defined
     * event and waits the specified time interval before returning.
     * @param hQuery Handle to the query
     * @param dwIntervalTime Time interval to wait, in seconds.
     * @param hNewDataEvent Handle to the event that you want PDH to signal after
     * the time interval expires. To create an event object, call the
     * {@link Kernel32#CreateEvent(com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES, boolean, boolean, String)}
     * function
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372566(v=vs.85).aspx">PdhCollectQueryDataEx</A>
     */
    int PdhCollectQueryDataEx(HANDLE hQuery, int dwIntervalTime, HANDLE hNewDataEvent);

    /**
     * Collects the current raw data value for all counters in the specified
     * query and updates the status code of each counter.
     * @param hQuery Handle to the query
     * @param pllTimeStamp Time stamp when the first counter value in the query
     * was retrieved. The time is specified as {@link WinBase.FILETIME}.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372569(v=vs.85).aspx">PdhCollectQueryDataWithTime</A>
     */
    int PdhCollectQueryDataWithTime(HANDLE hQuery, LONGLONGByReference pllTimeStamp);

    /**
     * Information on time intervals as applied to the sampling of performance data.
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373071(v=vs.85).aspx">PDH_TIME_INFO</A>
     */
    @FieldOrder({"StartTime", "EndTime", "SampleCount"})
    public class PDH_TIME_INFO extends Structure {
        /** Starting time of the sample interval, in local FILETIME format. */
        public long StartTime;
        /** Ending time of the sample interval, in local FILETIME format. */
        public long EndTime;
        /** Number of samples collected during the interval. */
        public int SampleCount;
    }

    /**
     * @param hQuery Handle to the query.
     * @param pInfo A {@link PDH_TIME_INFO} structure that specifies the time range.
     * @return ERROR_SUCCESS if successful
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa372677(v=vs.85).aspx">PdhSetQueryTimeRange</A>
     */
    int PdhSetQueryTimeRange(HANDLE hQuery, PDH_TIME_INFO pInfo);

    /**
     * Returns the specified object's counter and instance names that exist on
     * the specified computer or in the specified log file.
     * 
     * @param szDataSource
     *            String that specifies the name of the log file used to
     *            enumerate the counter and instance names. If NULL, the
     *            function uses the computer specified in the szMachineName
     *            parameter to enumerate the names.
     * @param szMachineName
     *            String that specifies the name of the computer that contains
     *            the counter and instance names that you want to enumerate.
     *            Include the leading slashes in the computer name, for example,
     *            \\computername. If the szDataSource parameter is NULL, you can
     *            set szMachineName to NULL to specify the local computer.
     * @param szObjectName
     *            String that specifies the name of the object whose counter and
     *            instance names you want to enumerate.
     * @param mszCounterList
     *            Caller-allocated buffer that receives a list of
     *            null-terminated counter names provided by the specified
     *            object. The list contains unique counter names. The list is
     *            terminated by two NULL characters. Set to NULL if the
     *            pcchCounterListLengthparameter is zero.
     * @param pcchCounterListLength
     *            Size of the mszCounterList buffer, in TCHARs. If zero on input
     *            and the object exists, the function returns PDH_MORE_DATA and
     *            sets this parameter to the required buffer size. If the buffer
     *            is larger than the required size, the function sets this
     *            parameter to the actual size of the buffer that was used. If
     *            the specified size on input is greater than zero but less than
     *            the required size, you should not rely on the returned size to
     *            reallocate the buffer.
     * @param mszInstanceList
     *            Caller-allocated buffer that receives a list of
     *            null-terminated instance names provided by the specified
     *            object. The list contains unique instance names. The list is
     *            terminated by two NULL characters. Set to NULL if
     *            pcchInstanceListLength is zero.
     * @param pcchInstanceListLength
     *            Size of the mszInstanceList buffer, in TCHARs. If zero on
     *            input and the object exists, the function returns
     *            PDH_MORE_DATA and sets this parameter to the required buffer
     *            size. If the buffer is larger than the required size, the
     *            function sets this parameter to the actual size of the buffer
     *            that was used. If the specified size on input is greater than
     *            zero but less than the required size, you should not rely on
     *            the returned size to reallocate the buffer. If the specified
     *            object does not support variable instances, then the returned
     *            value will be zero. If the specified object does support
     *            variable instances, but does not currently have any instances,
     *            then the value returned is 2, which is the size of an empty
     *            MULTI_SZ list string.
     * @param dwDetailLevel
     *            Detail level of the performance items to return. All items
     *            that are of the specified detail level or less will be
     *            returned.
     * @param dwFlags
     *            This parameter must be zero.
     * @return If the function succeeds, it returns ERROR_SUCCESS. If the
     *         function fails, the return value is a system error code or a PDH
     *         error code.
     * @see <A HREF=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa372677(v=vs.85).aspx">PdhEnumObjectItems</A>
     */
    int PdhEnumObjectItems(String szDataSource, String szMachineName, String szObjectName, Pointer mszCounterList,
            DWORDByReference pcchCounterListLength, Pointer mszInstanceList, DWORDByReference pcchInstanceListLength,
            int dwDetailLevel, int dwFlags);

    /**
     * Returns the counter index corresponding to the specified counter name.
     * 
     * @param szMachineName
     *            Null-terminated string that specifies the name of the computer
     *            where the specified counter is located. The computer name can
     *            be specified by the DNS name or the IP address. If NULL, the
     *            function uses the local computer.
     * @param szNameBuffer
     *            Null-terminated string that contains the counter name.
     * @param pdwIndex
     *            Index of the counter.
     * @return If the function succeeds, it returns ERROR_SUCCESS. If the
     *         function fails, the return value is a system error code or a PDH
     *         error code.
     * @see <A HREF=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa372647(v=vs.85).aspx">PdhLookupPerfIndexByName</A>
     */
    int PdhLookupPerfIndexByName(String szMachineName, String szNameBuffer, DWORDByReference pdwIndex);

    /**
     * Returns the performance object name or counter name corresponding to the
     * specified index.
     * 
     * @param szMachineName
     *            Null-terminated string that specifies the name of the computer
     *            where the specified performance object or counter is located.
     *            The computer name can be specified by the DNS name or the IP
     *            address. If NULL, the function uses the local computer.
     * @param dwNameIndex
     *            Index of the performance object or counter.
     * @param szNameBuffer
     *            Caller-allocated buffer that receives the null-terminated name
     *            of the performance object or counter. Set to NULL if
     *            pcchNameBufferSize is zero.
     * @param pcchNameBufferSize
     *            Size of the szNameBuffer buffer, in TCHARs. If zero on input,
     *            the function returns PDH_MORE_DATA and sets this parameter to
     *            the required buffer size. If the buffer is larger than the
     *            required size, the function sets this parameter to the actual
     *            size of the buffer that was used. If the specified size on
     *            input is greater than zero but less than the required size,
     *            you should not rely on the returned size to reallocate the
     *            buffer.
     * @return If the function succeeds, it returns ERROR_SUCCESS. If the
     *         function fails, the return value is a system error code or a PDH
     *         error code.
     * @see <A HREF=
     *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa372648(v=vs.85).aspx">PdhLookupPerfNameByIndex</A>
     */
    int PdhLookupPerfNameByIndex(String szMachineName, int dwNameIndex, Pointer szNameBuffer,
            DWORDByReference pcchNameBufferSize);
}
