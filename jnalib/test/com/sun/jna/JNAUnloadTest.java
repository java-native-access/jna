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

import java.lang.ref.WeakReference;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import junit.framework.TestCase;

public class JNAUnloadTest extends TestCase {
    
    private static class TestLoader extends URLClassLoader {
        public TestLoader() throws MalformedURLException {
            super(new URL[] {
                new File("build/classes").toURI().toURL(),
            }, null);
        }
    }

    // TODO: test auto-dispose of callback memory

    public void testUnload() throws Exception {
        ClassLoader loader = new TestLoader();
        Class cls = Class.forName("com.sun.jna.Native", true, loader);
        assertEquals("Wrong class loader", loader, cls.getClassLoader());

        WeakReference ref = new WeakReference(cls);
        WeakReference clref = new WeakReference(loader);
        loader = null;
        cls = null;
        System.gc();
        assertNull("Class not GC'd: " + ref.get(), ref.get());
        assertNull("ClassLoader not GC'd: " + clref.get(), clref.get());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(JNAUnloadTest.class);
    }
}
