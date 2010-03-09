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
package com.sun.jna.contrib.platformutil.win32;

import junit.framework.TestCase;

public class Advapi32UtilTest extends TestCase {

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(Advapi32UtilTest.class);
        System.out.println("GetUserName: " + Advapi32Util.getUserName());
        System.out.println("ConvertSidToStringSid: " + Advapi32Util.convertSidToStringSid(Advapi32Util.getAccountSid(Advapi32Util.getUserName())));
        System.out.println("AccountSidString: " + Advapi32Util.getAccountSidString(Advapi32Util.getUserName()));
        System.out.println("AccountNameBySid: " + Advapi32Util.getAccountName(Advapi32Util.getAccountSid(Advapi32Util.getUserName())));
        System.out.println("AccountNameBySidString: " + Advapi32Util.getAccountName(Advapi32Util.getAccountSidString(Advapi32Util.getUserName())));
    }
    
	public void testGetUsername() {
		String username = Advapi32Util.getUserName();
		assertTrue(username.length() > 0);
	}
	
	public void testGetAccountNameAndSid() throws Exception {
		String accountName = Kernel32Util.getComputerName() + "\\Administrator";
		byte[] sidBytes = Advapi32Util.getAccountSid(accountName);
		assertTrue(sidBytes.length > 0);
		assertEquals(accountName.toLowerCase(), Advapi32Util.getAccountName(sidBytes).toLowerCase());
	}
	
	public void testGetAccountNameFromSid() throws Exception {
        assertEquals("Everyone", Advapi32Util.getAccountName("S-1-1-0"));		
	}

	public void testGetAccountSidFromName() throws Exception {
        assertEquals("S-1-1-0", Advapi32Util.getAccountSidString("Everyone"));		
	}
	
	public void testConvertSid() throws Exception {
    	String sidString = "S-1-1-0"; // Everyone
    	byte[] sidBytes = Advapi32Util.convertStringSidToSid(sidString);
    	assertTrue(sidBytes.length > 0);
    	String convertedSidString = Advapi32Util.convertSidToStringSid(sidBytes);
    	assertEquals(convertedSidString, sidString);
	}
}
