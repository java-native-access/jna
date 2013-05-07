/* Copyright (c) Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Native;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Crypt32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Crypt32UtilTest.class);
    }
    
    public void testCryptProtectUnprotectData() {
    	byte[] data = new byte[2];
    	data[0] = 42;
    	data[1] = 12;
    	byte[] protectedData = Crypt32Util.cryptProtectData(data);
    	byte[] unprotectedData = Crypt32Util.cryptUnprotectData(protectedData);
    	assertEquals(data.length, unprotectedData.length);
    	assertEquals(data[0], unprotectedData[0]);
    	assertEquals(data[1], unprotectedData[1]);
    }
    
    public void testCryptProtectUnprotectMachineKey() {
    	String s = "Hello World";
    	byte[] data = Native.toByteArray(s);
    	byte[] protectedData = Crypt32Util.cryptProtectData(data, 
    			WinCrypt.CRYPTPROTECT_LOCAL_MACHINE | WinCrypt.CRYPTPROTECT_UI_FORBIDDEN);
    	byte[] unprotectedData = Crypt32Util.cryptUnprotectData(protectedData, 
    			WinCrypt.CRYPTPROTECT_LOCAL_MACHINE);
    	String unprotectedString = Native.toString(unprotectedData);
    	assertEquals(s, unprotectedString);
    }
}