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
package com.sun.jna.examples.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

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
		char[] lpAccountName = Native.toCharArray("Administrator");
		assertFalse(Advapi32.INSTANCE.LookupAccountNameW(
				null, lpAccountName, null, pSid, null, pDomain, peUse));
		assertEquals(W32Errors.ERROR_INSUFFICIENT_BUFFER, Kernel32.INSTANCE.GetLastError());
		assertTrue(pSid.getValue() > 0);
		byte[] sid = new byte[pSid.getValue()];
		char[] referencedDomainName = new char[pDomain.getValue() + 1]; 
		assertTrue(Advapi32.INSTANCE.LookupAccountNameW(
				null, lpAccountName, sid, pSid, referencedDomainName, pDomain, peUse));
		assertEquals(1, peUse.getPointer().getInt(0)); // SidTypeUser
		assertTrue(Native.toString(referencedDomainName).length() > 0);
    }
    
    public void testConvertSidToStringSid() {
    	String sidString = "S-1-1-0"; // Everyone
    	PointerByReference sid = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertStringSidToSidW(
    			Native.toCharArray(sidString), sid));
    	int sidLength = Advapi32.INSTANCE.GetLengthSid(sid);
    	assertTrue(sidLength > 0);
    	byte[] sidBytes = sid.getValue().getByteArray(0, sidLength);
    	PointerByReference convertedSidStringPtr = new PointerByReference();
    	assertTrue(Advapi32.INSTANCE.ConvertSidToStringSidW(
    			sidBytes, convertedSidStringPtr));
    	String convertedSidString = convertedSidStringPtr.getValue().getString(0, true);
    	assertEquals(convertedSidString, sidString);
    	assertEquals(null, Kernel32.INSTANCE.LocalFree(
    			convertedSidStringPtr.getValue()));
    }
}
