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
package com.sun.jna;

import java.awt.Toolkit;

import junit.framework.TestCase;

// TODO: verify load from jna.library.path
public class LibraryLoadTest extends TestCase {
    
    public void testLoadJNALibrary() {
        assertTrue("Point size should never be zero", Pointer.SIZE > 0);
    }
    
    public static interface CLibrary extends Library {
        int wcslen(WString wstr);
        int strlen(String str);
        int atol(String str);
    }
    
    public void testLoadCLibrary() {
        Native.loadLibrary(System.getProperty("os.name").startsWith("Windows")
                           ? "msvcrt" : "c", CLibrary.class);
    }
    
    public void testLoadAWTAfterJNA() {
        if (Pointer.SIZE > 0) {
            Toolkit.getDefaultToolkit();
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LibraryLoadTest.class);
    }
}
