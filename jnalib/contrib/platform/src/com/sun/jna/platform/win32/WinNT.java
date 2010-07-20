/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import com.sun.jna.FromNativeContext;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * Ported from WinNT.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinNT extends StdCallLibrary {

	//
	// The following are masks for the predefined standard access types
	//

	public static final int DELETE = 0x00010000;
	public static final int READ_CONTROL = 0x00020000;
	public static final int WRITE_DAC = 0x00040000;
	public static final int WRITE_OWNER = 0x00080000;
	public static final int SYNCHRONIZE = 0x00100000;

	public static final int STANDARD_RIGHTS_REQUIRED = 0x000F0000;

	public static final int STANDARD_RIGHTS_READ = READ_CONTROL;
	public static final int STANDARD_RIGHTS_WRITE = READ_CONTROL;
	public static final int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;

	public static final int STANDARD_RIGHTS_ALL = 0x001F0000;

	public static final int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

	//
	// Token Specific Access Rights.
	//

	/**
	 * Required to attach a primary token to a process. The
	 * SE_ASSIGNPRIMARYTOKEN_NAME privilege is also required to accomplish this
	 * task.
	 */
	public static final int TOKEN_ASSIGN_PRIMARY = 0x0001;
	/**
	 * Required to duplicate an access token.
	 */
	public static final int TOKEN_DUPLICATE = 0x0002;
	/**
	 * Required to attach an impersonation access token to a process.
	 */
	public static final int TOKEN_IMPERSONATE = 0x0004;
	/**
	 * Required to query an access token.
	 */
	public static final int TOKEN_QUERY = 0x0008;
	/**
	 * Required to query the source of an access token.
	 */
	public static final int TOKEN_QUERY_SOURCE = 0x0010;
	/**
	 * Required to enable or disable the privileges in an access token.
	 */
	public static final int TOKEN_ADJUST_PRIVILEGES = 0x0020;
	/**
	 * Required to adjust the attributes of the groups in an access token.
	 */
	public static final int TOKEN_ADJUST_GROUPS = 0x0040;
	/**
	 * Required to change the default owner, primary group, or DACL of an access
	 * token.
	 */
	public static final int TOKEN_ADJUST_DEFAULT = 0x0080;
	/**
	 * Required to adjust the session ID of an access token. The SE_TCB_NAME
	 * privilege is required.
	 */
	public static final int TOKEN_ADJUST_SESSIONID = 0x0100;

	public static final int TOKEN_ALL_ACCESS_P = STANDARD_RIGHTS_REQUIRED
			| TOKEN_ASSIGN_PRIMARY | TOKEN_DUPLICATE | TOKEN_IMPERSONATE
			| TOKEN_QUERY | TOKEN_QUERY_SOURCE | TOKEN_ADJUST_PRIVILEGES
			| TOKEN_ADJUST_GROUPS | TOKEN_ADJUST_DEFAULT;

	/**
	 * Combines all possible access rights for a token.
	 */
	public static final int TOKEN_ALL_ACCESS = TOKEN_ALL_ACCESS_P
			| TOKEN_ADJUST_SESSIONID;

	/**
	 * Combines STANDARD_RIGHTS_READ and TOKEN_QUERY.
	 */
	public static final int TOKEN_READ = STANDARD_RIGHTS_READ | TOKEN_QUERY;

	/**
	 * Combines STANDARD_RIGHTS_WRITE, TOKEN_ADJUST_PRIVILEGES,
	 * TOKEN_ADJUST_GROUPS, and TOKEN_ADJUST_DEFAULT.
	 */
	public static final int TOKEN_WRITE = STANDARD_RIGHTS_WRITE
			| TOKEN_ADJUST_PRIVILEGES | TOKEN_ADJUST_GROUPS
			| TOKEN_ADJUST_DEFAULT;

	/**
	 * Combines STANDARD_RIGHTS_EXECUTE and TOKEN_IMPERSONATE.
	 */
	public static final int TOKEN_EXECUTE = STANDARD_RIGHTS_EXECUTE;

	public static final int THREAD_TERMINATE = 0x0001;
	public static final int THREAD_SUSPEND_RESUME = 0x0002;
	public static final int THREAD_GET_CONTEXT = 0x0008;
	public static final int THREAD_SET_CONTEXT = 0x0010;
	public static final int THREAD_QUERY_INFORMATION = 0x0040;
	public static final int THREAD_SET_INFORMATION = 0x0020;
	public static final int THREAD_SET_THREAD_TOKEN = 0x0080;
	public static final int THREAD_IMPERSONATE = 0x0100;
	public static final int THREAD_DIRECT_IMPERSONATION = 0x0200;
	public static final int THREAD_SET_LIMITED_INFORMATION = 0x0400;
	public static final int THREAD_QUERY_LIMITED_INFORMATION = 0x0800;
	public static final int THREAD_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0x3FF;
	
	/**
	 * The SECURITY_IMPERSONATION_LEVEL enumeration type contains values that specify security 
	 * impersonation levels. Security impersonation levels govern the degree to which a server 
	 * process can act on behalf of a client process.
	 */
	public abstract class SECURITY_IMPERSONATION_LEVEL {
		/**
		 * The server process cannot obtain identification information about the client,
		 * and it cannot impersonate the client. It is defined with no value given, and 
		 * thus, by ANSI C rules, defaults to a value of zero. 
		 */
		public static final int SecurityAnonymous = 0;
		/**
		 * The server process can obtain information about the client, such as security 
		 * identifiers and privileges, but it cannot impersonate the client. This is useful
		 * for servers that export their own objects, for example, database products that 
		 * export tables and views. Using the retrieved client-security information, the 
		 * server can make access-validation decisions without being able to use other 
		 * services that are using the client's security context. 
		 */
		public static final int SecurityIdentification = 1;
		/**
		 * The server process can impersonate the client's security context on its local system. 
		 * The server cannot impersonate the client on remote systems. 
		 */
		public static final int SecurityImpersonation = 2;
		/**
		 * The server process can impersonate the client's security context on remote systems. 
		 */
		public static final int SecurityDelegation = 3;
	}
	
	/**
	 * The TOKEN_INFORMATION_CLASS enumeration type contains values that specify the type of 
	 * information being assigned to or retrieved from an access token. 
	 */
	public abstract class TOKEN_INFORMATION_CLASS {
		public static final int TokenUser = 1;
	    public static final int  TokenGroups = 2; 
	    public static final int  TokenPrivileges = 3; 
	    public static final int  TokenOwner = 4;
	    public static final int  TokenPrimaryGroup = 5; 
	    public static final int  TokenDefaultDacl = 6;
	    public static final int  TokenSource = 7;
	    public static final int  TokenType = 8;
	    public static final int  TokenImpersonationLevel = 9; 
	    public static final int  TokenStatistics = 10;
	    public static final int  TokenRestrictedSids = 11; 
	    public static final int  TokenSessionId = 12;
	    public static final int  TokenGroupsAndPrivileges = 13; 
	    public static final int  TokenSessionReference = 14;
	    public static final int  TokenSandBoxInert = 15;
	    public static final int  TokenAuditPolicy = 16;
	    public static final int  TokenOrigin = 17;
	    public static final int  TokenElevationType = 18;
	    public static final int  TokenLinkedToken = 19; 
	    public static final int  TokenElevation = 20;
	    public static final int  TokenHasRestrictions = 21;
	    public static final int  TokenAccessInformation = 22;
	    public static final int  TokenVirtualizationAllowed = 23; 
	    public static final int  TokenVirtualizationEnabled = 24; 
	    public static final int  TokenIntegrityLevel = 25;
	    public static final int  TokenUIAccess = 26;
	    public static final int  TokenMandatoryPolicy = 27; 
	    public static final int  TokenLogonSid = 28;
	}
	
	/**
	 * The SID_AND_ATTRIBUTES structure represents a security identifier (SID) and its 
	 * attributes. SIDs are used to uniquely identify users or groups.
	 */
	public static class SID_AND_ATTRIBUTES extends Structure {
		public SID_AND_ATTRIBUTES() {
			super();
		}
		
		public SID_AND_ATTRIBUTES(Pointer memory) {
			useMemory(memory);
			read();
		}
		
		/**
		 * Pointer to a SID structure. 
		 */
		public PSID.ByReference Sid;
		
		/**
		 * Specifies attributes of the SID. This value contains up to 32 one-bit flags.
		 * Its meaning depends on the definition and use of the SID. 
		 */
		public int Attributes;
	}
	
	/**
	 * The TOKEN_OWNER structure contains the default owner 
	 * security identifier (SID) that will be applied to newly created objects.
	 */
	public static class TOKEN_OWNER extends Structure {
		public TOKEN_OWNER() {
			super();
		}

		public TOKEN_OWNER(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * Pointer to a SID structure representing a user who will become the owner of any 
		 * objects created by a process using this access token. The SID must be one of the 
		 * user or group SIDs already in the token. 
		 */
		public PSID.ByReference Owner; // PSID
	}

	public static class PSID extends Structure {
		
    	public static class ByReference extends PSID implements Structure.ByReference {
    		
    	}
		
		public PSID() {
			super();
		}
		
		public PSID(byte[] data) {
			super();
			Memory memory = new Memory(data.length);
			memory.write(0, data, 0, data.length);
			setPointer(memory);
		}
		
		public PSID(int size) {
			super(new Memory(size));
		}
		
		public PSID(Pointer memory) {
			super(memory);
			read();
		}

        public void setPointer(Pointer p) {
            useMemory(p);
            read();
        }
		
        public byte[] getBytes() {
        	int len = Advapi32.INSTANCE.GetLengthSid(this);
        	return getPointer().getByteArray(0, len);
        }
        
        public Pointer sid;                
	}
	
    public static class PSIDByReference extends ByReference {
        public PSIDByReference() {
            this(null);
        }
        public PSIDByReference(PSID h) {
            super(Pointer.SIZE);
            setValue(h);
        }
        public void setValue(PSID h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }
        public PSID getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null)
                return null;
            PSID h = new PSID();
            h.setPointer(p);
            return h;
        }
    }	
	
	/**
	 * The TOKEN_USER structure identifies the user associated with an access token.
	 */
	public static class TOKEN_USER extends Structure {
		public TOKEN_USER() {
			super();
		}
		
		public TOKEN_USER(Pointer memory) {
			super(memory);
			read();
		}
		
		public TOKEN_USER(int size) {
			super(new Memory(size));
		}

		/**
		 * Specifies a SID_AND_ATTRIBUTES structure representing the user associated with 
		 * the access token. There are currently no attributes defined for user security 
		 * identifiers (SIDs). 
		 */
		public SID_AND_ATTRIBUTES User;
	}
	
	/**
	 * The TOKEN_GROUPS structure contains information about the group security identifiers 
	 * (SIDs) in an access token.
	 */
	public static class TOKEN_GROUPS extends Structure {
		public TOKEN_GROUPS() {
			super();
		}
		
		public TOKEN_GROUPS(Pointer memory) {
			super(memory);
			read();
		}
		
		public TOKEN_GROUPS(int size) {
			super(new Memory(size));
		}

		/**
		 * Specifies the number of groups in the access token. 
		 */
		public int GroupCount;
		public SID_AND_ATTRIBUTES Group0;
		
		/**
		 * Specifies an array of SID_AND_ATTRIBUTES structures that contain a set of SIDs 
		 * and corresponding attributes. 
		 */
		public SID_AND_ATTRIBUTES[] getGroups() {
			return (SID_AND_ATTRIBUTES[]) Group0.toArray(GroupCount);
		}
	}

	/**
	 * The SID_NAME_USE enumeration type contains values that specify the type of a security identifier (SID).
	 */
	public abstract class SID_NAME_USE { 
		/**
		 * Indicates a user SID. 
		 */
	    public static final int SidTypeUser = 1;
	    /**
	     * Indicates a group SID. 
	     */
	    public static final int SidTypeGroup = 2;
	    /**
	     * Indicates a domain SID. 
	     */
	    public static final int SidTypeDomain = 3;
	    /**
	     * Indicates an alias SID. 
	     */
	    public static final int SidTypeAlias = 4;
	    /**
	     * Indicates a SID for a well-known group. 
	     */
	    public static final int SidTypeWellKnownGroup = 5;
	    /**
	     * Indicates a SID for a deleted account. 
	     */
	    public static final int SidTypeDeletedAccount = 6;
	    /**
	     * Indicates an invalid SID. 
	     */
	    public static final int SidTypeInvalid = 7;
	    /**
	     * Indicates an unknown SID type. 
	     */
	    public static final int SidTypeUnknown = 8;
	    /**
	     * Indicates a SID for a computer. 
	     */
	    public static final int SidTypeComputer = 9;
	    /**
	     * ?
	     */
	    public static final int SidTypeLabel = 10;
	}	
	
    public static final int FILE_LIST_DIRECTORY = 0x00000001;

    public static final int CREATE_NEW =         1;
    public static final int CREATE_ALWAYS =      2;
    public static final int OPEN_EXISTING =      3;
    public static final int OPEN_ALWAYS =        4;
    public static final int TRUNCATE_EXISTING =  5;

    public static final int FILE_FLAG_WRITE_THROUGH =        0x80000000;
    public static final int FILE_FLAG_OVERLAPPED =           0x40000000;
    public static final int FILE_FLAG_NO_BUFFERING =         0x20000000;
    public static final int FILE_FLAG_RANDOM_ACCESS =        0x10000000;
    public static final int FILE_FLAG_SEQUENTIAL_SCAN =      0x08000000;
    public static final int FILE_FLAG_DELETE_ON_CLOSE =      0x04000000;
    public static final int FILE_FLAG_BACKUP_SEMANTICS =     0x02000000;
    public static final int FILE_FLAG_POSIX_SEMANTICS =      0x01000000;
    public static final int FILE_FLAG_OPEN_REPARSE_POINT =   0x00200000;
    public static final int FILE_FLAG_OPEN_NO_RECALL =       0x00100000;

    public static final int GENERIC_WRITE = 0x40000000;
    
    public static class SECURITY_ATTRIBUTES extends Structure {
        public final int nLength = size();
        public Pointer lpSecurityDescriptor;
        public boolean bInheritHandle;
    }	
    
    public static final int PAGE_READONLY = 0x02;
    public static final int PAGE_READWRITE = 0x04;
    public static final int PAGE_WRITECOPY = 0x08;
    public static final int PAGE_EXECUTE = 0x10;
    public static final int PAGE_EXECUTE_READ = 0x20;
    public static final int PAGE_EXECUTE_READWRITE = 0x40;    
    
    public static final int SECTION_QUERY = 0x0001;
    public static final int SECTION_MAP_WRITE = 0x0002;
    public static final int SECTION_MAP_READ = 0x0004;
    public static final int SECTION_MAP_EXECUTE = 0x0008;
    public static final int SECTION_EXTEND_SIZE = 0x0010;    
    
	public static final int FILE_SHARE_READ  = 0x00000001;
	public static final int FILE_SHARE_WRITE = 0x00000002; 
	public static final int FILE_SHARE_DELETE = 0x00000004; 
	public static final int FILE_ATTRIBUTE_READONLY = 0x00000001; 
	public static final int FILE_ATTRIBUTE_HIDDEN = 0x00000002; 
	public static final int FILE_ATTRIBUTE_SYSTEM = 0x00000004; 
	public static final int FILE_ATTRIBUTE_DIRECTORY = 0x00000010; 
	public static final int FILE_ATTRIBUTE_ARCHIVE = 0x00000020; 
	public static final int FILE_ATTRIBUTE_DEVICE = 0x00000040; 
	public static final int FILE_ATTRIBUTE_NORMAL = 0x00000080; 
	public static final int FILE_ATTRIBUTE_TEMPORARY = 0x00000100; 
	public static final int FILE_ATTRIBUTE_SPARSE_FILE = 0x00000200; 
	public static final int FILE_ATTRIBUTE_REPARSE_POINT = 0x00000400; 
	public static final int FILE_ATTRIBUTE_COMPRESSED = 0x00000800; 
	public static final int FILE_ATTRIBUTE_OFFLINE = 0x00001000; 
	public static final int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x00002000; 
	public static final int FILE_ATTRIBUTE_ENCRYPTED = 0x00004000;
	public static final int FILE_ATTRIBUTE_VIRTUAL = 0x00010000;
	public static final int FILE_NOTIFY_CHANGE_FILE_NAME = 0x00000001; 
	public static final int FILE_NOTIFY_CHANGE_DIR_NAME = 0x00000002; 
	public static final int FILE_NOTIFY_CHANGE_NAME = 0x00000003;
	public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 0x00000004; 
	public static final int FILE_NOTIFY_CHANGE_SIZE = 0x00000008; 
	public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 0x00000010; 
	public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 0x00000020; 
	public static final int FILE_NOTIFY_CHANGE_CREATION = 0x00000040; 
	public static final int FILE_NOTIFY_CHANGE_SECURITY = 0x00000100; 
	public static final int FILE_ACTION_ADDED  = 0x00000001; 
	public static final int FILE_ACTION_REMOVED  = 0x00000002; 
	public static final int FILE_ACTION_MODIFIED = 0x00000003; 
	public static final int FILE_ACTION_RENAMED_OLD_NAME = 0x00000004; 
	public static final int FILE_ACTION_RENAMED_NEW_NAME = 0x00000005;
	public static final int FILE_CASE_SENSITIVE_SEARCH = 0x00000001; 
	public static final int FILE_CASE_PRESERVED_NAMES = 0x00000002;
	public static final int FILE_UNICODE_ON_DISK = 0x00000004;
	public static final int FILE_PERSISTENT_ACLS = 0x00000008;
	public static final int FILE_FILE_COMPRESSION = 0x00000010; 
	public static final int FILE_VOLUME_QUOTAS = 0x00000020;
	public static final int FILE_SUPPORTS_SPARSE_FILES = 0x00000040; 
	public static final int FILE_SUPPORTS_REPARSE_POINTS = 0x00000080; 
	public static final int FILE_SUPPORTS_REMOTE_STORAGE = 0x00000100; 
	public static final int FILE_VOLUME_IS_COMPRESSED = 0x00008000; 
	public static final int FILE_SUPPORTS_OBJECT_IDS = 0x00010000; 
	public static final int FILE_SUPPORTS_ENCRYPTION = 0x00020000; 
	public static final int FILE_NAMED_STREAMS = 0x00040000; 
	public static final int FILE_READ_ONLY_VOLUME = 0x00080000; 
	public static final int FILE_SEQUENTIAL_WRITE_ONCE = 0x00100000; 
	public static final int FILE_SUPPORTS_TRANSACTIONS = 0x00200000; 
	

    /** 
     * The FILE_NOTIFY_INFORMATION structure describes the changes found by the 
     * ReadDirectoryChangesW function.
     * 
     * This structure is non-trivial since it is a pattern stamped into a large 
     * block of result memory rather than something that stands alone or is used 
     * for input.
     */
	public static class FILE_NOTIFY_INFORMATION extends Structure {
		public int NextEntryOffset;
	    public int Action;
	    public int FileNameLength;
	    // filename is not nul-terminated, so we can't use a String/WString
	    public char[] FileName = new char[1];
	    
	    private FILE_NOTIFY_INFORMATION() { 
	    	
	    }
	    
	    public FILE_NOTIFY_INFORMATION(int size) {
	    	if (size < size()) {
	           throw new IllegalArgumentException("Size must greater than "	
	        		   + size() + ", requested " + size);
	    	}
	    	allocateMemory(size);
	    }
	   
	    /** WARNING: this filename may be either the short or long form of the filename. */
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
	
	/**
	 * Registry options.
	 */
	
	public static final int KEY_QUERY_VALUE = 0x0001;
	public static final int KEY_SET_VALUE = 0x0002;
	public static final int KEY_CREATE_SUB_KEY = 0x0004;
	public static final int KEY_ENUMERATE_SUB_KEYS = 0x0008;
	public static final int KEY_NOTIFY = 0x0010;
	public static final int KEY_CREATE_LINK = 0x0020;
	public static final int KEY_WOW64_32KEY = 0x0200;
	public static final int KEY_WOW64_64KEY = 0x0100;
	public static final int KEY_WOW64_RES = 0x0300;

	public static final int KEY_READ = STANDARD_RIGHTS_READ |
		KEY_QUERY_VALUE |
		KEY_ENUMERATE_SUB_KEYS |
		KEY_NOTIFY
		& (~SYNCHRONIZE);

	public static final int KEY_WRITE = STANDARD_RIGHTS_WRITE |
	  	KEY_SET_VALUE |
	  	KEY_CREATE_SUB_KEY
	  	& (~SYNCHRONIZE);	

	public static final int KEY_EXECUTE = KEY_READ
	  	& (~SYNCHRONIZE);

	public static final int KEY_ALL_ACCESS = STANDARD_RIGHTS_ALL |
	  	KEY_QUERY_VALUE |
	  	KEY_SET_VALUE |
	  	KEY_CREATE_SUB_KEY |
	  	KEY_ENUMERATE_SUB_KEYS |
	  	KEY_NOTIFY |
	  	KEY_CREATE_LINK
	  	& (~SYNCHRONIZE);

	//
	// Open/Create Options
	//

	/**
	 * Parameter is reserved.
	 */
	public static final int REG_OPTION_RESERVED = 0x00000000;
	/**
	 * Key is preserved when system is rebooted.
	 */
	public static final int REG_OPTION_NON_VOLATILE = 0x00000000; 
	/**
	 * Key is not preserved when system is rebooted.
	 */
	public static final int REG_OPTION_VOLATILE = 0x00000001;
	/**
	 * Created key is a symbolic link.
	 */
	public static final int REG_OPTION_CREATE_LINK = 0x00000002;
	/**
	 * Open for backup or restore special access rules privilege required.
	 */
	public static final int REG_OPTION_BACKUP_RESTORE = 0x00000004;
	/**
	 * Open symbolic link.
	 */
	public static final int REG_OPTION_OPEN_LINK = 0x00000008;

	public static final int REG_LEGAL_OPTION = REG_OPTION_RESERVED |
	 	REG_OPTION_NON_VOLATILE |
	 	REG_OPTION_VOLATILE |
	 	REG_OPTION_CREATE_LINK |
	 	REG_OPTION_BACKUP_RESTORE |
	 	REG_OPTION_OPEN_LINK;

	//
	// Key creation/open disposition
	//

	/**
	 * New Registry Key created.
	 */
	public static final int REG_CREATED_NEW_KEY = 0x00000001;
	/**
	 * Existing Key opened.
	 */
	public static final int REG_OPENED_EXISTING_KEY = 0x00000002;

	public static final int REG_STANDARD_FORMAT = 1;
	public static final int REG_LATEST_FORMAT = 2;
	public static final int REG_NO_COMPRESSION = 4;

	//
	// Key restore & hive load flags
	//

	/**
	 * Restore whole hive volatile.
	 */
	public static final int REG_WHOLE_HIVE_VOLATILE = 0x00000001;
	/**
	 * Unwind changes to last flush.
	 */
	public static final int REG_REFRESH_HIVE = 0x00000002;
	/**
	 * Never lazy flush this hive.
	 */
	public static final int REG_NO_LAZY_FLUSH = 0x00000004;
	/**
	 * Force the restore process even when we have open handles on subkeys.
	 */
	public static final int REG_FORCE_RESTORE = 0x00000008;
	/**
	 * Loads the hive visible to the calling process.
	 */
	public static final int REG_APP_HIVE = 0x00000010;
	/**
	 * Hive cannot be mounted by any other process while in use.
	 */
	public static final int REG_PROCESS_PRIVATE = 0x00000020;
	/**
	 * Starts Hive Journal.
	 */
	public static final int REG_START_JOURNAL = 0x00000040;
	/**
	 * Grow hive file in exact 4k increments.
	 */
	public static final int REG_HIVE_EXACT_FILE_GROWTH = 0x00000080;
	/**
	 * No RM is started for this hive = no transactions.
	 */
	public static final int REG_HIVE_NO_RM = 0x00000100;
	/**
	 * Legacy single logging is used for this hive.
	 */
	public static final int REG_HIVE_SINGLE_LOG = 0x00000200;

	//
	// Unload Flags
	//
	
	public static final int REG_FORCE_UNLOAD = 1;

	//
	// Notify filter values
	//
	
	public static final int REG_NOTIFY_CHANGE_NAME = 0x00000001;
	public static final int REG_NOTIFY_CHANGE_ATTRIBUTES = 0x00000002;
	public static final int REG_NOTIFY_CHANGE_LAST_SET = 0x00000004;
	public static final int REG_NOTIFY_CHANGE_SECURITY = 0x00000008;

	public static final int REG_LEGAL_CHANGE_FILTER = REG_NOTIFY_CHANGE_NAME |
		REG_NOTIFY_CHANGE_ATTRIBUTES |
		REG_NOTIFY_CHANGE_LAST_SET |
		REG_NOTIFY_CHANGE_SECURITY;

	//
	// Predefined Value Types.
	//

	/**
	 * No value type.
	 */
	public static final int REG_NONE = 0 ;
	/**
	 * Unicode null-terminated string.
	 */
	public static final int REG_SZ = 1;
	/**
	 * Unicode null-terminated string with environment variable references.
	 */
	public static final int REG_EXPAND_SZ = 2;
	/**
	 * Free-formed binary.
	 */
	public static final int REG_BINARY = 3;
	/**
	 * 32-bit number.
	 */
	public static final int REG_DWORD = 4;
	/**
	 * 32-bit number, same as REG_DWORD.
	 */
	public static final int REG_DWORD_LITTLE_ENDIAN = 4;
	/**
	 * 32-bit number.
	 */
	public static final int REG_DWORD_BIG_ENDIAN = 5;
	/**
	 * Symbolic link (unicode).
	 */
	public static final int REG_LINK = 6;
	/**
	 * Multiple unicode strings.
	 */
	public static final int REG_MULTI_SZ = 7;
	/**
	 * Resource list in the resource map.
	 */
	public static final int REG_RESOURCE_LIST = 8;
	/**
	 * Resource list in the hardware description.
	 */
	public static final int REG_FULL_RESOURCE_DESCRIPTOR = 9;
	/**
	 * 
	 */
	public static final int REG_RESOURCE_REQUIREMENTS_LIST = 10 ;
	/**
	 * 64-bit number.
	 */
	public static final int REG_QWORD = 11 ;
	/**
	 * 64-bit number, same as REG_QWORD.
	 */
	public static final int REG_QWORD_LITTLE_ENDIAN = 11;

	/**
	 * A 64-bit value that is guaranteed to be unique on the operating system 
	 * that generated it until the system is restarted. 
	 */
	public static class LUID extends Structure {
		int LowPart;
		int HighPart;
	}
	
	/**
	 * A 64-bit integer;
	 */
	public static class LARGE_INTEGER extends Structure {
		public static class ByReference extends LARGE_INTEGER 
			implements Structure.ByReference {

		}
		
		public static class LowHigh extends Structure {
			public DWORD LowPart;
			public DWORD HighPart;
		}
		
		public static class UNION extends Union {
			public LowHigh lh;
			public long value;
		}
		
		public UNION u;
		
		/**
		 * Low DWORD.
		 * @return
		 *  DWORD.
		 */
		public DWORD getLow() {
			return u.lh.LowPart;
		}
		
		/**
		 * High DWORD.
		 * @return
		 *  DWORD.
		 */
		public DWORD getHigh() {
			return u.lh.HighPart;
		}

		/**
		 * 64-bit value.
		 * @return
		 *  64-bit value.
		 */
		public long getValue() {
			return u.value;
		}
	}
	
	/**
	 * Handle to an object.
	 */
    public static class HANDLE extends PointerType {
        private boolean immutable;
        public HANDLE() { }
        public HANDLE(Pointer p) { setPointer(p); immutable = true; }
        
        /** Override to the appropriate object for INVALID_HANDLE_VALUE. */
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            if (WinBase.INVALID_HANDLE_VALUE.equals(o))
                return WinBase.INVALID_HANDLE_VALUE;
            return o;
        }
        
        public void setPointer(Pointer p) {
            if (immutable) {
                throw new UnsupportedOperationException("immutable reference");
            }
 
            super.setPointer(p);
        }
    }

    /**
     * LPHANDLE
     */
    public static class HANDLEByReference extends ByReference {
        
    	public HANDLEByReference() {
            this(null);
        }
        
        public HANDLEByReference(HANDLE h) {
            super(Pointer.SIZE);
            setValue(h);
        }
        
        public void setValue(HANDLE h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }
        
        public HANDLE getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null)
                return null;
            if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) 
                return WinBase.INVALID_HANDLE_VALUE;
            HANDLE h = new HANDLE();
            h.setPointer(p);
            return h;
        }
    }
    

    /**
     * Return code used by interfaces. It is zero upon success and 
     * nonzero to represent an error code or status information. 
     */
    class HRESULT extends NativeLong {
    	public HRESULT() {
    		
    	}
    	
    	public HRESULT(int value) {
    		super(value);
    	}
    }
    
    /**
     * The WELL_KNOWN_SID_TYPE enumeration type is a list of commonly used security identifiers 
     * (SIDs). Programs can pass these values to the CreateWellKnownSid function to create a SID 
     * from this list.
     */
    public abstract class WELL_KNOWN_SID_TYPE {
    	/**
    	 * Indicates a null SID.
    	 */
    	public static final int WinNullSid = 0;
    	/**
    	 * Indicates a SID that matches everyone.
    	 */
    	public static final int WinWorldSid = 1;
    	/**
    	 * Indicates a local SID. 
    	 */
    	public static final int WinLocalSid = 2;
    	/**
    	 * Indicates a SID that matches the owner or creator of an object.
    	 */
    	public static final int WinCreatorOwnerSid = 3;
    	/**
    	 * Indicates a SID that matches the creator group of an object. 
    	 */
    	public static final int WinCreatorGroupSid = 4;
    	/**
    	 * Indicates a creator owner server SID.
    	 */
    	public static final int WinCreatorOwnerServerSid = 5;
    	/**
    	 * Indicates a creator group server SID. 
    	 */
    	public static final int WinCreatorGroupServerSid = 6;
    	/**
    	 * Indicates a SID for the Windows NT authority. 
    	 */
    	public static final int WinNtAuthoritySid = 7;
    	/**
    	 * Indicates a SID for a dial-up account.
    	 */
    	public static final int WinDialupSid = 8;
    	/**
    	 * Indicates a SID for a network account. This SID is added to the process of a token 
    	 * when it logs on across a network. The corresponding logon type is 
    	 * LOGON32_LOGON_NETWORK. 
    	 */
    	public static final int WinNetworkSid = 9;
    	/**
    	 * Indicates a SID for a batch process. This SID is added to the process of a token 
    	 * when it logs on as a batch job. The corresponding logon type is LOGON32_LOGON_BATCH. 
    	 */
    	public static final int WinBatchSid = 10;
    	/**
    	 * Indicates a SID for an interactive account. This SID is added to the process of a 
    	 * token when it logs on interactively. The corresponding logon type is
    	 * LOGON32_LOGON_INTERACTIVE. 
    	 */
    	public static final int WinInteractiveSid = 11;
    	/**
    	 * Indicates a SID for a service. This SID is added to the process of a token when it 
    	 * logs on as a service. The corresponding logon type is LOGON32_LOGON_bSERVICE.
    	 */
    	public static final int WinServiceSid = 12;
    	/**
    	 * Indicates a SID for the anonymous account. 
    	 */
    	public static final int WinAnonymousSid = 13;
    	/**
    	 * Indicates a proxy SID. 
    	 */
    	public static final int WinProxySid = 14;
    	/**
    	 * Indicates a SID for an enterprise controller. 
    	 */
    	public static final int WinEnterpriseControllersSid = 15;
    	/**
    	 * Indicates a SID for self.
    	 */
    	public static final int WinSelfSid = 16;
    	/**
    	 * Indicates a SID that matches any authenticated user. 
    	 */
    	public static final int WinAuthenticatedUserSid = 17;
    	/**
    	 * Indicates a SID for restricted code. 
    	 */
    	public static final int WinRestrictedCodeSid = 18;
    	/**
    	 * Indicates a SID that matches a terminal server account. 
    	 */
    	public static final int WinTerminalServerSid = 19;
    	/**
    	 * Indicates a SID that matches remote logons. 
    	 */
    	public static final int WinRemoteLogonIdSid = 20;
    	/**
    	 * Indicates a SID that matches logon IDs. 
    	 */
    	public static final int WinLogonIdsSid = 21;
    	/**
    	 * Indicates a SID that matches the local system. 
    	 */
    	public static final int WinLocalSystemSid = 22;
    	/**
    	 * Indicates a SID that matches a local service. 
    	 */
    	public static final int WinLocalServiceSid = 23;
    	/**
    	 * Indicates a SID that matches a network service. 
    	 */
    	public static final int WinNetworkServiceSid = 24;
    	/**
    	 * Indicates a SID that matches the domain account. 
    	 */
    	public static final int WinBuiltinDomainSid = 25;
    	/**
    	 * Indicates a SID that matches the administrator account. 
    	 */
    	public static final int WinBuiltinAdministratorsSid = 26;
    	/**
    	 * Indicates a SID that matches built-in user accounts. 
    	 */
    	public static final int WinBuiltinUsersSid = 27;
    	/**
    	 * Indicates a SID that matches the guest account. 
    	 */
    	public static final int WinBuiltinGuestsSid = 28;
    	/**
    	 * Indicates a SID that matches the power users group. 
    	 */
    	public static final int WinBuiltinPowerUsersSid = 29;
    	/**
    	 * Indicates a SID that matches the account operators account. 
    	 */
    	public static final int WinBuiltinAccountOperatorsSid = 30;
    	/**
    	 * Indicates a SID that matches the system operators group. 
    	 */
    	public static final int WinBuiltinSystemOperatorsSid = 31;
    	/**
    	 * Indicates a SID that matches the print operators group. 
    	 */
    	public static final int WinBuiltinPrintOperatorsSid = 32;
    	/**
    	 * Indicates a SID that matches the backup operators group. 
    	 */
    	public static final int WinBuiltinBackupOperatorsSid = 33;
    	/**
    	 * Indicates a SID that matches the replicator account. 
    	 */
    	public static final int WinBuiltinReplicatorSid = 34;
    	/**
    	 * Indicates a SID that matches pre-Windows 2000 compatible accounts. 
    	 */
    	public static final int WinBuiltinPreWindows2000CompatibleAccessSid = 35;
    	/**
    	 * Indicates a SID that matches remote desktop users. 
    	 */
    	public static final int WinBuiltinRemoteDesktopUsersSid = 36;
    	/**
    	 * Indicates a SID that matches the network operators group.
    	 */
    	public static final int WinBuiltinNetworkConfigurationOperatorsSid = 37;
    	/**
    	 * Indicates a SID that matches the account administrators group. 
    	 */
    	public static final int WinAccountAdministratorSid = 38;
    	/**
    	 * Indicates a SID that matches the account guest group. 
    	 */
    	public static final int WinAccountGuestSid = 39;
    	/**
    	 * Indicates a SID that matches account Kerberos target group. 
    	 */
    	public static final int WinAccountKrbtgtSid = 40;
    	/**
    	 * Indicates a SID that matches the account domain administrator group. 
    	 */
    	public static final int WinAccountDomainAdminsSid = 41;
    	/**
    	 * Indicates a SID that matches the account domain users group. 
    	 */
    	public static final int WinAccountDomainUsersSid = 42;
    	/**
    	 * Indicates a SID that matches the account domain guests group. 
    	 */
    	public static final int WinAccountDomainGuestsSid = 43;
    	/**
    	 * Indicates a SID that matches the account computer group. 
    	 */
    	public static final int WinAccountComputersSid = 44;
    	/**
    	 * Indicates a SID that matches the account controller group. 
    	 */
    	public static final int WinAccountControllersSid = 45;
    	/**
    	 * Indicates a SID that matches the certificate administrators group.
    	 */
    	public static final int WinAccountCertAdminsSid = 46;
    	/**
    	 * Indicates a SID that matches the schema administrators group. 
    	 */
    	public static final int WinAccountSchemaAdminsSid = 47;
    	/**
    	 * Indicates a SID that matches the enterprise administrators group. 
    	 */
    	public static final int WinAccountEnterpriseAdminsSid = 48;
    	/**
    	 * Indicates a SID that matches the policy administrators group. 
    	 */
    	public static final int WinAccountPolicyAdminsSid = 49;
    	/**
    	 * Indicates a SID that matches the RAS and IAS server account. 
    	 */
    	public static final int WinAccountRasAndIasServersSid = 50;
    	/**
    	 * Indicates a SID present when the Microsoft NTLM authentication package 
    	 * authenticated the client. 
    	 */
    	public static final int WinNTLMAuthenticationSid = 51;
    	/**
    	 * Indicates a SID present when the Microsoft Digest authentication package 
    	 * authenticated the client. 
    	 */
    	public static final int WinDigestAuthenticationSid = 52;
    	/**
    	 * Indicates a SID present when the Secure Channel (SSL/TLS) authentication 
    	 * package authenticated the client. 
    	 */
    	public static final int WinSChannelAuthenticationSid = 53;
    	/**
    	 * Indicates a SID present when the user authenticated from within the forest 
    	 * or across a trust that does not have the selective authentication option 
    	 * enabled. If this SID is present, then WinOtherOrganizationSid cannot be present. 
    	 */
    	public static final int WinThisOrganizationSid = 54;
    	/**
    	 * Indicates a SID present when the user authenticated across a forest with the
    	 * selective authentication option enabled. If this SID is present, then 
    	 * WinThisOrganizationSid cannot be present. 
    	 */
    	public static final int WinOtherOrganizationSid = 55;
    	/**
    	 * Indicates a SID that allows a user to create incoming forest trusts. It is added 
    	 * to the token of users who are a member of the Incoming Forest Trust Builders 
    	 * built-in group in the root domain of the forest. 
    	 */
    	public static final int WinBuiltinIncomingForestTrustBuildersSid = 56;
    	/**
    	 * Indicates a SID that matches the performance monitor user group. 
    	 */
    	public static final int WinBuiltinPerfMonitoringUsersSid = 57;
    	/**
    	 * Indicates a SID that matches the performance log user group. 
    	 */
    	public static final int WinBuiltinPerfLoggingUsersSid = 58;
    	/**
    	 * Indicates a SID that matches the Windows Authorization Access group.
    	 */
    	public static final int WinBuiltinAuthorizationAccessSid = 59;
    	/**
    	 * Indicates a SID is present in a server that can issue Terminal Server licenses. 
    	 */
    	public static final int WinBuiltinTerminalServerLicenseServersSid = 60;
    	/**
    	 * 
    	 */
    	public static final int WinBuiltinDCOMUsersSid = 61;
    	/**
    	 * 
    	 */
    	public static final int WinBuiltinIUsersSid = 62;
    	/**
    	 * 
    	 */
    	public static final int WinIUserSid = 63;
    	/**
    	 * 
    	 */
    	public static final int WinBuiltinCryptoOperatorsSid = 64;
    	/**
    	 * 
    	 */
    	public static final int WinUntrustedLabelSid = 65;
    	/**
    	 * 
    	 */
    	public static final int WinLowLabelSid = 66;
    	/**
    	 * 
    	 */
    	public static final int WinMediumLabelSid = 67;
    	/**
    	 * 
    	 */
    	public static final int WinHighLabelSid = 68;
    	/**
    	 * 
    	 */
    	public static final int WinSystemLabelSid = 69;
    	/**
    	 * 
    	 */
    	public static final int WinWriteRestrictedCodeSid = 70;
    	/**
    	 * 
    	 */
    	public static final int WinCreatorOwnerRightsSid = 71;
    	/**
    	 * 
    	 */
    	public static final int WinCacheablePrincipalsGroupSid = 72;
    	/**
    	 * 
    	 */
    	public static final int WinNonCacheablePrincipalsGroupSid = 73;
    	/**
    	 * 
    	 */
    	public static final int WinEnterpriseReadonlyControllersSid = 74;
    	/**
    	 * Indicates a SID that matches a read-only enterprise domain controller.
    	 */
    	public static final int WinAccountReadonlyControllersSid = 75;
    	/**
    	 * Indicates a SID that matches the built-in DCOM certification services access group.
    	 */
    	public static final int WinBuiltinEventLogReadersGroup = 76;
    }
    
    /**
     * Current SID revision level.
     */
    public static final int SID_REVISION = 1;
    public static final int SID_MAX_SUB_AUTHORITIES = 15;
    public static final int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
    
    /**
     * Maximum bytes used by a SID.
     * (sizeof(SID) - sizeof(DWORD) + (SID_MAX_SUB_AUTHORITIES * sizeof(DWORD)))
     */
    public static final int SECURITY_MAX_SID_SIZE = 68;
    
    /**
     * The OSVERSIONINFO data structure contains operating system version information. 
     * The information includes major and minor version numbers, a build number, a 
     * platform identifier, and descriptive text about the operating system. This structure 
     * is used with the GetVersionEx function.
     */
    public static class OSVERSIONINFO extends Structure {
    	/**
    	 * Size of this data structure, in bytes. Set this member to sizeof(OSVERSIONINFO) 
    	 * before calling the GetVersionEx function.
    	 */
        public DWORD dwOSVersionInfoSize;      
        /**
         * Major version number of the operating system. 
         */
        public DWORD dwMajorVersion;
        /**
         * Minor version number of the operating system.
         */
        public DWORD dwMinorVersion;
        /**
         * Build number of the operating system.
         */
        public DWORD dwBuildNumber;
        /**
         * Operating system platform.
         */
        public DWORD dwPlatformId;
        /**
         * Pointer to a null-terminated string, such as "Service Pack 3", 
         * that indicates the latest Service Pack installed on the system.
         */
        public char szCSDVersion[];
        
        public OSVERSIONINFO() {
        	szCSDVersion = new char[128];
        	dwOSVersionInfoSize = new DWORD(size()); // sizeof(OSVERSIONINFO)
        }

		public OSVERSIONINFO(Pointer memory) {
			useMemory(memory);
			read();
		}
    };
    
    /**
     * Contains operating system version information. The information includes major and minor version numbers, 
     * a build number, a platform identifier, and information about product suites and the latest Service Pack 
     * installed on the system.
     */
    public static class OSVERSIONINFOEX extends Structure {
    	/**
    	 * The size of this data structure, in bytes.
    	 */
        public DWORD dwOSVersionInfoSize;
        /**
         * The major version number of the operating system.
         */
        public DWORD dwMajorVersion;
        /**
         * The minor version number of the operating system.
         */
        public DWORD dwMinorVersion;
        /**
         * The build number of the operating system.
         */
        public DWORD dwBuildNumber;
        /**
         * The operating system platform. This member can be VER_PLATFORM_WIN32_NT.
         */
        public DWORD dwPlatformId;
        /**
         * A null-terminated string, such as "Service Pack 3", that indicates the latest Service Pack 
         * installed on the system. If no Service Pack has been installed, the string is empty.
         */
        public char szCSDVersion[];
        /**
         * The major version number of the latest Service Pack installed on the system. For example, for 
         * Service Pack 3, the major version number is 3. If no Service Pack has been installed, the value 
         * is zero.
         */
        public WORD wServicePackMajor;
        /**
         * The minor version number of the latest Service Pack installed on the system. For example, for 
         * Service Pack 3, the minor version number is 0.
         */
        public WORD wServicePackMinor;
        /**
         * A bit mask that identifies the product suites available on the system.
         */
        public WORD wSuiteMask;
        /**
         * Any additional information about the system. 
         */
        public byte wProductType;
        /**
         * Reserved for future use.
         */
        public byte wReserved;
        
        public OSVERSIONINFOEX() {
        	szCSDVersion = new char[128];
        	dwOSVersionInfoSize = new DWORD(size()); // sizeof(OSVERSIONINFOEX)
        }

		public OSVERSIONINFOEX(Pointer memory) {
			useMemory(memory);
			read();
		}
    };

    public static final int VER_EQUAL = 1;
    public static final int VER_GREATER = 2;
    public static final int VER_GREATER_EQUAL = 3;
    public static final int VER_LESS = 4;
    public static final int VER_LESS_EQUAL = 5;
    public static final int VER_AND = 6;
    public static final int VER_OR = 7;
    
    public static final int VER_CONDITION_MASK = 7;
    public static final int VER_NUM_BITS_PER_CONDITION_MASK = 3;

    public static final int VER_MINORVERSION  = 0x0000001;
    public static final int VER_MAJORVERSION = 0x0000002;
    public static final int VER_BUILDNUMBER = 0x0000004;
    public static final int VER_PLATFORMID = 0x0000008;
    public static final int VER_SERVICEPACKMINOR = 0x0000010;
    public static final int VER_SERVICEPACKMAJOR = 0x0000020;
    public static final int VER_SUITENAME = 0x0000040;
    public static final int VER_PRODUCT_TYPE = 0x0000080;

    public static final int VER_NT_WORKSTATION = 0x0000001;
    public static final int VER_NT_DOMAIN_CONTROLLER = 0x0000002;
    public static final int VER_NT_SERVER = 0x0000003;

    public static final int VER_PLATFORM_WIN32s = 0;
    public static final int VER_PLATFORM_WIN32_WINDOWS = 1;
    public static final int VER_PLATFORM_WIN32_NT = 2;
}