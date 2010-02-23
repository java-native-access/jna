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
    
}
