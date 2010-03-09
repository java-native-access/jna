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
import com.sun.jna.ptr.IntByReference;

public class Secur32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Secur32Test.class);
    }
    
    public void testGetUserNameEx() {
    	IntByReference len = new IntByReference();
    	Secur32.INSTANCE.GetUserNameEx(
    			Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, null, len);
    	assertTrue(len.getValue() > 0);
    	char[] buffer = new char[len.getValue() + 1];
    	assertTrue(Secur32.INSTANCE.GetUserNameEx(
    			Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, buffer, len));
    	String username = Native.toString(buffer);
    	assertTrue(username.length() > 0);
    }
}
