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

import java.io.File;

import junit.framework.TestCase;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.LMAccess.USER_INFO_1;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.EVENTLOGRECORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SECURITY_IMPERSONATION_LEVEL;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;
import com.sun.jna.platform.win32.WinNT.TOKEN_TYPE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc.SC_STATUS_TYPE;
import com.sun.jna.platform.win32.Winsvc.SERVICE_STATUS_PROCESS;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Advapi32Test extends TestCase {

    private static final String EVERYONE = "S-1-1-0";
    private static final String NOBODY = "S-1-0-0";

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
    	String sidString = EVERYONE;
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue("SID conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	assertTrue("Converted SID not valid: " + sid.getValue(), Advapi32.INSTANCE.IsValidSid(sid.getValue()));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid.getValue());
    	assertTrue(sidLength > 0);
    	assertTrue(Advapi32.INSTANCE.IsValidSid(sid.getValue()));
    }

    public void testGetSidLength() {
    	String sidString = EVERYONE;
    	PSIDByReference sid = new PSIDByReference();
    	assertTrue("SID conversion failed", Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	assertEquals("Wrong SID lenght", 12, Advapi32.INSTANCE.GetLengthSid(sid.getValue()));
    }
    
    public void testLookupAccountSid() {
    	// get SID bytes
    	String sidString = EVERYONE;
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
    	String sidString = EVERYONE;
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
    
    public void testDuplicateTokenEx() {
    	HANDLEByReference hExistingToken = new HANDLEByReference();
    	HANDLEByReference phNewToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
    	assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
    			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, hExistingToken));
    	assertTrue(Advapi32.INSTANCE.DuplicateTokenEx(hExistingToken.getValue(),
    			WinNT.GENERIC_READ, null, SECURITY_IMPERSONATION_LEVEL.SecurityAnonymous,
    			TOKEN_TYPE.TokenPrimary, phNewToken));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(phNewToken.getValue()));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hExistingToken.getValue()));
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
		WinNT.TOKEN_OWNER owner = new WinNT.TOKEN_OWNER(tokenInformationLength.getValue());
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
    	String sidString = EVERYONE;
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
    	assertEquals(EVERYONE, convertedSidString);
    }
    
    public void testOpenEventLog() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	assertNotNull(h);
    	assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }
    
    public void testRegisterEventSource() {
    	// the Security event log is reserved
    	HANDLE h = Advapi32.INSTANCE.RegisterEventSource(null, "Security");
    	assertNull(h);
    	assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());
    }

    public void testReportEvent() {
    	String applicationEventLog = "SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application";
    	String jnaEventSource = "JNADevEventSource";    	
    	String jnaEventSourceRegistryPath = applicationEventLog + "\\" + jnaEventSource;
    	Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, jnaEventSourceRegistryPath);    	
    	HANDLE h = Advapi32.INSTANCE.RegisterEventSource(null, jnaEventSource);    	
    	IntByReference before = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, before));
    	assertNotNull(h);
    	String s[] = { "JNA", "Event" };
    	Memory m = new Memory(4);
    	m.setByte(0, (byte) 1);
    	m.setByte(1, (byte) 2);
    	m.setByte(2, (byte) 3);
    	m.setByte(3, (byte) 4);
    	assertTrue(Advapi32.INSTANCE.ReportEvent(h, WinNT.EVENTLOG_ERROR_TYPE, 0, 0, null, 2, 4, s, m));
    	IntByReference after = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, after));
    	assertTrue(before.getValue() < after.getValue());
    	assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
    	assertTrue(Advapi32.INSTANCE.DeregisterEventSource(h));
    	Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, jnaEventSourceRegistryPath);
    }
    
    public void testGetNumberOfEventLogRecords() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
    	IntByReference n = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, n));
    	assertTrue(n.getValue() >= 0);
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }
    
    /*
    public void testClearEventLog() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	assertFalse(h.equals(WinBase.INVALID_HANDLE_VALUE));
    	IntByReference before = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, before));
    	assertTrue(before.getValue() >= 0);
    	assertTrue(Advapi32.INSTANCE.ClearEventLog(h, null));
    	IntByReference after = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(h, after));
    	assertTrue(after.getValue() < before.getValue() || before.getValue() == 0);
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));   	
    }
    */
    
    public void testBackupEventLog() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	assertNotNull(h);
    	String backupFileName = Kernel32Util.getTempPath() + "\\JNADevEventLog.bak";    	
    	File f = new File(backupFileName);
    	if (f.exists()) {
    		f.delete();
    	}
		
    	assertTrue(Advapi32.INSTANCE.BackupEventLog(h, backupFileName));
    	HANDLE hBackup = Advapi32.INSTANCE.OpenBackupEventLog(null, backupFileName);
    	assertNotNull(hBackup);
    	
    	IntByReference n = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetNumberOfEventLogRecords(hBackup, n));
    	assertTrue(n.getValue() >= 0);
    	
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(hBackup));
    }
    
    public void testReadEventLog() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	IntByReference pnBytesRead = new IntByReference();
    	IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
    	Memory buffer = new Memory(1);
    	assertFalse(Advapi32.INSTANCE.ReadEventLog(h, 
    			WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ, 
    			0, buffer, (int) buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded));
    	assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
    	assertTrue(pnMinNumberOfBytesNeeded.getValue() > 0);
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }
    
    public void testReadEventLogEntries() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	IntByReference pnBytesRead = new IntByReference();
    	IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
    	Memory buffer = new Memory(1024 * 64);
    	// shorten test, avoid iterating through all events
    	int maxReads = 3;     	
    	int rc = 0;
    	while(true) {
            if (maxReads-- <= 0)
                break;			
            if (! Advapi32.INSTANCE.ReadEventLog(h, 
                                                 WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ, 
                                                 0, buffer, (int) buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                rc = Kernel32.INSTANCE.GetLastError();
                if (rc == W32Errors.ERROR_INSUFFICIENT_BUFFER) {
                    buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());
                    rc = 0;
                    continue;
                }    			
                break;
            }
            int dwRead = pnBytesRead.getValue();
            Pointer pevlr = buffer;
            int maxRecords = 3;
            while (dwRead > 0 && maxRecords-- > 0) {
                EVENTLOGRECORD record = new EVENTLOGRECORD(pevlr);
                /*
                  System.out.println(record.RecordNumber.intValue()
                  + " Event ID: " + record.EventID.intValue()
                  + " Event Type: " + record.EventType.intValue()
                  + " Event Source: " + pevlr.getString(record.size(), true));
                */
                dwRead -= record.Length.intValue();
                pevlr = pevlr.share(record.Length.intValue());
            }
    	}
        assertTrue("Unexpected error after reading event log: "
                   + new Win32Exception(rc),
                   rc == W32Errors.ERROR_HANDLE_EOF || rc == 0);
        assertTrue("Error closing event log",
                   Advapi32.INSTANCE.CloseEventLog(h));    	
    }
    
    public void testGetOldestEventLogRecord() {
    	HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, "Application");
    	IntByReference oldestRecord = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetOldestEventLogRecord(h, oldestRecord));
    	assertTrue(oldestRecord.getValue() >= 0);
    	assertTrue(Advapi32.INSTANCE.CloseEventLog(h));
    }

    public void testQueryServiceStatusEx() {
        
    	SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(scmHandle);

    	SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_STATUS);
    	assertNotNull(serviceHandle);

    	IntByReference pcbBytesNeeded = new IntByReference();

    	assertFalse(Advapi32.INSTANCE.QueryServiceStatusEx(serviceHandle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
    			null, 0, pcbBytesNeeded));
    	assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
    	
    	assertTrue(pcbBytesNeeded.getValue() > 0);

    	SERVICE_STATUS_PROCESS status = new SERVICE_STATUS_PROCESS(pcbBytesNeeded.getValue());

    	assertTrue(Advapi32.INSTANCE.QueryServiceStatusEx(serviceHandle, SC_STATUS_TYPE.SC_STATUS_PROCESS_INFO,
    			status, status.size(), pcbBytesNeeded));

    	assertTrue(status.dwCurrentState == Winsvc.SERVICE_STOPPED || 
    			status.dwCurrentState == Winsvc.SERVICE_RUNNING);

    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }

    
    public void testControlService() {
    	SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(scmHandle);

    	SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG);
    	assertNotNull(serviceHandle);

    	Winsvc.SERVICE_STATUS serverStatus = new Winsvc.SERVICE_STATUS();

    	assertNotNull(serviceHandle);
    	assertFalse(Advapi32.INSTANCE.ControlService(serviceHandle, Winsvc.SERVICE_CONTROL_STOP, serverStatus));
    	assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());

    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }
    
    public void testStartService() {
    	SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(scmHandle);
    	
    	SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG);
    	assertNotNull(serviceHandle);
    	
    	assertFalse(Advapi32.INSTANCE.StartService(serviceHandle, 0, null));
    	assertEquals(W32Errors.ERROR_ACCESS_DENIED, Kernel32.INSTANCE.GetLastError());

    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }
    
    public void testOpenService() {
    	assertNull(Advapi32.INSTANCE.OpenService(null, "eventlog", Winsvc.SERVICE_QUERY_CONFIG ));
    	assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());

    	SC_HANDLE scmHandle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(scmHandle);
    	
    	SC_HANDLE serviceHandle = Advapi32.INSTANCE.OpenService(scmHandle, "eventlog", Winsvc.SERVICE_QUERY_CONFIG );
    	assertNotNull(serviceHandle);
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(serviceHandle));

    	assertNull(Advapi32.INSTANCE.OpenService(scmHandle, "slashesArentValidChars/", Winsvc.SERVICE_QUERY_CONFIG ));
    	assertEquals(W32Errors.ERROR_INVALID_NAME, Kernel32.INSTANCE.GetLastError());

    	assertNull(Advapi32.INSTANCE.OpenService(scmHandle, "serviceDoesNotExist", Winsvc.SERVICE_QUERY_CONFIG ));
    	assertEquals(W32Errors.ERROR_SERVICE_DOES_NOT_EXIST, Kernel32.INSTANCE.GetLastError());

    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(scmHandle));
    }
    
    public void testOpenSCManager() {
    	SC_HANDLE handle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(handle);
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(handle));
    	
    	assertNull(Advapi32.INSTANCE.OpenSCManager("invalidMachineName", null, Winsvc.SC_MANAGER_CONNECT));
    	assertEquals(W32Errors.RPC_S_SERVER_UNAVAILABLE, Kernel32.INSTANCE.GetLastError());

    	assertNull(Advapi32.INSTANCE.OpenSCManager(null, "invalidDatabase", Winsvc.SC_MANAGER_CONNECT));
    	assertEquals(W32Errors.ERROR_INVALID_NAME, Kernel32.INSTANCE.GetLastError());
    }
    
    public void testCloseServiceHandle() throws Exception {
    	SC_HANDLE handle = Advapi32.INSTANCE.OpenSCManager(null, null, Winsvc.SC_MANAGER_CONNECT);
    	assertNotNull(handle);
    	assertTrue(Advapi32.INSTANCE.CloseServiceHandle(handle));
    	
    	assertFalse(Advapi32.INSTANCE.CloseServiceHandle(null));
    	assertEquals(W32Errors.ERROR_INVALID_HANDLE, Kernel32.INSTANCE.GetLastError());
    }
    
    public void testCreateProcessAsUser() {
    	HANDLEByReference hToken = new HANDLEByReference();
    	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
    	assertTrue(Advapi32.INSTANCE.OpenProcessToken(processHandle,
    			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, hToken));
    	
    	assertFalse(Advapi32.INSTANCE.CreateProcessAsUser(hToken.getValue(), null, "InvalidCmdLine.jna",
    			null, null, false, 0, null, null, new WinBase.STARTUPINFO(),
    			new WinBase.PROCESS_INFORMATION()));
    	assertEquals(W32Errors.ERROR_FILE_NOT_FOUND, Kernel32.INSTANCE.GetLastError());
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hToken.getValue()));
    }
    
    /**
     * Tests both {@link Advapi32#LookupPrivilegeValue} and {@link Advapi32#LookupPrivilegeName}
     */
    public void testLookupPrivilegeValueAndLookupPrivilegeName() {
    	WinNT.LUID luid = new WinNT.LUID();
    	
    	assertFalse(Advapi32.INSTANCE.LookupPrivilegeValue(null, "InvalidName", luid));
    	assertEquals(Kernel32.INSTANCE.GetLastError(), W32Errors.ERROR_NO_SUCH_PRIVILEGE);
    	
    	assertTrue(Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_BACKUP_NAME, luid));
    	assertTrue(luid.LowPart > 0 || luid.HighPart > 0);
    	
    	char[] lpName = new char[256];
    	IntByReference cchName = new IntByReference(lpName.length);
    	assertTrue(Advapi32.INSTANCE.LookupPrivilegeName(null, luid, lpName, cchName));
    	assertEquals(WinNT.SE_BACKUP_NAME.length(), cchName.getValue());
    	assertEquals(WinNT.SE_BACKUP_NAME, Native.toString(lpName));
    }
    
    public void testAdjustTokenPrivileges() {
    	HANDLEByReference hToken = new HANDLEByReference();
    	assertTrue(Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
    			WinNT.TOKEN_ADJUST_PRIVILEGES | WinNT.TOKEN_QUERY, hToken));
    	
    	// Find an already enabled privilege
    	TOKEN_PRIVILEGES tp = new TOKEN_PRIVILEGES(1024);
    	IntByReference returnLength = new IntByReference();
    	assertTrue(Advapi32.INSTANCE.GetTokenInformation(hToken.getValue(),	WinNT.TOKEN_INFORMATION_CLASS.TokenPrivileges,
    			tp, tp.size(), returnLength));
    	assertTrue(tp.PrivilegeCount.intValue() > 0);
    	
    	WinNT.LUID luid = null;
    	for (int i=0; i<tp.PrivilegeCount.intValue(); i++) {
    		if ((tp.Privileges[i].Attributes.intValue() & WinNT.SE_PRIVILEGE_ENABLED) > 0) {
    			luid = tp.Privileges[i].Luid;
    		}
    	}
    	assertTrue(luid != null);    	
    	
    	// Re-enable it. That should succeed.
    	tp = new WinNT.TOKEN_PRIVILEGES(1);
    	tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
    	
    	assertTrue(Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tp, 0, null, null));
    	assertTrue(Kernel32.INSTANCE.CloseHandle(hToken.getValue()));
    }
    
    public void testImpersonateSelf() {
    	assertTrue(Advapi32.INSTANCE.ImpersonateSelf(WinNT.SECURITY_IMPERSONATION_LEVEL.SecurityAnonymous));
    	assertTrue(Advapi32.INSTANCE.RevertToSelf());
    }
}
