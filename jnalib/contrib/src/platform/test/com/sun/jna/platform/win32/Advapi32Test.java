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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.W32API.HANDLE;
import com.sun.jna.platform.win32.W32API.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

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
        Memory tokenInformationBuffer = new Memory(tokenInformationLength.getValue());
		WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationBuffer);
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
        Memory tokenInformationBuffer = new Memory(tokenInformationLength.getValue());
		WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationBuffer);
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
    
    /*
    public void testImpersonateLoggedOnUser() {
    	USER_INFO_1 userInfo = new USER_INFO_1();
    	userInfo.usri1_name = new WString("JNAAdvapi32TestImp");
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
				assertTrue(Advapi32.INSTANCE.ImpersonateLoggedOnUser(phUser.getValue()));
				assertTrue(Advapi32.INSTANCE.RevertToSelf());
			} finally {
				if (phUser.getValue() != Kernel32.INVALID_HANDLE_VALUE) {
					Kernel32.INSTANCE.CloseHandle(phUser.getValue());
				}				
			}
		} finally {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
	    			null, userInfo.usri1_name.toString()));			
		}
    }
	*/
}
