package com.sun.jna.platform.win32;

import java.util.ArrayList;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.LMAccess.GROUP_USERS_INFO_0;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_INFO_1;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_USERS_INFO_0;
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
	    		throw new LastErrorException(rc);
	    	}
	    	return bufptr.getValue().getString(0);
		} finally {
			if (W32Errors.ERROR_SUCCESS != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
				throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
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
				throw new LastErrorException(rc);			
			}
			return bufferType.getValue();
		} finally {
			if (lpNameBuffer.getPointer() != null) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);			
				}
			}
		}		
	}
	
	/**
	 * Get information about a computer.
	 * @param computerName
	 * @return Domain or workgroup name.
	 */
	public static String getDomainName(String computerName) {
		PointerByReference lpNameBuffer = new PointerByReference();
		IntByReference bufferType = new IntByReference();
		
		try {
			int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
			if (LMErr.NERR_Success != rc) {
				throw new LastErrorException(rc);			
			}		
			// type of domain: bufferType.getValue()
			return lpNameBuffer.getValue().getString(0, true);
		} finally {
			if (lpNameBuffer.getPointer() != null) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);			
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
				throw new LastErrorException(rc);
			}
			LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
			LMAccess.LOCALGROUP_INFO_1[] groups = (LOCALGROUP_INFO_1[]) group.toArray(entriesRead.getValue());
			
			ArrayList<LocalGroup> result = new ArrayList<LocalGroup>(); 
			for(LOCALGROUP_INFO_1 lgpi : groups) {
				LocalGroup lgp = new LocalGroup();
				lgp.name = lgpi.lgrui1_name.toString();			
				lgp.comment = lgpi.lgrui1_comment.toString();;
				result.add(lgp);
			}
			return result.toArray(new LocalGroup[0]);
		} finally {			
			if (bufptr.getValue() != Pointer.NULL) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);
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
				throw new LastErrorException(rc);
			}
			LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
			LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[]) group.toArray(entriesRead.getValue());
			
			ArrayList<LocalGroup> result = new ArrayList<LocalGroup>(); 
			for(LMAccess.GROUP_INFO_1 lgpi : groups) {
				LocalGroup lgp = new LocalGroup();
				lgp.name = lgpi.grpi1_name.toString();			
				lgp.comment = lgpi.grpi1_comment.toString();;
				result.add(lgp);
			}
			return result.toArray(new LocalGroup[0]);
		} finally {			
			if (bufptr.getValue() != Pointer.NULL) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);
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
			int rc = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, 
					LMCons.MAX_PREFERRED_LENGTH, entriesRead, 
					totalEntries, null);
			if (LMErr.NERR_Success != rc || bufptr.getValue() == Pointer.NULL) {
				throw new LastErrorException(rc);
			}
			LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
			LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[]) user.toArray(entriesRead.getValue());
			
			ArrayList<User> result = new ArrayList<User>(); 
			for(LMAccess.USER_INFO_1 lu : users) {
				User auser = new User();
				auser.name = lu.usri1_name.toString();
				result.add(auser);
			}
			return result.toArray(new User[0]);
		} finally {			
			if (bufptr.getValue() != Pointer.NULL) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);
				}
			}
		}
	}
	
	/**
	 * Get local groups of the current user.
	 * @return Local groups.
	 */
	public static Group[] getCurrentUserLocalGroups() {
		return getUserLocalGroups(Advapi32Util.getUserName());
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
	    	int rc = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 
	    			0, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries);
	    	if (rc != LMErr.NERR_Success) {
	    		throw new LastErrorException(rc);
	    	}
	    	LOCALGROUP_USERS_INFO_0 lgroup = new LOCALGROUP_USERS_INFO_0(bufptr.getValue());    	
	    	LOCALGROUP_USERS_INFO_0[] lgroups = (LOCALGROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
			ArrayList<Group> result = new ArrayList<Group>(); 
	        for (LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
				LocalGroup lgp = new LocalGroup();
				lgp.name = lgpi.lgrui0_name.toString();
				result.add(lgp);
			}
			return result.toArray(new Group[0]);
    	} finally {
    		if (bufptr.getValue() != Pointer.NULL) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);
				}
			}
    	}
	}
	
	/**
	 * Get local groups of the current user.
	 * @return Local groups.
	 */
	public static Group[] getCurrentUserGroups() {
		return getUserGroups(Advapi32Util.getUserName());
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
	    	int rc = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 
	    			0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesread, totalentries);
	    	if (rc != LMErr.NERR_Success) {
	    		throw new LastErrorException(rc);
	    	}
	    	GROUP_USERS_INFO_0 lgroup = new GROUP_USERS_INFO_0(bufptr.getValue());    	
	    	GROUP_USERS_INFO_0[] lgroups = (GROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
			ArrayList<Group> result = new ArrayList<Group>(); 
	        for (GROUP_USERS_INFO_0 lgpi : lgroups) {
				Group lgp = new Group();
				lgp.name = lgpi.grui0_name.toString();
				result.add(lgp);
			}
			return result.toArray(new Group[0]);
    	} finally {
    		if (bufptr.getValue() != Pointer.NULL) {
				int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
				if (LMErr.NERR_Success != rc) {
					throw new LastErrorException(rc);
				}
			}
    	}
	}	
}
