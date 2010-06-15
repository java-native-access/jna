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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Advapi32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Advapi32Test.class);
    }
    
    public void testGetUserName() {    	
		IntByReference len = new IntByReference();
		assertFalse(Advapi32.INSTANCE.GetUserNameW(null, len));
		assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
		char[] buffer = new char[len.getValue()];
		assertTrue(Advapi32.INSTANCE.GetUserNameW(buffer, len));
		String username = Native.toString(buffer);
		assertTrue(username.length() > 0);
    }
    
    public void testLookupAccountName() {
		IntByReference pSid = new IntByReference(0);
		IntByReference pDomain = new IntByReference(0);
		PointerByReference peUse = new PointerByReference();
		String accountName = "Administrator";
		assertFalse(Advapi32.INSTANCE.LookupAccountName(
				null, accountName, null, pSid, null, pDomain, peUse));
		assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
		assertTrue(pSid.getValue() > 0);
		Memory sidMemory = new Memory(pSid.getValue());
		PSID pSidMemory = new PSID(sidMemory);
		char[] referencedDomainName = new char[pDomain.getValue() + 1]; 
		assertTrue(Advapi32.INSTANCE.LookupAccountName(
				null, accountName, pSidMemory, pSid, referencedDomainName, pDomain, peUse));
		assertEquals(SID_NAME_USE.SidTypeUser, peUse.getPointer().getInt(0));
		assertTrue(Native.toString(referencedDomainName).length() > 0);
    }
    
    public void testIsValidSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	assertTrue(Advapi32.INSTANCE.IsValidSid(sid.getValue()));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid.getValue());
    	assertTrue(sidLength > 0);
    	assertTrue(Advapi32.INSTANCE.IsValidSid(sid.getValue()));
    }

    public void testGetSidLength() {
    	String sidString = "S-1-1-0"; // Everyone
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	assertTrue(12 == Advapi32.INSTANCE.GetLengthSid(sid.getValue()));
    }
    
    public void testLookupAccountSid() {
    	// get SID bytes
    	String sidString = "S-1-1-0"; // Everyone
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid.getValue());
    	assertTrue(sidLength > 0);
    	// lookup account
    	IntByReference cchName = new IntByReference();
    	IntByReference cchReferencedDomainName = new IntByReference();
    	PointerByReference peUse = new PointerByReference();
    	assertFalse(Advapi32.INSTANCE.LookupAccountSid(null, sid.getValue(), 
    			null, cchName, null, cchReferencedDomainName, peUse));
		assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
    	assertTrue(cchName.getValue() > 0);
    	assertTrue(cchReferencedDomainName.getValue() > 0);
		char[] referencedDomainName = new char[cchReferencedDomainName.getValue()];
		char[] name = new char[cchName.getValue()];
    	assertTrue(Advapi32.INSTANCE.LookupAccountSid(null, sid.getValue(), 
    			name, cchName, referencedDomainName, cchReferencedDomainName, peUse));
		assertEquals(5, peUse.getPointer().getInt(0)); // SidTypeWellKnownGroup
		String nameString = Native.toString(name);
		String referencedDomainNameString = Native.toString(referencedDomainName);
		assertTrue(nameString.length() > 0);
		assertEquals("Everyone", nameString);
		assertTrue(referencedDomainNameString.length() == 0);
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(sid.getValue().getPointer()));
    }
    
    public void testConvertSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(
    			sidString, sid));
    	PointerByReference convertedSidStringPtr = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertSidToStringSid(
    			sid.getValue(), convertedSidStringPtr));
    	String convertedSidString = convertedSidStringPtr.getValue().getString(0, true);
    	assertEquals(convertedSidString, sidString);
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(convertedSidStringPtr.getValue()));
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(sid.getValue().getPointer()));
    }
    
    public void testLogonUser() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	assertFalse(Advapi32.INSTANCE.LogonUser("AccountDoesntExist", ".", "passwordIsInvalid", 
    			WinBase.LOGON32_LOGON_NETWORK, WinBase.LOGON32_PROVIDER_DEFAULT, phToken));
    	assertTrue(W32Errors.ERROR_SUCCESS != Kernel32.INSTANCE.GetLastError());
    }
    
    public void testOpenThreadTokenNoToken() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
    	assertNotNull(threadHandle);
    	assertFalse(Advapi32.INSTANCE.OpenThreadToken(threadHandle, 
    			WinNT.TOKEN_READ, false, phToken));
    	assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
    }
    
    public void testOpenProcessToken() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
    	assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
    			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));    	
    }
    
    public void testOpenThreadOrProcessToken() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
    	if (! Advapi32.INSTANCE.OpenThreadToken(threadHandle, 
    			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, true, phToken)) {
        	assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
        	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        	assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
        			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
    	}
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));
    }
    
    public void testDuplicateToken() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLEByReference phTokenDup = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
        		WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
        assertTrue(Advapi32.INSTANCE.DuplicateToken(phToken.getValue(), 
        		WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation, phTokenDup));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phTokenDup.getValue()));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));
    }
    
    public void testGetTokenOwnerInformation() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
        		WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
        IntByReference tokenInformationLength = new IntByReference();
        assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenOwner, null, 0, tokenInformationLength));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
        Memory tokenInformationBuffer = new Memory(tokenInformationLength.getValue());
		WinNT.TOKEN_OWNER owner = new WinNT.TOKEN_OWNER(tokenInformationBuffer);
        assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenOwner, owner, 
        		tokenInformationLength.getValue(), tokenInformationLength));
        assertTrue(tokenInformationLength.getValue() > 0);
        assertTrue(Advapi32.INSTANCE.IsValidSid(owner.Owner));
        int sidLength = Advapi32.INSTANCE.GetLengthSid(owner.Owner);
        assertTrue(sidLength < tokenInformationLength.getValue());
        assertTrue(sidLength > 0);
    	// System.out.println(Advapi32Util.convertSidToStringSid(owner.Owner));
        assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));    	
    }
    
    public void testGetTokenUserInformation() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
        		WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
        IntByReference tokenInformationLength = new IntByReference();
        assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenUser, null, 0, tokenInformationLength));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
		WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
        assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenUser, user, 
        		tokenInformationLength.getValue(), tokenInformationLength));
        assertTrue(tokenInformationLength.getValue() > 0);
        assertTrue(Advapi32.INSTANCE.IsValidSid(user.User.Sid));
        int sidLength = Advapi32.INSTANCE.GetLengthSid(user.User.Sid);
        assertTrue(sidLength > 0);
        assertTrue(sidLength < tokenInformationLength.getValue());
    	// System.out.println(Advapi32Util.convertSidToStringSid(user.User.Sid));
        assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));
    }    
    
    public void testGetTokenGroupsInformation() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
        assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle, 
        		WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken));
        IntByReference tokenInformationLength = new IntByReference();
        assertFalse(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, null, 0, tokenInformationLength));
        assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
		WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
        assertTrue(Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(), 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, groups, 
        		tokenInformationLength.getValue(), tokenInformationLength));
        assertTrue(tokenInformationLength.getValue() > 0);
        assertTrue(groups.GroupCount > 0);
    	for (SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
    		assertTrue(Advapi32.INSTANCE.IsValidSid(sidAndAttribute.Sid));
    		// System.out.println(Advapi32Util.convertSidToStringSid(sidAndAttribute.Sid));
    	}
        assertTrue(Kernel32.INSTANCE.CloseHandle(phToken.getValue()));
    }
    
    public void testImpersonateLoggedOnUser() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNAAdvapi32TestImp");
    	userInfo.usri1_password = new WString("!JNAP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
		try {
			HANDLEByReference phUser = new HANDLEByReference();
			try {
				assertTrue(Advapi32.INSTANCE.LogonUser(userInfo.usri1_name.toString(),
						null, userInfo.usri1_password.toString(), WinBase.LOGON32_LOGON_NETWORK, 
						WinBase.LOGON32_PROVIDER_DEFAULT, phUser));
				assertTrue(Advapi32.INSTANCE.ImpersonateLoggedOnUser(phUser.getValue()));
				assertTrue(Advapi32.INSTANCE.RevertToSelf());
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
    
    public void testRegOpenKeyEx() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft", 0, WinNT.KEY_READ, phKey));
    	assertTrue(WinBase.INVALID_HANDLE_VALUE != phKey.getValue());
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	
    }
    
    public void testRegQueryValueEx() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", 0, WinNT.KEY_READ, phKey));
    	IntByReference lpcbData = new IntByReference();
    	IntByReference lpType = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phKey.getValue(), "User Agent", 0, lpType, (char[]) null, lpcbData));
    	assertEquals(WinNT.REG_SZ, lpType.getValue());
    	assertTrue(lpcbData.getValue() > 0);
    	char[] buffer = new char[lpcbData.getValue()];
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phKey.getValue(), "User Agent", 0, lpType, buffer, lpcbData)); 
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	
    }
    
    public void testRegDeleteValue() {
    	assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Advapi32.INSTANCE.RegDeleteValue(
    			WinReg.HKEY_CURRENT_USER, "JNAAdvapi32TestDoesntExist"));
    }
    
    public void testRegSetValueEx_REG_SZ() {
    	HKEYByReference phKey = new HKEYByReference();
    	// create parent key
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
    	HKEYByReference phkTest = new HKEYByReference();
    	IntByReference lpdwDisposition = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
    			phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS, 
    			null, phkTest, lpdwDisposition));
    	// write a REG_SZ value
    	char[] lpData = Native.toCharArray("Test");
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegSetValueEx(
    			phkTest.getValue(), "REG_SZ", 0, WinNT.REG_SZ, lpData, lpData.length * 2));
    	// re-read the REG_SZ value
    	IntByReference lpType = new IntByReference();
    	IntByReference lpcbData = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phkTest.getValue(), "REG_SZ", 0, lpType, (char[]) null, lpcbData));
    	assertEquals(WinNT.REG_SZ, lpType.getValue());
    	assertTrue(lpcbData.getValue() > 0);
    	char[] buffer = new char[lpcbData.getValue()];
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phkTest.getValue(), "REG_SZ", 0, lpType, buffer, lpcbData)); 
    	assertEquals("Test", Native.toString(buffer));
    	// delete the test key
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(
    			phkTest.getValue()));    	    	    	
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
    			phKey.getValue(), "JNAAdvapi32Test"));
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	    	    	
    }
    
    public void testRegSetValueEx_DWORD() {
    	HKEYByReference phKey = new HKEYByReference();
    	// create parent key
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
    	HKEYByReference phkTest = new HKEYByReference();
    	IntByReference lpdwDisposition = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
    			phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS, 
    			null, phkTest, lpdwDisposition));
    	// write a REG_DWORD value
    	int value = 42145;
        byte[] data = new byte[4];
        data[0] = (byte)(value & 0xff);
        data[1] = (byte)((value >> 8) & 0xff);
        data[2] = (byte)((value >> 16) & 0xff);
        data[3] = (byte)((value >> 24) & 0xff);
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegSetValueEx(
    			phkTest.getValue(), "DWORD", 0, WinNT.REG_DWORD, data, 4));    	
    	// re-read the REG_DWORD value
    	IntByReference lpType = new IntByReference();
    	IntByReference lpcbData = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phkTest.getValue(), "DWORD", 0, lpType, (char[]) null, lpcbData));
    	assertEquals(WinNT.REG_DWORD, lpType.getValue());
    	assertEquals(4, lpcbData.getValue());
    	IntByReference valueRead = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryValueEx(
    			phkTest.getValue(), "DWORD", 0, lpType, valueRead, lpcbData));
    	assertEquals(value, valueRead.getValue());
    	// delete the test key
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(
    			phkTest.getValue()));    	    	    	
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
    			phKey.getValue(), "JNAAdvapi32Test"));
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	    	    	
    }
    
    public void testRegCreateKeyEx() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software", 0, WinNT.KEY_WRITE | WinNT.KEY_READ, phKey));
    	HKEYByReference phkResult = new HKEYByReference();
    	IntByReference lpdwDisposition = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCreateKeyEx(
    			phKey.getValue(), "JNAAdvapi32Test", 0, null, 0, WinNT.KEY_ALL_ACCESS, 
    			null, phkResult, lpdwDisposition));
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phkResult.getValue()));    	    	    	
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegDeleteKey(
    			phKey.getValue(), "JNAAdvapi32Test"));
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	    	
    }
    
    public void testRegDeleteKey() {
    	assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Advapi32.INSTANCE.RegDeleteKey(
    			WinReg.HKEY_CURRENT_USER, "JNAAdvapi32TestDoesntExist"));
    }
    
    public void testRegEnumKeyEx() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", 
    			0, WinNT.KEY_READ, phKey));
    	IntByReference lpcSubKeys = new IntByReference();
    	IntByReference lpcMaxSubKeyLen = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
    			phKey.getValue(), null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null, 
    			null, null, null, null));
    	char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
    	for (int i = 0; i < lpcSubKeys.getValue(); i++) {
    		IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
        	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegEnumKeyEx(
        			phKey.getValue(), i, name, lpcchValueName, null, null, null, null));
        	assertEquals(Native.toString(name).length(), lpcchValueName.getValue());
    	}
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	    	
    }
    
    public void testRegEnumValue() {
    	HKEYByReference phKey = new HKEYByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegOpenKeyEx(
    			WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", 
    			0, WinNT.KEY_READ, phKey));
    	IntByReference lpcValues = new IntByReference();
    	IntByReference lpcMaxValueNameLen = new IntByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
    			phKey.getValue(), null, null, null, null, null, null, lpcValues, 
    			lpcMaxValueNameLen, null, null, null));
    	char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
    	for (int i = 0; i < lpcValues.getValue(); i++) {
    		IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
    		IntByReference lpType = new IntByReference(); 
        	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegEnumValue(
        			phKey.getValue(), i, name, lpcchValueName, null, 
        			lpType, null, null));
        	assertEquals(Native.toString(name).length(), lpcchValueName.getValue());
    	}
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegCloseKey(phKey.getValue()));    	
    }
    
    public void testRegQueryInfoKey() {
    	IntByReference lpcClass = new IntByReference();
    	IntByReference lpcSubKeys = new IntByReference();
    	IntByReference lpcMaxSubKeyLen = new IntByReference();
    	IntByReference lpcValues = new IntByReference();
    	IntByReference lpcMaxClassLen = new IntByReference();
    	IntByReference lpcMaxValueNameLen = new IntByReference();
    	IntByReference lpcMaxValueLen = new IntByReference();
    	IntByReference lpcbSecurityDescriptor = new IntByReference();
    	FILETIME lpftLastWriteTime = new FILETIME();
    	assertEquals(W32Errors.ERROR_SUCCESS, Advapi32.INSTANCE.RegQueryInfoKey(
    			WinReg.HKEY_LOCAL_MACHINE, null, lpcClass, null, 
    			lpcSubKeys, lpcMaxSubKeyLen, lpcMaxClassLen, lpcValues, 
    			lpcMaxValueNameLen, lpcMaxValueLen, lpcbSecurityDescriptor, 
    			lpftLastWriteTime));
    	assertTrue(lpcSubKeys.getValue() > 0);
    }
    
    public void testIsWellKnownSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	assertTrue(Advapi32.INSTANCE.IsWellKnownSid(sid.getValue(), 
    			WELL_KNOWN_SID_TYPE.WinWorldSid));
    	assertFalse(Advapi32.INSTANCE.IsWellKnownSid(sid.getValue(), 
    			WELL_KNOWN_SID_TYPE.WinAccountAdministratorSid));
    }
    
    public void testCreateWellKnownSid() {
    	PSID pSid = new PSID(WinNT.SECURITY_MAX_SID_SIZE);
    	IntByReference cbSid = new IntByReference(WinNT.SECURITY_MAX_SID_SIZE);
    	assertTrue(Advapi32.INSTANCE.CreateWellKnownSid(WELL_KNOWN_SID_TYPE.WinWorldSid,
    			null, pSid, cbSid));
    	assertTrue(Advapi32.INSTANCE.IsWellKnownSid(pSid, 
    			WELL_KNOWN_SID_TYPE.WinWorldSid));
    	assertTrue(cbSid.getValue() <= WinNT.SECURITY_MAX_SID_SIZE);
    	PointerByReference convertedSidStringPtr = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertSidToStringSid(
    			pSid, convertedSidStringPtr));
    	String convertedSidString = convertedSidStringPtr.getValue().getString(0, true);
    	assertEquals("S-1-1-0", convertedSidString);
    }
}
