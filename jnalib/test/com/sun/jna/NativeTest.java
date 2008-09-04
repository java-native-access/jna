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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;

public class NativeTest extends TestCase {
    
    public void testDefaultStringEncoding() throws Exception {
        String encoding = System.getProperty("file.encoding");
        // Keep stuff within the extended ASCII range so we work with more
        // limited native encodings
        String unicode = "Un \u00e9l\u00e9ment gr\u00e2ce \u00e0 l'index";
        
        if (!unicode.equals(new String(unicode.getBytes()))) {
            // If the extended characters aren't encodable in the default 
            // encoding, punt and use straight ASCII
            unicode = "";
            for (char ch=1;ch < 128;ch++) {
                unicode += ch;
            }
        }
        String unicodez = unicode + "\0more stuff";
        
        byte[] defaultEncoded = Native.getBytes(unicode);
        byte[] expected = unicode.getBytes();
        for (int i=0;i < Math.min(defaultEncoded.length, expected.length);i++) {
            assertEquals("Improperly encoded (" + encoding + ") from Java at " + i, 
                         expected[i], defaultEncoded[i]);
        }
        assertEquals("Wrong number of encoded characters (" + encoding + ")", 
                     expected.length, defaultEncoded.length);
        String result = Native.toString(defaultEncoded);
        assertEquals("Improperly decoded from native bytes (" + encoding + ")", 
                     unicode, result);
        
        assertEquals("Should truncate bytes at NUL terminator",
                     unicode, Native.toString(unicodez.getBytes()));
    }
    
    public void testCustomStringEncoding() throws Exception {
        Properties oldprops = (Properties)System.getProperties().clone();
        try {
            String encoding = "UTF8";
            System.setProperty("jna.encoding", encoding);
            String unicode = "\u0444\u043b\u0441\u0432\u0443";
            String unicodez = unicode + "\0more stuff";
            byte[] utf8 = Native.getBytes(unicode);
            byte[] expected = unicode.getBytes(encoding);
            for (int i=0;i < Math.min(utf8.length, expected.length);i++) {
                assertEquals("Improperly encoded at " + i, 
                             expected[i], utf8[i]);
            }
            assertEquals("Wrong number of encoded characters", expected.length, utf8.length);
            String result = Native.toString(utf8);
            assertEquals("Improperly decoded", unicode, result);
            
            assertEquals("Should truncate bytes at NUL terminator",
                         unicode, Native.toString(unicodez.getBytes(encoding)));
        }
        finally {
            System.setProperties(oldprops);
        }
    }
    
    public static interface TestLib extends Library {
        interface VoidCallback extends Callback {
            void callback();
        }
        void callVoidCallback(VoidCallback callback);
    }
    public void testSynchronizedAccess() throws Exception {
        final boolean[] lockHeld = { false };
        final NativeLibrary nlib = NativeLibrary.getInstance("testlib");
        final TestLib lib = (TestLib)Native.loadLibrary("testlib", TestLib.class);
        final TestLib synchlib = (TestLib)Native.synchronizedLibrary(lib); 
        final TestLib.VoidCallback cb = new TestLib.VoidCallback() {
            public void callback() {
                lockHeld[0] = Thread.holdsLock(nlib);
            }
        };
        Thread t0 = new Thread() {
            public void run() {
                lib.callVoidCallback(cb);
            }
        };
        t0.start();
        t0.join();
        assertFalse("NativeLibrary lock should not be held during native call to normal library",
                   lockHeld[0]);

        Thread t1 = new Thread() {
            public void run() {
                synchlib.callVoidCallback(cb);
            }
        };
        t1.start();
        t1.join();
        assertTrue("NativeLibrary lock should be held during native call to synchronized library",
                   lockHeld[0]);
    }

    interface TestInterface extends Library {
        static class InnerTestClass extends Structure {
            interface TestCallback extends Callback { }
            static class InnerSubclass extends InnerTestClass implements Structure.ByReference { }
        }
    }
    
    public void testFindInterfaceClass() throws Exception {
        Class interfaceClass = TestInterface.class;
        Class cls = TestInterface.InnerTestClass.class;
        Class subClass = TestInterface.InnerTestClass.InnerSubclass.class;
        Class callbackClass = TestInterface.InnerTestClass.TestCallback.class;
        assertEquals("Enclosing interface not found for class",
                     interfaceClass, Native.findLibraryClass(cls));
        assertEquals("Enclosing interface not found for derived class",
                     interfaceClass, Native.findLibraryClass(subClass));
        assertEquals("Enclosing interface not found for callback",
                     interfaceClass, Native.findLibraryClass(callbackClass));
    }

    public interface TestInterfaceWithInstance extends Library {
        int TEST_ALIGNMENT = Structure.ALIGN_NONE;
        TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        Map TEST_OPTS = new HashMap() { {
            put(OPTION_TYPE_MAPPER, TEST_MAPPER);
            put(OPTION_STRUCTURE_ALIGNMENT, new Integer(TEST_ALIGNMENT));
        }};
        TestInterfaceWithInstance ARBITRARY = (TestInterfaceWithInstance) 
            Native.loadLibrary("testlib", TestInterfaceWithInstance.class, TEST_OPTS);
    }
    public void testOptionsInferenceFromInstanceField() {
        assertEquals("Wrong options found for interface which provides an instance", 
                     TestInterfaceWithInstance.TEST_OPTS,
                     Native.getLibraryOptions(TestInterfaceWithInstance.class));
        assertEquals("Wrong type mapper found", 
                     TestInterfaceWithInstance.TEST_MAPPER,
                     Native.getTypeMapper(TestInterfaceWithInstance.class));
        assertEquals("Wrong alignment found", 
                     TestInterfaceWithInstance.TEST_ALIGNMENT,
                     Native.getStructureAlignment(TestInterfaceWithInstance.class));
    }
    
    public interface TestInterfaceWithOptions extends Library {
        int TEST_ALIGNMENT = Structure.ALIGN_NONE;
        TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        Map OPTIONS = new HashMap() { {
            put(OPTION_TYPE_MAPPER, TEST_MAPPER);
            put(OPTION_STRUCTURE_ALIGNMENT, new Integer(TEST_ALIGNMENT));
        }};
    }
    public void testOptionsInferenceFromOptionsField() {
        assertEquals("Wrong options found for interface which provides OPTIONS", 
                     TestInterfaceWithOptions.OPTIONS,
                     Native.getLibraryOptions(TestInterfaceWithOptions.class));
        assertEquals("Wrong type mapper found", 
                     TestInterfaceWithOptions.TEST_MAPPER,
                     Native.getTypeMapper(TestInterfaceWithOptions.class));
        assertEquals("Wrong alignment found", 
                     TestInterfaceWithOptions.TEST_ALIGNMENT,
                     Native.getStructureAlignment(TestInterfaceWithOptions.class));
    }

    public interface TestInterfaceWithTypeMapper extends Library {
        TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        TypeMapper TYPE_MAPPER = TEST_MAPPER;
    }
    public void testOptionsInferenceFromTypeMapperField() {
        assertEquals("Wrong type mapper found for interface which provides TYPE_MAPPER", 
                     TestInterfaceWithTypeMapper.TEST_MAPPER,
                     Native.getTypeMapper(TestInterfaceWithTypeMapper.class));
    }

    public interface TestInterfaceWithAlignment extends Library {
        int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE;
    }
    public void testOptionsInferenceFromAlignmentField() {
        assertEquals("Wrong alignment found for interface which provides STRUCTURE_ALIGNMENT", 
                     Structure.ALIGN_NONE,
                     Native.getStructureAlignment(TestInterfaceWithAlignment.class));
    }

    // TODO extract (alignment|typemapper) 
    // from (variable|options)

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NativeTest.class);
    }
}
