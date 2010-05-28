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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from LMAccess.h.
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface LMAccess extends StdCallLibrary {
	
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

	/**
	 * The GROUP_USERS_INFO_0 structure contains global group member information.
	 */
	public static class GROUP_USERS_INFO_0 extends Structure {
		public GROUP_USERS_INFO_0() {
			super();
		}

		public GROUP_USERS_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated Unicode character string that specifies a name. 
		 */
	    public WString grui0_name;
	}
	
	/**
	 * The LOCALGROUP_USERS_INFO_0 structure contains local group member information.
	 */
	public static class LOCALGROUP_USERS_INFO_0 extends Structure {
		public LOCALGROUP_USERS_INFO_0() {
			super();
		}

		public LOCALGROUP_USERS_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a Unicode string specifying the name of a local group to which the user belongs. 
		 */
	    public WString lgrui0_name;
	}
	
	/**
	 * The GROUP_INFO_0 structure contains the name of a global group in the security
	 * database, which is the security accounts manager (SAM) database or, in the case
	 * of domain controllers, the Active Directory.
	 */
	public static class GROUP_INFO_0  extends Structure {
		public GROUP_INFO_0() {
			super();
		}

		public GROUP_INFO_0(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated Unicode character string that specifies 
		 * the name of the global group.
		 */
	    public WString grpi0_name;
	}

	/**
	 * The GROUP_INFO_1 structure contains a global group name and a comment to 
	 * associate with the group.
	 */
	public static class GROUP_INFO_1  extends Structure {
		public GROUP_INFO_1() {
			super();
		}

		public GROUP_INFO_1(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated Unicode character string that specifies 
		 * the name of the global group. 
		 */
	    public WString grpi1_name;
	    /**
	     * Pointer to a null-terminated Unicode character string that specifies 
	     * a remark associated with the global group. This member can be a null 
	     * string. The comment can contain MAXCOMMENTSZ characters. 
	     */
	    public WString grpi1_comment;
	}

	/**
	 * The GROUP_INFO_2 structure contains information about a global group, including
	 * name, identifier, and resource attributes.
	 */
	public static class GROUP_INFO_2  extends Structure {
		public GROUP_INFO_2() {
			super();
		}

		public GROUP_INFO_2(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated Unicode character string that 
		 * specifies the name of the global group.
		 */
	    public WString grpi2_name;
	    /**
	     * Pointer to a null-terminated Unicode character string that contains a 
	     * remark associated with the global group. This member can be a null string. 
	     * The comment can contain MAXCOMMENTSZ characters. 
	     */
	    public WString grpi2_comment;
	    /**
	     * Specifies a DWORD value that contains the relative identifier (RID) of 
	     * the global group.
	     */
	    public int grpi2_group_id;
	    /**
	     * These attributes are hard-coded to SE_GROUP_MANDATORY, SE_GROUP_ENABLED, 
	     * and SE_GROUP_ENABLED_BY_DEFAULT. 
	     */
	    public int grpi2_attributes;
	}

	/**
	 * The GROUP_INFO_3 structure contains information about a global group, including 
	 * name, security identifier (SID), and resource attributes.
	 */
	public static class GROUP_INFO_3  extends Structure {
		public GROUP_INFO_3() {
			super();
		}

		public GROUP_INFO_3(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated Unicode character string that 
		 * specifies the name of the global group. 
		 */
	    public WString grpi3_name;
	    /**
	     * Pointer to a null-terminated Unicode character string that 
	     * contains a remark associated with the global group. This member can be 
	     * a null string. The comment can contain MAXCOMMENTSZ characters. 
	     */
	    public WString grpi3_comment;
	    /**
	     * Pointer to a SID structure that contains the security identifier (SID) that 
	     * uniquely identifies the global group.
	     */
	    public PSID grpi3_group_sid;
	    /**
	     * These attributes are hard-coded to SE_GROUP_MANDATORY, SE_GROUP_ENABLED, and 
	     * SE_GROUP_ENABLED_BY_DEFAULT.
	     */
	    public int grpi3_attributes;
	}
	
	//
	// Privilege levels (USER_INFO_X field usriX_priv (X = 0/1)).
	//

	public static final int USER_PRIV_MASK = 3;
	public static final int USER_PRIV_GUEST = 0;
	public static final int USER_PRIV_USER = 1;
	public static final int USER_PRIV_ADMIN = 2;	
}
