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
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Shell32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32Test.class);
    }

    public void testStructurePacking() {
        Structure s = new ShellAPI.SHFILEOPSTRUCT();
        final int SIZE = Pointer.SIZE * 5 + 10; // 5 pointers, 2 ints, 1 short
        assertEquals("Wrong structure size", SIZE, s.size());
    }
    
    public void testSHGetFolderPath() {
    	char[] pszPath = new char[WinDef.MAX_PATH];
    	assertEquals(W32Errors.S_OK, Shell32.INSTANCE.SHGetFolderPath(null, 
    			ShlObj.CSIDL_PROGRAM_FILES, null, ShlObj.SHGFP_TYPE_CURRENT, 
    			pszPath));
    	assertTrue(Native.toString(pszPath).length() > 0);
    }
}
