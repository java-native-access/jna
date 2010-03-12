package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * 
 * Ported from WinNT.h.
 * 
 * @author dblock[at]dblock.org Windows SDK 6.0A
 */
public abstract class WinNT {

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

		/**
		 * Specifies a SID_AND_ATTRIBUTES structure representing the user associated with 
		 * the access token. There are currently no attributes defined for user security 
		 * identifiers (SIDs). 
		 */
		public SID_AND_ATTRIBUTES User;
	}
}
