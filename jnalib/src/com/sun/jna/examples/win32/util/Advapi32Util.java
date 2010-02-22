package com.sun.jna.examples.win32.util;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.examples.win32.*;
import com.sun.jna.examples.win32.Advapi32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Advapi32 API.
 */
public abstract class Advapi32Util {

	/**
	 * Retrieves the name of the user associated with the current thread.
	 * @return A user name.
	 */
	public static String GetUserName() {
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
	 * Retrieves a security identifier (SID) for the account and the name of the domain
	 * on which the account was found.
	 * @param systemName Specifies the name of the system.
	 * @param accountName Specifies the account name.
	 * @return A SID.
	 * @throws Exception
	 */
	public static byte[] LookupAccountName(String systemName, String accountName) throws Exception {
		IntByReference pSid = new IntByReference(0);
		IntByReference pDomain = new IntByReference(0);
		PointerByReference peUse = new PointerByReference();
		
		char[] lpSystemName = (systemName == null) ? null : Native.toCharArray(systemName);
		char[] lpAccountName = (accountName == null) ? null : Native.toCharArray(accountName);

		if (Advapi32.INSTANCE.LookupAccountNameW(lpSystemName, lpAccountName, null, pSid, null, pDomain, peUse)) {
			throw new Exception("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
		}
		
		int rc = Kernel32.INSTANCE.GetLastError();
		if (pSid.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new LastErrorException(rc);
		}

		byte[] sid = new byte[pSid.getValue()];
		char[] referencedDomainName = new char[pDomain.getValue() + 1]; 

		if (! Advapi32.INSTANCE.LookupAccountNameW(lpSystemName, lpAccountName, sid, pSid, referencedDomainName, pDomain, peUse)) {
			throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
		}
		
		// type of SID: peUse.getPointer().getInt(0)
		// domain: Native.toString(referencedDomainName)
		
		return sid;
	}	
	
	/**
	 * Convert a security identifier (SID) to a string format suitable for display, storage, or transmission.
	 * @param sid
	 * @return A SID.
	 */
	public static String ConvertSidToStringSid(byte[] sid) {
		PointerByReference stringSid = new PointerByReference();
		if (! Advapi32.INSTANCE.ConvertSidToStringSidW(sid, stringSid)) {
			throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
		}
		String result = stringSid.getValue().getString(0, true);
		Kernel32.INSTANCE.LocalFree(stringSid.getPointer()); 
		return result;
	}
}
