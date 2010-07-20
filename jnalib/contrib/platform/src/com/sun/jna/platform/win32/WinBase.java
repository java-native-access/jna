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
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDLONG;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Ported from Winbase.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public abstract class WinBase {

    /** Constant value representing an invalid HANDLE. */
    public static HANDLE INVALID_HANDLE_VALUE = new HANDLE(Pointer.createConstant(
    		Pointer.SIZE == 8 ? -1 : 0xFFFFFFFFL));

	public static final int WAIT_FAILED = 0xFFFFFFFF;
	public static final int WAIT_OBJECT_0 = ((NTStatus.STATUS_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	public static final int WAIT_ABANDONED_0 = ((NTStatus.STATUS_ABANDONED_WAIT_0 ) + 0 );
	
	/**
	 * Maximum computer name length.
	 * @return 15 on MAC, 31 on everything else.
	 */
	public static int MAX_COMPUTERNAME_LENGTH() {
		if (Platform.isMac()) {
			return 15;
		} else {
			return 31;			
		}
	}
	
	/**
	 * This logon type is intended for users who will be interactively using the computer, such 
	 * as a user being logged on by a terminal server, remote shell, or similar process. This 
	 * logon type has the additional expense of caching logon information for disconnected operations; 
	 * therefore, it is inappropriate for some client/server applications, such as a mail server. 
	 */
	public static final int LOGON32_LOGON_INTERACTIVE = 2;
	/**
	 * This logon type is intended for high performance servers to authenticate plaintext passwords. 
	 * The LogonUser function does not cache credentials for this logon type.
	 */
	public static final int LOGON32_LOGON_NETWORK = 3;
	/**
	 * This logon type is intended for batch servers, where processes may be executing on behalf 
	 * of a user without their direct intervention. This type is also for higher performance servers 
	 * that process many plaintext authentication attempts at a time, such as mail or Web servers. 
	 * The LogonUser function does not cache credentials for this logon type.
	 */
	public static final int LOGON32_LOGON_BATCH = 4;
	/**
	 * Indicates a service-type logon. The account provided must have the service privilege enabled.
	 */
	public static final int LOGON32_LOGON_SERVICE = 5;
	/**
	 * This logon type is for GINA DLLs that log on users who will be interactively using the computer. 
	 * This logon type can generate a unique audit record that shows when the workstation was unlocked.
	 */
	public static final int LOGON32_LOGON_UNLOCK = 7;
	/**
	 * This logon type preserves the name and password in the authentication package, which allows the 
	 * server to make connections to other network servers while impersonating the client. A server can 
	 * accept plaintext credentials from a client, call LogonUser, verify that the user can access the 
	 * system across the network, and still communicate with other servers.
	 */
	public static final int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
	/**
	 * This logon type allows the caller to clone its current token and specify new credentials for 
	 * outbound connections. The new logon session has the same local identifier but uses different 
	 * credentials for other network connections. This logon type is supported only by the 
	 * LOGON32_PROVIDER_WINNT50 logon provider.
	 */
	public static final int LOGON32_LOGON_NEW_CREDENTIALS = 9;

	/**
	 * Use the standard logon provider for the system. The default security provider is negotiate, 
	 * unless you pass NULL for the domain name and the user name is not in UPN format. In this case, 
	 * the default provider is NTLM. 
	 */
	public static final int LOGON32_PROVIDER_DEFAULT = 0;
	
	/**
	 * Use the Windows NT 3.5 logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT35 = 1;
	/**
	 * Use the NTLM logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT40 = 2;
	/**
	 * Use the negotiate logon provider.
	 */
	public static final int LOGON32_PROVIDER_WINNT50 = 3;	
	
	
    /**
    * The FILETIME structure is a 64-bit value representing the number of 
    * 100-nanosecond intervals since January 1, 1601 (UTC).
    * Conversion code in this class Copyright 2002-2004 Apache Software Foundation.
    * @author Rainer Klute (klute@rainer-klute.de) for the Apache Software Foundation (org.apache.poi.hpsf)
    */
	public static class FILETIME extends Structure {
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
	
	/* Local Memory Flags */
	public static final int  LMEM_FIXED = 0x0000;
	public static final int  LMEM_MOVEABLE = 0x0002;
	public static final int  LMEM_NOCOMPACT = 0x0010;
	public static final int  LMEM_NODISCARD = 0x0020;
	public static final int  LMEM_ZEROINIT = 0x0040;
	public static final int  LMEM_MODIFY = 0x0080;
	public static final int  LMEM_DISCARDABLE = 0x0F00;
	public static final int  LMEM_VALID_FLAGS = 0x0F72;
	public static final int  LMEM_INVALID_HANDLE = 0x8000;

	public static final int  LHND = (LMEM_MOVEABLE | LMEM_ZEROINIT);
	public static final int  LPTR = (LMEM_FIXED | LMEM_ZEROINIT);

	/* Flags returned by LocalFlags (in addition to LMEM_DISCARDABLE) */
	public static final int  LMEM_DISCARDED = 0x4000;
	public static final int  LMEM_LOCKCOUNT = 0x00FF;	
	
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
    public static final int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x00000100;
    /**
     * Insert sequences in the message definition are to be ignored and passed through
     * to the output buffer unchanged. This flag is useful for fetching a message for 
     * later formatting. If this flag is set, the Arguments parameter is ignored.
     */
    public static final int FORMAT_MESSAGE_IGNORE_INSERTS  = 0x00000200;
    /**
     * The lpSource parameter is a pointer to a null-terminated message definition.
     * The message definition may contain insert sequences, just as the message text 
     * in a message table resource may. Cannot be used with FORMAT_MESSAGE_FROM_HMODULE
     * or FORMAT_MESSAGE_FROM_SYSTEM.
     */
    public static final int FORMAT_MESSAGE_FROM_STRING     = 0x00000400;
    /**
     * The lpSource parameter is a module handle containing the message-table 
     * resource(s) to search. If this lpSource handle is NULL, the current process's
     * application image file will be searched. Cannot be used with 
     * FORMAT_MESSAGE_FROM_STRING.
     */
    public static final int FORMAT_MESSAGE_FROM_HMODULE    = 0x00000800;
    /**
     * The function should search the system message-table resource(s) for the 
     * requested message. If this flag is specified with FORMAT_MESSAGE_FROM_HMODULE,
     * the function searches the system message table if the message is not found in 
     * the module specified by lpSource. Cannot be used with FORMAT_MESSAGE_FROM_STRING. 
     * If this flag is specified, an application can pass the result of the 
     * GetLastError function to retrieve the message text for a system-defined error.
     */
    public static final int FORMAT_MESSAGE_FROM_SYSTEM     = 0x00001000;
    /**
     * The Arguments parameter is not a va_list structure, but is a pointer to an array
     * of values that represent the arguments. This flag cannot be used with 64-bit 
     * argument values. If you are using 64-bit values, you must use the va_list 
     * structure.
     */
    public static final int FORMAT_MESSAGE_ARGUMENT_ARRAY  = 0x00002000;

    /**
     * The drive type cannot be determined.
     */
    public static final int DRIVE_UNKNOWN = 0;
    /**
     * The root path is invalid, for example, no volume is mounted at the path.
     */
    public static final int DRIVE_NO_ROOT_DIR = 1;
    /**
     * The drive is a type that has removable media, for example, a floppy drive 
     * or removable hard disk.
     */
    public static final int DRIVE_REMOVABLE = 2;
    /**
     * The drive is a type that cannot be removed, for example, a fixed hard drive.
     */
    public static final int DRIVE_FIXED = 3;
    /**
     * The drive is a remote (network) drive.
     */
    public static final int DRIVE_REMOTE = 4;
    /**
     * The drive is a CD-ROM drive.
     */
    public static final int DRIVE_CDROM = 5;
    /**
     * The drive is a RAM disk.
     */
    public static final int DRIVE_RAMDISK = 6;    
    
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
    
    public static final int INFINITE = 0xFFFFFFFF;

    /**
     * Contains information about the current computer system. This includes the architecture and 
     * type of the processor, the number of processors in the system, the page size, and other such
     * information.
     */
    public static class SYSTEM_INFO extends Structure {
    	
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
			 * Processor architecture.
			 */
			public PI.ByReference pi;
		}
		
		/**
		 * Processor architecture.
		 */
		public UNION.ByReference processorArchitecture;
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
}
