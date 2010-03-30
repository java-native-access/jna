package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.W32API.HRESULT;

/**
 * Ole32 Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Ole32Util {
	
	/**
	 * Convert a string to a GUID.
	 * @param guidString
	 *  String representation of a GUID, including { }.
	 * @return
	 *  A GUID.
	 */
	public static GUID getGUIDFromString(String guidString) {
		GUID.ByReference lpiid = new GUID.ByReference();
    	HRESULT hr = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
    	if (! hr.equals(W32Errors.S_OK)) {
    		throw new RuntimeException(hr.toString());
    	}
    	return lpiid;
	}
	
	/**
	 * Convert a GUID into a string.
	 * @param guid
	 *  GUID.
	 * @return
	 *  String representation of a GUID.
	 */
	public static String getStringFromGUID(GUID guid) {
		GUID.ByReference pguid = new GUID.ByReference(guid.getPointer());
    	int max = 39;
    	char[] lpsz = new char[max];
    	int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, max);
    	if (len == 0) {
    		throw new RuntimeException("StringFromGUID2");
    	}
    	lpsz[len - 1] = 0;    	
    	return Native.toString(lpsz);
	}
	
	/**
	 * Generate a new GUID.
	 * @return
	 *  New GUID.
	 */
	public static GUID generateGUID() {
		GUID.ByReference pguid = new GUID.ByReference();
    	HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
    	if (! hr.equals(W32Errors.S_OK)) {
    		throw new RuntimeException(hr.toString());
    	}
    	return pguid;
	}
}
