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
                     interfaceClass, Native.findEnclosingLibraryClass(cls));
        assertEquals("Enclosing interface not found for derived class",
                     interfaceClass, Native.findEnclosingLibraryClass(subClass));
        assertEquals("Enclosing interface not found for callback",
                     interfaceClass, Native.findEnclosingLibraryClass(callbackClass));
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

    public void testCharArrayToString() {
        char[] buf = { 'a', 'b', 'c', '\0', 'd', 'e' };
        assertEquals("Wrong String generated", "abc", Native.toString(buf));
    }

    public void testByteArrayToString() {
        byte[] buf = { 'a', 'b', 'c', '\0', 'd', 'e' };
        assertEquals("Wrong String generated", "abc", Native.toString(buf));
    }

    public void testToByteArray() {
        final String VALUE = getName();
        byte[] buf = Native.toByteArray(VALUE);
        assertEquals("Wrong byte array length", VALUE.length()+1, buf.length);
        assertEquals("Missing NUL terminator", (byte)0, buf[buf.length-1]);
        assertEquals("Wrong byte array contents", VALUE, new String(buf, 0, buf.length-1));
    }

    public void testToCharArray() {
        final String VALUE = getName();
        char[] buf = Native.toCharArray(VALUE);
        assertEquals("Wrong char array length", VALUE.length()+1, buf.length);
        assertEquals("Missing NUL terminator", (char)0, buf[buf.length-1]);
        assertEquals("Wrong char array contents: " + new String(buf), VALUE, new String(buf, 0, buf.length-1));
    }

    public void testOSPrefix() {
        assertEquals("Wrong resource path", "/com/sun/jna/win32-x86",
                     Native.getNativeLibraryResourcePath(Platform.WINDOWS,
                                                         "x86", "Windows"));
        assertEquals("Wrong resource path Windows/i386", "/com/sun/jna/win32-x86",
                     Native.getNativeLibraryResourcePath(Platform.WINDOWS,
                                                         "i386", "Windows"));
        assertEquals("Wrong resource path Mac/x86", "/com/sun/jna/darwin",
                     Native.getNativeLibraryResourcePath(Platform.MAC,
                                                         "x86", "Darwin"));
        assertEquals("Wrong resource path Mac/x86_64", "/com/sun/jna/darwin",
                     Native.getNativeLibraryResourcePath(Platform.MAC,
                                                         "x86_64", "Mac"));
        assertEquals("Wrong resource path Solaris/sparc", "/com/sun/jna/sunos-sparc",
                     Native.getNativeLibraryResourcePath(Platform.SOLARIS,
                                                         "sparc", "Solaris"));
        assertEquals("Wrong resource path SunOS/sparcv9", "/com/sun/jna/sunos-sparcv9",
                     Native.getNativeLibraryResourcePath(Platform.SOLARIS,
                                                         "sparcv9", "SunOS"));
        assertEquals("Wrong resource path Linux/i386", "/com/sun/jna/linux-i386",
                     Native.getNativeLibraryResourcePath(Platform.LINUX,
                                                         "i386", "Linux/Gnu"));
        assertEquals("Wrong resource path Linux/x86", "/com/sun/jna/linux-i386",
                     Native.getNativeLibraryResourcePath(Platform.LINUX,
                                                         "x86", "Linux"));
        assertEquals("Wrong resource path OpenBSD/x86", "/com/sun/jna/openbsd-i386",
                     Native.getNativeLibraryResourcePath(Platform.OPENBSD,
                                                         "x86", "OpenBSD"));
        assertEquals("Wrong resource path FreeBSD/x86", "/com/sun/jna/freebsd-i386",
                     Native.getNativeLibraryResourcePath(Platform.FREEBSD,
                                                         "x86", "FreeBSD"));
        assertEquals("Wrong resource path other/other", "/com/sun/jna/name-ppc",
                     Native.getNativeLibraryResourcePath(Platform.UNSPECIFIED,
                                                         "PowerPC", "Name Of System"));

    }

    public static class DirectMapping {
        public static class DirectStructure extends Structure {
            public int field;
        }
        public static interface DirectCallback extends Callback {
            void invoke();
        }
        public DirectMapping(Map options) {
            Native.register(getClass(), NativeLibrary.getInstance("testlib", options));
        }
    }

    public void testGetTypeMapperForDirectMapping() {
        final TypeMapper mapper = new DefaultTypeMapper();
        Map options = new HashMap();
        options.put(Library.OPTION_TYPE_MAPPER, mapper);
        DirectMapping lib = new DirectMapping(options);
        assertEquals("Wrong type mapper for direct mapping",
                     mapper, Native.getTypeMapper(DirectMapping.class));
        assertEquals("Wrong type mapper for direct mapping nested structure",
                     mapper, Native.getTypeMapper(DirectMapping.DirectStructure.class));
        assertEquals("Wrong type mapper for direct mapping nested callback",
                     mapper, Native.getTypeMapper(DirectMapping.DirectCallback.class));
    }

    private static class TestCallback implements Callback {
        public static final TypeMapper TYPE_MAPPER = new DefaultTypeMapper();
        public void callback() { }
    }
    public void testGetTypeMapperFromCallbackInterface() throws Exception {
        assertEquals("Wrong type mapper for callback class",
                     TestCallback.TYPE_MAPPER,
                     Native.getTypeMapper(TestCallback.class));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NativeTest.class);
    }
}
