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
            super(memory);
            read();
        }
                    
        public WString lgrui0_name;
    }

    public static class LOCALGROUP_INFO_1 extends Structure {
        public LOCALGROUP_INFO_1() {
            super();
        }

        public LOCALGROUP_INFO_1(Pointer memory) {
            super(memory);
            read();
        }

        public WString lgrui1_name;
        public WString lgrui1_comment;
    }
	
    //
    // bit masks for the NetUserEnum filter parameter.
    //
                
    int FILTER_TEMP_DUPLICATE_ACCOUNT = 0x0001;
    int FILTER_NORMAL_ACCOUNT = 0x0002;
    // int FILTER_PROXY_ACCOUNT = 0x0004;
    int FILTER_INTERDOMAIN_TRUST_ACCOUNT = 0x0008;
    int FILTER_WORKSTATION_TRUST_ACCOUNT = 0x0010;
    int FILTER_SERVER_TRUST_ACCOUNT = 0x0020;	
	
    /**
     * The USER_INFO_0 structure contains a user account name.
     */
    public static class USER_INFO_0 extends Structure {
        public USER_INFO_0() {
            super();
        }

        public USER_INFO_0(Pointer memory) {
            super(memory);
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
            super(memory);
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
     * The USER_INFO_23 structure contains information about a user account, 
     * including the account name, the user's full name, a comment associated with the account, 
     * and the user's security identifier (SID).
     *
     * Note : 
     * The USER_INFO_23 structure supersedes the USER_INFO_20 structure. 
     * It is recommended that applications use the USER_INFO_23 structure instead of the USER_INFO_20 structure.
     */
    public static class USER_INFO_23 extends Structure {
        public USER_INFO_23() {
            super();
        }

        public USER_INFO_23(Pointer memory) {
            useMemory(memory);
            read();
        }
		
        /** 
         * A pointer to a Unicode string that specifies the name of the user account. 
         * Calls to the NetUserSetInfo function ignore this member.
         */
        WString usri23_name;
        /** 
         * A pointer to a Unicode string that contains the full name of the user. 
         * This string can be a null string, or it can have any number of characters before the terminating null character.
         */
        WString usri23_full_name;
        /** 
         * A pointer to a Unicode string that contains a comment associated with the user account. 
         * This string can be a null string, or it can have any number of characters before the terminating null character.
         */
        WString usri23_comment;
        /** 
         * This member can be one or more of the following values. 
         * Note that setting user account control flags may require certain privileges and control access rights. 
         * For more information, see the Remarks section of the NetUserSetInfo function.
         * Value	         Meaning
         * UF_SCRIPT         The logon script executed. This value must be set.
         * UF_ACCOUNTDISABLE The user's account is disabled.
         * UF_HOMEDIR_REQUIRED The home directory is required. This value is ignored.
         * UF_PASSWD_NOTREQD No password is required.
         * UF_PASSWD_CANT_CHANGE The user cannot change the password.
         * UF_LOCKOUT        The account is currently locked out. You can call the NetUserSetInfo function to clear this value and unlock a previously locked account. You cannot use this value to lock a previously unlocked account.
         * UF_DONT_EXPIRE_PASSWD The password should never expire on the account.
         * UF_ENCRYPTED_TEXT_PASSWORD_ALLOWED The user's password is stored under reversible encryption in the Active Directory.
         * UF_NOT_DELEGATED Marks the account as "sensitive"; other users cannot act as delegates of this user account.
         * UF_SMARTCARD_REQUIRED Requires the user to log on to the user account with a smart card.
         * UF_USE_DES_KEY_ONLY Restrict this principal to use only Data Encryption Standard (DES) encryption types for keys.
         * UF_DONT_REQUIRE_PREAUTH This account does not require Kerberos preauthentication for logon.
         * UF_TRUSTED_FOR_DELEGATION The account is enabled for delegation. This is a security-sensitive setting; accounts with this option enabled should be tightly controlled. This setting allows a service running under the account to assume a client's identity and authenticate as that user to other remote servers on the network.
         * UF_PASSWORD_EXPIRED The user's password has expired. Windows 2000:  This value is not supported.
         * UF_TRUSTED_TO_AUTHENTICATE_FOR_DELEGATION The account is trusted to authenticate a user outside of the Kerberos security package and delegate that user through constrained delegation. This is a security-sensitive setting; accounts with this option enabled should be tightly controlled. This setting allows a service running under the account to assert a client's identity and authenticate as that user to specifically configured services on the network. Windows XP/2000:  This value is not supported.
         * 
         * The following values describe the account type. Only one value can be set. You cannot change the account type using the NetUserSetInfo function.
         * Value	             Meaning
         * UF_NORMAL_ACCOUNT     This is a default account type that represents a typical user.
         * UF_TEMP_DUPLICATE_ACCOUNT This is an account for users whose primary account is in another domain. This account provides user access to this domain, but not to any domain that trusts this domain. The User Manager refers to this account type as a local user account.
         * UF_WORKSTATION_TRUST_ACCOUNT This is a computer account for a computer that is a member of this domain.
         * UF_SERVER_TRUST_ACCOUNT This is a computer account for a backup domain controller that is a member of this domain.
         * UF_INTERDOMAIN_TRUST_ACCOUNT This is a permit to trust account for a domain that trusts other domains.
         */
        int  usri23_flags;
        /** 
         * A pointer to a SID structure that contains the security identifier (SID) 
         * that uniquely identifies the user. The NetUserAdd and NetUserSetInfo functions ignore this member.
         */
        PSID   usri23_user_sid;		
    }	
	
    /**
     * The GROUP_USERS_INFO_0 structure contains global group member information.
     */
    public static class GROUP_USERS_INFO_0 extends Structure {
        public GROUP_USERS_INFO_0() {
            super();
        }

        public GROUP_USERS_INFO_0(Pointer memory) {
            super(memory);
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
            super(memory);
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
            super(memory);
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
            super(memory);
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
            super(memory);
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
            super(memory);
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

    int USER_PRIV_MASK = 3;
    int USER_PRIV_GUEST = 0;
    int USER_PRIV_USER = 1;
    int USER_PRIV_ADMIN = 2;	
}
