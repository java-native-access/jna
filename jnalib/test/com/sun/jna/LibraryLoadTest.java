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

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import junit.framework.TestCase;

// TODO: verify load from jna.library.path
public class LibraryLoadTest extends TestCase {
    
    public void testLoadJNALibrary() {
        assertTrue("Point size should never be zero", Pointer.SIZE > 0);
    }
    
    public void testLoadJAWT() {
        if (GraphicsEnvironment.isHeadless()) return;

        Frame f = new Frame(getName());
        f.pack();
        try {
            Native.getWindowPointer(f);
        }
        finally {
            f.dispose();
        }
    }
    
    public static interface CLibrary extends Library {
        int wcslen(WString wstr);
        int strlen(String str);
        int atol(String str);
    }

    private Object load() {
        return Native.loadLibrary(System.getProperty("os.name").startsWith("Windows")
                                  ? "msvcrt" : "c", CLibrary.class);
    }
    
    public void testLoadCLibrary() {
        load();
    }
    
    public void testLoadAWTAfterJNA() {
        if (Pointer.SIZE > 0) {
            Toolkit.getDefaultToolkit();
        }
    }
    
    public void testHandleObjectMethods() {
        CLibrary lib = (CLibrary)load();
        String method = "toString";
        try {
            lib.toString();
            method = "hashCode";
            lib.hashCode();
            method = "equals";
            lib.equals(null);
        }
        catch(UnsatisfiedLinkError e) {
            fail("Object method '" + method + "' not handled");
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LibraryLoadTest.class);
    }
}
