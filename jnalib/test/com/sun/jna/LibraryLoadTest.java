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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class LibraryLoadTest extends TestCase {
    
    public void testLoadJNALibrary() {
        assertTrue("Point size should never be zero", Pointer.SIZE > 0);
    }
    
    public void testLoadJAWT() {
        if (GraphicsEnvironment.isHeadless()) return;

        Frame f = new Frame(getName());
        f.pack();
        try {
            // FIXME: this works as a test, but fails in ShapedWindowDemo
            // if the JAWT load workaround is not used
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

    public void testLoadAWTAfterJNA() {
        if (Pointer.SIZE > 0) {
            Toolkit.getDefaultToolkit();
        }
    }
    
    private Object load() {
        return Native.loadLibrary(System.getProperty("os.name").startsWith("Windows")
                                  ? "msvcrt" : "c", CLibrary.class);
    }
    
    public void testLoadCLibrary() {
        load();
    }
    
    private static final String UNICODE = "\u0444\u043b\u0441\u0432\u0443";
    private void copy(File src, File dst) throws Exception {
        FileInputStream is = new FileInputStream(src);
        FileOutputStream os = new FileOutputStream(dst);
        int count;
        byte[] buf = new byte[1024];
        try {
            while ((count = is.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, count);
            }
        }
        finally {
            try { is.close(); } catch(IOException e) { }
            try { os.close(); } catch(IOException e) { } 
        }
    }

    /** System property name (set by Ant) when running with 64-bit JVM. */
    static final String DIR_BUILD_SUFFIX = "dir.build.suffix";

    public void testLoadLibraryWithUnicodeName() throws Exception {
        String tmp = System.getProperty("java.io.tmpdir");
        String libName = System.mapLibraryName("jnidispatch");
                                    // use "build-d64" for 64-bit jvm's
        File src = new File("build" + System.getProperty(DIR_BUILD_SUFFIX, "") 
                + "/native", libName);
        String newLibName = UNICODE;
        if (libName.startsWith("lib"))
            newLibName = "lib" + newLibName;
        int dot = libName.lastIndexOf(".");
        if (dot != -1) {
            if (Platform.isMac()) {
                newLibName += ".dylib";
            }
            else {
                newLibName += libName.substring(dot, libName.length());
            }
        }
        File dst = new File(tmp, newLibName);
        dst.deleteOnExit();
        copy(src, dst);
        NativeLibrary.addSearchPath(UNICODE, tmp);
        NativeLibrary.getInstance(UNICODE);
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
