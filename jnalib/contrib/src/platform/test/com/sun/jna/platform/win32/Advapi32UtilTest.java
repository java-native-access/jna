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
package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;

public class Advapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32UtilTest.class);
        String currentUserName = Advapi32Util.getUserName();
        System.out.println("GetUserName: " + currentUserName);
		
        for(Advapi32Util.Group group : Advapi32Util.getCurrentUserGroups()) {
			System.out.println(" " + group.name + " [" + group.sidString + "]");
		}
		
		Account accountByName = Advapi32Util.getAccountByName(currentUserName);
		System.out.println("AccountByName: " + currentUserName);
        System.out.println(" Fqn: " + accountByName.fqn);
        System.out.println(" Domain: " + accountByName.domain);
        System.out.println(" Sid: " + accountByName.sidString);
        
        Account accountBySid = Advapi32Util.getAccountBySid(new PSID(accountByName.sid));
		System.out.println("AccountBySid: " + accountByName.sidString);
        System.out.println(" Fqn: " + accountBySid.fqn);
        System.out.println(" Name: " + accountBySid.name);
        System.out.println(" Domain: " + accountBySid.domain);
    }
    
	public void testGetUsername() {
		String username = Advapi32Util.getUserName();
		assertTrue(username.length() > 0);
	}
	
	public void testGetAccountBySid() {		
		String accountName = Advapi32Util.getUserName();
		Account currentUser = Advapi32Util.getAccountByName(accountName);
		Account account = Advapi32Util.getAccountBySid(new PSID(currentUser.sid));
		assertEquals(SID_NAME_USE.SidTypeUser, account.accountType);
		assertEquals(currentUser.fqn.toLowerCase(), account.fqn.toLowerCase());
		assertEquals(currentUser.name.toLowerCase(), account.name.toLowerCase());
		assertEquals(currentUser.domain.toLowerCase(), account.domain.toLowerCase());
		assertEquals(currentUser.sidString, account.sidString);		
	}

	public void testGetAccountByName() {		
		String accountName = Advapi32Util.getUserName();
		Account account = Advapi32Util.getAccountByName(accountName);
		assertEquals(SID_NAME_USE.SidTypeUser, account.accountType);
	}
	
	public void testGetAccountNameFromSid() {
        assertEquals("Everyone", Advapi32Util.getAccountBySid("S-1-1-0").name);		
	}

	public void testGetAccountSidFromName() {
        assertEquals("S-1-1-0", Advapi32Util.getAccountByName("Everyone").sidString);
	}
	
	public void testConvertSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	byte[] sidBytes = Advapi32Util.convertStringSidToSid(sidString);
    	assertTrue(sidBytes.length > 0);
    	String convertedSidString = Advapi32Util.convertSidToStringSid(new PSID(sidBytes));
    	assertEquals(convertedSidString, sidString);
	}
	
	public void testGetCurrentUserGroups() {
		Advapi32Util.Group[] groups = Advapi32Util.getCurrentUserGroups();
		assertTrue(groups.length > 0);
		for(Advapi32Util.Group group : groups) {
			assertTrue(group.name.length() > 0);
			assertTrue(group.sidString.length() > 0);
			assertTrue(group.sid.length > 0);
		}
	}
}
