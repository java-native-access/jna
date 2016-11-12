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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinNT.HRESULT;

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
		GUID lpiid = new GUID();
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
		GUID pguid = new GUID(guid.getPointer());
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
		GUID pguid = new GUID();
    	HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
    	if (! hr.equals(W32Errors.S_OK)) {
    		throw new RuntimeException(hr.toString());
    	}
    	return pguid;
	}
}
