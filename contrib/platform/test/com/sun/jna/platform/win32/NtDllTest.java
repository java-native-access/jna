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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Wdm.KEY_BASIC_INFORMATION;
import com.sun.jna.platform.win32.Wdm.KEY_INFORMATION_CLASS;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtDllTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NtDllTest.class);
    }
    
    public void testZwQueryKey() {
    	// open a key
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
    	// query key info
    	IntByReference resultLength = new IntByReference();    	
    	assertEquals(NTStatus.STATUS_BUFFER_TOO_SMALL, NtDll.INSTANCE.ZwQueryKey(
    			phKey.getValue(), KEY_INFORMATION_CLASS.KeyBasicInformation, 
    			null, 0, resultLength));
    	assertTrue(resultLength.getValue() > 0);
    	KEY_BASIC_INFORMATION keyInformation = new KEY_BASIC_INFORMATION(resultLength.getValue());
    	assertEquals(NTStatus.STATUS_SUCCESS, NtDll.INSTANCE.ZwQueryKey(
    			phKey.getValue(), Wdm.KEY_INFORMATION_CLASS.KeyBasicInformation, 
    			keyInformation, resultLength.getValue(), resultLength));    	
    	// show
    	assertEquals("Software", keyInformation.getName());
    	// close key
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	    	    	    	
    }
}
