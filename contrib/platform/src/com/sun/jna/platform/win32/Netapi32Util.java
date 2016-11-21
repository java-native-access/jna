/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32;

import java.util.ArrayList;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.DsGetDC.DS_DOMAIN_TRUSTS;
import com.sun.jna.platform.win32.DsGetDC.PDOMAIN_CONTROLLER_INFO;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.LMAccess.GROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_INFO_1;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_23;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Netapi32 Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Netapi32Util {
	
    /**
     * A group.
     */
    public static class Group {
        /**
         * Group name.
         */
        public String name;
    }
	
    /**
     * A user.
     */
    public static class User {
        /**
         * The name of the user account. 
         */
        public String name;
        /**
         * Contains a comment associated with the user account.
         */
        public String comment;
    }
	
    public static class UserInfo extends User {
        /**
         * The full name belonging to the user account
         */
        public String fullName;
        /**
         * The SID of the user account
         */
        public String sidString;
        /**
         * The SID of the user account
         */
        public PSID sid;
        /**
         * The flags of the user account
         */
        public int flags;
    }
	
    /**
     * A local group.
     */
    public static class LocalGroup extends Group {
        /**
         * Group comment.
         */
        public String comment;
    }
	
    /**
     * Returns the name of the primary domain controller (PDC) on the current computer.
     * @return The name of the primary domain controller.
     */
    public static String getDCName() {
        return getDCName(null, null);
    }
	
    /**
     * Returns the name of the primary domain controller (PDC).
     * @param serverName 
     * 	Specifies the DNS or NetBIOS name of the remote server on which the function is 
     * 	to execute.
     * @param domainName
     * 	Specifies the name of the domain.
     * @return 
     *  Name of the primary domain controller.
     */
    public static String getDCName(String serverName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        try {		
            int rc = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
            if (LMErr.NERR_Success != rc) {
                throw new Win32Exception(rc);
            }
            return bufptr.getValue().getWideString(0);
        } finally {
            if (W32Errors.ERROR_SUCCESS != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
    }

    /**
     * Return the domain/workgroup join status for a computer. 
     * @return Join status.
     */
    public static int getJoinStatus() {
        return getJoinStatus(null);
    }
	
    /**
     * Return the domain/workgroup join status for a computer. 
     * @param computerName Computer name.
     * @return Join status.
     */
    public static int getJoinStatus(String computerName) {
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
		
        try {
            int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (LMErr.NERR_Success != rc) {
                throw new Win32Exception(rc);			
            }
            return bufferType.getValue();
        } finally {
            if (lpNameBuffer.getPointer() != null) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);			
                }
            }
        }		
    }
	
    /**
     * Get information about a computer.
     * @param computerName computer name
     * @return Domain or workgroup name.
     */
    public static String getDomainName(String computerName) {
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
		
        try {
            int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (LMErr.NERR_Success != rc) {
                throw new Win32Exception(rc);			
            }		
            // type of domain: bufferType.getValue()
            return lpNameBuffer.getValue().getWideString(0);
        } finally {
            if (lpNameBuffer.getPointer() != null) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);			
                }
            }
        }
    }

    /**
     * Get the names of local groups on the current computer.
     * @return An array of local group names.
     */
    public static LocalGroup[] getLocalGroups() {
        return getLocalGroups(null);
    }
		
    /**
     * Get the names of local groups on a computer.
     * @param serverName Name of the computer.
     * @return An array of local group names.
     */
    public static LocalGroup[] getLocalGroups(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();		
        try {
            int rc = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesRead, totalEntries, null);
            if (LMErr.NERR_Success != rc || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc);
            }
            LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
            LMAccess.LOCALGROUP_INFO_1[] groups = (LOCALGROUP_INFO_1[]) group.toArray(entriesRead.getValue());
			
            ArrayList<LocalGroup> result = new ArrayList<LocalGroup>(); 
            for(LOCALGROUP_INFO_1 lgpi : groups) {
                LocalGroup lgp = new LocalGroup();
                if (lgpi.lgrui1_name != null) {
                	lgp.name = lgpi.lgrui1_name.toString();
                }
                if (lgpi.lgrui1_comment != null) {
                	lgp.comment = lgpi.lgrui1_comment.toString();
                }
                result.add(lgp);
            }
            return result.toArray(new LocalGroup[0]);
        } finally {			
            if (bufptr.getValue() != Pointer.NULL) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);
                }
            }
        }
    }

    /**
     * Get the names of global groups on a computer.
     * @return An array of group names.
     */
    public static Group[] getGlobalGroups() {
        return getGlobalGroups(null);
    }
	
    /**
     * Get the names of global groups on a computer.
     * @param serverName Name of the computer.
     * @return An array of group names.
     */
    public static Group[] getGlobalGroups(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();		
        try {
            int rc = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, 
                                                    LMCons.MAX_PREFERRED_LENGTH, entriesRead, 
                                                    totalEntries, null);
            if (LMErr.NERR_Success != rc || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc);
            }
            LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
            LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[]) group.toArray(entriesRead.getValue());
			
            ArrayList<LocalGroup> result = new ArrayList<LocalGroup>(); 
            for(LMAccess.GROUP_INFO_1 lgpi : groups) {
                LocalGroup lgp = new LocalGroup();
                if (lgpi.grpi1_name != null) {
                	lgp.name = lgpi.grpi1_name.toString();
                }
                if (lgpi.grpi1_comment != null) {
                	lgp.comment = lgpi.grpi1_comment.toString();
                }
                result.add(lgp);
            }
            return result.toArray(new LocalGroup[0]);
        } finally {			
            if (bufptr.getValue() != Pointer.NULL) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);
                }
            }
        }
    }
	
    /**
     * Get the names of users on a local computer.
     * @return Users.
     */
    public static User[] getUsers() {
        return getUsers(null);
    }

    /**
     * Get the names of users on a computer.
     * @param serverName Name of the computer.
     * @return An array of users.
     */
    public static User[] getUsers(String serverName) {
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();		
        try {
            int rc = Netapi32.INSTANCE.NetUserEnum(
            		serverName, 1, 0, bufptr, 
                    LMCons.MAX_PREFERRED_LENGTH, entriesRead, 
                    totalEntries, null);
            if (LMErr.NERR_Success != rc || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc);
            }
            LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
            LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[]) user.toArray(entriesRead.getValue());
            ArrayList<User> result = new ArrayList<User>(); 
            for(LMAccess.USER_INFO_1 lu : users) {
                User auser = new User();
                if (lu.usri1_name != null) {
                	auser.name = lu.usri1_name.toString();
                }
                result.add(auser);
            }
            return result.toArray(new User[0]);
        } finally {			
            if (bufptr.getValue() != Pointer.NULL) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);
                }
            }
        }
    }
	
    /**
     * Get local groups of the current user.
     * @return Local groups.
     */
    public static Group[] getCurrentUserLocalGroups() {
        return getUserLocalGroups(Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible));
    }
	
    /**
     * Get local groups of a given user.
     * @param userName User name.
     * @return Local groups.
     */
    public static Group[] getUserLocalGroups(String userName) {
        return getUserLocalGroups(userName, null);
    }
	
    /**
     * Get local groups of a given user on a given system.
     * @param userName User name.
     * @param serverName Server name.
     * @return Local groups.
     */
    public static Group[] getUserLocalGroups(String userName, String serverName) {
    	PointerByReference bufptr = new PointerByReference();
    	IntByReference entriesread = new IntByReference();
    	IntByReference totalentries = new IntByReference();
    	try {
            int rc = Netapi32.INSTANCE.NetUserGetLocalGroups(
            		serverName, userName, 
                    0, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries);
            if (rc != LMErr.NERR_Success) {
                throw new Win32Exception(rc);
            }
            LOCALGROUP_USERS_INFO_0 lgroup = new LOCALGROUP_USERS_INFO_0(bufptr.getValue());    	
            LOCALGROUP_USERS_INFO_0[] lgroups = (LOCALGROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
            ArrayList<Group> result = new ArrayList<Group>(); 
            for (LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
                LocalGroup lgp = new LocalGroup();
                if (lgpi.lgrui0_name != null) {
                	lgp.name = lgpi.lgrui0_name.toString();
                }
                result.add(lgp);
            }
            return result.toArray(new Group[0]);
    	} finally {
            if (bufptr.getValue() != Pointer.NULL) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);
                }
            }
    	}
    }
	
    /**
     * Get groups of a given user.
     * @param userName User name.
     * @return Groups.
     */
    public static Group[] getUserGroups(String userName) {
        return getUserGroups(userName, null);
    }
	
    /**
     * Get groups of a given user on a given system.
     * @param userName User name.
     * @param serverName Server name.
     * @return Groups.
     */
    public static Group[] getUserGroups(String userName, String serverName) {
    	PointerByReference bufptr = new PointerByReference();
    	IntByReference entriesread = new IntByReference();
    	IntByReference totalentries = new IntByReference();
    	try {
            int rc = Netapi32.INSTANCE.NetUserGetGroups(
            		serverName, userName, 
                    0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries);
            if (rc != LMErr.NERR_Success) {
                throw new Win32Exception(rc);
            }
            GROUP_USERS_INFO_0 lgroup = new GROUP_USERS_INFO_0(bufptr.getValue());    	
            GROUP_USERS_INFO_0[] lgroups = (GROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
            ArrayList<Group> result = new ArrayList<Group>(); 
            for (GROUP_USERS_INFO_0 lgpi : lgroups) {
                Group lgp = new Group();
                if (lgpi.grui0_name != null) {
                	lgp.name = lgpi.grui0_name.toString();
                }
                result.add(lgp);
            }
            return result.toArray(new Group[0]);
    	} finally {
            if (bufptr.getValue() != Pointer.NULL) {
                int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                if (LMErr.NERR_Success != rc) {
                    throw new Win32Exception(rc);
                }
            }
    	}
    }	
	
    /**
     * A domain controller.
     */
    public static class DomainController {
        /**
         * Specifies the computer name of the discovered domain controller.
         */
        public String name;
        /**
         * Specifies the address of the discovered domain controller.
         */
        public String address;
        /**
         * Indicates the type of string that is contained in the 
         * DomainControllerAddress member.
         */
        public int addressType;
        /**
         * The GUID of the domain.
         */
        public GUID domainGuid;
        /**
         * Pointer to a null-terminated string that specifies the name of the domain. 
         */
        public String domainName;
        /**
         * Pointer to a null-terminated string that specifies the name of the domain at the root 
         * of the DS tree.
         */
        public String dnsForestName;
        /**
         * Contains a set of flags that describe the domain controller. 
         */
        public int flags;
        /**
         * The name of the site that the computer belongs to.
         */
        public String clientSiteName;
    }
	
    /**
     * Return the domain controller for a current computer.
     * @return
     *  Domain controller information.
     */
    public static DomainController getDC() {
        PDOMAIN_CONTROLLER_INFO pdci = new PDOMAIN_CONTROLLER_INFO();
        int rc = Netapi32.INSTANCE.DsGetDcName(null, null, null, null, 0, pdci);
    	if (W32Errors.ERROR_SUCCESS != rc) {
            throw new Win32Exception(rc);
    	}
    	DomainController dc = new DomainController();
    	if (pdci.dci.DomainControllerAddress != null) {
    		dc.address = pdci.dci.DomainControllerAddress.toString();
    	}
    	dc.addressType = pdci.dci.DomainControllerAddressType;
    	if (pdci.dci.ClientSiteName != null) {
    		dc.clientSiteName = pdci.dci.ClientSiteName.toString();
    	}
    	if (pdci.dci.DnsForestName != null) {
    		dc.dnsForestName = pdci.dci.DnsForestName.toString();
    	}
    	dc.domainGuid = pdci.dci.DomainGuid;
    	if (pdci.dci.DomainName != null) {
    		dc.domainName = pdci.dci.DomainName.toString();
    	}
    	dc.flags = pdci.dci.Flags;
    	if (pdci.dci.DomainControllerName != null) {
    		dc.name = pdci.dci.DomainControllerName.toString();
    	}
        rc = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
        if (LMErr.NERR_Success != rc) {
            throw new Win32Exception(rc);
        }
        return dc;
    }
	
    /**
     * A domain trust relationship.
     */
    public static class DomainTrust {
        /**
         * NetBIOS name of the domain.
         */
        public String NetbiosDomainName;
        /**
         * DNS name of the domain.
         */
        public String DnsDomainName;
        /**
         * Contains the security identifier of the domain represented by this structure.
         */
        public PSID DomainSid;
        /**
         * Contains the string representation of the security identifier of the domain 
         * represented by this structure.
         */
        public String DomainSidString;
        /**
         * Contains the GUID of the domain represented by this structure.
         */
        public GUID DomainGuid;
        /**
         * Contains the string representation of the GUID of the domain represented by 
         * this structure.
         */	
        public String DomainGuidString;
		
        /**
         * Contains a set of flags that specify more data about the domain trust.
         */
        private int flags;
		
        /**
         * The domain represented by this structure is a member of the same forest 
         * as the server specified in the ServerName parameter of the 
         * DsEnumerateDomainTrusts function.
         * @return
         *  True or false.
         */
        public boolean isInForest() { 
            return (flags & DsGetDC.DS_DOMAIN_IN_FOREST) != 0; 
        }
		
        /**
         * The domain represented by this structure is directly trusted by the domain
         * that the server specified in the ServerName parameter of the 
         * DsEnumerateDomainTrusts function is a member of.
         * @return
         *  True or false.
         */
        public boolean isOutbound() { 
            return (flags & DsGetDC.DS_DOMAIN_DIRECT_OUTBOUND) != 0; 
        }
		
        /**
         * The domain represented by this structure is the root of a tree and a member 
         * of the same forest as the server specified in the ServerName parameter of the
         * DsEnumerateDomainTrusts function.
         * @return
         *  True or false.
         */
        public boolean isRoot() { 
            return (flags & DsGetDC.DS_DOMAIN_TREE_ROOT) != 0; 
        }
	
        /**
         * The domain represented by this structure is the primary domain of the server
         * specified in the ServerName parameter of the DsEnumerateDomainTrusts function.
         * @return
         *  True or false.
         */
        public boolean isPrimary() { 
            return (flags & DsGetDC.DS_DOMAIN_PRIMARY) != 0; 
        }
		
        /**
         * The domain represented by this structure is running in the Windows 2000 native mode.
         * @return
         *  True or false.
         */
        public boolean isNativeMode() {
            return (flags & DsGetDC.DS_DOMAIN_NATIVE_MODE) != 0; 
        }
		
        /**
         * The domain represented by this structure directly trusts the domain that
         * the server specified in the ServerName parameter of the DsEnumerateDomainTrusts
         * function is a member of.
         * @return
         *  True or false.
         */
        public boolean isInbound() { 
            return (flags & DsGetDC.DS_DOMAIN_DIRECT_INBOUND) != 0; 
        }		
    }
	
    /**
     * Retrieve all domain trusts.
     * @return
     *  An array of domain trusts.
     */
    public static DomainTrust[] getDomainTrusts() {
        return getDomainTrusts(null);
    }
	
    /**
     * Retrieve all domain trusts for a given server.
     * @param serverName
     *  Server name.
     * @return
     *  An array of domain trusts.
     */
    public static DomainTrust[] getDomainTrusts(String serverName) {
    	IntByReference domainTrustCount = new IntByReference();
        PointerByReference domainsPointerRef = new PointerByReference();
        int rc = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, 
                DsGetDC.DS_DOMAIN_VALID_FLAGS, domainsPointerRef, domainTrustCount);
    	if(W32Errors.NO_ERROR != rc) {
            throw new Win32Exception(rc);
    	}
    	try {
            DS_DOMAIN_TRUSTS domainTrustRefs = new DS_DOMAIN_TRUSTS(domainsPointerRef.getValue());
            DS_DOMAIN_TRUSTS[] domainTrusts = (DS_DOMAIN_TRUSTS[]) domainTrustRefs.toArray(new DS_DOMAIN_TRUSTS[domainTrustCount.getValue()]);
            ArrayList<DomainTrust> trusts = new ArrayList<DomainTrust>(domainTrustCount.getValue());
            for(DS_DOMAIN_TRUSTS domainTrust : domainTrusts) {
                DomainTrust t = new DomainTrust();
                if (domainTrust.DnsDomainName != null) {
                	t.DnsDomainName = domainTrust.DnsDomainName.toString();
                }
                if (domainTrust.NetbiosDomainName != null) {
                	t.NetbiosDomainName = domainTrust.NetbiosDomainName.toString();
                }
                t.DomainSid = domainTrust.DomainSid;
                if (domainTrust.DomainSid != null) {
                	t.DomainSidString = Advapi32Util.convertSidToStringSid(domainTrust.DomainSid);
                }
                t.DomainGuid = domainTrust.DomainGuid;
                if (domainTrust.DomainGuid != null) {
                	t.DomainGuidString = Ole32Util.getStringFromGUID(domainTrust.DomainGuid);
                }
                t.flags = domainTrust.Flags;
                trusts.add(t);
            }
            return trusts.toArray(new DomainTrust[0]);
    	} finally {
            rc = Netapi32.INSTANCE.NetApiBufferFree(domainsPointerRef.getValue());
            if(W32Errors.NO_ERROR != rc) {
                throw new Win32Exception(rc);
            }
    	}
    }
	
    public static UserInfo getUserInfo(String accountName) {
        return getUserInfo(accountName, Netapi32Util.getDCName());
    }
	
    public static UserInfo getUserInfo(String accountName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        int rc = -1;
        try {
            rc = Netapi32.INSTANCE.NetUserGetInfo(domainName, accountName, (short)23, bufptr);
            if (rc == LMErr.NERR_Success) {
                USER_INFO_23 info_23 = new USER_INFO_23(bufptr.getValue());
                UserInfo userInfo = new UserInfo();
                if (info_23.usri23_comment != null) {
                	userInfo.comment = info_23.usri23_comment.toString();
                }
                userInfo.flags = info_23.usri23_flags;
                if (info_23.usri23_full_name != null) {
                	userInfo.fullName = info_23.usri23_full_name.toString();
                }
                if (info_23.usri23_name != null) {
                	userInfo.name = info_23.usri23_name.toString();
                }
                if (info_23.usri23_user_sid != null) {
                	userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
                }
                userInfo.sid = info_23.usri23_user_sid;
                return userInfo;
            } else {
                throw new Win32Exception(rc);
            }
        } finally {
            if (bufptr.getValue() != Pointer.NULL) {
                Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
            }
        }
    }

}
