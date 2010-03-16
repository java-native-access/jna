package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

/**
 * Ported from LMAccess.h.
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface LMAccess {
	
	public static class LOCALGROUP_INFO_0 extends Structure {
		public LOCALGROUP_INFO_0() {
			super();
		}

		public LOCALGROUP_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		public WString lgrui0_name;
	}

	public static class LOCALGROUP_INFO_1 extends Structure {
		public LOCALGROUP_INFO_1() {
			super();
		}

		public LOCALGROUP_INFO_1(Pointer memory) {
			useMemory(memory);
			read();
		}

		public WString lgrui1_name;
		public WString lgrui1_comment;
	}
	
	//
	// bit masks for the NetUserEnum filter parameter.
	//

	public static final int FILTER_TEMP_DUPLICATE_ACCOUNT = 0x0001;
	public static final int FILTER_NORMAL_ACCOUNT = 0x0002;
	// public static final int FILTER_PROXY_ACCOUNT = 0x0004;
	public static final int FILTER_INTERDOMAIN_TRUST_ACCOUNT = 0x0008;
	public static final int FILTER_WORKSTATION_TRUST_ACCOUNT = 0x0010;
	public static final int FILTER_SERVER_TRUST_ACCOUNT = 0x0020;	
	
	/**
	 * The USER_INFO_0 structure contains a user account name.
	 */
	public static class USER_INFO_0 extends Structure {
		public USER_INFO_0() {
			super();
		}

		public USER_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a Unicode string that specifies the name of the user account. 
		 */
	    public WString usri0_name;
	}
	
	/**
	 * The USER_INFO_1 structure contains information about a user account, including 
	 * account name, password data, privilege level, and the path to the user's home 
	 * directory.
	 */
	public static class USER_INFO_1 extends Structure {
		public USER_INFO_1() {
			super();
		}

		public USER_INFO_1(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a Unicode string that specifies the name of the user 
		 * account.
		 */
	    public WString usri1_name;
	    /**
	     * Pointer to a Unicode string that specifies the password of the user
	     * indicated by the usri1_name member. 
	     */
	    public WString usri1_password;
	    /**
	     * Specifies a DWORD value that indicates the number of seconds that have 
	     * elapsed since the usri1_password member was last changed.
	     */
	    public int usri1_password_age;
	    /**
	     * Specifies a DWORD value that indicates the level of privilege assigned 
	     * to the usri1_name member.
	     */
	    public int usri1_priv;
	    /**
	     * Pointer to a Unicode string specifying the path of the home directory 
	     * for the user specified in the usri1_name member. 
	     */
	    public WString usri1_home_dir;
	    /**
	     * Pointer to a Unicode string that contains a comment to associate with 
	     * the user account.
	     */
	    public WString usri1_comment;
	    /**
	     * Specifies a DWORD value that determines several features.
	     */
	    public int usri1_flags;
	    /**
	     * Pointer to a Unicode string specifying the path for the user's 
	     * logon script file. 
	     */
	    public WString usri1_script_path;
	}
}
