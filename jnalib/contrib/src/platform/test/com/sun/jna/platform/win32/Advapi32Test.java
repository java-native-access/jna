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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.W32API.HANDLE;
import com.sun.jna.platform.win32.W32API.HANDLEByReference;
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
		byte[] sid = new byte[pSid.getValue()];
		char[] referencedDomainName = new char[pDomain.getValue() + 1]; 
		assertTrue(Advapi32.INSTANCE.LookupAccountName(
				null, accountName, sid, pSid, referencedDomainName, pDomain, peUse));
		assertEquals(1, peUse.getPointer().getInt(0)); // SidTypeUser
		assertTrue(Native.toString(referencedDomainName).length() > 0);
    }
    
    public void testLookupAccountSid() {
    	// get SID bytes
    	String sidString = "S-1-1-0"; // Everyone
    	PointerByReference sid = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(sidString, sid));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid);
    	assertTrue(sidLength > 0);
    	byte[] sidBytes = sid.getValue().getByteArray(0, sidLength);
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(sid.getValue()));
    	// lookup account
    	IntByReference cchName = new IntByReference();
    	IntByReference cchReferencedDomainName = new IntByReference();
    	PointerByReference peUse = new PointerByReference();
    	assertFalse(Advapi32.INSTANCE.LookupAccountSid(null, sidBytes, 
    			null, cchName, null, cchReferencedDomainName, peUse));
		assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
    	assertTrue(cchName.getValue() > 0);
    	assertTrue(cchReferencedDomainName.getValue() > 0);
		char[] referencedDomainName = new char[cchReferencedDomainName.getValue()];
		char[] name = new char[cchName.getValue()];
    	assertTrue(Advapi32.INSTANCE.LookupAccountSid(null, sidBytes, 
    			name, cchName, referencedDomainName, cchReferencedDomainName, peUse));
		assertEquals(5, peUse.getPointer().getInt(0)); // SidTypeWellKnownGroup
		String nameString = Native.toString(name);
		String referencedDomainNameString = Native.toString(referencedDomainName);
		assertTrue(nameString.length() > 0);
		assertEquals("Everyone", nameString);
		assertTrue(referencedDomainNameString.length() == 0);
    }
    
    public void testConvertSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	PointerByReference sid = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSid(
    			sidString, sid));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid);
    	assertTrue(sidLength > 0);
    	byte[] sidBytes = sid.getValue().getByteArray(0, sidLength);
    	PointerByReference convertedSidStringPtr = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertSidToStringSid(
    			sidBytes, convertedSidStringPtr));
    	String convertedSidString = convertedSidStringPtr.getValue().getString(0, true);
    	assertEquals(convertedSidString, sidString);
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(
    			convertedSidStringPtr.getValue()));
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(
    			sid.getValue()));
    }
    
    public void testLogonUser() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	assertFalse(Advapi32.INSTANCE.LogonUser("AccountDoesntExist", ".", "passwordIsInvalid", 
    			WinBase.LOGON32_LOGON_NETWORK, WinBase.LOGON32_PROVIDER_DEFAULT, phToken));
    	assertTrue(W32Errors.ERROR_SUCCESS != Kernel32.INSTANCE.GetLastError());
    }
    
    public void testOpenThreadToken() {
    	HANDLEByReference phToken = new HANDLEByReference();
    	HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
    	assertNotNull(threadHandle);
    	assertFalse(Advapi32.INSTANCE.OpenThreadToken(threadHandle, WinNT.TOKEN_READ, false, phToken));
    	assertEquals(W32Errors.ERROR_NO_TOKEN, Kernel32.INSTANCE.GetLastError());
    }
}
