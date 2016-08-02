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

import com.sun.jna.platform.win32.WinReg.HKEYByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtDllUtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NtDllUtilTest.class);
    }

    public void testGetKeyName() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
        // Keys are case insensitive (https://msdn.microsoft.com/de-de/library/windows/desktop/ms724946(v=vs.85).aspx)
    	assertEquals("software", NtDllUtil.getKeyName(phKey.getValue()).toLowerCase());
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));
    }
}
