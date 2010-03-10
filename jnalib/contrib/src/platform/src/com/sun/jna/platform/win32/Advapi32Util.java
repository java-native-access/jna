package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Advapi32 utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Advapi32Util {

	/**
	 * Retrieves the name of the user associated with the current thread.
	 * @return A user name.
	 */
	public static String getUserName() {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len); 
		
		if (! result) {
			
			int rc = Kernel32.INSTANCE.GetLastError();

			switch(rc) {
			case W32Errors.ERROR_INSUFFICIENT_BUFFER:
				buffer = new char[len.getValue()];
				break;
			default:
				throw new LastErrorException(Native.getLastError());
			}
			
			result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
		}
		
		if (! result) {
			throw new LastErrorException(Native.getLastError());
		}
		
		return Native.toString(buffer);
	}

	/**
	 * Retrieves a security identifier (SID) for the account on the current system.
	 * @param accountName Specifies the account name.
	 * @return A SID.
	 */
	public static byte[] getAccountSid(String accountName) {
		return getAccountSid(null, accountName);
	}
			
	/**
	 * Retrieves a security identifier (SID) for the account.
	 * @param systemName Name of the system.
	 * @param accountName Account name.
	 * @return A SID.
	 */
	public static byte[] getAccountSid(String systemName, String accountName) {
		IntByReference pSid = new IntByReference(0);
		IntByReference pDomain = new IntByReference(0);
		PointerByReference peUse = new PointerByReference();
		
		if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, null, pSid, null, pDomain, peUse)) {
			throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
		}
		
		int rc = Kernel32.INSTANCE.GetLastError();
		if (pSid.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new LastErrorException(rc);
		}

		byte[] sid = new byte[pSid.getValue()];
		char[] referencedDomainName = new char[pDomain.getValue() + 1]; 

		if (! Advapi32.INSTANCE.LookupAccountName(systemName, accountName, sid, pSid, referencedDomainName, pDomain, peUse)) {
			throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
		}
		
		// type of SID: peUse.getPointer().getInt(0)
		// domain: Native.toString(referencedDomainName)
		
		return sid;
	}

	/**
	 * Get the account name by SID on the local system.
	 * 
	 * @param sid SID.
	 * @return Account name.
	 */
	public static String getAccountName(byte[] sid) {
		return getAccountName(null, sid);
	}
	
	/**
	 * Get the account name by SID.
	 * 
	 * @param systemName Name of the system.
	 * @param sid SID.
	 * @return Account name.
	 */
	public static String getAccountName(String systemName, byte[] sid) {
    	IntByReference cchName = new IntByReference();
    	IntByReference cchReferencedDomainName = new IntByReference();
    	PointerByReference peUse = new PointerByReference();

    	if (Advapi32.INSTANCE.LookupAccountSid(null, sid, 
    			null, cchName, null, cchReferencedDomainName, peUse)) {
			throw new RuntimeException("LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
    	}
    	
    	int rc = Kernel32.INSTANCE.GetLastError();
		if (cchName.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new LastErrorException(rc);
		}    	
		
		char[] referencedDomainName = new char[cchReferencedDomainName.getValue()];
		char[] name = new char[cchName.getValue()];
    	
		if (! Advapi32.INSTANCE.LookupAccountSid(null, sid, 
    			name, cchName, referencedDomainName, cchReferencedDomainName, peUse)) {
    		throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
    	}
		
    	// type of SID: peUse.getPointer().getInt(0)
    	
		if (cchReferencedDomainName.getValue() > 0) {
			return Native.toString(referencedDomainName) + "\\" + Native.toString(name);
		} else {
			return Native.toString(name);
		}
	}
	
	/**
	 * Convert a security identifier (SID) to a string format suitable for display, 
	 * storage, or transmission.
	 * @param sid SID bytes.
	 * @return String SID.
	 */
	public static String convertSidToStringSid(byte[] sid) {
		PointerByReference stringSid = new PointerByReference();
		if (! Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
			throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
		}
		String result = stringSid.getValue().getString(0, true);
		Kernel32.INSTANCE.LocalFree(stringSid.getValue()); 
		return result;
	}
	
	/**
	 * Convert a string representation of a security identifier (SID) to 
	 * a binary format.
	 * @param sid String SID.
	 * @return SID bytes.
	 */
	public static byte[] convertStringSidToSid(String sid) {
		PointerByReference sidBytes = new PointerByReference();
		if (! Advapi32.INSTANCE.ConvertStringSidToSid(sid, sidBytes)) {
			throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
		}
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sidBytes);
    	byte[] result = sidBytes.getValue().getByteArray(0, sidLength);
		Kernel32.INSTANCE.LocalFree(sidBytes.getValue()); 
		return result;
	}	

	/**
	 * Get the string representation of a SID for a given account on the local system.
	 * 
	 * @param accountName Account name.
	 * @return SID.
	 */
	public static String getAccountSidString(String accountName) {
		return convertSidToStringSid(getAccountSid(null, accountName));
	}
	
	/**
	 * Get the string representation of a SID for a given account.
	 * 
	 * @param systemName System name.
	 * @param accountName Account name.
	 * @return SID.
	 */
	public static String getAccountSidString(String systemName, String accountName) {
		return convertSidToStringSid(getAccountSid(systemName, accountName));
	}

	/**
	 * Get an account name from a string SID on the local machine.
	 * 
	 * @param sidString SID.
	 * @return Account name.
	 */
	public static String getAccountName(String sidString) {
		return getAccountName(null, sidString); 
	}
	
	/**
	 * Get an account name from a string SID.
	 * 
	 * @param systemName System name.
	 * @param sidString SID.
	 * @return Account name.
	 */
	public static String getAccountName(String systemName, String sidString) {
		return getAccountName(systemName, convertStringSidToSid(sidString)); 
	}
}
