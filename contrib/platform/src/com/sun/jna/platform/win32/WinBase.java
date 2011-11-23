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
 * Avoid including any NIO Buffer mappings here; put them in Kernel32 instead.
 * @author dblock[at]dblock.org
 */
public interface WinBase extends StdCallLibrary, WinDef {

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
     * @return 15 on MAC, 31 on everything else.
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

    /**
     * Frees the specified local memory object and invalidates its handle.
     * @param hLocal
     *  A handle to the local memory object.
     * @return 
     * 	If the function succeeds, the return value is NULL.
     * 	If the function fails, the return value is equal to a handle to the local memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer LocalFree(Pointer hLocal);
    
    /**
     * Frees the specified global memory object and invalidates its handle.
     * @param hGlobal 
     *  A handle to the global memory object. 
     * @return 
     * 	If the function succeeds, the return value is NULL
     * 	If the function fails, the return value is equal to a handle to the global memory object.
     *  To get extended error information, call GetLastError.
     */
    Pointer GlobalFree(Pointer hGlobal);

    /**
     * The GetModuleHandle function retrieves a module handle for the specified module 
     * if the file has been mapped into the address space of the calling process.
     * @param name
     *  Pointer to a null-terminated string that contains the name of the module 
     *  (either a .dll or .exe file). 
     * @return
     *  If the function succeeds, the return value is a handle to the specified module. 
     *  If the function fails, the return value is NULL. To get extended error 
     *  information, call GetLastError.
     */
    HMODULE GetModuleHandle(String name);
    
    /**
     * The GetSystemTime function retrieves the current system date and time. 
     * The system time is expressed in Coordinated Universal Time (UTC).
     * @param lpSystemTime
     *  Pointer to a SYSTEMTIME structure to receive the current system date and time. 
     */
    void GetSystemTime(WinBase.SYSTEMTIME lpSystemTime);
    
    /**
     * The GetTickCount function retrieves the number of milliseconds that have elapsed since the system was started, up to 49.7 days.
     * @return 
     *  Number of milliseconds that have elapsed since the system was started.
     */
    int GetTickCount();
    
    /**
     * The GetCurrentThreadId function retrieves the thread identifier of the calling thread.
     * @return 
     *  The return value is the thread identifier of the calling thread.
     */
    int GetCurrentThreadId();
    
    /**
     * The GetCurrentThread function retrieves a pseudo handle for the current thread.
     * @return 
     *  The return value is a pseudo handle for the current thread.
     */
    HANDLE GetCurrentThread();
    
    /**
     * This function returns the process identifier of the calling process. 
     * @return 
     *  The return value is the process identifier of the calling process.
     */
    int GetCurrentProcessId();
    
    /**
     * This function returns a pseudohandle for the current process. 
     * @return 
     *  The return value is a pseudohandle to the current process. 
     */
    HANDLE GetCurrentProcess();
    
    /**
     * The GetProcessId function retrieves the process identifier of the 
     * specified process.
     * @param process
     *  Handle to the process. The handle must have the PROCESS_QUERY_INFORMATION access right. 
     * @return
     *  If the function succeeds, the return value is the process identifier of the 
     *  specified process. If the function fails, the return value is zero. To get
     *  extended error information, call GetLastError.
     */
    int GetProcessId(HANDLE process);
    
    /**
     * The GetProcessVersion function retrieves the major and minor version numbers of the system 
     * on which the specified process expects to run.
     * @param processId
     *  Process identifier of the process of interest. A value of zero specifies the 
     *  calling process. 
     * @return
     *  If the function succeeds, the return value is the version of the system on 
     *  which the process expects to run. The high word of the return value contains 
     *  the major version number. The low word of the return value contains the minor
     *  version number. If the function fails, the return value is zero. To get extended
     *  error information, call GetLastError. The function fails if ProcessId is an 
     *  invalid value.
     */
    int GetProcessVersion(int processId);
    
    /**
     * Retrieves the termination status of the specified process.
     * @param hProcess A handle to the process.
     * @param lpExitCode A pointer to a variable to receive the process termination status.
     * @return If the function succeeds, the return value is nonzero.
     * 
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean GetExitCodeProcess(HANDLE hProcess, IntByReference lpExitCode);
    
    /**
     * Terminates the specified process and all of its threads.
     * @param hProcess A handle to the process to be terminated.
     * @param uExitCode The exit code to be used by the process and threads
     *  terminated as a result of this call.
     * @return If the function succeeds, the return value is nonzero.
     * 
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean TerminateProcess(HANDLE hProcess, int uExitCode);
    
    /**
     * The GetLastError function retrieves the calling thread's last-error code value.
     * The last-error code is maintained on a per-thread basis. Multiple threads do not
     * overwrite each other's last-error code.
     * @return
     *  The return value is the calling thread's last-error code value.
     */
    int GetLastError();
    
    /**
     * The SetLastError function sets the last-error code for the calling thread.
     * @param dwErrCode
     *  Last-error code for the thread. 
     */
    void SetLastError(int dwErrCode);
    
    /**
     * The GetDriveType function determines whether a disk drive is a removable,
     * fixed, CD-ROM, RAM disk, or network drive.
     * @param lpRootPathName
     *  Pointer to a null-terminated string that specifies the root directory of
     *  the disk to return information about. A trailing backslash is required. 
     *  If this parameter is NULL, the function uses the root of the current directory.
     * @return
     *  The return value specifies the type of drive.
     */
    int GetDriveType(String lpRootPathName);

    /**
     * The FormatMessage function formats a message string. The function requires a
     * message definition as input. The message definition can come from a buffer 
     * passed into the function. It can come from a message table resource in an 
     * already-loaded module. Or the caller can ask the function to search the 
     * system's message table resource(s) for the message definition. The function 
     * finds the message definition in a message table resource based on a message 
     * identifier and a language identifier. The function copies the formatted message 
     * text to an output buffer, processing any embedded insert sequences if requested.
     * @param dwFlags
     *  Formatting options, and how to interpret the lpSource parameter. The low-order
     *  byte of dwFlags specifies how the function handles line breaks in the output 
     *  buffer. The low-order byte can also specify the maximum width of a formatted
     *  output line. <p/>
     * This version of the function assumes FORMAT_MESSAGE_ALLOCATE_BUFFER is
     *  <em>not</em> set.
     * @param lpSource
     *  Location of the message definition.
     * @param dwMessageId
     *  Message identifier for the requested message.
     * @param dwLanguageId
     *  Language identifier for the requested message.
     * @param lpBuffer
     *  Pointer to a buffer that receives the null-terminated string that specifies the 
     *  formatted message.
     * @param nSize
     *  This this parameter specifies the size of the output buffer, in TCHARs. If FORMAT_MESSAGE_ALLOCATE_BUFFER is 
     * @param va_list
     *  Pointer to an array of values that are used as insert values in the formatted message.
     * @return
     * 	If the function succeeds, the return value is the number of TCHARs stored in 
     * 	the output buffer, excluding the terminating null character. If the function 
     * 	fails, the return value is zero. To get extended error information, call 
     *  GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, Pointer lpBuffer,
                      int nSize, Pointer va_list);
    
    /**
     * The FormatMessage function formats a message string. The function requires a
     * message definition as input. The message definition can come from a buffer 
     * passed into the function. It can come from a message table resource in an 
     * already-loaded module. Or the caller can ask the function to search the 
     * system's message table resource(s) for the message definition. The function 
     * finds the message definition in a message table resource based on a message 
     * identifier and a language identifier. The function copies the formatted message 
     * text to an output buffer, processing any embedded insert sequences if requested.
     * @param dwFlags
     *  Formatting options, and how to interpret the lpSource parameter. The low-order
     *  byte of dwFlags specifies how the function handles line breaks in the output 
     *  buffer. The low-order byte can also specify the maximum width of a formatted
     *  output line. <p/>
     * This version of the function assumes FORMAT_MESSAGE_ALLOCATE_BUFFER is
     *  set.
     * @param lpSource
     *  Location of the message definition.
     * @param dwMessageId
     *  Message identifier for the requested message.
     * @param dwLanguageId
     *  Language identifier for the requested message.
     * @param lpBuffer
     *  Pointer to a pointer that receives the allocated buffer in which the
     *  null-terminated string that specifies the formatted message is written.
     * @param nSize
     *  This parameter specifies the minimum number of TCHARs to allocate for an 
     *  output buffer.
     * @param va_list
     *  Pointer to an array of values that are used as insert values in the formatted message.
     * @return
     * 	If the function succeeds, the return value is the number of TCHARs stored in 
     * 	the output buffer, excluding the terminating null character. If the function 
     * 	fails, the return value is zero. To get extended error information, call 
     *  GetLastError.
     */
    int FormatMessage(int dwFlags, Pointer lpSource, int dwMessageId,
                      int dwLanguageId, PointerByReference lpBuffer,
                      int nSize, Pointer va_list);
    
    /**
     * The CreateFile function creates or opens a file, file stream, directory, physical
     * disk, volume, console buffer, tape drive, communications resource, mailslot, or 
     * named pipe. The function returns a handle that can be used to access an object.
     * @param lpFileName
     *  A pointer to a null-terminated string that specifies the name of an object to create or open. 
     * @param dwDesiredAccess
     *  The access to the object, which can be read, write, or both.
     * @param dwShareMode
     *  The sharing mode of an object, which can be read, write, both, or none.
     * @param lpSecurityAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure that determines whether or not 
     *  the returned handle can be inherited by child processes.  If lpSecurityAttributes 
     *  is NULL, the handle cannot be inherited. 
     * @param dwCreationDisposition
     *  An action to take on files that exist and do not exist. 
     * @param dwFlagsAndAttributes
     *  The file attributes and flags. 
     * @param hTemplateFile
     *  Handle to a template file with the GENERIC_READ access right. The template file
     *  supplies file attributes and extended attributes for the file that is being 
     *  created. This parameter can be NULL.
     * @return
     * 	If the function succeeds, the return value is an open handle to a specified file.
     *  If a specified file exists before the function call and dwCreationDisposition is
     *  CREATE_ALWAYS or OPEN_ALWAYS, a call to GetLastError returns ERROR_ALREADY_EXISTS,
     *  even when the function succeeds. If a file does not exist before the call, 
     *  GetLastError returns 0 (zero). If the function fails, the return value is 
     *  INVALID_HANDLE_VALUE. To get extended error information, call GetLastError.
     */
    HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, 
    		WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes, int dwCreationDisposition, 
    		int dwFlagsAndAttributes, HANDLE hTemplateFile);
        
    
    /**
     * Copies an existing file to a new file.
     * 
     * @param lpExistingFileName
     *   The name of an existing file.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path. For more information, see Naming a File.
     *
     *   If lpExistingFileName does not exist, CopyFile fails, and GetLastError returns ERROR_FILE_NOT_FOUND.
     *
     * @param lpNewFileName
     *   The name of the new file.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path. For more information, see Naming a File.
     * 
     * @param bFailIfExists
     *   If this parameter is TRUE and the new file specified by lpNewFileName already exists, the function fails. If
     *   this parameter is FALSE and the new file already exists, the function overwrites the existing file and succeeds.
     * 
     * @return
     *   If the function succeeds, the return value is nonzero. If the function fails, the return value is zero. To get
     *   extended error information, call GetLastError.
     */
    boolean CopyFile(String lpExistingFileName, String lpNewFileName, boolean bFailIfExists);

    /**
     * Moves an existing file or a directory, including its children.
     *
     * @param lpExistingFileName
     *
     *   The current name of the file or directory on the local computer.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path. For more information, see Naming a File.
     *
     * @param lpNewFileName
     *
     *   The new name for the file or directory. The new name must not already exist. A new file may be on a different
     *   file system or drive. A new directory must be on the same drive.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path. For more information, see Naming a File.
     *
     * @return
     *
     *   If the function succeeds, the return value is nonzero.
     *
     *   If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean MoveFile(String lpExistingFileName, String lpNewFileName);

    /**
     * Moves an existing file or directory, including its children, with various move options.
     *
     * @param lpExistingFileName
     *
     *   The current name of the file or directory on the local computer.
     *
     *   If dwFlags specifies MOVEFILE_DELAY_UNTIL_REBOOT, the file cannot exist on a remote share, because delayed
     *   operations are performed before the network is available.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path. For more information, see Naming a File
     *
     *   Windows 2000:  If you prepend the file name with "\\?\", you cannot also specify the
     *   MOVEFILE_DELAY_UNTIL_REBOOT flag for dwFlags.
     *
     * @param lpNewFileName
     *
     *   The new name of the file or directory on the local computer.
     *
     *   When moving a file, the destination can be on a different file system or volume. If the destination is on
     *   another drive, you must set the MOVEFILE_COPY_ALLOWED flag in dwFlags.
     *
     *   When moving a directory, the destination must be on the same drive.
     *
     *   If dwFlags specifies MOVEFILE_DELAY_UNTIL_REBOOT and lpNewFileName is NULL, MoveFileEx registers the
     *   lpExistingFileName file to be deleted when the system restarts. If lpExistingFileName refers to a directory,
     *   the system removes the directory at restart only if the directory is empty.
     *
     * @param dwFlags
     *   This parameter can be one or more of the following values.
     *
     * @return
     *
     *   If the function succeeds, the return value is nonzero.
     *
     *   If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean MoveFileEx(String lpExistingFileName, String lpNewFileName, DWORD dwFlags);

    /**
     * The CreateDirectory function creates a new directory. If the underlying file
     * system supports security on files and directories, the function applies a 
     * specified security descriptor to the new directory.
     * @param lpPathName
     *  Pointer to a null-terminated string that specifies the path of the directory 
     *  to be created. 
     * @param lpSecurityAttributes
     *  Pointer to a SECURITY_ATTRIBUTES structure. The lpSecurityDescriptor member 
     *  of the structure specifies a security descriptor for the new directory. If 
     *  lpSecurityAttributes is NULL, the directory gets a default security descriptor. 
     * @return
     *  If the function succeeds, the return value is nonzero. If the function fails, 
     *  the return value is zero. To get extended error information, call GetLastError. 
     */
    boolean CreateDirectory(String lpPathName, 
    		WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes);

	/**
	 * Reads data from the specified file or input/output (I/O) device. Reads
	 * occur at the position specified by the file pointer if supported by the
	 * device.
	 * 
	 * This function is designed for both synchronous and asynchronous
	 * operations. For a similar function designed solely for asynchronous
	 * operation, see ReadFileEx
	 * 
	 * @param hFile A handle to the device (for example, a file, file stream,
	 *  physical disk, volume, console buffer, tape drive, socket, communications
	 *  resource, mailslot, or pipe).
	 * @param lpBuffer A pointer to the buffer that receives the data read from a file or device.
	 * @param nNumberOfBytesToRead The maximum number of bytes to be read.
	 * @param lpNumberOfBytesRead A pointer to the variable that receives the number of bytes
	 *  read when using a synchronous hFile parameter
	 * @param lpOverlapped A pointer to an OVERLAPPED structure is required if the hFile
	 *  parameter was opened with FILE_FLAG_OVERLAPPED, otherwise it can be NULL.
	 * @return If the function succeeds, the return value is nonzero (TRUE).
	 *  If the function fails, or is completing asynchronously, the return value is zero (FALSE).
	 *  To get extended error information, call the GetLastError function.
	 *  
	 *  Note  The GetLastError code ERROR_IO_PENDING is not a failure; it designates the read
	 *  operation is pending completion asynchronously. For more information, see Remarks.
	 */
    boolean ReadFile(
    		HANDLE hFile,
    		Pointer lpBuffer,
    		int nNumberOfBytesToRead,
    		IntByReference lpNumberOfBytesRead,
    		WinBase.OVERLAPPED lpOverlapped);
    /**
     * Creates an input/output (I/O) completion port and associates it with a specified
     * file handle, or creates an I/O completion port that is not yet associated with a
     * file handle, allowing association at a later time.
     * @param FileHandle
     *  An open file handle or INVALID_HANDLE_VALUE.
     * @param ExistingCompletionPort
     *  A handle to an existing I/O completion port or NULL.
     * @param CompletionKey
     *  The per-handle user-defined completion key that is included in every I/O completion 
     *  packet for the specified file handle.
     * @param NumberOfConcurrentThreads
     * 	The maximum number of threads that the operating system can allow to concurrently 
     * 	process I/O completion packets for the I/O completion port.
     * @return
     * 	If the function succeeds, the return value is the handle to an I/O completion port:
     *   If the ExistingCompletionPort parameter was NULL, the return value is a new handle.
     *   If the ExistingCompletionPort parameter was a valid I/O completion port handle, the return value is that same handle.
     *   If the FileHandle parameter was a valid handle, that file handle is now associated with the returned I/O completion port.
     *   If the function fails, the return value is NULL. To get extended error information, call the GetLastError function.
     */
    HANDLE CreateIoCompletionPort(HANDLE FileHandle, HANDLE ExistingCompletionPort,
    		Pointer CompletionKey, int NumberOfConcurrentThreads);

    /**
     * Attempts to dequeue an I/O completion packet from the specified I/O completion 
     * port. If there is no completion packet queued, the function waits for a pending 
     * I/O operation associated with the completion port to complete.
     * @param CompletionPort
     *  A handle to the completion port.
     * @param lpNumberOfBytes
     *  A pointer to a variable that receives the number of bytes transferred during 
     *  an I/O operation that has completed.
     * @param lpCompletionKey
     *  A pointer to a variable that receives the completion key value associated with 
     *  the file handle whose I/O operation has completed.
     * @param lpOverlapped
     *  A pointer to a variable that receives the address of the OVERLAPPED structure 
     *  that was specified when the completed I/O operation was started. 
     * @param dwMilliseconds
     *  The number of milliseconds that the caller is willing to wait for a completion 
     *  packet to appear at the completion port. 
     * @return
     *  Returns nonzero (TRUE) if successful or zero (FALSE) otherwise.
     */
    boolean GetQueuedCompletionStatus(HANDLE CompletionPort, 
    		IntByReference lpNumberOfBytes, ByReference lpCompletionKey, 
    		PointerByReference lpOverlapped, int dwMilliseconds);

    /**
     * Posts an I/O completion packet to an I/O completion port.
     * @param CompletionPort
     *  A handle to an I/O completion port to which the I/O completion packet is to be posted.
     * @param dwNumberOfBytesTransferred
     *  The value to be returned through the lpNumberOfBytesTransferred parameter of the GetQueuedCompletionStatus function.
     * @param dwCompletionKey
     *  The value to be returned through the lpCompletionKey parameter of the GetQueuedCompletionStatus function.
     * @param lpOverlapped
     *  The value to be returned through the lpOverlapped parameter of the GetQueuedCompletionStatus function.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError .
     */
    boolean PostQueuedCompletionStatus(HANDLE CompletionPort,
    		int dwNumberOfBytesTransferred, Pointer dwCompletionKey,
    		WinBase.OVERLAPPED lpOverlapped);
    
    /**
     * Waits until the specified object is in the signaled state or the time-out interval elapses.
	 * To enter an alertable wait state, use the WaitForSingleObjectEx function. 
	 * To wait for multiple objects, use the WaitForMultipleObjects.
	 * @param hHandle 
	 *  A handle to the object. For a list of the object types whose handles can be specified, see the following Remarks section.
	 *  If this handle is closed while the wait is still pending, the function's behavior is undefined.
	 *	The handle must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param dwMilliseconds
     * 	The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the object is signaled or the interval elapses. 
     * 	If dwMilliseconds is zero, the function does not enter a wait state if the object is not signaled; it always returns immediately. 
     * 	If dwMilliseconds is INFINITE, the function will return only when the object is signaled.
     * @return	
     *  If the function succeeds, the return value indicates the event that caused the function to return.
     */    
    int WaitForSingleObject(HANDLE hHandle, int dwMilliseconds);
    
    /**
     * Waits until one or all of the specified objects are in the signaled state or the time-out interval elapses.
     * To enter an alertable wait state, use the WaitForMultipleObjectsEx function.
     * @param nCount 
     * 	The number of object handles in the array pointed to by lpHandles. The maximum number of object handles is MAXIMUM_WAIT_OBJECTS.
     * @param hHandle
     * 	An array of object handles. For a list of the object types whose handles can be specified, see the following Remarks section. The array can contain handles to objects of different types. 
     * 	It may not contain multiple copies of the same handle.
     * 	If one of these handles is closed while the wait is still pending, the function's behavior is undefined.
     * 	The handles must have the SYNCHRONIZE access right. For more information, see Standard Access Rights.
     * @param bWaitAll
     * 	If this parameter is TRUE, the function returns when the state of all objects in the lpHandles array is signaled. 
     * 	If FALSE, the function returns when the state of any one of the objects is set to signaled. 
     * 	In the latter case, the return value indicates the object whose state caused the function to return.
     * @param dwMilliseconds
     *  The time-out interval, in milliseconds. If a nonzero value is specified, the function waits until the specified objects are signaled or the interval elapses. 
     * 	If dwMilliseconds is zero, the function does not enter a wait state if the specified objects are not signaled; it always returns immediately. 
     * 	If dwMilliseconds is INFINITE, the function will return only when the specified objects are signaled.
     * @return	
     *  If the function succeeds, the return value indicates the event that caused the function to return. 
     */
    int WaitForMultipleObjects(int nCount, HANDLE[] hHandle, boolean bWaitAll, int dwMilliseconds);
    
    /**
     * The DuplicateHandle function duplicates an object handle. 
     * 
     * @param hSourceProcessHandle
     *  Handle to the process with the handle to duplicate. 
     *  The handle must have the PROCESS_DUP_HANDLE access right.
     * @param hSourceHandle
     *  Handle to duplicate. This is an open object handle that is valid in the 
     *  context of the source process.
     * @param hTargetProcessHandle
     *  Handle to the process that is to receive the duplicated handle. 
     *  The handle must have the PROCESS_DUP_HANDLE access right. 
     * @param lpTargetHandle
     *  Pointer to a variable that receives the duplicate handle. This handle value is valid in 
     *  the context of the target process. If hSourceHandle is a pseudo handle returned by 
     *  GetCurrentProcess or GetCurrentThread, DuplicateHandle converts it to a real handle to 
     *  a process or thread, respectively.
     * @param dwDesiredAccess
     *  Access requested for the new handle. 
     * @param bInheritHandle
     *  Indicates whether the handle is inheritable.
     * @param dwOptions
     *  Optional actions.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean DuplicateHandle(HANDLE hSourceProcessHandle, HANDLE hSourceHandle,
    		HANDLE hTargetProcessHandle, HANDLEByReference lpTargetHandle,
    		int dwDesiredAccess, boolean bInheritHandle, int dwOptions);
    
    /**
     * The CloseHandle function closes an open object handle.
     * 
     * @param hObject
     *  Handle to an open object. This parameter can be a pseudo handle or INVALID_HANDLE_VALUE.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean CloseHandle(HANDLE hObject);
    
    // TODO: figure out how OVERLAPPED is used and apply an appropriate mapping
    interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallCallback {
        void callback(int errorCode, int nBytesTransferred,
        		WinBase.OVERLAPPED overlapped);
    }
    
    /**
     * Retrieves information that describes the changes within the specified directory. 
     * The function does not report changes to the specified directory itself.
     * Note: there's no ReadDirectoryChangesA.
     * @param directory
     *  A handle to the directory to be monitored. This directory must be opened with the 
     *  FILE_LIST_DIRECTORY access right.
     * @param info
     *  A pointer to the DWORD-aligned formatted buffer in which the read results are to be returned.
     * @param length
     *  The size of the buffer that is pointed to by the lpBuffer parameter, in bytes.
     * @param watchSubtree
     *  If this parameter is TRUE, the function monitors the directory tree rooted at the specified
     *  directory. If this parameter is FALSE, the function monitors only the directory specified by
     *  the hDirectory parameter.
     * @param notifyFilter
     *  The filter criteria that the function checks to determine if the wait operation has completed.
     * @param bytesReturned
     *  For synchronous calls, this parameter receives the number of bytes transferred into the 
     *  lpBuffer parameter. For asynchronous calls, this parameter is undefined. You must use an asynchronous 
     *  notification technique to retrieve the number of bytes transferred.
     * @param overlapped
     *  A pointer to an OVERLAPPED structure that supplies data to be used during asynchronous operation. 
     *  Otherwise, this value is NULL. The Offset and OffsetHigh members of this structure are not used.
     * @param completionRoutine
     * 	A pointer to a completion routine to be called when the operation has been completed or canceled and
     *  the calling thread is in an alertable wait state.
     * @return
     *  If the function succeeds, the return value is nonzero. For synchronous calls, this means that the 
     *  operation succeeded. For asynchronous calls, this indicates that the operation was successfully queued.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     *  If the network redirector or the target file system does not support this operation, the function 
     *  fails with ERROR_INVALID_FUNCTION.
     */
    public boolean ReadDirectoryChangesW(HANDLE directory, 
    		WinNT.FILE_NOTIFY_INFORMATION info, int length, boolean watchSubtree,
            int notifyFilter, IntByReference bytesReturned, WinBase.OVERLAPPED overlapped, 
            OVERLAPPED_COMPLETION_ROUTINE completionRoutine);

    /**
     * Retrieves the short path form of the specified path.
     * @param lpszLongPath
     *  The path string. 
     * @param lpdzShortPath
     *  A pointer to a buffer to receive the null-terminated short form of the path that lpszLongPath specifies.
     * @param cchBuffer
     *  The size of the buffer that lpszShortPath points to, in TCHARs.
     * @return
     *  If the function succeeds, the return value is the length, in TCHARs, of the string that is copied to 
     *  lpszShortPath, not including the terminating null character.
     *  If the lpszShortPath buffer is too small to contain the path, the return value is the size of the buffer, 
     *  in TCHARs, that is required to hold the path and the terminating null character. 
     *  If the function fails for any other reason, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    int GetShortPathName(String lpszLongPath, char[] lpdzShortPath, int cchBuffer);

    /**
     * The LocalAlloc function allocates the specified number of bytes from the heap. 
     * Windows memory management does not provide a separate local heap and global heap.
     * @param type
     *  Memory allocation attributes. The default is the LMEM_FIXED value.
     * @param cbInput
     *  Number of bytes to allocate. If this parameter is zero and the uFlags parameter 
     *  specifies LMEM_MOVEABLE, the function returns a handle to a memory object that 
     *  is marked as discarded. 
     * @return
     *  If the function succeeds, the return value is a handle to the newly allocated memory object.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    Pointer LocalAlloc(int type, int cbInput);

    /**
     * Writes data to the specified file or input/output (I/O) device.
     * @param hFile
     *  A handle to the file or I/O device (for example, a file, file stream, physical disk, volume, 
     *  console buffer, tape drive, socket, communications resource, mailslot, or pipe). 
     * @param lpBuffer
     *  A pointer to the buffer containing the data to be written to the file or device.
     * @param nNumberOfBytesToWrite
     *  The number of bytes to be written to the file or device.
     * @param lpNumberOfBytesWritten
     *  A pointer to the variable that receives the number of bytes written when using a synchronous hFile parameter. 
     * @param lpOverlapped
     *  A pointer to an OVERLAPPED structure is required if the hFile parameter was opened with FILE_FLAG_OVERLAPPED, 
     *  otherwise this parameter can be NULL. 
     * @return
     *  If the function succeeds, the return value is nonzero (TRUE).
     *  If the function fails, or is completing asynchronously, the return value is zero (FALSE). 
     *  To get extended error information, call the GetLastError function. 
     */
    boolean WriteFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite,
                      IntByReference lpNumberOfBytesWritten,
                      WinBase.OVERLAPPED lpOverlapped);

    /**
     * Creates or opens a named or unnamed event object.
     * @param lpEventAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure. If this parameter is NULL, 
     *  the handle cannot be inherited by child processes.
     * @param bManualReset
     *  If this parameter is TRUE, the function creates a manual-reset event object,
     *  which requires the use of the ResetEvent function to set the event state to nonsignaled. 
     *  If this parameter is FALSE, the function creates an auto-reset event object, and system 
     *  automatically resets the event state to nonsignaled after a single waiting thread has
     *  been released. 
     * @param bInitialState
     *  If this parameter is TRUE, the initial state of the event object is signaled; otherwise, 
     *  it is nonsignaled. 
     * @param lpName
     *  The name of the event object. The name is limited to MAX_PATH characters. Name comparison 
     *  is case sensitive. 
     * @return
     *  If the function succeeds, the return value is a handle to the event object. If the named event 
     *  object existed before the function call, the function returns a handle to the existing object
     *  and GetLastError returns ERROR_ALREADY_EXISTS. 
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError. 
     */
    HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES lpEventAttributes,
                       boolean bManualReset, boolean bInitialState,
                       String lpName);

    /**
     * Sets the specified event object to the signaled state.
     * @param hEvent
     *  A handle to the event object. The CreateEvent or OpenEvent function returns this handle.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean SetEvent(HANDLE hEvent);

    /**
     * Sets the specified event object to the signaled state and then resets it to the nonsignaled 
     * state after releasing the appropriate number of waiting threads.
     * @param hEvent
     *  A handle to the event object. The CreateEvent or OpenEvent function returns this handle. 
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean PulseEvent(HANDLE hEvent);

    /**
     * Creates or opens a named or unnamed file mapping object for a specified file.
     * @param hFile
     *  A handle to the file from which to create a file mapping object. 
     * @param lpAttributes
     *  A pointer to a SECURITY_ATTRIBUTES structure that determines whether a returned handle can be inherited by child processes. The lpSecurityDescriptor member of the SECURITY_ATTRIBUTES structure specifies a security descriptor for a new file mapping object. 
     * @param flProtect
     *  Specifies the page protection of the file mapping object. All mapped views of the object must be compatible with this protection. 
     * @param dwMaximumSizeHigh
     *  The high-order DWORD of the maximum size of the file mapping object.
     * @param dwMaximumSizeLow
     *  The low-order DWORD of the maximum size of the file mapping object. 
     * @param lpName
     *  The name of the file mapping object. 
     * @return
     *  If the function succeeds, the return value is a handle to the newly created file mapping object.
     *  If the object exists before the function call, the function returns a handle to the existing object (with its current size, not the specified size), and GetLastError returns ERROR_ALREADY_EXISTS. 
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    HANDLE CreateFileMapping(HANDLE hFile, WinBase.SECURITY_ATTRIBUTES lpAttributes,
                             int flProtect, int dwMaximumSizeHigh,
                             int dwMaximumSizeLow, String lpName);

    /**
     * Maps a view of a file mapping into the address space of a calling process.
     * @param hFileMappingObject
     *  A handle to a file mapping object. The CreateFileMapping and OpenFileMapping functions return this handle.
     * @param dwDesiredAccess
     *  The type of access to a file mapping object, which determines the protection of the pages.
     * @param dwFileOffsetHigh
     *  A high-order DWORD of the file offset where the view begins.
     * @param dwFileOffsetLow
     *  A low-order DWORD of the file offset where the view is to begin.
     * @param dwNumberOfBytesToMap
     *  The number of bytes of a file mapping to map to the view. 
     * @return
     *  If the function succeeds, the return value is the starting address of the mapped view.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
    Pointer MapViewOfFile(HANDLE hFileMappingObject, int dwDesiredAccess,
                          int dwFileOffsetHigh, int dwFileOffsetLow,
                          int dwNumberOfBytesToMap);

    /**
     * Unmaps a mapped view of a file from the calling process's address space.
     * @param lpBaseAddress
     *  A pointer to the base address of the mapped view of a file that is to be unmapped. 
     * @return
     *  If the function succeeds, the return value is the starting address of the mapped view.
     *  If the function fails, the return value is NULL. To get extended error information, call GetLastError.
     */
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
    
    /**
     * The OpenThread function opens an existing thread object.
     * @param dwDesiredAccess
     *  Access to the thread object. This access right is checked against any security 
     *  descriptor for the thread.
     * @param bInheritHandle
     *  If this parameter is TRUE, the new process inherits the handle. If the parameter 
     *  is FALSE, the handle is not inherited. 
     * @param dwThreadId
     *  Identifier of the thread to be opened. 
     * @return
     *  If the function succeeds, the return value is an open handle to the specified process.
     *  If the function fails, the return value is NULL. To get extended error information, 
     *  call GetLastError.
     */
    HANDLE OpenThread(int dwDesiredAccess, boolean bInheritHandle, int dwThreadId);
    
    /**
     * Creates a new process and its primary thread. The new process runs in the security context of the calling
     * process.
     * 
     * @param lpApplicationName The name of the module to be executed.
     * @param lpCommandLine The command line to be executed.
     * @param lpProcessAttributes
     *   A pointer to a SECURITY_ATTRIBUTES structure that determines whether the returned handle to the new process
     *   object can be inherited by child processes. If lpProcessAttributes is NULL, the handle cannot be inherited.
     *
     * @param lpThreadAttributes
     *   A pointer to a SECURITY_ATTRIBUTES structure that determines whether the returned handle to the new thread
     *   object can be inherited by child processes. If lpThreadAttributes is NULL, the handle cannot be inherited.
     *
     * @param bInheritHandles
     *   If this parameter TRUE, each inheritable handle in the calling process is inherited by the new process. If the
     *   parameter is FALSE, the handles are not inherited. Note that inherited handles have the same value and access
     *   rights as the original handles.
     *
     * @param dwCreationFlags The flags that control the priority class and the creation of the process.
     * @param lpEnvironment
     *   A pointer to the environment block for the new process. If this parameter is NULL, the new process uses the
     *   environment of the calling process.
     *
     * @param lpCurrentDirectory The full path to the current directory for the process.
     * @param lpStartupInfo A pointer to a STARTUPINFO or STARTUPINFOEX structure.
     * @param lpProcessInformation
     *   A pointer to a PROCESS_INFORMATION structure that receives identification information about the new process.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean CreateProcess(String lpApplicationName, String lpCommandLine, WinBase.SECURITY_ATTRIBUTES lpProcessAttributes,
                          WinBase.SECURITY_ATTRIBUTES lpThreadAttributes, boolean bInheritHandles, DWORD dwCreationFlags,
                          Pointer lpEnvironment, String lpCurrentDirectory, WinBase.STARTUPINFO lpStartupInfo,
                          WinBase.PROCESS_INFORMATION.ByReference lpProcessInformation);

    /**
     * This function returns a handle to an existing process object.
     * @param fdwAccess
     *  Not supported; set to zero. 
     * @param fInherit
     *  Not supported; set to FALSE. 
     * @param IDProcess
     *  Specifies the process identifier of the process to open. 
     * @return
     *  An open handle to the specified process indicates success. 
     *  NULL indicates failure. 
     *  To get extended error information, call GetLastError. 
     */
    HANDLE OpenProcess(int fdwAccess, boolean fInherit, int IDProcess);    

    /**
     * The GetTempPath function retrieves the path of the directory designated 
     * for temporary files.
     * @param nBufferLength
     *  Size of the string buffer identified by lpBuffer, in TCHARs.
     * @param buffer
     *  Pointer to a string buffer that receives the null-terminated string specifying the 
     *  temporary file path. The returned string ends with a backslash, for example, 
     *  C:\TEMP\. 
     * @return
     *  If the function succeeds, the return value is the length, in TCHARs, of the string 
     *  copied to lpBuffer, not including the terminating null character. If the return value 
     *  is greater than nBufferLength, the return value is the length, in TCHARs, of the 
     *  buffer required to hold the path.
     *  
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    DWORD GetTempPath(DWORD nBufferLength, char[] buffer);
    
    /**
     * The SetEnvironmentVariable function sets the contents of the specified environment
     * variable for the current process.
     * @param lpName
     *  Pointer to a string containing the name of the environment variable to set.
     * @param lpValue
     *  Pointer to a string containing the value to set it to.
     *  if this value is NULL, the variable is deleted from the current process' environment.
     *
     * @return
     *  If the function succeeds, the return value is non-zero.
     *  If the function fails, the return value is zero.  To get extended error information,
     *  call GetLastError.
     */
    boolean SetEnvironmentVariable(String lpName, String lpValue);

    /**
     * The GetVersion function returns the current version number of the operating system.
     * @return
     *  If the function succeeds, the return value includes the major and minor version numbers 
     *  of the operating system in the low order word, and information about the operating system 
     *  platform in the high order word.
     */
    DWORD GetVersion();

    /**
     * The GetVersionEx function obtains extended information about the version of the operating 
     * system that is currently running.
     * @param lpVersionInfo
     *  Pointer to an OSVERSIONINFO data structure that the function fills with operating system 
     *  version information. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError. The function fails if you specify an invalid value for the 
     *  dwOSVersionInfoSize member of the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFO lpVersionInfo);
    
    /**
     * The GetVersionEx function obtains extended information about the version of the operating 
     * system that is currently running.
     * @param lpVersionInfo
     *  Pointer to an OSVERSIONINFOEX data structure that the function fills with operating system 
     *  version information. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError. The function fails if you specify an invalid value for the 
     *  dwOSVersionInfoSize member of the OSVERSIONINFO or OSVERSIONINFOEX structure.
     */
    boolean GetVersionEx(OSVERSIONINFOEX lpVersionInfo);
    
    /**
     * The GetSystemInfo function returns information about the current system.
     * @param lpSystemInfo
     *  Pointer to a SYSTEM_INFO structure that receives the information. 
     */
    void GetSystemInfo(SYSTEM_INFO lpSystemInfo);
    
    /**
     * The GetNativeSystemInfo function retrieves information about the current system to an 
     * application running under WOW64. If the function is called from a 64-bit application, 
     * it is equivalent to the GetSystemInfo function.
     * @param lpSystemInfo
     *  Pointer to a SYSTEM_INFO structure that receives the information. 
     */
    void GetNativeSystemInfo(SYSTEM_INFO lpSystemInfo);
    
    /**
     * The IsWow64Process function determines whether the specified process is running under WOW64.
     * @param hProcess
     *  Handle to a process. 
     * @param Wow64Process
     *  Pointer to a value that is set to TRUE if the process is running under WOW64. 
     *  Otherwise, the value is set to FALSE. 
     * @return
     *  If the function succeeds, the return value is a nonzero value.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean IsWow64Process(HANDLE hProcess, IntByReference Wow64Process);
    
    /**
     * Retrieves information about the system's current usage of both physical and virtual memory.
     * @param lpBuffer
     *  A pointer to a MEMORYSTATUSEX structure that receives information about current memory availability.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, 
     *  call GetLastError.
     */
    boolean GlobalMemoryStatusEx(MEMORYSTATUSEX lpBuffer);
    
    /**
     * Retrieves the date and time that a file or directory was created, last accessed, and last modified.
     * 
     * @param hFile
     *   A handle to the file or directory for which dates and times are to be retrieved. The handle must have been
     *   created using the CreateFile function with the GENERIC_READ access right.
     *
     * @param lpCreationTime
     *   A pointer to a FILETIME structure to receive the date and time the file or directory was created. This
     *   parameter can be NULL if the application does not require this information.
     * 
     * @param lpLastAccessTime
     *   A pointer to a FILETIME structure to receive the date and time the file or directory was last accessed. The
     *   last access time includes the last time the file or directory was written to, read from, or, in the case of
     *   executable files, run. This parameter can be NULL if the application does not require this information.
     * 
     * @param lpLastWriteTime
     *   A pointer to a FILETIME structure to receive the date and time the file or directory was last written to,
     *   truncated, or overwritten (for example, with WriteFile or SetEndOfFile). This date and time is not updated when
     *   file attributes or security descriptors are changed. This parameter can be NULL if the application does not
     *   require this information.
     *
     * @return
     *   If the function succeeds, the return value is nonzero. If the function fails, the return value is zero. To get
     *   extended error information, call GetLastError.
     */
    boolean GetFileTime(HANDLE hFile, WinBase.FILETIME.ByReference lpCreationTime,
                        WinBase.FILETIME.ByReference lpLastAccessTime, WinBase.FILETIME.ByReference lpLastWriteTime);

    /**
     * Sets the date and time that the specified file or directory was created, last accessed, or last modified.
     *
     * @param hFile
     *  A handle to the file or directory. The handle must have been created using the CreateFile function with the
     *  FILE_WRITE_ATTRIBUTES access right. For more information, see File Security and Access Rights.
     * @param lpCreationTime
     *  A pointer to a FILETIME structure that contains the new creation date and time for the file or directory. This
     *  parameter can be NULL if the application does not need to change this information.
     * @param lpLastAccessTime
     *  A pointer to a FILETIME structure that contains the new last access date and time for the file or directory. The
     *  last access time includes the last time the file or directory was written to, read from, or (in the case of
     *  executable files) run. This parameter can be NULL if the application does not need to change this information.
     *
     *  To preserve the existing last access time for a file even after accessing a file, call SetFileTime immediately
     *  after opening the file handle with this parameter's FILETIME structure members initialized to 0xFFFFFFFF.
     * @param lpLastWriteTime
     *  A pointer to a FILETIME structure that contains the new last modified date and time for the file or directory.
     *  This parameter can be NULL if the application does not need to change this information.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *
     *  If the function fails, the return value is zero. To get extended error information,
     *  call GetLastError.
     */
    int SetFileTime(HANDLE hFile, WinBase.FILETIME lpCreationTime, WinBase.FILETIME lpLastAccessTime,
                        WinBase.FILETIME lpLastWriteTime);

    /**
     * Sets the attributes for a file or directory.
     * 
     * @param lpFileName
     *   The name of the file whose attributes are to be set.
     *
     *   The name is limited to MAX_PATH characters. To extend this limit to 32,767 wide characters, prepend "\\?\" to
     *   the path.
     *
     * @param dwFileAttributes
     *   The file attributes to set for the file. This parameter can be one or more values, combined using the
     *   bitwise-OR operator. However, all other values override FILE_ATTRIBUTE_NORMAL.
     * 
     * @return
     *   If the function succeeds, the return value is nonzero. If the function fails, the return value is zero. To get
     *   extended error information, call GetLastError.
     */
    boolean SetFileAttributes(String lpFileName, DWORD dwFileAttributes);

    /**
     * The GetLogicalDriveStrings function fills a buffer with strings that specify 
     * valid drives in the system.
     * @param nBufferLength
     *  Maximum size of the buffer pointed to by lpBuffer, in TCHARs. This size does not include 
     *  the terminating null character. If this parameter is zero, lpBuffer is not used.
     * @param lpBuffer
     *  Pointer to a buffer that receives a series of null-terminated strings, one for each valid 
     *  drive in the system, plus with an additional null character. Each string is a device name.
     * @return
     *  If the function succeeds, the return value is the length, in characters, of the strings 
     *  copied to the buffer, not including the terminating null character. Note that an ANSI-ASCII 
     *  null character uses one byte, but a Unicode null character uses two bytes.
     *  If the buffer is not large enough, the return value is greater than nBufferLength. It is 
     *  the size of the buffer required to hold the drive strings.
     *  If the function fails, the return value is zero. To get extended error information, use 
     *  the GetLastError function.
     */
    DWORD GetLogicalDriveStrings(DWORD nBufferLength, char[] lpBuffer);
    
    /**
     * The GetDiskFreeSpaceEx function retrieves information about the amount of space that is 
     * available on a disk volume, which is the total amount of space, the total amount of free 
     * space, and the total amount of free space available to the user that is associated with 
     * the calling thread.
     * @param lpDirectoryName
     *  A pointer to a null-terminated string that specifies a directory on a disk. 
     *  If this parameter is NULL, the function uses the root of the current disk. 
     *  If this parameter is a UNC name, it must include a trailing backslash, for example,
     *   \\MyServer\MyShare\.
     *   This parameter does not have to specify the root directory on a disk. The function 
     *   accepts any directory on a disk. 
     * @param lpFreeBytesAvailable
     *   A pointer to a variable that receives the total number of free bytes on a disk that
     *   are available to the user who is associated with the calling thread.  
     *   This parameter can be NULL.
     * @param lpTotalNumberOfBytes
     *  A pointer to a variable that receives the total number of bytes on a disk that are 
     *  available to the user who is associated with the calling thread. 
     *  This parameter can be NULL.
     * @param lpTotalNumberOfFreeBytes
     *  A pointer to a variable that receives the total number of free bytes on a disk. 
     *  This parameter can be NULL.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is 0 (zero). To get extended error information, 
     *  call GetLastError.
     */
    boolean GetDiskFreeSpaceEx(String lpDirectoryName, LARGE_INTEGER.ByReference lpFreeBytesAvailable,
    		LARGE_INTEGER.ByReference lpTotalNumberOfBytes, LARGE_INTEGER.ByReference lpTotalNumberOfFreeBytes);
    
    /**
     * Deletes an existing file.
     * @param filename
     *  The name of the file to be deleted.
     * @return
     *  If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero (0). To get extended error information, call GetLastError.
     */
    public boolean DeleteFile(String filename);
    
    /**
     * Creates an anonymous pipe, and returns handles to the read and write ends of the pipe.
     * @param hReadPipe A pointer to a variable that receives the read handle for the pipe.
     * @param hWritePipe A pointer to a variable that receives the write handle for the pipe.
     * @param lpPipeAttributes A pointer to a SECURITY_ATTRIBUTES structure that determines whether
     * the returned handle can be inherited by child processes.
     * @param nSize The size of the buffer for the pipe, in bytes.
     * @return If the function succeeds, the return value is nonzero.
     * If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    public boolean CreatePipe(
    		HANDLEByReference hReadPipe,
    		HANDLEByReference hWritePipe,
    		WinBase.SECURITY_ATTRIBUTES lpPipeAttributes,
    		int nSize);

    /**
     * Sets certain properties of an object handle.
     * @param hObject A handle to an object whose information is to be set.
     * @param dwMask A mask that specifies the bit flags to be changed. Use the same constants shown in the description of dwFlags.
     * @param dwFlags Set of bit flags that specifies properties of the object handle.
     * @return If the function succeeds, the return value is nonzero.
     *  If the function fails, the return value is zero. To get extended error information, call GetLastError.
     */
    boolean SetHandleInformation(
    		HANDLE hObject,
    		int dwMask,
    		int dwFlags);

    /**
     * Retrieves file system attributes for a specified file or directory.
     * @param lpFileName The name of the file or directory. Prepend \\?\ to the path for names up to 32,767 wide characters
     * @return INVALID_FILE_ATTRIBUTES if the function fails, otherwise the file attributes WinNT.FILE_ATTRIBUTE_*
     */
    public int GetFileAttributes(String lpFileName);

    /**
     * Sends a control code directly to a specified device driver, causing the corresponding device to perform the
     * corresponding operation.
     *
     * @param hDevice A handle to the device on which the operation is to be performed. The device is typically a
     *   volume, directory, file, or stream. To retrieve a device handle, use the CreateFile function. For more
     *   information, see Remarks.
     *
     * @param dwIoControlCode The control code for the operation. This value identifies the specific operation to be
     *   performed and the type of device on which to perform it. For a list of the control codes, see Remarks. The
     *   documentation for each control code provides usage details for the lpInBuffer, nInBufferSize, lpOutBuffer, and
     *   nOutBufferSize parameters.
     *
     * @param lpInBuffer A pointer to the input buffer that contains the data required to perform the operation. The
     *   format of this data depends on the value of the dwIoControlCode parameter. This parameter can be NULL if
     *   dwIoControlCode specifies an operation that does not require input data.
     *
     * @param nInBufferSize The size of the input buffer, in bytes.
     *
     * @param lpOutBuffer A pointer to the output buffer that is to receive the data returned by the operation. The
     *   format of this data depends on the value of the dwIoControlCode parameter. This parameter can be NULL if
     *   dwIoControlCode specifies an operation that does not return data.
     *
     * @param nOutBufferSize The size of the output buffer, in bytes.
     *
     * @param lpBytesReturned A pointer to a variable that receives the size of the data stored in the output buffer,
     *   in bytes. If the output buffer is too small to receive any data, the call fails, GetLastError returns
     *   ERROR_INSUFFICIENT_BUFFER, and lpBytesReturned is zero. If the output buffer is too small to hold all of the
     *   data but can hold some entries, some drivers will return as much data as fits. In this case, the call fails,
     *   GetLastError returns ERROR_MORE_DATA, and lpBytesReturned indicates the amount of data received. Your
     *   application should call DeviceIoControl again with the same operation, specifying a new starting point. If
     *   lpOverlapped is NULL, lpBytesReturned cannot be NULL. Even when an operation returns no output data and
     *   lpOutBuffer is NULL, DeviceIoControl makes use of lpBytesReturned. After such an operation, the value of
     *   lpBytesReturned is meaningless. If lpOverlapped is not NULL, lpBytesReturned can be NULL. If this parameter is
     *   not NULL and the operation returns data, lpBytesReturned is meaningless until the overlapped operation has
     *   completed. To retrieve the number of bytes returned, call GetOverlappedResult. If hDevice is associated with
     *   an I/O completion port, you can retrieve the number of bytes returned by calling GetQueuedCompletionStatus.
     *
     * @param lpOverlapped A pointer to an OVERLAPPED structure. If hDevice was opened without specifying
     *   FILE_FLAG_OVERLAPPED, lpOverlapped is ignored. If hDevice was opened with the FILE_FLAG_OVERLAPPED flag, the
     *   operation is performed as an overlapped (asynchronous) operation. In this case, lpOverlapped must point to a
     *   valid OVERLAPPED structure that contains a handle to an event object. Otherwise, the function fails in
     *   unpredictable ways. For overlapped operations, DeviceIoControl returns immediately, and the event object is
     *   signaled when the operation has been completed. Otherwise, the function does not return until the operation has
     *   been completed or an error occurs.
     *
     * @return
     *  If the function succeeds, the return value is nonzero.
     *
     *  If the function fails, the return value is zero. To get extended error information,
     *  call GetLastError.
     */
    boolean DeviceIoControl(HANDLE hDevice, int dwIoControlCode, Pointer lpInBuffer, int nInBufferSize,
        Pointer lpOutBuffer, int nOutBufferSize, IntByReference lpBytesReturned, Pointer lpOverlapped);
	
    /**
     * Retrieves information about the amount of space that is available on a disk volume, which is the total amount of 
     * space, the total amount of free space, and the total amount of free space available to the user that is 
     * associated with the calling thread.
     *
     * @param lpDirectoryName
     * @param lpFreeBytesAvailable
     * @param lpTotalNumberOfBytes
     * @param lpTotalNumberOfFreeBytes
     * @return
     *  If the function succeeds, the return value is nonzero.
     * 
     *  If the function fails, the return value is zero (0). To get extended error information, 
     *  call GetLastError.
     */
     boolean GetDiskFreeSpaceEx(String lpDirectoryName, LongByReference lpFreeBytesAvailable, LongByReference lpTotalNumberOfBytes, LongByReference lpTotalNumberOfFreeBytes);

    /**
     * Takes a snapshot of the specified processes, as well as the heaps, modules, and threads used by these processes.
     *  
     * @param dwFlags
     *   The portions of the system to be included in the snapshot.
     * 
     * @param th32ProcessID
     *   The process identifier of the process to be included in the snapshot. This parameter can be zero to indicate
     *   the current process. This parameter is used when the TH32CS_SNAPHEAPLIST, TH32CS_SNAPMODULE,
     *   TH32CS_SNAPMODULE32, or TH32CS_SNAPALL value is specified. Otherwise, it is ignored and all processes are
     *   included in the snapshot.
     *
     *   If the specified process is the Idle process or one of the CSRSS processes, this function fails and the last
     *   error code is ERROR_ACCESS_DENIED because their access restrictions prevent user-level code from opening them.
     *
     *   If the specified process is a 64-bit process and the caller is a 32-bit process, this function fails and the
     *   last error code is ERROR_PARTIAL_COPY (299).
     *
     * @return
     *   If the function succeeds, it returns an open handle to the specified snapshot.
     *
     *   If the function fails, it returns INVALID_HANDLE_VALUE. To get extended error information, call GetLastError.
     *   Possible error codes include ERROR_BAD_LENGTH.
     */
    HANDLE CreateToolhelp32Snapshot(DWORD dwFlags, DWORD th32ProcessID);

    /**
     * Retrieves information about the first process encountered in a system snapshot.
     *
     * @param hSnapshot A handle to the snapshot returned from a previous call to the CreateToolhelp32Snapshot function.
     * @param lppe A pointer to a PROCESSENTRY32 structure. It contains process information such as the name of the
     *   executable file, the process identifier, and the process identifier of the parent process.
     * @return
     *   Returns TRUE if the first entry of the process list has been copied to the buffer or FALSE otherwise. The
     *   ERROR_NO_MORE_FILES error value is returned by the GetLastError function if no processes exist or the snapshot
     *   does not contain process information.
     */
    boolean Process32First(HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32.ByReference lppe);

    /**
     * Retrieves information about the next process recorded in a system snapshot.
     *
     * @param hSnapshot A handle to the snapshot returned from a previous call to the CreateToolhelp32Snapshot function.
     * @param lppe A pointer to a PROCESSENTRY32 structure.
     * @return
     *   Returns TRUE if the next entry of the process list has been copied to the buffer or FALSE otherwise. The
     *   ERROR_NO_MORE_FILES error value is returned by the GetLastError function if no processes exist or the snapshot
     *   does not contain process information.
     */
    boolean Process32Next(HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32.ByReference lppe);
}
