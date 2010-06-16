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

import java.util.TreeMap;

import junit.framework.TestCase;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Advapi32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32UtilTest.class);
        String currentUserName = Advapi32Util.getUserName();
        System.out.println("GetUserName: " + currentUserName);
		
        for(Account group : Advapi32Util.getCurrentUserGroups()) {
			System.out.println(" " + group.fqn + " [" + group.sidString + "]");
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
		Account[] groups = Advapi32Util.getCurrentUserGroups();
		assertTrue(groups.length > 0);
		for(Account group : groups) {
			assertTrue(group.name.length() > 0);
			assertTrue(group.sidString.length() > 0);
			assertTrue(group.sid.length > 0);
		}
	}
	
	public void testGetUserGroups() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNANetapi32TestUser");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
		try {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(
	    			null, 1, userInfo, null));
			HANDLEByReference phUser = new HANDLEByReference();
			try {
				assertTrue(Advapi32.INSTANCE.LogonUser(userInfo.usri1_name.toString(),
						null, userInfo.usri1_password.toString(), WinBase.LOGON32_LOGON_NETWORK, 
						WinBase.LOGON32_PROVIDER_DEFAULT, phUser));
				Account[] groups = Advapi32Util.getTokenGroups(phUser.getValue());
				assertTrue(groups.length > 0);
				for(Account group : groups) {
					assertTrue(group.name.length() > 0);
					assertTrue(group.sidString.length() > 0);
					assertTrue(group.sid.length > 0);
				}
			} finally {
				if (phUser.getValue() != WinBase.INVALID_HANDLE_VALUE) {
					Kernel32.INSTANCE.CloseHandle(phUser.getValue());
				}				
			}
		} finally {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
	    			null, userInfo.usri1_name.toString()));			
		}
	}
	
	public void testGetUserAccount() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNANetapi32TestUser");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
		try {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(
	    			null, 1, userInfo, null));
			HANDLEByReference phUser = new HANDLEByReference();
			try {
				assertTrue(Advapi32.INSTANCE.LogonUser(userInfo.usri1_name.toString(),
						null, userInfo.usri1_password.toString(), WinBase.LOGON32_LOGON_NETWORK, 
						WinBase.LOGON32_PROVIDER_DEFAULT, phUser));
				Advapi32Util.Account account = Advapi32Util.getTokenAccount(phUser.getValue());
				assertTrue(account.name.length() > 0);
				assertEquals(userInfo.usri1_name.toString(), account.name);
			} finally {
				if (phUser.getValue() != WinBase.INVALID_HANDLE_VALUE) {
					Kernel32.INSTANCE.CloseHandle(phUser.getValue());
				}
			}
		} finally {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
	    			null, userInfo.usri1_name.toString()));			
		}
	}	
	
	public void testRegistryKeyExists() {
		assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, 
				""));
		assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, 
				"Software\\Microsoft"));
		assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, 
				"KeyDoesNotExist\\SubKeyDoesNotExist"));
	}
	
	public void testRegistryValueExists() {
		assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, 
				"Software\\Microsoft", ""));
		assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, 
				"Software\\Microsoft", "KeyDoesNotExist"));
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, 
				"SYSTEM\\CurrentControlSet\\Control", "SystemBootDevice"));
	}	
	
	public void testRegistryCreateDeleteKey() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		assertFalse(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
	}

	public void testRegistryDeleteValue() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue", 42);
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
		Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue");		
		assertFalse(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
	}
	
	public void testRegistrySetGetIntValue() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue", 42);
		assertEquals(42, Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, 
				"Software\\JNA", "IntValue"));
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "IntValue"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
	}
	
	public void testRegistrySetGetStringValue() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue", "Hello World");
		assertEquals("Hello World", Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, 
				"Software\\JNA", "StringValue"));
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");		
	}

	public void testRegistryGetKeys() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
		String[] subKeys = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, "Software\\JNA");
		assertEquals(2, subKeys.length);
		assertEquals(subKeys[0], "Key1");
		assertEquals(subKeys[1], "Key2");
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key1");
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "Key2");
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");				
	}
	
	public void testRegistryGetValues() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "FourtyTwo", 42);
		Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "42", "FourtyTwo");
		TreeMap<String, Object> values = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, "Software\\JNA");
		assertEquals(2, values.keySet().size());
		assertEquals("FourtyTwo", values.get("42"));
		assertEquals(42, values.get("FourtyTwo"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");						
	}
	
	public void testIsWellKnownSid() {		
		String everyoneString = "S-1-1-0";
        assertTrue(Advapi32Util.isWellKnownSid(everyoneString, WELL_KNOWN_SID_TYPE.WinWorldSid));		
        assertFalse(Advapi32Util.isWellKnownSid(everyoneString, WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
        byte[] everyoneBytes = Advapi32Util.convertStringSidToSid(everyoneString);
        assertTrue(Advapi32Util.isWellKnownSid(everyoneBytes, WELL_KNOWN_SID_TYPE.WinWorldSid));		
        assertFalse(Advapi32Util.isWellKnownSid(everyoneBytes, WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
	}
}
