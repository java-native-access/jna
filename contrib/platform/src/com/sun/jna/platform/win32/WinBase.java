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

import java.util.Date;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDLONG;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.ByteByReference;

/**
 * Ported from Winbase.h (kernel32.dll/kernel services).
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface WinBase extends StdCallLibrary, WinDef, BaseTSD {

    /** Constant value representing an invalid HANDLE. */
    HANDLE INVALID_HANDLE_VALUE =
        new HANDLE(Pointer.createConstant(Pointer.SIZE == 8
                                          ? -1 : 0xFFFFFFFFL));

    int WAIT_FAILED = 0xFFFFFFFF;
    int WAIT_OBJECT_0 = ((NTStatus.STATUS_WAIT_0 ) + 0 );
    int WAIT_ABANDONED = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
    int WAIT_ABANDONED_0 = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	
    /**
     * Maximum computer name length.
     * The value is 15 on Mac, 31 on everything else.
     */
    int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;
	
    /**
     * This logon type is intended for users who will be interactively using the computer, such 
     * as a user being logged on by a terminal server, remote shell, or similar process. This 
     * logon type has the additional expense of caching logon information for disconnected operations; 
     * therefore, it is inappropriate for some client/server applications, such as a mail server. 
     */
    int LOGON32_LOGON_INTERACTIVE = 2;
    /**
     * This logon type is intended for high performance servers to authenticate plaintext passwords. 
     * The LogonUser function does not cache credentials for this logon type.
     */
    int LOGON32_LOGON_NETWORK = 3;
    /**
     * This logon type is intended for batch servers, where processes may be executing on behalf 
     * of a user without their direct intervention. This type is also for higher performance servers 
     * that process many plaintext authentication attempts at a time, such as mail or Web servers. 
     * The LogonUser function does not cache credentials for this logon type.
     */
    int LOGON32_LOGON_BATCH = 4;
    /**
     * Indicates a service-type logon. The account provided must have the service privilege enabled.
     */
    int LOGON32_LOGON_SERVICE = 5;
    /**
     * This logon type is for GINA DLLs that log on users who will be interactively using the computer. 
     * This logon type can generate a unique audit record that shows when the workstation was unlocked.
     */
    int LOGON32_LOGON_UNLOCK = 7;
    /**
     * This logon type preserves the name and password in the authentication package, which allows the 
     * server to make connections to other network servers while impersonating the client. A server can 
     * accept plaintext credentials from a client, call LogonUser, verify that the user can access the 
     * system across the network, and still communicate with other servers.
     */
    int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
    /**
     * This logon type allows the caller to clone its current token and specify new credentials for 
     * outbound connections. The new logon session has the same local identifier but uses different 
     * credentials for other network connections. This logon type is supported only by the 
     * LOGON32_PROVIDER_WINNT50 logon provider.
     */
    int LOGON32_LOGON_NEW_CREDENTIALS = 9;

    /**
     * Use the standard logon provider for the system. The default security provider is negotiate, 
     * unless you pass NULL for the domain name and the user name is not in UPN format. In this case, 
     * the default provider is NTLM. 
     */
    int LOGON32_PROVIDER_DEFAULT = 0;
	
    /**
     * Use the Windows NT 3.5 logon provider.
     */
    int LOGON32_PROVIDER_WINNT35 = 1;
    /**
     * Use the NTLM logon provider.
     */
    int LOGON32_PROVIDER_WINNT40 = 2;
    /**
     * Use the negotiate logon provider.
     */
    int LOGON32_PROVIDER_WINNT50 = 3;	
	
    /**
     * If this flag is set, a child process created with the bInheritHandles parameter of
     * CreateProcess set to TRUE will inherit the object handle.
     */
    int HANDLE_FLAG_INHERIT = 1;
	
    /**
     * If this flag is set, calling the {@link Kernel32#CloseHandle} function will not
     * close the object handle.
     */
    int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2;

    // STARTUPINFO flags
    int STARTF_USESHOWWINDOW = 0x001;
    int STARTF_USESIZE = 0x002;
    int STARTF_USEPOSITION = 0x004;
    int STARTF_USECOUNTCHARS = 0x008;
    int STARTF_USEFILLATTRIBUTE = 0x010;
    int STARTF_RUNFULLSCREEN = 0x020;
    int STARTF_FORCEONFEEDBACK = 0x040;
    int STARTF_FORCEOFFFEEDBACK = 0x080;
    int STARTF_USESTDHANDLES = 0x100;
	
    // Process Creation flags
    int DEBUG_PROCESS = 0x00000001;
    int DEBUG_ONLY_THIS_PROCESS = 0x00000002;
    int CREATE_SUSPENDED = 0x00000004;
    int DETACHED_PROCESS = 0x00000008;
    int CREATE_NEW_CONSOLE = 0x00000010;
    int CREATE_NEW_PROCESS_GROUP = 0x00000200;
    int CREATE_UNICODE_ENVIRONMENT = 0x00000400;
    int CREATE_SEPARATE_WOW_VDM = 0x00000800;
    int CREATE_SHARED_WOW_VDM = 0x00001000;
    int CREATE_FORCEDOS = 0x00002000;
    int INHERIT_PARENT_AFFINITY = 0x00010000;
    int CREATE_PROTECTED_PROCESS = 0x00040000;
    int EXTENDED_STARTUPINFO_PRESENT = 0x00080000;
    int CREATE_BREAKAWAY_FROM_JOB = 0x01000000;
    int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 0x02000000;
    int CREATE_DEFAULT_ERROR_MODE = 0x04000000;
    int CREATE_NO_WINDOW = 0x08000000;

    /* Invalid return values */
    int INVALID_FILE_SIZE           = 0xFFFFFFFF;
    int INVALID_SET_FILE_POINTER    = 0xFFFFFFFF;
    int INVALID_FILE_ATTRIBUTES     = 0xFFFFFFFF;
	
    /**
     * Return code for a process still active.
     */
    int STILL_ACTIVE = WinNT.STATUS_PENDING;

	
    /**
     * The FILETIME structure is a 64-bit value representing the number of 
     * 100-nanosecond intervals since January 1, 1601 (UTC).
     * Conversion code in this class Copyright 2002-2004 Apache Software Foundation.
     * @author Rainer Klute (klute@rainer-klute.de) for the Apache Software Foundation (org.apache.poi.hpsf)
     */
    public static class FILETIME extends Structure {
        public int dwLowDateTime;
        public int dwHighDateTime;        

        public static class ByReference extends FILETIME implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public FILETIME(Date date) {
            long rawValue = dateToFileTime(date);
            dwHighDateTime = (int)(rawValue >> 32 & 0xffffffffL);
            dwLowDateTime = (int)(rawValue & 0xffffffffL);
        }

        public FILETIME() {
        }

        public FILETIME(Pointer memory) {
            super(memory);
            read();
        }

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
	
    /* Local Memory Flags */
    int  LMEM_FIXED = 0x0000;
    int  LMEM_MOVEABLE = 0x0002;
    int  LMEM_NOCOMPACT = 0x0010;
    int  LMEM_NODISCARD = 0x0020;
    int  LMEM_ZEROINIT = 0x0040;
    int  LMEM_MODIFY = 0x0080;
    int  LMEM_DISCARDABLE = 0x0F00;
    int  LMEM_VALID_FLAGS = 0x0F72;
    int  LMEM_INVALID_HANDLE = 0x8000;

    int  LHND = (LMEM_MOVEABLE | LMEM_ZEROINIT);
    int  LPTR = (LMEM_FIXED | LMEM_ZEROINIT);

    /* Flags returned by LocalFlags (in addition to LMEM_DISCARDABLE) */
    int  LMEM_DISCARDED = 0x4000;
    int  LMEM_LOCKCOUNT = 0x00FF;	
	
    /**
     * Specifies a date and time, using individual members for the month, 
     * day, year, weekday, hour, minute, second, and millisecond. The time 
     * is either in coordinated universal time (UTC) or local time, depending 
     * on the function that is being called.
     * http://msdn.microsoft.com/en-us/library/ms724950(VS.85).aspx
     */
    public static class SYSTEMTIME extends Structure {
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
     * The lpBuffer parameter is a pointer to a PVOID pointer, and that the nSize 
     * parameter specifies the minimum number of TCHARs to allocate for an output 
     * message buffer. The function allocates a buffer large enough to hold the 
     * formatted message, and places a pointer to the allocated buffer at the address 
     * specified by lpBuffer. The caller should use the LocalFree function to free 
     * the buffer when it is no longer needed.
     */
    int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x00000100;
    /**
     * Insert sequences in the message definition are to be ignored and passed through
     * to the output buffer unchanged. This flag is useful for fetching a message for 
     * later formatting. If this flag is set, the Arguments parameter is ignored.
     */
    int FORMAT_MESSAGE_IGNORE_INSERTS  = 0x00000200;
    /**
     * The lpSource parameter is a pointer to a null-terminated message definition.
     * The message definition may contain insert sequences, just as the message text 
     * in a message table resource may. Cannot be used with FORMAT_MESSAGE_FROM_HMODULE
     * or FORMAT_MESSAGE_FROM_SYSTEM.
     */
    int FORMAT_MESSAGE_FROM_STRING     = 0x00000400;
    /**
     * The lpSource parameter is a module handle containing the message-table 
     * resource(s) to search. If this lpSource handle is NULL, the current process's
     * application image file will be searched. Cannot be used with 
     * FORMAT_MESSAGE_FROM_STRING.
     */
    int FORMAT_MESSAGE_FROM_HMODULE    = 0x00000800;
    /**
     * The function should search the system message-table resource(s) for the 
     * requested message. If this flag is specified with FORMAT_MESSAGE_FROM_HMODULE,
     * the function searches the system message table if the message is not found in 
     * the module specified by lpSource. Cannot be used with FORMAT_MESSAGE_FROM_STRING. 
     * If this flag is specified, an application can pass the result of the 
     * GetLastError function to retrieve the message text for a system-defined error.
     */
    int FORMAT_MESSAGE_FROM_SYSTEM     = 0x00001000;
    /**
     * The Arguments parameter is not a va_list structure, but is a pointer to an array
     * of values that represent the arguments. This flag cannot be used with 64-bit 
     * argument values. If you are using 64-bit values, you must use the va_list 
     * structure.
     */
    int FORMAT_MESSAGE_ARGUMENT_ARRAY  = 0x00002000;

    /**
     * The drive type cannot be determined.
     */
    int DRIVE_UNKNOWN = 0;
    /**
     * The root path is invalid, for example, no volume is mounted at the path.
     */
    int DRIVE_NO_ROOT_DIR = 1;
    /**
     * The drive is a type that has removable media, for example, a floppy drive 
     * or removable hard disk.
     */
    int DRIVE_REMOVABLE = 2;
    /**
     * The drive is a type that cannot be removed, for example, a fixed hard drive.
     */
    int DRIVE_FIXED = 3;
    /**
     * The drive is a remote (network) drive.
     */
    int DRIVE_REMOTE = 4;
    /**
     * The drive is a CD-ROM drive.
     */
    int DRIVE_CDROM = 5;
    /**
     * The drive is a RAM disk.
     */
    int DRIVE_RAMDISK = 6;    
    
    /**
     * The OVERLAPPED structure contains information used in 
     * asynchronous (or overlapped) input and output (I/O).
     */
    public static class OVERLAPPED extends Structure {
        public ULONG_PTR Internal;
        public ULONG_PTR InternalHigh;
        public int Offset;
        public int OffsetHigh;
        public HANDLE hEvent;
    }        
    
    int INFINITE = 0xFFFFFFFF;

    /**
     * Contains information about the current computer system. This includes the architecture and 
     * type of the processor, the number of processors in the system, the page size, and other such
     * information.
     */
    public static class SYSTEM_INFO extends Structure {
    	
        /** Unnamed inner structure. */
    	public static class PI extends Structure {
    		
            public static class ByReference extends PI implements Structure.ByReference {
        		
            }

            /**
             * System's processor architecture. 
             * This value can be one of the following values:
             *  
             * 	PROCESSOR_ARCHITECTURE_UNKNOWN
             * 	PROCESSOR_ARCHITECTURE_INTEL
             * 	PROCESSOR_ARCHITECTURE_IA64
             * 	PROCESSOR_ARCHITECTURE_AMD64
             */
            public WORD wProcessorArchitecture;
            /**
             * Reserved for future use.
             */
            public WORD wReserved;
    	}
    	
        /** Unnamed inner union. */
        public static class UNION extends Union {
			
            public static class ByReference extends UNION implements Structure.ByReference {
	    		
            }

            /**
             * An obsolete member that is retained for compatibility with Windows NT 3.5 and earlier.
             * New applications should use the wProcessorArchitecture branch of the union.
             * Windows Me/98/95: The system always sets this member to zero, the value defined 
             * for PROCESSOR_ARCHITECTURE_INTEL.
             */
            public DWORD dwOemID;
            /**
             * Processor architecture (unnamed struct).
             */
            public PI pi;
        }
		
        /**
         * Processor architecture (unnamed union).
         */
        public UNION processorArchitecture;
        /**
         * Page size and the granularity of page protection and commitment.
         */
        public DWORD dwPageSize;
        /**
         * Pointer to the lowest memory address accessible to applications and dynamic-link libraries (DLLs). 
         */
        public Pointer lpMinimumApplicationAddress;
        /**
         * Pointer to the highest memory address accessible to applications and DLLs. 
         */
        public Pointer lpMaximumApplicationAddress;
        /**
         * Mask representing the set of processors configured into the system. Bit 0 is processor 0; bit 31 is processor 31. 
         */
        public DWORD_PTR dwActiveProcessorMask;
        /**
         * Number of processors in the system. 
         */
        public DWORD dwNumberOfProcessors;
        /**
         * An obsolete member that is retained for compatibility with Windows NT 3.5 and Windows Me/98/95. 
         * Use the wProcessorArchitecture, wProcessorLevel, and wProcessorRevision members to determine 
         * the type of processor. 
         * 	PROCESSOR_INTEL_386
         * 	PROCESSOR_INTEL_486
         * 	PROCESSOR_INTEL_PENTIUM
         */
        public DWORD dwProcessorType; 
        /**
         * Granularity for the starting address at which virtual memory can be allocated.
         */
        public DWORD dwAllocationGranularity;
        /**
         * System's architecture-dependent processor level. It should be used only for display purposes. 
         * To determine the feature set of a processor, use the IsProcessorFeaturePresent function.
         * If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_INTEL, wProcessorLevel is defined by the CPU vendor.
         * If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_IA64, wProcessorLevel is set to 1.
         */
        public WORD wProcessorLevel;
        /**
         * Architecture-dependent processor revision.
         */
        public WORD wProcessorRevision;
    }
    
    /**
     * Contains information about the current state of both physical and virtual memory, including 
     * extended memory. The GlobalMemoryStatusEx function stores information in this structure.
     */
    public static class MEMORYSTATUSEX extends Structure {
    	/**
    	 * The size of the structure, in bytes.
    	 */
        public DWORD dwLength;
        /**
         * A number between 0 and 100 that specifies the approximate percentage of physical memory 
         * that is in use (0 indicates no memory use and 100 indicates full memory use).
         */
        public DWORD dwMemoryLoad;
        /**
         * The amount of actual physical memory, in bytes.
         */
        public DWORDLONG ullTotalPhys;
        /**
         * The amount of physical memory currently available, in bytes. This is the amount of physical
         * memory that can be immediately reused without having to write its contents to disk first. 
         * It is the sum of the size of the standby, free, and zero lists.
         */
        public DWORDLONG ullAvailPhys;
        /**
         * The current committed memory limit for the system or the current process, whichever is smaller, in bytes. 
         */
        public DWORDLONG ullTotalPageFile;
        /**
         * The maximum amount of memory the current process can commit, in bytes. This value is equal to or smaller
         * than the system-wide available commit value.
         */
        public DWORDLONG ullAvailPageFile;
        /**
         * The size of the user-mode portion of the virtual address space of the calling process, in bytes.
         */
        public DWORDLONG ullTotalVirtual;
        /**
         * The amount of unreserved and uncommitted memory currently in the user-mode portion of the 
         * virtual address space of the calling process, in bytes.
         */
        public DWORDLONG ullAvailVirtual;
        /**
         * Reserved. This value is always 0.
         */
        public DWORDLONG ullAvailExtendedVirtual;
        
        public MEMORYSTATUSEX() {
            dwLength = new DWORD(size());
        }
    };
    
    /**
     * The SECURITY_ATTRIBUTES structure contains the security descriptor for an
     * object and specifies whether the handle retrieved by specifying this
     * structure is inheritable. This structure provides security settings for
     * objects created by various functions, such as {@link Kernel32#CreateFile},
     * {@link Kernel32#CreatePipe}, or {@link Advapi32#RegCreateKeyEx}.
     */
    public static class SECURITY_ATTRIBUTES extends Structure {
    	/**
    	 * The size of the structure, in bytes.
    	 */
        public DWORD dwLength;
        
        /**
         * A pointer to a SECURITY_DESCRIPTOR structure that controls access to the object.
         */
        public Pointer lpSecurityDescriptor;
        
        /**
         * A Boolean value that specifies whether the returned handle is inherited when
         * a new process is created
         */
        public boolean bInheritHandle;
        
        public SECURITY_ATTRIBUTES() {
            dwLength = new DWORD(size());
        }
    }
    
    /**
     * Specifies the window station, desktop, standard handles, and appearance of the main
     * window for a process at creation time.
     */
    public static class STARTUPINFO extends Structure {
        /**
         * The size of the structure, in bytes.
         */
    	public DWORD cb;
    	
    	/**
    	 * Reserved; must be NULL.
    	 */
        public String lpReserved;
		
        /**
         * The name of the desktop, or the name of both the desktop and window station for this process.
         * A backslash in the string indicates that the string includes both the desktop and window
         * station names. For more information, see Thread Connection to a Desktop.
         */
        public String lpDesktop;
		
        /**
         * For console processes, this is the title displayed in the title bar
         * if a new console window is created. If NULL, the name of the
         * executable file is used as the window title instead. This parameter
         * must be NULL for GUI or console processes that do not create a new
         * console window.
         */
        public String lpTitle;
		
        /**
         * If dwFlags specifies STARTF_USEPOSITION, this member is the x offset
         * of the upper left corner of a window if a new window is created, in
         * pixels. Otherwise, this member is ignored.
         * 
         * The offset is from the upper left corner of the screen. For GUI
         * processes, the specified position is used the first time the new
         * process calls CreateWindow to create an overlapped window if the x
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwX;

        /**
         * If dwFlags specifies STARTF_USEPOSITION, this member is the y offset
         * of the upper left corner of a window if a new window is created, in
         * pixels. Otherwise, this member is ignored.
         * 
         * The offset is from the upper left corner of the screen. For GUI
         * processes, the specified position is used the first time the new
         * process calls CreateWindow to create an overlapped window if the y
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwY;

        /**
         * If dwFlags specifies STARTF_USESIZE, this member is the width of the
         * window if a new window is created, in pixels. Otherwise, this member
         * is ignored.
         * 
         * For GUI processes, this is used only the first time the new process
         * calls CreateWindow to create an overlapped window if the nWidth
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwXSize;
		
        /**
         * If dwFlags specifies STARTF_USESIZE, this member is the height of the
         * window if a new window is created, in pixels. Otherwise, this member
         * is ignored.
         * 
         * For GUI processes, this is used only the first time the new process
         * calls CreateWindow to create an overlapped window if the nHeight
         * parameter of CreateWindow is CW_USEDEFAULT.
         */
        public DWORD dwYSize;

        /**
         * If dwFlags specifies STARTF_USECOUNTCHARS, if a new console window is
         * created in a console process, this member specifies the screen buffer
         * width, in character columns. Otherwise, this member is ignored.
         */
        public DWORD dwXCountChars;

        /**
         * If dwFlags specifies STARTF_USECOUNTCHARS, if a new console window is
         * created in a console process, this member specifies the screen buffer
         * height, in character rows. Otherwise, this member is ignored.
         */
        public DWORD dwYCountChars;

        /**
         * If dwFlags specifies STARTF_USEFILLATTRIBUTE, this member is the
         * initial text and background colors if a new console window is created
         * in a console application. Otherwise, this member is ignored.
         * 
         * This value can be any combination of the following values:
         * FOREGROUND_BLUE, FOREGROUND_GREEN, FOREGROUND_RED,
         * FOREGROUND_INTENSITY, BACKGROUND_BLUE, BACKGROUND_GREEN,
         * BACKGROUND_RED, and BACKGROUND_INTENSITY. For example, the following
         * combination of values produces red text on a white background:
         * 
         * FOREGROUND_RED| BACKGROUND_RED| BACKGROUND_GREEN| BACKGROUND_BLUE
         */
        public DWORD dwFillAttribute;

        /**
         * A bit field that determines whether certain STARTUPINFO members are
         * used when the process creates a window.
         */
        public int dwFlags;

        /**
         * If dwFlags specifies STARTF_USESHOWWINDOW, this member can be any of
         * the values that can be specified in the nCmdShow parameter for the
         * ShowWindow function, except for SW_SHOWDEFAULT. Otherwise, this
         * member is ignored.
         * 
         * For GUI processes, the first time ShowWindow is called, its nCmdShow
         * parameter is ignored wShowWindow specifies the default value. In
         * subsequent calls to ShowWindow, the wShowWindow member is used if the
         * nCmdShow parameter of ShowWindow is set to SW_SHOWDEFAULT.
         */
        public WORD wShowWindow;

        /**
         * Reserved for use by the C Run-time; must be zero.
         */
        public WORD cbReserved2;

        /**
         * Reserved for use by the C Run-time; must be NULL.
         */
        public ByteByReference lpReserved2;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard input handle for the process. If STARTF_USESTDHANDLES is not
         * specified, the default for standard input is the keyboard buffer.
         * 
         * If dwFlags specifies STARTF_USEHOTKEY, this member specifies a hotkey
         * value that is sent as the wParam parameter of a WM_SETHOTKEY message
         * to the first eligible top-level window created by the application
         * that owns the process. If the window is created with the WS_POPUP
         * window style, it is not eligible unless the WS_EX_APPWINDOW extended
         * window style is also set. For more information, see CreateWindowEx.
         * 
         * Otherwise, this member is ignored.
         */
        public HANDLE hStdInput;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard output handle for the process. Otherwise, this member is
         * ignored and the default for standard output is the console window's
         * buffer.
         */
        public HANDLE hStdOutput;

        /**
         * If dwFlags specifies STARTF_USESTDHANDLES, this member is the
         * standard error handle for the process. Otherwise, this member is
         * ignored and the default for standard error is the console window's
         * buffer.
         */
        public HANDLE hStdError;
		
        public STARTUPINFO() {
            cb = new DWORD(size());
        }
    }

    /**
     * Contains information about a newly created process and its primary
     * thread. It is used with the CreateProcess, CreateProcessAsUser,
     * CreateProcessWithLogonW, or CreateProcessWithTokenW function.
     */
    public static class PROCESS_INFORMATION extends Structure {

    	/**
         * A handle to the newly created process. The handle is used to specify
         * the process in all functions that perform operations on the process
         * object.
         */
        public HANDLE hProcess;
		
        /**
         * A handle to the primary thread of the newly created process. The
         * handle is used to specify the thread in all functions that perform
         * operations on the thread object.
         */
        public HANDLE hThread;

        /**
         * A value that can be used to identify a process. The value is valid
         * from the time the process is created until all handles to the process
         * are closed and the process object is freed; at this point, the
         * identifier may be reused.
         */
        public DWORD dwProcessId;

        /**
         * A value that can be used to identify a thread. The value is valid
         * from the time the thread is created until all handles to the thread
         * are closed and the thread object is freed; at this point, the
         * identifier may be reused.
         */
        public DWORD dwThreadId;

        public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public PROCESS_INFORMATION() {
        }

        public PROCESS_INFORMATION(Pointer memory) {
            super(memory);
            read();
        }
    }

    /**
     * If the file is to be moved to a different volume, the function simulates the move by using the CopyFile and DeleteFile functions.
     *
     * This value cannot be used with MOVEFILE_DELAY_UNTIL_REBOOT.
     */
    int MOVEFILE_COPY_ALLOWED = 0x2;

    /**
     * Reserved for future use.
     */
    int MOVEFILE_CREATE_HARDLINK = 0x10;

    /**
     * The system does not move the file until the operating system is restarted. The system moves the file immediately
     * after AUTOCHK is executed, but before creating any paging files. Consequently, this parameter enables the
     * function to delete paging files from previous startups.
     *
     * This value can be used only if the process is in the context of a user who belongs to the administrators group or
     * the LocalSystem account.
     *
     * This value cannot be used with MOVEFILE_COPY_ALLOWED.
     *
     * Windows Server 2003 and Windows XP:  For information about special situations where this functionality can fail,
     * and a suggested workaround solution, see Files are not exchanged when Windows Server 2003 restarts if you use the
     * MoveFileEx function to schedule a replacement for some files in the Help and Support Knowledge Base.
     *
     * Windows 2000:  If you specify the MOVEFILE_DELAY_UNTIL_REBOOT flag for dwFlags, you cannot also prepend the file
     * name that is specified by lpExistingFileName with "\\?".
     */
    int MOVEFILE_DELAY_UNTIL_REBOOT = 0x4;

    /**
     * The function fails if the source file is a link source, but the file cannot be tracked after the move. This
     * situation can occur if the destination is a volume formatted with the FAT file system.
     */
    int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 0x20;

    /**
     * If a file named lpNewFileName exists, the function replaces its contents with the contents of the
     * lpExistingFileName file, provided that security requirements regarding access control lists (ACLs) are met. For
     * more information, see the Remarks section of this topic.
     *
     * This value cannot be used if lpNewFileName or lpExistingFileName names a directory.
     */
    int MOVEFILE_REPLACE_EXISTING = 0x1;

    /**
     * The function does not return until the file is actually moved on the disk.
     *
     * Setting this value guarantees that a move performed as a copy and delete operation is flushed to disk before the
     * function returns. The flush occurs at the end of the copy operation.
     *
     * This value has no effect if MOVEFILE_DELAY_UNTIL_REBOOT is set.
     */
    int MOVEFILE_WRITE_THROUGH = 0x8;

}
