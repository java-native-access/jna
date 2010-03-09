/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.examples.win32;

import java.nio.Buffer;
import java.util.Date;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/** Definition (incomplete) of <code>kernel32.dll</code>. */
public interface Kernel32 extends W32API {

    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS);

	/**
	 * Specifies a date and time, using individual members for the month, 
	 * day, year, weekday, hour, minute, second, and millisecond. The time 
	 * is either in coordinated universal time (UTC) or local time, depending 
	 * on the function that is being called.
	 * http://msdn.microsoft.com/en-us/library/ms724950(VS.85).aspx
	 */
    public class SYSTEMTIME extends Structure {
    	// The year. The valid values for this member are 1601 through 30827.
        public short wYear;
        // The month. The valid values for this member are 1 through 12.
        public short wMonth;
        // The day of the week. The valid values for this member are 0 through 6.
        public short wDayOfWeek;
        // The day of the month. The valid values for this member are 1 through 31.
        public short wDay;
        // The hour. The valid values for this member are 0 through 23.
        public short wHour;
        // The minute. The valid values for this member are 0 through 59.
        public short wMinute;
        // The second. The valid values for this member are 0 through 59.
        public short wSecond;
        // The millisecond. The valid values for this member are 0 through 999.
        public short wMilliseconds;
    }
    
    /**
     * Frees the specified local memory object and invalidates its handle.
     * @param hLocal A handle to the local memory object.
     * @return 
     * 	If the function succeeds, the return value is NULL.
     * 	If the function fails, the return value is equal to a handle to the local memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer LocalFree(Pointer hLocal);
    
    /**
     * Frees the specified global memory object and invalidates its handle.
     * @param hGlobal A handle to the global memory object. 
     * @return 
     * 	If the function succeeds, the return value is NULL
     * 	If the function fails, the return value is equal to a handle to the global memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer GlobalFree(Pointer hGlobal);

    HMODULE GetModuleHandle(String name);
    void GetSystemTime(SYSTEMTIME result);
    int GetCurrentThreadId();
    HANDLE GetCurrentThread();
    int GetCurrentProcessId();
    HANDLE GetCurrentProcess();
    int GetProcessId(HANDLE process);
    int GetProcessVersion(int processId);
    int GetLastError();
    void SetLastError(int dwErrCode);
    int GetDriveType(String rootPathName);

    int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x0100;
    int FORMAT_MESSAGE_FROM_SYSTEM = 0x1000;
    int FORMAT_MESSAGE_IGNORE_INSERTS = 0x200;
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, PointerByReference lpBuffer,
                      int nSize, Pointer va_list);
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, Buffer lpBuffer,
                      int nSize, Pointer va_list);

    int FILE_LIST_DIRECTORY = 0x00000001;

    int FILE_SHARE_READ = 1;
    int FILE_SHARE_WRITE = 2;
    int FILE_SHARE_DELETE = 4;

    int CREATE_NEW =         1;
    int CREATE_ALWAYS =      2;
    int OPEN_EXISTING =      3;
    int OPEN_ALWAYS =        4;
    int TRUNCATE_EXISTING =  5;

    int FILE_FLAG_WRITE_THROUGH =        0x80000000;
    int FILE_FLAG_OVERLAPPED =           0x40000000;
    int FILE_FLAG_NO_BUFFERING =         0x20000000;
    int FILE_FLAG_RANDOM_ACCESS =        0x10000000;
    int FILE_FLAG_SEQUENTIAL_SCAN =      0x08000000;
    int FILE_FLAG_DELETE_ON_CLOSE =      0x04000000;
    int FILE_FLAG_BACKUP_SEMANTICS =     0x02000000;
    int FILE_FLAG_POSIX_SEMANTICS =      0x01000000;
    int FILE_FLAG_OPEN_REPARSE_POINT =   0x00200000;
    int FILE_FLAG_OPEN_NO_RECALL =       0x00100000;

    int FILE_ATTRIBUTE_READONLY = 0x00000001;
    int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
    int FILE_ATTRIBUTE_SYSTEM = 0x00000004;
    int FILE_ATTRIBUTE_DIRECTORY = 0x00000010;
    int FILE_ATTRIBUTE_ARCHIVE = 0x00000020;
    int FILE_ATTRIBUTE_DEVICE = 0x00000040;
    int FILE_ATTRIBUTE_NORMAL = 0x00000080;
    int FILE_ATTRIBUTE_TEMPORARY = 0x00000100;
    int FILE_ATTRIBUTE_SPARSE_FILE = 0x00000200;
    int FILE_ATTRIBUTE_REPARSE_POINT = 0x00000400;
    int FILE_ATTRIBUTE_COMPRESSED = 0x00000800;
    int FILE_ATTRIBUTE_OFFLINE = 0x00001000;
    int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x00002000;
    int FILE_ATTRIBUTE_ENCRYPTED = 0x00004000;

    int DRIVE_UNKNOWN = 0;
    int DRIVE_NO_ROOT_DIR = 1;
    int DRIVE_REMOVABLE = 2;
    int DRIVE_FIXED = 3;
    int DRIVE_REMOTE = 4;
    int DRIVE_CDROM = 5;
    int DRIVE_RAMDISK = 6;

    int GENERIC_WRITE = 0x40000000;
    class SECURITY_ATTRIBUTES extends Structure {
        public int nLength = size();
        public Pointer lpSecurityDescriptor;
        public boolean bInheritHandle;
    }
    HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode,
                      SECURITY_ATTRIBUTES lpSecurityAttributes,
                      int dwCreationDisposition, int dwFlagsAndAttributes,
                      HANDLE hTemplateFile);
    boolean CreateDirectory();

    HANDLE CreateIoCompletionPort(HANDLE FileHandle,
                                  HANDLE ExistingCompletionPort,
                                  Pointer CompletionKey,
                                  int NumberOfConcurrentThreads);
    int INFINITE = 0xFFFFFFFF;
    boolean GetQueuedCompletionStatus(HANDLE CompletionPort,
                                      IntByReference lpNumberOfBytes,
                                      ByReference lpCompletionKey,
                                      PointerByReference lpOverlapped,
                                      int dwMilliseconds);

    boolean PostQueuedCompletionStatus(HANDLE CompletionPort,
                                       int dwNumberOfBytesTransferred,
                                       Pointer dwCompletionKey,
                                       OVERLAPPED lpOverlapped);
    
    /**
     * Waits until the specified object is in the signaled state or the time-out interval elapses.
	 * To enter an alertable wait state, use the WaitForSingleObjectEx function. 
	 * To wait for multiple objects, use the WaitForMultipleObjects.
	 * 
	 * 
	 * @param hHandle 
	 * 			A handle to the object. For a list of the object types whose handles can be specified, see the following Remarks section.
	 *     		If this handle is closed while the wait is still pending, the function's behavior is undefined.
	 *			The handle must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param dwMilliseconds
     * 			The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the object is signaled or the interval elapses. 
     * 			If dwMilliseconds is zero, the function does not enter a wait state if the object is not signaled; it always returns immediately. 
     * 			If dwMilliseconds is INFINITE, the function will return only when the object is signaled.
     * 	
     * @return	If the function succeeds, the return value indicates the event that caused the function to return. It can be one of the four following values:
     * 			WAIT_ABANDONED 0x00000080L
     * 			WAIT_OBJECT_0 0x00000000L
     * 			WAIT_TIMEOUT 0x00000102L
     * 			WAIT_FAILED (DWORD)0xFFFFFFFF
     */    
    int WaitForSingleObject(HANDLE hHandle, int dwMilliseconds);
    
    /**
     * Waits until one or all of the specified objects are in the signaled state or the time-out interval elapses.
     * To enter an alertable wait state, use the WaitForMultipleObjectsEx function.
     * 
     * @param nCount 
     * 			The number of object handles in the array pointed to by lpHandles. The maximum number of object handles is MAXIMUM_WAIT_OBJECTS.
     * @param hHandle
     * 			An array of object handles. For a list of the object types whose handles can be specified, see the following Remarks section. The array can contain handles to objects of different types. 
     * 			It may not contain multiple copies of the same handle.
     * 			If one of these handles is closed while the wait is still pending, the function's behavior is undefined.
     * 			The handles must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param bWaitAll
     * 			If this parameter is TRUE, the function returns when the state of all objects in the lpHandles array is signaled. 
     * 			If FALSE, the function returns when the state of any one of the objects is set to signaled. 
     * 			In the latter case, the return value indicates the object whose state caused the function to return.
     * @param dwMilliseconds
     * 			The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the specified objects are signaled or the interval elapses. 
     * 			If dwMilliseconds is zero, the function does not enter a wait state if the specified objects are not signaled; it always returns immediately. 
     * 			If dwMilliseconds is INFINITE, the function will return only when the specified objects are signaled.
     * @return	If the function succeeds, the return value indicates the event that caused the function to return. 
     * 			It can be one of the following values. (Note that WAIT_OBJECT_0 is defined as 0 and WAIT_ABANDONED_0 is defined as 0x00000080L.)
     * 			WAIT_OBJECT_0 to (WAIT_OBJECT_0 + nCount– 1) 
     * 			WAIT_ABANDONED_0 to (WAIT_ABANDONED_0 + nCount– 1)
     * 			WAIT_TIMEOUT 0x00000102L, 
     * 			WAIT_FAILED (DWORD)0xFFFFFFFF
     */
    int WaitForMultipleObjects(int nCount, HANDLE[] hHandle, boolean bWaitAll, int dwMilliseconds);
    
    boolean DuplicateHandle(HANDLE hSourceProcessHandle,
                            HANDLE hSourceHandle,
                            HANDLE hTargetProcessHandle,
                            HANDLEByReference lpTargetHandle,
                            int dwDesiredAccess,
                            boolean bInheritHandle,
                            int dwOptions);
    boolean CloseHandle(HANDLE hObject);

    int FILE_ACTION_ADDED = 1;
    int FILE_ACTION_REMOVED = 2;
    int FILE_ACTION_MODIFIED = 3;
    int FILE_ACTION_RENAMED_OLD_NAME = 4;
    int FILE_ACTION_RENAMED_NEW_NAME = 5;

    int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
    int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
    int FILE_NOTIFY_CHANGE_NAME = 3;
    int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
    int FILE_NOTIFY_CHANGE_SIZE = 8;
    int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
    int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
    int FILE_NOTIFY_CHANGE_CREATION = 64;
    int FILE_NOTIFY_CHANGE_EA = 128;
    int FILE_NOTIFY_CHANGE_SECURITY = 256;
    int FILE_NOTIFY_CHANGE_STREAM_NAME = 512;
    int FILE_NOTIFY_CHANGE_STREAM_SIZE = 1024;
    int FILE_NOTIFY_CHANGE_STREAM_WRITE = 2048;
    /** This structure is non-trivial since it is a pattern stamped
     * into a large block of result memory rather than something that stands
     * alone or is used for input.
     */
    class FILE_NOTIFY_INFORMATION extends Structure {
        public int NextEntryOffset;
        public int Action;
        public int FileNameLength;
        // filename is not nul-terminated, so we can't use a String/WString
        public char[] FileName = new char[1];

        private FILE_NOTIFY_INFORMATION() { }
        public FILE_NOTIFY_INFORMATION(int size) {
            if (size < size())
                throw new IllegalArgumentException("Size must greater than "
                                                   + size() + ", requested "
                                                   + size);
            allocateMemory(size);
        }
        /** WARNING: this filename may be either the short or long form
         * of the filename.
         */
        public String getFilename() {
            return new String(FileName, 0, FileNameLength/2);
        }
        public void read() {
            // avoid reading filename until we know how long it is
            FileName = new char[0];
            super.read();
            FileName = getPointer().getCharArray(12, FileNameLength/2);
        }
        public FILE_NOTIFY_INFORMATION next() {
            if (NextEntryOffset == 0)
                return null;
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(getPointer(), NextEntryOffset);
            next.read();
            return next;
        }
    }
    class OVERLAPPED extends Structure {
        public ULONG_PTR Internal;
        public ULONG_PTR InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public HANDLE hEvent;
    }
    // TODO: figure out how OVERLAPPED is used and apply an appropriate mapping
    interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
        void callback(int errorCode, int nBytesTransferred, OVERLAPPED overlapped);
    }
    /** NOTE: only exists in unicode form (W suffix).  Define this method
     * explicitly with the W suffix to avoid inadvertent calls in ASCII mode.
     */
    boolean ReadDirectoryChangesW(HANDLE directory,
                                  FILE_NOTIFY_INFORMATION info,
                                  int length,
                                  boolean watchSubtree,
                                  int notifyFilter,
                                  IntByReference bytesReturned,
                                  OVERLAPPED overlapped,
                                  OVERLAPPED_COMPLETION_ROUTINE completionRoutine);

    /** ASCII version.  Use {@link Native#toString(byte[])} to obtain the short
     * path from the <code>byte</code> array.
     * Use only if <code>w32.ascii==true</code>.
     */
    int GetShortPathName(String lpszLongPath, byte[] lpdzShortPath, int cchBuffer);

    /** Unicode version (the default).  Use {@link Native#toString(char[])} to
     * obtain the short path from the <code>char</code> array.
     */
    int GetShortPathName(String lpszLongPath, char[] lpdzShortPath, int cchBuffer);

    /**
     *
     * Conversion code in this class Copyright 2002-2004 Apache Software Foundation.
     * @author Rainer Klute (klute@rainer-klute.de) for the Apache Software Foundation (org.apache.poi.hpsf)
     */
    static class FILETIME extends Structure {
      public int dwLowDateTime;
      public int dwHighDateTime;

      /**
       * <p>The difference between the Windows epoch (1601-01-01
       * 00:00:00) and the Unix epoch (1970-01-01 00:00:00) in
       * milliseconds: 11644473600000L. (Use your favorite spreadsheet
       * program to verify the correctness of this value. By the way,
       * did you notice that you can tell from the epochs which
       * operating system is the modern one? :-))</p>
       */
      private static final long EPOCH_DIFF = 11644473600000L;

      /**
       * <p>Converts a Windows FILETIME into a {@link Date}. The Windows
       * FILETIME structure holds a date and time associated with a
       * file. The structure identifies a 64-bit integer specifying the
       * number of 100-nanosecond intervals which have passed since
       * January 1, 1601. This 64-bit value is split into the two double
       * words stored in the structure.</p>
       *
       * @param high The higher double word of the FILETIME structure.
       * @param low The lower double word of the FILETIME structure.
       * @return The Windows FILETIME as a {@link Date}.
       */
      public static Date filetimeToDate(final int high, final int low) {
        final long filetime = (long) high << 32 | low & 0xffffffffL;
        final long ms_since_16010101 = filetime / (1000 * 10);
        final long ms_since_19700101 = ms_since_16010101 - EPOCH_DIFF;
        return new Date(ms_since_19700101);
      }

      /**
       * <p>Converts a {@link Date} into a filetime.</p>
       *
       * @param date The date to be converted
       * @return The filetime
       *
       * @see #filetimeToDate
       */
      public static long dateToFileTime(final Date date) {
        final long ms_since_19700101 = date.getTime();
        final long ms_since_16010101 = ms_since_19700101 + EPOCH_DIFF;
        return ms_since_16010101 * 1000 * 10;
      }

      public Date toDate() {
        return filetimeToDate(dwHighDateTime, dwLowDateTime);
      }

      public long toLong() {
        return toDate().getTime();
      }

      public String toString() {
        return super.toString() + ": " + toDate().toString(); //$NON-NLS-1$
      }
    }

    int LMEM_ZEROINIT = 0x0040;
    int LMEM_FIXED = 0x0000;
    int LPTR = LMEM_FIXED | LMEM_ZEROINIT;

    Pointer LocalAlloc(int type, int cbInput);

    boolean WriteFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite,
                      IntByReference lpNumberOfBytesWritten,
                      OVERLAPPED lpOverlapped);

    HANDLE CreateEvent(SECURITY_ATTRIBUTES lpEventAttributes,
                       boolean bManualReset, boolean bInitialState,
                       String lpName);

    boolean SetEvent(HANDLE hEvent);

    boolean PulseEvent(HANDLE hEvent);

    int PAGE_READONLY = 0x02;
    int PAGE_READWRITE = 0x04;
    int PAGE_WRITECOPY = 0x08;
    int PAGE_EXECUTE = 0x10;
    int PAGE_EXECUTE_READ = 0x20;
    int PAGE_EXECUTE_READWRITE = 0x40;

    HANDLE CreateFileMapping(HANDLE hFile, SECURITY_ATTRIBUTES lpAttributes,
                             int flProtect, int dwMaximumSizeHigh,
                             int dwMaximumSizeLow, String lpName);

    int SECTION_QUERY = 0x0001;
    int SECTION_MAP_WRITE = 0x0002;
    int SECTION_MAP_READ = 0x0004;
    int SECTION_MAP_EXECUTE = 0x0008;
    int SECTION_EXTEND_SIZE = 0x0010;

    Pointer MapViewOfFile(HANDLE hFileMappingObject, int dwDesiredAccess,
                          int dwFileOffsetHigh, int dwFileOffsetLow,
                          int dwNumberOfBytesToMap);

    boolean UnmapViewOfFile(Pointer lpBaseAddress);
    
    /**
     * Retrieves only the NetBIOS name of the local computer.
     * 
     * @param buffer 
     * 	A pointer to a buffer that receives the computer name or the cluster virtual server
     *  name. The buffer size should be large enough to contain MAX_COMPUTERNAME_LENGTH + 1
     *  characters.
     * @param lpnSize
     * 	On input, specifies the size of the buffer, in TCHARs. On output, the number of TCHARs 
     * 	copied to the destination buffer, not including the terminating null character. If 
     *  the buffer is too small, the function fails and GetLastError returns 
     *  ERROR_BUFFER_OVERFLOW. The lpnSize parameter specifies the size of the buffer required,
     *  including the terminating null character.
     * @return
     * 	If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    public boolean GetComputerName(char[] buffer, IntByReference lpnSize);
}
