/* Copyright (c) 2007-2009 Timothy Wall, All Rights Reserved
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

public class JNAUnloadTest extends TestCase {
    
    private static final String BUILDDIR =
        System.getProperty("jna.builddir", "build"
                           + (Platform.is64Bit() ? "-d64" : ""));

    private static class TestLoader extends URLClassLoader {
        public TestLoader(boolean fromJar) throws MalformedURLException {
            super(new URL[] {
                    Platform.isWindowsCE() 
                    ? new File("/Storage Card/" + (fromJar ? "jna.jar" : "test.jar")).toURI().toURL()
                    : new File(BUILDDIR + (fromJar ? "/jna.jar" : "/classes")).toURI().toURL(),
            }, null);
        }
        protected Class findClass(String name) throws ClassNotFoundException {
            String boot = System.getProperty("jna.boot.library.path");
            if (boot != null) {
                System.setProperty("jna.boot.library.path", "");
            }
            Class cls = super.findClass(name);
            if (boot != null) {
                System.setProperty("jna.boot.library.path", boot);
            }
            return cls;
        }
    }

    public void testLoadFromJar() throws Exception {
        Class.forName("com.sun.jna.Native", true, new TestLoader(true));
    }

    public void testAvoidJarUnpacking() throws Exception {
        System.setProperty("jna.nounpack", "true");
        ClassLoader loader = new TestLoader(true);
        try {
            Class cls = Class.forName("com.sun.jna.Native", true, loader);

            fail("Class com.sun.jna.Native should not be loadable if jna.nounpack=true: "
                 + cls.getClassLoader());
        }
        catch(UnsatisfiedLinkError e) {
        }
        finally {
            System.setProperty("jna.nounpack", "false");
        }
    }

    // Fails under clover
    public void testUnloadFromJar() throws Exception {
        File jar = new File((Platform.isWindowsCE() ? "/Storage Card" : BUILDDIR) + "/jna.jar");
        if (!jar.exists()) {
            throw new Error("Expected JNA jar file at " + jar + " is missing");
        }

        ClassLoader loader = new TestLoader(true);
        Class cls = Class.forName("com.sun.jna.Native", true, loader);
        assertEquals("Wrong class loader", loader, cls.getClassLoader());

        Field field = cls.getDeclaredField("nativeLibraryPath");
        field.setAccessible(true);
        String path = (String)field.get(null);
        assertNotNull("Native library path unavailable", path);
        assertTrue("Native library not unpacked from jar: " + path,
                   path.startsWith(System.getProperty("java.io.tmpdir")));

        WeakReference ref = new WeakReference(cls);
        WeakReference clref = new WeakReference(loader);
        loader = null;
        cls = null;
        field = null;
        System.gc();
        for (int i=0;i < 100 && (ref.get() != null || clref.get() != null);i++) {
            Thread.sleep(10);
            System.gc();
        }
        assertNull("Class not GC'd: " + ref.get(), ref.get());
        assertNull("ClassLoader not GC'd: " + clref.get(), clref.get());

        // Check for temporary file deletion
        File f = new File(path);
        for (int i=0;i < 100 && f.exists();i++) {
            Thread.sleep(10);
            System.gc();
        }

        if (f.exists()) {
            assertTrue("Temporary jnidispatch not marked for later deletion: "
                       + f, new File(f.getAbsolutePath()+".x").exists());
        }

        // Should be able to load again without complaints about library
        // already loaded in another class loader
        try {
            loader = new TestLoader(true);
            cls = Class.forName("com.sun.jna.Native", true, loader);
        }
        catch(Throwable t) {
            fail("Couldn't load class again after discarding first load: " + t.getMessage());
        }
        finally {
            loader = null;
            cls = null;
            System.gc();
        }
    }

    // Fails under clover and OpenJDK(linux/ppc)
    public void testUnload() throws Exception {
        ClassLoader loader = new TestLoader(false);
        Class cls = Class.forName("com.sun.jna.Native", true, loader);
        assertEquals("Wrong class loader", loader, cls.getClassLoader());

        Field field = cls.getDeclaredField("nativeLibraryPath");
        field.setAccessible(true);
        String path = (String)field.get(null);
        assertNotNull("Native library not found", path);

        WeakReference ref = new WeakReference(cls);
        WeakReference clref = new WeakReference(loader);
        loader = null;
        cls = null;
        field = null;
        System.gc();
        for (int i=0;i < 100 && (ref.get() != null || clref.get() != null);i++) {
            Thread.sleep(10);
            System.gc();
        }
        assertNull("Class not GC'd: " + ref.get(), ref.get());
        assertNull("ClassLoader not GC'd: " + clref.get(), clref.get());

        Throwable throwable = null;
        // NOTE: IBM J9 needs some extra time to unload the native library,
        // so try a few times before failing
        for (int i=0;i < 100;i++) {
            System.gc();
            Thread.sleep(10);
            try {
                loader = new TestLoader(false);
                cls = Class.forName("com.sun.jna.Native", true, loader);
                break;
            }
            catch(Throwable t) {
                loader = null;
                throwable = t;
            }
        }
        try {
            if (loader == null) {
                fail("Native library not unloaded: " + throwable.getMessage());
            }
        }
        finally {
            loader = null;
            cls = null;
            System.gc();
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(JNAUnloadTest.class);
    }
}
