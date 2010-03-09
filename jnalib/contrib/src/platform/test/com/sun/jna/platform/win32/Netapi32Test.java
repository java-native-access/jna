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

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Netapi32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Netapi32Test.class);
    }

    public void testNetGetJoinInformation() {
		IntByReference bufferType = new IntByReference();
    	assertEquals(W32Errors.ERROR_INVALID_PARAMETER, Netapi32.INSTANCE.NetGetJoinInformation(
    			null, null, bufferType));
    	PointerByReference lpNameBuffer = new PointerByReference();
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetGetJoinInformation(
    			null, lpNameBuffer, bufferType));
    	assertTrue(lpNameBuffer.getValue().getString(0).length() > 0);
    	assertTrue(bufferType.getValue() > 0);
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
    			lpNameBuffer.getValue()));
    }
    
    public void testNetGetLocalGroups() {
    	for(int i = 0; i < 2; i++) {
			PointerByReference bufptr = new PointerByReference();
			IntByReference entriesRead = new IntByReference();
			IntByReference totalEntries = new IntByReference();		
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetLocalGroupEnum(null, i, bufptr, 
	    			LMCons.MAX_PREFERRED_LENGTH, 
	    			entriesRead, 
	    			totalEntries, 
	    			null));
	    	assertTrue(entriesRead.getValue() > 0);
	    	assertEquals(totalEntries.getValue(), entriesRead.getValue());
	    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(
	    			bufptr.getValue()));
    	}
    }
    
    public void testNetGetDCName() {
    	PointerByReference lpNameBuffer = new PointerByReference();
    	IntByReference BufferType = new IntByReference();
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetGetJoinInformation(null, lpNameBuffer, BufferType));    	
    	if (BufferType.getValue() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
	    	PointerByReference bufptr = new PointerByReference();
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetGetDCName(null, null, bufptr));
	    	String dc = bufptr.getValue().getString(0);
	    	assertTrue(dc.length() > 0);
	    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()));
    	}
    	assertEquals(W32Errors.ERROR_SUCCESS, Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()));
    }
}
