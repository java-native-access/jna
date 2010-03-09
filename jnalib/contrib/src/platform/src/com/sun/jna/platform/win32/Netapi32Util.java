package com.sun.jna.platform.win32;

import java.util.ArrayList;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.LMAccess.LOCALGROUP_INFO_0;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Netapi32 Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Netapi32Util {

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
	public static String[] getLocalGroups() {
		return getLocalGroups(null);
	}
		
	/**
	 * Get the names of local groups on a computer.
	 * @param serverName Name of the computer.
	 * @return An array of local group names.
	 */
	public static String[] getLocalGroups(String serverName) {
		PointerByReference bufptr = new PointerByReference();
		IntByReference entriesRead = new IntByReference();
		IntByReference totalEntries = new IntByReference();		
		try {
			int rc = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 0, bufptr, LMCons.MAX_PREFERRED_LENGTH, entriesRead, totalEntries, null);
			if (LMErr.NERR_Success != rc || bufptr.getValue() == Pointer.NULL) {
				throw new LastErrorException(rc);
			}
			LMAccess.LOCALGROUP_INFO_0 group = new LMAccess.LOCALGROUP_INFO_0(bufptr.getValue());
			LMAccess.LOCALGROUP_INFO_0[] groups = (LOCALGROUP_INFO_0[]) group.toArray(entriesRead.getValue());
			
			ArrayList<String> result = new ArrayList<String>(); 
			for(LOCALGROUP_INFO_0 lgpi : groups) {
				result.add(lgpi.lgrui0_name.toString());
			}
			return result.toArray(new String[0]);
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
