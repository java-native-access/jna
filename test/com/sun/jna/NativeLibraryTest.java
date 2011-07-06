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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class NativeLibraryTest extends TestCase {
    
    public static interface TestLibrary extends Library {
        int callCount();
    }
    public void testGCNativeLibrary() throws Exception {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        WeakReference ref = new WeakReference(lib);
        lib = null;
        System.gc();
        long start = System.currentTimeMillis();
        while (ref.get() != null) {
            Thread.sleep(10);
            if (System.currentTimeMillis() - start > 5000) 
                break;
        }
        assertNull("Library not GC'd", ref.get());
    }

    public void testAvoidDuplicateLoads() {
        TestLibrary lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        assertEquals("Library should be loaded exactly once",
                     1, lib.callCount());
        assertEquals("Library should not be reloaded",
                     2, lib.callCount());
    }
    
    public void testUseSingleLibraryInstance() {
        TestLibrary lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        int count = lib.callCount();
        TestLibrary lib2 = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Interfaces should share a library instance",
                     count + 1, count2);
    }

    public void testAliasLibraryFilename() {
        TestLibrary lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        int count = lib.callCount();
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        TestLibrary lib2 = (TestLibrary)Native.loadLibrary(nl.getFile().getName(), TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Simple filename load not aliased", count + 1, count2);
    }
    
    public void testAliasLibraryFullPath() {
        TestLibrary lib = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        int count = lib.callCount();
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        TestLibrary lib2 = (TestLibrary)Native.loadLibrary(nl.getFile().getAbsolutePath(), TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Full pathname load not aliased", count + 1, count2);
    }
    
    public void testAliasSimpleLibraryName() throws Exception {
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        File file = nl.getFile();
        WeakReference ref = new WeakReference(nl);
        nl = null;
        System.gc();
        long start = System.currentTimeMillis();
        while (ref.get() != null) {
            Thread.sleep(10);
            if (System.currentTimeMillis() - start > 5000) 
                fail("Timed out waiting for library to be GC'd");
        }
        TestLibrary lib = (TestLibrary)Native.loadLibrary(file.getAbsolutePath(), TestLibrary.class);
        int count = lib.callCount();
        TestLibrary lib2 = (TestLibrary)Native.loadLibrary("testlib", TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Simple library name not aliased", count + 1, count2);
    }
    public void testFunctionHoldsLibraryReference() throws Exception {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        WeakReference ref = new WeakReference(lib);
        Function f = lib.getFunction("callCount");
        lib = null;
        System.gc();
        long start = System.currentTimeMillis();
        while (ref.get() != null && System.currentTimeMillis() - start < 2000) {
            Thread.sleep(10);            
        }
        assertNotNull("Library GC'd when it should not be", ref.get());
        f.invokeInt(new Object[0]);
        f = null;
        System.gc();
        while (ref.get() != null && System.currentTimeMillis() - start < 5000) {
            Thread.sleep(10);            
        }
        assertNull("Library not GC'd", ref.get());
    }
    
    public void testLoadFrameworkLibrary() {
        if (Platform.isMac()) {
            try {
                NativeLibrary lib = NativeLibrary.getInstance("CoreServices");
                assertNotNull("CoreServices not found", lib);
            }
            catch(UnsatisfiedLinkError e) {
                fail("Should search /System/Library/Frameworks");
            }
        }
    }
    
    public void testLookupGlobalVariable() {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        Pointer global = lib.getGlobalVariableAddress("test_global");
        assertNotNull("Test variable not found", global);
        final int MAGIC = 0x12345678;
        assertEquals("Wrong value for library global variable", MAGIC, global.getInt(0));
        
        global.setInt(0, MAGIC+1);
        assertEquals("Library global variable not updated", MAGIC+1, global.getInt(0));
    }
    
    public void testMatchUnversionedToVersioned() throws Exception {
    	File lib0 = File.createTempFile("lib", ".so.0");
    	File dir = lib0.getParentFile();
    	String name = lib0.getName();
    	name = name.substring(3, name.indexOf(".so"));
    	lib0.deleteOnExit();
    	File lib1 = new File(dir, "lib" + name + ".so.1.0");
        lib1.createNewFile();
    	lib1.deleteOnExit();
    	File lib1_1 = new File(dir, "lib" + name + ".so.1.1");
        lib1_1.createNewFile();
    	lib1_1.deleteOnExit();
    	List path = Arrays.asList(new String[] { dir.getAbsolutePath() });
    	assertEquals("Latest versioned library not found when unversioned requested",
                     lib1_1.getAbsolutePath(),	
                     NativeLibrary.matchLibrary(name, path));
    }
    
    public void testAvoidFalseMatch() throws Exception {
        File lib0 = File.createTempFile("lib", ".so.1");
    	File dir = lib0.getParentFile();
        lib0.deleteOnExit();
    	String name = lib0.getName();
    	name = name.substring(3, name.indexOf(".so"));
        File lib1 = new File(dir, "lib" + name + "-client.so.2");
        lib1.createNewFile();
        lib1.deleteOnExit();
    	List path = Arrays.asList(new String[] { dir.getAbsolutePath() });
    	assertEquals("Library with similar prefix should be ignored",
                     lib0.getAbsolutePath(),	
                     NativeLibrary.matchLibrary(name, path));
    }

    public void testParseVersion() throws Exception {
    	String[] VERSIONS = {
    		"1",
    		"1.2",
    		"1.2.3",
    		"1.2.3.4",
    	};
    	double[] EXPECTED = {
    		1, 1.02, 1.0203, 1.020304,
    	};
    	for (int i=0;i < VERSIONS.length;i++) {
    		assertEquals("Badly parsed version", EXPECTED[i], NativeLibrary.parseVersion(VERSIONS[i]), 0.0000001);
    	}
    }
    
    public void testGetProcess() {
        NativeLibrary process = NativeLibrary.getProcess();
        // Access a common C library function
        process.getFunction("printf");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NativeLibraryTest.class);
    }
}
