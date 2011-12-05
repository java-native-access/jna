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
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * Ported from WinNT.h
 * Microsoft Windows SDK 6.0A.
 * Avoid including any NIO Buffer mappings here; put them in a 
 * DLL-derived interface (e.g. kernel32, user32, etc) instead.
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {

    //
    // The following are masks for the predefined standard access types
    //

    int DELETE		= 0x00010000;
    int READ_CONTROL	= 0x00020000;
    int WRITE_DAC	= 0x00040000;
    int WRITE_OWNER	= 0x00080000;
    int SYNCHRONIZE	= 0x00100000;

    int STANDARD_RIGHTS_REQUIRED	= 0x000F0000;
    int STANDARD_RIGHTS_READ		= READ_CONTROL;
    int STANDARD_RIGHTS_WRITE		= READ_CONTROL;
    int STANDARD_RIGHTS_EXECUTE		= READ_CONTROL;
    int STANDARD_RIGHTS_ALL		= 0x001F0000;

    int SPECIFIC_RIGHTS_ALL		= 0x0000FFFF;

    //
    // Token Specific Access Rights.
    //

    /**
     * Required to attach a primary token to a process. The
     * SE_ASSIGNPRIMARYTOKEN_NAME privilege is also required to accomplish this
     * task.
     */
    int TOKEN_ASSIGN_PRIMARY = 0x0001;

    /**
     * Required to duplicate an access token.
     */
    int TOKEN_DUPLICATE = 0x0002;

    /**
     * Required to attach an impersonation access token to a process.
     */
    int TOKEN_IMPERSONATE = 0x0004;

    /**
     * Required to query an access token.
     */
    int TOKEN_QUERY = 0x0008;

    /**
     * Required to query the source of an access token.
     */
    int TOKEN_QUERY_SOURCE = 0x0010;

    /**
     * Required to enable or disable the privileges in an access token.
     */
    int TOKEN_ADJUST_PRIVILEGES = 0x0020;

    /**
     * Required to adjust the attributes of the groups in an access token.
     */
    int TOKEN_ADJUST_GROUPS = 0x0040;

    /**
     * Required to change the default owner, primary group, or DACL of an access
     * token.
     */
    int TOKEN_ADJUST_DEFAULT = 0x0080;

    /**
     * Required to adjust the session ID of an access token. The SE_TCB_NAME
     * privilege is required.
     */
    int TOKEN_ADJUST_SESSIONID = 0x0100;

    int TOKEN_ALL_ACCESS_P = STANDARD_RIGHTS_REQUIRED |
        TOKEN_ASSIGN_PRIMARY |
        TOKEN_DUPLICATE |
        TOKEN_IMPERSONATE |
        TOKEN_QUERY |
        TOKEN_QUERY_SOURCE |
        TOKEN_ADJUST_PRIVILEGES |
        TOKEN_ADJUST_GROUPS |
        TOKEN_ADJUST_DEFAULT;

    /**
     * Combines all possible access rights for a token.
     */
    int TOKEN_ALL_ACCESS = TOKEN_ALL_ACCESS_P | TOKEN_ADJUST_SESSIONID;

    /**
     * Combines STANDARD_RIGHTS_READ and TOKEN_QUERY.
     */
    int TOKEN_READ = STANDARD_RIGHTS_READ | TOKEN_QUERY;

    /**
     * Combines STANDARD_RIGHTS_WRITE, TOKEN_ADJUST_PRIVILEGES,
     * TOKEN_ADJUST_GROUPS, and TOKEN_ADJUST_DEFAULT.
     */
    int TOKEN_WRITE = STANDARD_RIGHTS_WRITE |
        TOKEN_ADJUST_PRIVILEGES |
        TOKEN_ADJUST_GROUPS |
        TOKEN_ADJUST_DEFAULT;

    /**
     * Combines STANDARD_RIGHTS_EXECUTE and TOKEN_IMPERSONATE.
     */
    int TOKEN_EXECUTE = STANDARD_RIGHTS_EXECUTE;

    int THREAD_TERMINATE			= 0x0001;
    int THREAD_SUSPEND_RESUME			= 0x0002;
    int THREAD_GET_CONTEXT			= 0x0008;
    int THREAD_SET_CONTEXT			= 0x0010;
    int THREAD_QUERY_INFORMATION		= 0x0040;
    int THREAD_SET_INFORMATION			= 0x0020;
    int THREAD_SET_THREAD_TOKEN			= 0x0080;
    int THREAD_IMPERSONATE			= 0x0100;
    int THREAD_DIRECT_IMPERSONATION		= 0x0200;
    int THREAD_SET_LIMITED_INFORMATION		= 0x0400;
    int THREAD_QUERY_LIMITED_INFORMATION	= 0x0800;
    int THREAD_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0x3FF;
    
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
	public static final int TokenGroups = 2; 
	public static final int TokenPrivileges = 3; 
	public static final int TokenOwner = 4;
	public static final int TokenPrimaryGroup = 5; 
	public static final int TokenDefaultDacl = 6;
	public static final int TokenSource = 7;
	public static final int TokenType = 8;
	public static final int TokenImpersonationLevel = 9; 
	public static final int TokenStatistics = 10;
	public static final int TokenRestrictedSids = 11; 
	public static final int TokenSessionId = 12;
	public static final int TokenGroupsAndPrivileges = 13; 
	public static final int TokenSessionReference = 14;
	public static final int TokenSandBoxInert = 15;
	public static final int TokenAuditPolicy = 16;
	public static final int TokenOrigin = 17;
	public static final int TokenElevationType = 18;
	public static final int TokenLinkedToken = 19; 
	public static final int TokenElevation = 20;
	public static final int TokenHasRestrictions = 21;
	public static final int TokenAccessInformation = 22;
	public static final int TokenVirtualizationAllowed = 23; 
	public static final int TokenVirtualizationEnabled = 24; 
	public static final int TokenIntegrityLevel = 25;
	public static final int TokenUIAccess = 26;
	public static final int TokenMandatoryPolicy = 27; 
	public static final int TokenLogonSid = 28;
    }
    
    /**
     * The TOKEN_TYPE enumeration type contains values that differentiate between
     * a primary token and an impersonation token.
     */
    public abstract class TOKEN_TYPE {
        public static final int TokenPrimary = 1;
        public static final int TokenImpersonation = 2;
    }
    
    /**
     * The LUID_AND_ATTRIBUTES structure represents a locally unique identifier (LUID) and its attributes.
     */
    public static class LUID_AND_ATTRIBUTES extends Structure {
	/**
	 * Specifies an LUID value.
	 */
	public LUID  Luid;
	
	/**
	 * Specifies attributes of the LUID. This value contains up to 32 one-bit flags.
	 * Its meaning is dependent on the definition and use of the LUID.
	 */
	public DWORD Attributes;
	
	public LUID_AND_ATTRIBUTES() {}
	
	public LUID_AND_ATTRIBUTES(LUID luid, DWORD attributes) {
	    this.Luid = luid;
	    this.Attributes = attributes;
	}
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
	    super(memory);
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
	
	public TOKEN_OWNER(int size) {
	    super(new Memory(size));
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
	    super(new Memory(data.length));
	    getPointer().write(0, data, 0, data.length);
	    read();
	}

	public PSID(int size) {
	    super(new Memory(size));
	}

	public PSID(Pointer memory) {
	    super(memory);
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
	    if (p == null) {
		return null;
	    } else {
		return new PSID(p);
	    }
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
     * The TOKEN_PRIVILEGES structure contains information about a set of privileges for an access token.
     */
    public static class TOKEN_PRIVILEGES extends Structure {
	/**
	 * This must be set to the number of entries in the Privileges array.
	 */
	public DWORD PrivilegeCount;

	/**
	 * Specifies an array of LUID_AND_ATTRIBUTES structures.
	 * Each structure contains the LUID and attributes of a privilege.
	 */
	public LUID_AND_ATTRIBUTES Privileges[];

	/**
	 * @param nbOfPrivileges Desired size of the Privileges array
	 */
	public TOKEN_PRIVILEGES(int nbOfPrivileges) {
	    PrivilegeCount = new DWORD(nbOfPrivileges);
	    Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
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

    /* File access rights */
    int FILE_READ_DATA			= 0x00000001;
    int FILE_LIST_DIRECTORY		= 0x00000001;
    int FILE_WRITE_DATA			= 0x00000002;
    int FILE_ADD_FILE			= 0x00000002;
    int FILE_APPEND_DATA		= 0x00000004;
    int FILE_ADD_SUBDIRECTORY		= 0x00000004;
    int FILE_CREATE_PIPE_INSTANCE	= 0x00000004;
    int FILE_READ_EA			= 0x00000008;
    int FILE_WRITE_EA			= 0x00000010;
    int FILE_EXECUTE			= 0x00000020;
    int FILE_TRAVERSE			= 0x00000020;
    int FILE_DELETE_CHILD		= 0x00000040;
    int FILE_READ_ATTRIBUTES		= 0x00000080;
    int FILE_WRITE_ATTRIBUTES		= 0x00000100;

    int FILE_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED |
        SYNCHRONIZE |
        0x000001FF;

    int FILE_GENERIC_READ = STANDARD_RIGHTS_READ |
        SYNCHRONIZE |
        FILE_READ_DATA |
        FILE_READ_ATTRIBUTES |
        FILE_READ_EA;

    int FILE_GENERIC_WRITE = STANDARD_RIGHTS_WRITE |
        SYNCHRONIZE |
        FILE_WRITE_DATA |
        FILE_WRITE_ATTRIBUTES |
        FILE_WRITE_EA |
        FILE_APPEND_DATA;

    int FILE_GENERIC_EXECUTE = STANDARD_RIGHTS_EXECUTE |
        SYNCHRONIZE |
        FILE_READ_ATTRIBUTES |
        FILE_EXECUTE;

    int CREATE_NEW		= 1;
    int CREATE_ALWAYS		= 2;
    int OPEN_EXISTING		= 3;
    int OPEN_ALWAYS		= 4;
    int TRUNCATE_EXISTING	= 5;

    int FILE_FLAG_WRITE_THROUGH		= 0x80000000;
    int FILE_FLAG_OVERLAPPED		= 0x40000000;
    int FILE_FLAG_NO_BUFFERING		= 0x20000000;
    int FILE_FLAG_RANDOM_ACCESS		= 0x10000000;
    int FILE_FLAG_SEQUENTIAL_SCAN	= 0x08000000;
    int FILE_FLAG_DELETE_ON_CLOSE	= 0x04000000;
    int FILE_FLAG_BACKUP_SEMANTICS	= 0x02000000;
    int FILE_FLAG_POSIX_SEMANTICS	= 0x01000000;
    int FILE_FLAG_OPEN_REPARSE_POINT	= 0x00200000;
    int FILE_FLAG_OPEN_NO_RECALL	= 0x00100000;

    //
    //  These are the generic rights.
    //

    int GENERIC_READ	= 0x80000000;
    int GENERIC_WRITE	= 0x40000000;
    int GENERIC_EXECUTE	= 0x20000000;
    int GENERIC_ALL	= 0x10000000;
    
    //
    // AccessSystemAcl access type
    //

    int ACCESS_SYSTEM_SECURITY			= 0x01000000;
	
    int PAGE_READONLY				= 0x02;
    int PAGE_READWRITE				= 0x04;
    int PAGE_WRITECOPY				= 0x08;
    int PAGE_EXECUTE				= 0x10;
    int PAGE_EXECUTE_READ			= 0x20;
    int PAGE_EXECUTE_READWRITE			= 0x40;    
    
    int SECTION_QUERY				= 0x0001;
    int SECTION_MAP_WRITE			= 0x0002;
    int SECTION_MAP_READ			= 0x0004;
    int SECTION_MAP_EXECUTE			= 0x0008;
    int SECTION_EXTEND_SIZE			= 0x0010;    
    
    int FILE_SHARE_READ				= 0x00000001;
    int FILE_SHARE_WRITE			= 0x00000002; 
    int FILE_SHARE_DELETE			= 0x00000004; 
    int FILE_ATTRIBUTE_READONLY			= 0x00000001; 
    int FILE_ATTRIBUTE_HIDDEN			= 0x00000002; 
    int FILE_ATTRIBUTE_SYSTEM			= 0x00000004; 
    int FILE_ATTRIBUTE_DIRECTORY		= 0x00000010; 
    int FILE_ATTRIBUTE_ARCHIVE			= 0x00000020; 
    int FILE_ATTRIBUTE_DEVICE			= 0x00000040; 
    int FILE_ATTRIBUTE_NORMAL			= 0x00000080; 
    int FILE_ATTRIBUTE_TEMPORARY		= 0x00000100; 
    int FILE_ATTRIBUTE_SPARSE_FILE		= 0x00000200; 
    int FILE_ATTRIBUTE_REPARSE_POINT		= 0x00000400; 
    int FILE_ATTRIBUTE_COMPRESSED		= 0x00000800; 
    int FILE_ATTRIBUTE_OFFLINE			= 0x00001000; 
    int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED	= 0x00002000; 
    int FILE_ATTRIBUTE_ENCRYPTED		= 0x00004000;
    int FILE_ATTRIBUTE_VIRTUAL			= 0x00010000;
    int FILE_NOTIFY_CHANGE_FILE_NAME		= 0x00000001; 
    int FILE_NOTIFY_CHANGE_DIR_NAME		= 0x00000002; 
    int FILE_NOTIFY_CHANGE_NAME			= 0x00000003;
    int FILE_NOTIFY_CHANGE_ATTRIBUTES		= 0x00000004; 
    int FILE_NOTIFY_CHANGE_SIZE			= 0x00000008; 
    int FILE_NOTIFY_CHANGE_LAST_WRITE		= 0x00000010; 
    int FILE_NOTIFY_CHANGE_LAST_ACCESS		= 0x00000020; 
    int FILE_NOTIFY_CHANGE_CREATION		= 0x00000040; 
    int FILE_NOTIFY_CHANGE_SECURITY		= 0x00000100; 
    int FILE_ACTION_ADDED			= 0x00000001; 
    int FILE_ACTION_REMOVED			= 0x00000002; 
    int FILE_ACTION_MODIFIED			= 0x00000003; 
    int FILE_ACTION_RENAMED_OLD_NAME		= 0x00000004; 
    int FILE_ACTION_RENAMED_NEW_NAME		= 0x00000005;
    int FILE_CASE_SENSITIVE_SEARCH		= 0x00000001; 
    int FILE_CASE_PRESERVED_NAMES		= 0x00000002;
    int FILE_UNICODE_ON_DISK			= 0x00000004;
    int FILE_PERSISTENT_ACLS			= 0x00000008;
    int FILE_FILE_COMPRESSION			= 0x00000010; 
    int FILE_VOLUME_QUOTAS			= 0x00000020;
    int FILE_SUPPORTS_SPARSE_FILES		= 0x00000040; 
    int FILE_SUPPORTS_REPARSE_POINTS		= 0x00000080; 
    int FILE_SUPPORTS_REMOTE_STORAGE		= 0x00000100; 
    int FILE_VOLUME_IS_COMPRESSED		= 0x00008000; 
    int FILE_SUPPORTS_OBJECT_IDS		= 0x00010000; 
    int FILE_SUPPORTS_ENCRYPTION		= 0x00020000; 
    int FILE_NAMED_STREAMS			= 0x00040000; 
    int FILE_READ_ONLY_VOLUME			= 0x00080000; 
    int FILE_SEQUENTIAL_WRITE_ONCE		= 0x00100000; 
    int FILE_SUPPORTS_TRANSACTIONS		= 0x00200000; 

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
	        throw new IllegalArgumentException("Size must greater than " + size() + ", requested " + size);
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
	    if (NextEntryOffset == 0) {
		return null;
	    }
	    FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
	    next.useMemory(getPointer(), NextEntryOffset);
	    next.read();
	    return next;
	}
    }    
    
    /**
     * Registry options.
     */
    int KEY_QUERY_VALUE		= 0x0001;
    int KEY_SET_VALUE		= 0x0002;
    int KEY_CREATE_SUB_KEY	= 0x0004;
    int KEY_ENUMERATE_SUB_KEYS	= 0x0008;
    int KEY_NOTIFY		= 0x0010;
    int KEY_CREATE_LINK		= 0x0020;
    int KEY_WOW64_32KEY		= 0x0200;
    int KEY_WOW64_64KEY		= 0x0100;
    int KEY_WOW64_RES		= 0x0300;

    int KEY_READ = STANDARD_RIGHTS_READ |
        KEY_QUERY_VALUE |
        KEY_ENUMERATE_SUB_KEYS |
        KEY_NOTIFY
        & (~SYNCHRONIZE);

    int KEY_WRITE = STANDARD_RIGHTS_WRITE |
        KEY_SET_VALUE |
        KEY_CREATE_SUB_KEY
        & (~SYNCHRONIZE);    

    int KEY_EXECUTE = KEY_READ
        & (~SYNCHRONIZE);

    int KEY_ALL_ACCESS = STANDARD_RIGHTS_ALL |
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
    int REG_OPTION_RESERVED = 0x00000000;

    /**
     * Key is preserved when system is rebooted.
     */
    int REG_OPTION_NON_VOLATILE = 0x00000000;
 
    /**
     * Key is not preserved when system is rebooted.
     */
    int REG_OPTION_VOLATILE = 0x00000001;

    /**
     * Created key is a symbolic link.
     */
    int REG_OPTION_CREATE_LINK = 0x00000002;

    /**
     * Open for backup or restore special access rules privilege required.
     */
    int REG_OPTION_BACKUP_RESTORE = 0x00000004;

    /**
     * Open symbolic link.
     */
    int REG_OPTION_OPEN_LINK = 0x00000008;

    int REG_LEGAL_OPTION = REG_OPTION_RESERVED |
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
    int REG_CREATED_NEW_KEY = 0x00000001;

    /**
     * Existing Key opened.
     */
    int REG_OPENED_EXISTING_KEY = 0x00000002;

    int REG_STANDARD_FORMAT = 1;
    int REG_LATEST_FORMAT = 2;
    int REG_NO_COMPRESSION = 4;

    //
    // Key restore & hive load flags
    //

    /**
     * Restore whole hive volatile.
     */
    int REG_WHOLE_HIVE_VOLATILE = 0x00000001;

    /**
     * Unwind changes to last flush.
     */
    int REG_REFRESH_HIVE = 0x00000002;

    /**
     * Never lazy flush this hive.
     */
    int REG_NO_LAZY_FLUSH = 0x00000004;

    /**
     * Force the restore process even when we have open handles on subkeys.
     */
    int REG_FORCE_RESTORE = 0x00000008;

    /**
     * Loads the hive visible to the calling process.
     */
    int REG_APP_HIVE = 0x00000010;

    /**
     * Hive cannot be mounted by any other process while in use.
     */
    int REG_PROCESS_PRIVATE = 0x00000020;

    /**
     * Starts Hive Journal.
     */
    int REG_START_JOURNAL = 0x00000040;

    /**
     * Grow hive file in exact 4k increments.
     */
    int REG_HIVE_EXACT_FILE_GROWTH = 0x00000080;

    /**
     * No RM is started for this hive = no transactions.
     */
    int REG_HIVE_NO_RM = 0x00000100;

    /**
     * Legacy single logging is used for this hive.
     */
    int REG_HIVE_SINGLE_LOG = 0x00000200;

    //
    // Unload Flags
    //
    
    int REG_FORCE_UNLOAD = 1;

    //
    // Notify filter values
    //
    
    int REG_NOTIFY_CHANGE_NAME		= 0x00000001;
    int REG_NOTIFY_CHANGE_ATTRIBUTES	= 0x00000002;
    int REG_NOTIFY_CHANGE_LAST_SET	= 0x00000004;
    int REG_NOTIFY_CHANGE_SECURITY	= 0x00000008;

    int REG_LEGAL_CHANGE_FILTER = REG_NOTIFY_CHANGE_NAME |
        REG_NOTIFY_CHANGE_ATTRIBUTES |
        REG_NOTIFY_CHANGE_LAST_SET |
        REG_NOTIFY_CHANGE_SECURITY;

    //
    // Predefined Value Types.
    //

    /**
     * No value type.
     */
    int REG_NONE = 0;

    /**
     * Unicode null-terminated string.
     */
    int REG_SZ = 1;

    /**
     * Unicode null-terminated string with environment variable references.
     */
    int REG_EXPAND_SZ = 2;

    /**
     * Free-formed binary.
     */
    int REG_BINARY = 3;

    /**
     * 32-bit number.
     */
    int REG_DWORD = 4;

    /**
     * 32-bit number, same as REG_DWORD.
     */
    int REG_DWORD_LITTLE_ENDIAN = 4;

    /**
     * 32-bit number.
     */
    int REG_DWORD_BIG_ENDIAN = 5;

    /**
     * Symbolic link (unicode).
     */
    int REG_LINK = 6;

    /**
     * Multiple unicode strings.
     */
    int REG_MULTI_SZ = 7;

    /**
     * Resource list in the resource map.
     */
    int REG_RESOURCE_LIST = 8;

    /**
     * Resource list in the hardware description.
     */
    int REG_FULL_RESOURCE_DESCRIPTOR = 9;

    /**
     * 
     */
    int REG_RESOURCE_REQUIREMENTS_LIST = 10 ;

    /**
     * 64-bit number.
     */
    int REG_QWORD = 11 ;

    /**
     * 64-bit number, same as REG_QWORD.
     */
    int REG_QWORD_LITTLE_ENDIAN = 11;

    /**
     * A 64-bit value that is guaranteed to be unique on the operating system 
     * that generated it until the system is restarted. 
     */
    public static class LUID extends Structure {
	public int LowPart;
	public int HighPart;
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

	public HANDLE() {}

	public HANDLE(Pointer p) {
	    setPointer(p);
	    immutable = true;
	}
	
	/** Override to the appropriate object for INVALID_HANDLE_VALUE. */
	public Object fromNative(Object nativeValue, FromNativeContext context) {
	    Object o = super.fromNative(nativeValue, context);
	    if (WinBase.INVALID_HANDLE_VALUE.equals(o)) {
		return WinBase.INVALID_HANDLE_VALUE;
	    }
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
	    if (p == null) {
		return null;
	    }
	    if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
		return WinBase.INVALID_HANDLE_VALUE;
	    }
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
    int SID_REVISION = 1;
    int SID_MAX_SUB_AUTHORITIES = 15;
    int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
    
    /**
     * Maximum bytes used by a SID.
     * (sizeof(SID) - sizeof(DWORD) + (SID_MAX_SUB_AUTHORITIES * sizeof(DWORD)))
     */
    int SECURITY_MAX_SID_SIZE = 68;
    
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
    }
 
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
    }

    int VER_EQUAL			= 1;
    int VER_GREATER			= 2;
    int VER_GREATER_EQUAL		= 3;
    int VER_LESS			= 4;
    int VER_LESS_EQUAL			= 5;
    int VER_AND				= 6;
    int VER_OR				= 7;
    
    int VER_CONDITION_MASK		= 7;
    int VER_NUM_BITS_PER_CONDITION_MASK	= 3;

    int VER_MINORVERSION		= 0x0000001;
    int VER_MAJORVERSION		= 0x0000002;
    int VER_BUILDNUMBER			= 0x0000004;
    int VER_PLATFORMID			= 0x0000008;
    int VER_SERVICEPACKMINOR		= 0x0000010;
    int VER_SERVICEPACKMAJOR		= 0x0000020;
    int VER_SUITENAME			= 0x0000040;
    int VER_PRODUCT_TYPE		= 0x0000080;

    int VER_NT_WORKSTATION		= 0x0000001;
    int VER_NT_DOMAIN_CONTROLLER	= 0x0000002;
    int VER_NT_SERVER			= 0x0000003;

    int VER_PLATFORM_WIN32s		= 0;
    int VER_PLATFORM_WIN32_WINDOWS	= 1;
    int VER_PLATFORM_WIN32_NT		= 2;
    
    /**
     * Read the records sequentially. If this is the first read operation, 
     * the EVENTLOG_FORWARDS_READ EVENTLOG_BACKWARDS_READ flags determines which 
     * record is read first.
     */
    int EVENTLOG_SEQUENTIAL_READ = 0x0001;

    /**
     * Begin reading from the record specified in the dwRecordOffset parameter.  
     * This option may not work with large log files if the function cannot determine the log file's size. 
     * For details, see Knowledge Base article, 177199.
     */
    int EVENTLOG_SEEK_READ = 0x0002;

    /**
     * The log is read in chronological order (oldest to newest). The default.
     */
    int EVENTLOG_FORWARDS_READ = 0x0004;

    /**
     * The log is read in reverse chronological order (newest to oldest). 
     */
    int EVENTLOG_BACKWARDS_READ = 0x0008;

    /**
     * Information event
     */
    int EVENTLOG_SUCCESS = 0x0000;

    /**
     * Error event
     */
    int EVENTLOG_ERROR_TYPE = 0x0001;

    /**
     * Warning event
     */
    int EVENTLOG_WARNING_TYPE = 0x0002;

    /**
     * Information event
     */
    int EVENTLOG_INFORMATION_TYPE = 0x0004;

    /**
     * Success Audit event
     */
    int EVENTLOG_AUDIT_SUCCESS = 0x0008;

    /**
     * Failure Audit event
     */
    int EVENTLOG_AUDIT_FAILURE = 0x0010;
    
    /**
     * The EVENTLOGRECORD structure contains information about an event record returned by the 
     * ReadEventLog function.
     */
    public static class EVENTLOGRECORD extends Structure {
	/**
	 * Size of this event record, in bytes. Note that this value is stored at both ends
	 * of the entry to ease moving forward or backward through the log. The length includes 
	 * any pad bytes inserted at the end of the record for DWORD alignment. 
	 */
	public DWORD Length;

	/**
	 * Reserved.
	 */
	public DWORD Reserved;

	/**
	 * Record number of the record. This value can be used with the EVENTLOG_SEEK_READ flag in
	 * the ReadEventLog function to begin reading at a specified record.
	 */
	public DWORD RecordNumber;

	/**
	 * Time at which this entry was submitted. This time is measured in the number of seconds 
	 * elapsed since 00:00:00 January 1, 1970, Universal Coordinated Time. 
	 */
	public DWORD TimeGenerated;

	/**
	 * Time at which this entry was received by the service to be written to the log. 
	 * This time is measured in the number of seconds elapsed since 00:00:00 January 1,
	 * 1970, Universal Coordinated Time. 
	 */
	public DWORD TimeWritten;

	/**
	 * Event identifier. The value is specific to the event source for the event, and is used
	 * with source name to locate a description string in the message file for the event source. 
	 */
	public DWORD EventID;

	/**
	 * Type of event.
	 */
	public WORD EventType;

	/**
	 * Number of strings present in the log (at the position indicated by StringOffset). 
	 * These strings are merged into the message before it is displayed to the user. 
	 */
	public WORD NumStrings;

	/**
	 * Category for this event. The meaning of this value depends on the event source.
	 */
	public WORD EventCategory;

	/**
	 * Reserved.
	 */
	public WORD ReservedFlags;

	/**
	 * Reserved.
	 */
	public DWORD ClosingRecordNumber;

	/**
	 * Offset of the description strings within this event log record. 
	 */
	public DWORD StringOffset;

	/**
	 * Size of the UserSid member, in bytes. This value can be zero if no security identifier was provided. 
	 */
	public DWORD UserSidLength;

	/**
	 * Offset of the security identifier (SID) within this event log record. 
	 * To obtain the user name for this SID, use the LookupAccountSid function. 
	 */
	public DWORD UserSidOffset;

	/**
	 * Size of the event-specific data (at the position indicated by DataOffset), in bytes. 
	 */
	public DWORD DataLength;

	/**
	 * Offset of the event-specific information within this event log record, in bytes. 
	 * This information could be something specific (a disk driver might log the number 
	 * of retries, for example), followed by binary information specific to the event 
	 * being logged and to the source that generated the entry. 
	 */
	public DWORD DataOffset;
	
	public EVENTLOGRECORD() {
	}
	
	public EVENTLOGRECORD(Pointer p) {
	    super(p);
	    read();
	}
    }
    
    //
    // Service Types (Bit Mask)
    //
    int SERVICE_KERNEL_DRIVER		= 0x00000001;
    int SERVICE_FILE_SYSTEM_DRIVER	= 0x00000002;
    int SERVICE_ADAPTER			= 0x00000004;
    int SERVICE_RECOGNIZER_DRIVER	= 0x00000008;
    int SERVICE_DRIVER			= SERVICE_KERNEL_DRIVER | SERVICE_FILE_SYSTEM_DRIVER | SERVICE_RECOGNIZER_DRIVER;
    int SERVICE_WIN32_OWN_PROCESS	= 0x00000010;
    int SERVICE_WIN32_SHARE_PROCESS	= 0x00000020;
    int SERVICE_WIN32			= SERVICE_WIN32_OWN_PROCESS | SERVICE_WIN32_SHARE_PROCESS;
    int SERVICE_INTERACTIVE_PROCESS	= 0x00000100;
    int SERVICE_TYPE_ALL		= SERVICE_WIN32 | SERVICE_ADAPTER | SERVICE_DRIVER | SERVICE_INTERACTIVE_PROCESS;
    int STATUS_PENDING			= 0x00000103;

    // Privilege Constants
    String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
    String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
    String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
    String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
    String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
    String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
    String SE_TCB_NAME = "SeTcbPrivilege";
    String SE_SECURITY_NAME = "SeSecurityPrivilege";
    String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
    String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
    String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
    String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
    String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
    String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
    String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
    String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
    String SE_BACKUP_NAME = "SeBackupPrivilege";
    String SE_RESTORE_NAME = "SeRestorePrivilege";
    String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
    String SE_DEBUG_NAME = "SeDebugPrivilege";
    String SE_AUDIT_NAME = "SeAuditPrivilege";
    String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
    String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
    String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
    String SE_UNDOCK_NAME = "SeUndockPrivilege";
    String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
    String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
    String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
    String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
    String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";

    int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 0x00000001;
    int SE_PRIVILEGE_ENABLED = 0x00000002;
    int SE_PRIVILEGE_REMOVED = 0X00000004;
    int SE_PRIVILEGE_USED_FOR_ACCESS = 0x80000000;

    int PROCESS_TERMINATE = 0x00000001;
    int PROCESS_SYNCHRONIZE = 0x00100000;
    
    /* Security information types */
    int OWNER_SECURITY_INFORMATION		= 0x00000001;
    int GROUP_SECURITY_INFORMATION		= 0x00000002;
    int DACL_SECURITY_INFORMATION		= 0x00000004;
    int SACL_SECURITY_INFORMATION		= 0x00000008;
    int LABEL_SECURITY_INFORMATION		= 0x00000010;
    int PROTECTED_DACL_SECURITY_INFORMATION	= 0x80000000;
    int PROTECTED_SACL_SECURITY_INFORMATION	= 0x40000000;
    int UNPROTECTED_DACL_SECURITY_INFORMATION	= 0x20000000;
    int UNPROTECTED_SACL_SECURITY_INFORMATION	= 0x10000000;

    public static class SECURITY_DESCRIPTOR extends Structure {
	public static class ByReference extends SECURITY_DESCRIPTOR implements Structure.ByReference {
	}

	public SECURITY_DESCRIPTOR() {
	}

	public SECURITY_DESCRIPTOR(byte[] data) {
	    super();
	    this.data = data;
	    useMemory(new Memory(data.length));
	}

	public SECURITY_DESCRIPTOR(Pointer memory) {
	    super(memory);
	}

	public byte[] data;
    }

    public static class ACL extends Structure {
        public ACL() { }
	public ACL(Pointer pointer) {
	    super(pointer);
	    read();
	    ACEs = new ACCESS_ACEStructure[AceCount];
	    int offset = size();
	    for (int i=0; i < AceCount; i++) {
		Pointer share = pointer.share(offset);
		// ACE_HEADER.AceType
		final byte aceType = share.getByte(0);
		ACCESS_ACEStructure ace = null;
		switch (aceType) {
                case ACCESS_ALLOWED_ACE_TYPE:
		    ace = new ACCESS_ALLOWED_ACE(share);
		    break;
                case ACCESS_DENIED_ACE_TYPE:
		    ace = new ACCESS_DENIED_ACE(share);
		    break;
                default:
		    throw new IllegalArgumentException("Unknwon ACE type " + aceType);
		}
		ACEs[i] = ace;
		offset += ace.AceSize;
	    }
	}

	public byte     AclRevision;
	public byte     Sbz1;
	public short    AclSize;
	public short    AceCount;
	public short    Sbz2;

	ACCESS_ACEStructure[] ACEs;

	public ACCESS_ACEStructure[] getACEStructures() {
	    return ACEs;
	}
    }

    public static class SECURITY_DESCRIPTOR_RELATIVE extends Structure {
	public static class ByReference extends SECURITY_DESCRIPTOR_RELATIVE implements Structure.ByReference {
	}

	public	byte	Revision;
	public	byte	Sbz1;
	public	short	Control;
	public	int	Owner;
	public	int	Group;
	public	int	Sacl;
	public	int	Dacl;

	private	ACL	DACL = null;

	public SECURITY_DESCRIPTOR_RELATIVE() {
	}

	public SECURITY_DESCRIPTOR_RELATIVE(byte[] data) {
	    super(new Memory(data.length));
	    getPointer().write(0, data, 0, data.length);
	    setDacl();
	}

	public SECURITY_DESCRIPTOR_RELATIVE(Memory memory) {
	    super(memory);
	    setDacl();
	}
 
	public ACL getDiscretionaryACL() {
	    return DACL;
	}

	private final void setDacl() {
	    read();
	    if (Dacl != 0) {
		DACL = new ACL(getPointer().share(Dacl));
	    }
	}
    }

    public static abstract class ACEStructure extends Structure {
	public	byte	AceType;
	public	byte	AceFlags;
	public	short	AceSize;

	PSID	psid;

	public ACEStructure(Pointer p) {
	    super(p);
	}

	public String getSidString() {
	    return Advapi32Util.convertSidToStringSid(psid);
	}

	public PSID getSID() {
	    return psid;
	}
    }

    /* ACE header */
    public static class ACE_HEADER extends ACEStructure {
	public ACE_HEADER(Pointer p) {
	    super(p);
	    read();
	}
    }

    /**
     * ACCESS_ALLOWED_ACE and ACCESS_DENIED_ACE have the same structure layout
     */
    public static abstract class ACCESS_ACEStructure extends ACEStructure {
	public ACCESS_ACEStructure(Pointer p) {
	    super(p);
	    read();
	    // AceSize - size of public members of the structure + size of DWORD (SidStart)
	    int sizeOfSID = super.AceSize - size() + 4;
	    // ACE_HEADER + size of int (Mask)
	    int offsetOfSID = 4 + 4;
	    byte[] data = p.getByteArray(offsetOfSID, sizeOfSID);
	    psid = new PSID(data);
	}

	public int Mask;

	/**
         * first 4 bytes of the SID
	 */
	public DWORD SidStart;
    }

    /* Access allowed ACE */
    public static class ACCESS_ALLOWED_ACE  extends ACCESS_ACEStructure {
	public ACCESS_ALLOWED_ACE(Pointer p) {
	    super(p);
	}
    }

    /* Access denied ACE */
    public static class ACCESS_DENIED_ACE  extends ACCESS_ACEStructure {
	public ACCESS_DENIED_ACE(Pointer p) {
  	    super(p);
	}
    }

    /* ACE types */
    byte ACCESS_ALLOWED_ACE_TYPE			= 0x00;
    byte ACCESS_DENIED_ACE_TYPE				= 0x01;
    byte SYSTEM_AUDIT_ACE_TYPE				= 0x02;
    byte SYSTEM_ALARM_ACE_TYPE				= 0x03;
    byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE		= 0x04;
    byte ACCESS_ALLOWED_OBJECT_ACE_TYPE			= 0x05;
    byte ACCESS_DENIED_OBJECT_ACE_TYPE			= 0x06;
    byte SYSTEM_AUDIT_OBJECT_ACE_TYPE	 		= 0x07;
    byte SYSTEM_ALARM_OBJECT_ACE_TYPE	 		= 0x08;
    byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE		= 0x09;
    byte ACCESS_DENIED_CALLBACK_ACE_TYPE		= 0x0A;
    byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE	= 0x0B;
    byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE		= 0x0C;
    byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE			= 0x0D;
    byte SYSTEM_ALARM_CALLBACK_ACE_TYPE			= 0x0E;
    byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE		= 0x0F;
    byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE		= 0x10;
    byte SYSTEM_MANDATORY_LABEL_ACE_TYPE		= 0x11;
    
    /* ACE inherit flags */
    byte OBJECT_INHERIT_ACE		= 0x01;
    byte CONTAINER_INHERIT_ACE		= 0x02;
    byte NO_PROPAGATE_INHERIT_ACE	= 0x04;
    byte INHERIT_ONLY_ACE		= 0x08;
    byte INHERITED_ACE			= 0x10;
    byte VALID_INHERIT_FLAGS		= 0x1F;

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
                                      IntByReference lpNumberOfBytes,
                                      ULONG_PTRByReference lpCompletionKey, 
                                      PointerByReference lpOverlapped,
                                      int dwMilliseconds);

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
