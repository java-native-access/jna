/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.examples.win32.util;

import com.sun.jna.examples.win32.Secur32.*;

import junit.framework.TestCase;

public class Advapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32UtilTest.class);
    }
    
	public void testGetUsername() {
		String username = Advapi32Util.GetUserName();
		System.out.println("Username: " + username);
		assertTrue(username.length() > 0);
	}
	
	public void testLookupAccount() throws Exception {
		String username = Secur32Util.GetUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
		System.out.println("Username: " + username);
		byte[] sid = Advapi32Util.LookupAccountName(null, username);
		System.out.println("Sid bytes: " + sid.length);
		String sidString = Advapi32Util.ConvertSidToStringSid(sid);
		assertTrue(sidString.length() > 0);
		assertTrue(sidString.startsWith("S-"));
	}    
}
