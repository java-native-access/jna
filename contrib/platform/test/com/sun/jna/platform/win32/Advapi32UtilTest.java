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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinNT.ACCESS_ACEStructure;
import com.sun.jna.platform.win32.WinNT.ACCESS_ALLOWED_ACE;
import com.sun.jna.platform.win32.WinNT.ACCESS_DENIED_ACE;
import com.sun.jna.platform.win32.WinNT.ACEStructure;
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
                    assertEquals("Error in NetUserAdd",
                                 LMErr.NERR_Success,
                                 Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
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
                    assertEquals("Error in NetUserDel",
                                 LMErr.NERR_Success,
                                 Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name.toString()));			
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
	
	public void testRegistryCreateKeyDisposition() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		assertTrue(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA"));
		assertFalse(Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA"));
		assertTrue(Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
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
	
	public void testRegistrySetGetLongValue() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "LongValue", 1234L);
		assertEquals(1234L, Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER, 
				"Software\\JNA", "LongValue"));
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "LongValue"));
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

	public void testRegistrySetGetExpandableStringValue() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue", "Temp is %TEMP%");
		assertEquals("Temp is %TEMP%", Advapi32Util.registryGetExpandableStringValue(WinReg.HKEY_CURRENT_USER, 
				"Software\\JNA", "StringValue"));
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringValue"));
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");		
	}
	
	public void testRegistrySetGetStringArray() {
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		String[] dataWritten = { "Hello", "World" };
		Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue", dataWritten);
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue"));
		String[] dataRead = Advapi32Util.registryGetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "MultiStringValue");
		assertEquals(dataWritten.length, dataRead.length);
		for(int i = 0; i < dataRead.length; i++) {
			assertEquals(dataWritten[i], dataRead[i]);
		}
		Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");		
	}
	
	public void testRegistrySetGetBinaryValue() {
		byte[] data = { 0x00, 0x01, 0x02 };
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue", data);
		byte[] read = Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue");
		assertEquals(data.length, read.length);		
		for(int i = 0; i < data.length; i++) {
			assertEquals(data[i], read[i]);
		}
		assertTrue(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "BinaryValue"));
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
		String uu = new String("A" + "\\u00ea" + "\\u00f1" + "\\u00fc" + "C");
		Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software", "JNA");
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "FourtyTwo" + uu, 42);
		Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "42" + uu, "FourtyTwo" + uu);
		Advapi32Util.registrySetExpandableStringValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "ExpandableString", "%TEMP%");
		byte[] dataWritten = { 0xD, 0xE, 0xA, 0xD, 0xB, 0xE, 0xE, 0xF };		
		Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "DeadBeef", dataWritten);
		String[] stringsWritten = { "Hello", "World", "Hello World", uu };
		Advapi32Util.registrySetStringArray(WinReg.HKEY_CURRENT_USER, "Software\\JNA", "StringArray", stringsWritten);		
		TreeMap<String, Object> values = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, "Software\\JNA");
		assertEquals(5, values.keySet().size());
		assertEquals("FourtyTwo" + uu, values.get("42" + uu));
		assertEquals(42, values.get("FourtyTwo" + uu));
		assertEquals("%TEMP%", values.get("ExpandableString"));
		byte[] dataRead = (byte[]) values.get("DeadBeef");
		assertEquals(dataWritten.length, dataRead.length);
		for(int i = 0; i < dataWritten.length; i++) {
			assertEquals(dataWritten[i], dataRead[i]);
		}
		String[] stringsRead = (String[]) values.get("StringArray");
		assertEquals(stringsWritten.length, stringsRead.length);
		for(int i = 0; i < stringsWritten.length; i++) {
			assertEquals(stringsWritten[i], stringsRead[i]);
		}
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
	
	public void testEventLogIteratorForwards() {
		EventLogIterator iter = new EventLogIterator("Application");
		try {
			int max = 100;
			int lastId = 0;
			while(iter.hasNext()) {
				EventLogRecord record = iter.next();
				assertTrue(record.getRecordNumber() > lastId);
				lastId = record.getRecordNumber();
				assertNotNull(record.getType().name());
				assertNotNull(record.getSource());
				if (record.getRecord().DataLength.intValue() > 0) {
					assertEquals(record.getData().length, 
							record.getRecord().DataLength.intValue());
				} else {
					assertNull(record.getData());
				}
				if (record.getRecord().NumStrings.intValue() > 0) {
					assertEquals(record.getStrings().length, 
							record.getRecord().NumStrings.intValue());
				} else {
					assertNull(record.getStrings());
				}
				
				if (max-- <= 0) {
					break; // shorten test
				}
				/*
				System.out.println(record.getRecordNumber()
						+ ": Event ID: " + record.getEventId()
						+ ", Event Type: " + record.getType()
						+ ", Event Source: " + record.getSource());
						*/
			}
		} finally {
			iter.close();
		}
	}
	
	public void testEventLogIteratorBackwards() {
		EventLogIterator iter = new EventLogIterator(null, 
				"Application", WinNT.EVENTLOG_BACKWARDS_READ);
		try {
			int max = 100;
			int lastId = -1;
			while(iter.hasNext()) {
				EventLogRecord record = iter.next();
				/*
				System.out.println(record.getRecordNumber()
						+ ": Event ID: " + record.getEventId()
						+ ", Event Type: " + record.getType()
						+ ", Event Source: " + record.getSource());
						*/
				assertTrue(record.getRecordNumber() < lastId || lastId == -1);
				lastId = record.getRecordNumber();
				if (max-- <= 0) {
					break; // shorten test
				}
			}
		} finally {
			iter.close();
		}
	}
	
	public void testGetEnvironmentBlock() {
		String expected = "KEY=value\0"
				+ "KEY_EMPTY=\0" 
				+ "KEY_NUMBER=2\0"
				+ "\0";

		// Order is important to kept checking result simple
		Map<String, String> mockEnvironment = new TreeMap<String, String>();
		mockEnvironment.put("KEY", "value");
		mockEnvironment.put("KEY_EMPTY", "");
		mockEnvironment.put("KEY_NUMBER", "2");
		mockEnvironment.put("KEY_NULL", null);		

		String block = Advapi32Util.getEnvironmentBlock(mockEnvironment);
		assertEquals("Environment block must comprise key=value pairs separated by NUL characters", expected, block);
	}
	
	public void testGetFileSecurity() {
            //final String userUnderTest = "iFinder";
            final String userUnderTest = Advapi32Util.getUserName();
		final Account userUnderTestAccount = Advapi32Util.getAccountByName(userUnderTest);
		final String userUnderTestSID = userUnderTestAccount.sidString; 
		//final String groupUnderTest = "Sales";
		final String groupUnderTest = "Everybody";
		final Account groupUnderTestAccount = Advapi32Util.getAccountByName(groupUnderTest);
		final String groupUnderTestSID = groupUnderTestAccount.sidString;

		//final String testRootDir = "C:\\Projekte\\JNA-fileAccessRights\\";
                final String testRootDir = System.getProperty("java.io.tmpdir");

		// User rights "modify" (Um) - includes some others: read, execute, write
		//   Traverse Folder/Execute File (WinNT.FILE_TRAVERSE, WinNT.FILE_READ_DATA)
		//   List Folder/Read Data (WinNT.FILE_LIST_DIRECTORY, WinNT.FILE_READ_DATA)
		//   Read Attributes (WinNT.FILE_READ_ATTRIBUTES)
		//   Read Extended Attributes (WinNT.FILE_READ_EA)
		//   Create Files/Write Data (WinNT.FILE_ADD_FILE, WinNT.FILE_WRITE_DATA)
		//   Create Folders/Append Data (WinNT.FILE_ADD_SUBDIRECTORY, WinNT.FILE_APPEND_DATA)
		//   Write Attributes (WinNT.FILE_WRITE_ATTRIBUTES)
		//   Write Extended Attributes (WinNT.FILE_WRITE_EA)
		//   Delete (WinNT.DELETE)
		//   Read Permissions (WinNT.FILE_READ_ATTRIBUTES)
		//   Synchronize (WinNT.SYNCHRONIZE)
		// Group rights allowed:"read" denied:"write" (Gr-w)
		//   List Folder/Read Data (WinNT.FILE_LIST_DIRECTORY, WinNT.FILE_READ_DATA)
		//   Read Attributes (WinNT.FILE_READ_ATTRIBUTES)
		//   Read Extended Attributes (WinNT.FILE_READ_EA)
		//   Read Permissions (WinNT.FILE_READ_ATTRIBUTES)
		ACCESS_ACEStructure[] fileSecurity_UmGr = Advapi32Util.getFileSecurity(testRootDir + "/UmGr-w.txt", false);
		System.out.println("UmGr-w.txt");
		checkFileSecurity(fileSecurity_UmGr, 
				userUnderTestSID, 
				new String[] {
				"0 0 DELETE READ_CONTROL SYNCHRONIZE FILE_READ_DATA FILE_LIST_DIRECTORY FILE_WRITE_DATA FILE_ADD_FILE FILE_APPEND_DATA FILE_ADD_SUBDIRECTORY FILE_CREATE_PIPE_INSTANCE FILE_READ_EA FILE_WRITE_EA FILE_EXECUTE FILE_TRAVERSE FILE_READ_ATTRIBUTES FILE_WRITE_ATTRIBUTES"
		},
		groupUnderTestSID,
		new String[] {
				"0 1 FILE_WRITE_DATA FILE_ADD_FILE FILE_APPEND_DATA FILE_ADD_SUBDIRECTORY FILE_CREATE_PIPE_INSTANCE FILE_WRITE_EA FILE_WRITE_ATTRIBUTES"
				, "0 0 READ_CONTROL SYNCHRONIZE FILE_READ_DATA FILE_LIST_DIRECTORY FILE_READ_EA FILE_READ_ATTRIBUTES"
		});

		// User full access
		ACCESS_ACEStructure[] fileSecurity_Uf = Advapi32Util.getFileSecurity(testRootDir + "/Uf.txt", false);
		//System.out.println("Uf.txt");
		checkFileSecurity(fileSecurity_Uf, userUnderTestSID, 
				new String[] {"0 0 DELETE READ_CONTROL WRITE_DAC WRITE_OWNER SYNCHRONIZE STANDARD_RIGHTS_REQUIRED STANDARD_RIGHTS_ALL FILE_READ_DATA FILE_LIST_DIRECTORY FILE_WRITE_DATA FILE_ADD_FILE FILE_APPEND_DATA FILE_ADD_SUBDIRECTORY FILE_CREATE_PIPE_INSTANCE FILE_READ_EA FILE_WRITE_EA FILE_EXECUTE FILE_TRAVERSE FILE_DELETE_CHILD FILE_READ_ATTRIBUTES FILE_WRITE_ATTRIBUTES"}, null, null);

		// file in directory - access rights copied
		ACCESS_ACEStructure[] fileSecurity_Grw_Cp = Advapi32Util.getFileSecurity(testRootDir + "/UrwGr.dir/Grw.txt", false);
		//System.out.println("UrwGr.dir/Grw.txt");
		checkFileSecurity(fileSecurity_Grw_Cp, null, null,
				groupUnderTestSID,
				new String[]{"16 0 READ_CONTROL SYNCHRONIZE FILE_READ_DATA FILE_LIST_DIRECTORY FILE_READ_EA FILE_READ_ATTRIBUTES"} 
				);

		/*
    // User rights: read, execute, list folder, write
    // Group rights: read
    ACCESS_ACEStructure[] fileSecurity_UrwGr_Dir = Advapi32Util.getFileSecurity(testRootDir + "/UrwGr.dir/", false);
    System.out.println("UrwGr.dir/");


    // file in directory - access rights inherited
    //  User rights: read, execute, write 
    ACCESS_ACEStructure[] fileSecurity_UrI_Inh = Advapi32Util.getFileSecurity(testRootDir + "/UrwGr.dir/UrI.txt", false);
    System.out.println("UrwGr.dir/UrI.txt");
		 */

	}

	private void checkFileSecurity(ACCESS_ACEStructure[] fileSecurity, String userUnderTestSID, String[] expectedUsrMasks, String groupUnderTestSID, String[] expectedGrpMasks) {
		int remainingUsr = userUnderTestSID == null ? 0 : expectedUsrMasks.length;
		int remainingGrp = groupUnderTestSID == null ? 0 : expectedGrpMasks.length;
		for (ACCESS_ACEStructure aces : fileSecurity) {
			if (userUnderTestSID != null && aces.getSidString().equals(userUnderTestSID)) {
				String start = (aces.AceFlags & WinNT.VALID_INHERIT_FLAGS) + " " + aces.AceType + " ";
				for (String expected : expectedUsrMasks) {
					if (expected.startsWith(start)) {
						String gotMask = start + getFileSecurityAsString(aces.Mask);
						System.out.println(expected);
						remainingUsr--;
						assertEquals(expected, gotMask);
					}
				}
			}
			if (groupUnderTestSID != null && aces.getSidString().equals(groupUnderTestSID)) {
				String start = (aces.AceFlags & WinNT.VALID_INHERIT_FLAGS) + " " + aces.AceType + " ";
				for (String expected : expectedGrpMasks) {
					if (expected.startsWith(start)) {
						String gotMask = start + getFileSecurityAsString(aces.Mask);
						System.out.println(expected);
						remainingGrp--;
						assertEquals(expected, gotMask);
					}
				}
			}
		}
		assertEquals("Not all ACLs found for user ", 0, remainingUsr);
		assertEquals("Not all ACLs found for group ", 0, remainingGrp);
	}

	private String getFileSecurityAsString(int accessMask) {
		final String[] FLAGS = {"DELETE", "READ_CONTROL", "WRITE_DAC", "WRITE_OWNER", "SYNCHRONIZE", 
				"STANDARD_RIGHTS_REQUIRED", "STANDARD_RIGHTS_ALL", "SPECIFIC_RIGHTS_ALL",
				"GENERIC_ALL", "GENERIC_EXECUTE", "GENERIC_WRITE", "GENERIC_READ",
				"FILE_READ_DATA", "FILE_LIST_DIRECTORY", "FILE_WRITE_DATA", "FILE_ADD_FILE",
				"FILE_APPEND_DATA", "FILE_ADD_SUBDIRECTORY", "FILE_CREATE_PIPE_INSTANCE",
				"FILE_READ_EA", "FILE_WRITE_EA", "FILE_EXECUTE", "FILE_TRAVERSE",
				"FILE_DELETE_CHILD", "FILE_READ_ATTRIBUTES", "FILE_WRITE_ATTRIBUTES"};

		String asString = "";

		for (String flag : FLAGS) {
			try {
				Field field = WinNT.class.getDeclaredField(flag);
				int int1 = field.getInt(null);
				if ((int1 & accessMask) == int1) {
					asString += flag + " "; 
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			}
		}
		return asString.trim();
	}
}

