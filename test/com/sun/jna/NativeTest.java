/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class NativeTest extends TestCase {
    
    private static final String UNICODE = "[\u0444]";

    public void testLongStringGeneration() {
        StringBuffer buf = new StringBuffer();
        final int MAX = Platform.isWindowsCE() ? 200000 : 2000000;
        for (int i=0;i < MAX;i++) {
            buf.append('a');
        }
        String s1 = buf.toString();
        Memory m = new Memory((MAX + 1)*Native.WCHAR_SIZE);
        m.setWideString(0, s1);
        assertEquals("Missing terminator after write", 0, m.getChar(MAX*Native.WCHAR_SIZE));
        String s2 = m.getWideString(0);
        assertEquals("Wrong string read length", s1.length(), s2.length());
        assertEquals("Improper wide string read", s1, s2);
    }

    public void testCustomStringEncoding() throws Exception {
        final String ENCODING = System.getProperty("file.encoding");
        // Keep stuff within the extended ASCII range so we work with more
        // limited native encodings
        String UNICODE = "Un \u00e9l\u00e9ment gr\u00e2ce \u00e0 l'index";
        
        if (!UNICODE.equals(new String(UNICODE.getBytes()))) {
            // If the extended characters aren't encodable in the default 
            // encoding, punt and use straight ASCII
            UNICODE = "";
            for (char ch=1;ch < 128;ch++) {
                UNICODE += ch;
            }
        }
        final String UNICODEZ = UNICODE + "\0more stuff";
        
        byte[] customEncoded = Native.getBytes(UNICODE, ENCODING);
        byte[] expected = UNICODE.getBytes(ENCODING);
        for (int i=0;i < Math.min(customEncoded.length, expected.length);i++) {
            assertEquals("Improperly encoded (" + ENCODING + ") from Java at " + i, 
                         expected[i], customEncoded[i]);
        }
        assertEquals("Wrong number of encoded characters (" + ENCODING + ")", 
                     expected.length, customEncoded.length);
        String result = Native.toString(customEncoded, ENCODING);
        assertEquals("Improperly decoded from native bytes (" + ENCODING + ")", 
                     UNICODE, result);
        
        assertEquals("Should truncate bytes at NUL terminator",
                     UNICODE, Native.toString(UNICODEZ.getBytes(ENCODING), ENCODING));
    }
    
    public void testDefaultStringEncoding() throws Exception {
        final String UNICODE = "\u0444\u043b\u0441\u0432\u0443";
        final String UNICODEZ = UNICODE + "\0more stuff";
        byte[] utf8 = Native.getBytes(UNICODE);
        byte[] expected = UNICODE.getBytes(Native.DEFAULT_ENCODING);
        for (int i=0;i < Math.min(utf8.length, expected.length);i++) {
            assertEquals("Improperly encoded at " + i, 
                         expected[i], utf8[i]);
        }
        assertEquals("Wrong number of encoded characters", expected.length, utf8.length);
        String result = Native.toString(utf8);
        assertEquals("Improperly decoded", UNICODE, result);
        
        assertEquals("Should truncate bytes at NUL terminator",
                     UNICODE, Native.toString(UNICODEZ.getBytes(Native.DEFAULT_ENCODING)));
    }
    
    public void testCustomizeDefaultStringEncoding() {
        Properties oldprops = (Properties)System.getProperties().clone();
        final String ENCODING = System.getProperty("file.encoding");
        try {
            System.setProperty("jna.encoding", ENCODING);
            assertEquals("Default encoding should match jna.encoding setting", ENCODING, Native.getDefaultStringEncoding());
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
        final NativeLibrary nlib = NativeLibrary.getInstance("testlib", TestLib.class.getClassLoader());
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
            protected List getFieldOrder() { 
                return Collections.EMPTY_LIST;
            }
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
        String TEST_ENCODING = "test-encoding";
        Map TEST_OPTS = new HashMap() { {
            put(OPTION_CLASSLOADER, TestInterfaceWithInstance.class.getClassLoader());
            put(OPTION_TYPE_MAPPER, TEST_MAPPER);
            put(OPTION_STRUCTURE_ALIGNMENT, new Integer(TEST_ALIGNMENT));
            put(OPTION_STRING_ENCODING, TEST_ENCODING);
        }};
        TestInterfaceWithInstance ARBITRARY = (TestInterfaceWithInstance) 
            Native.loadLibrary("testlib", TestInterfaceWithInstance.class, TEST_OPTS);
        abstract class TestStructure extends Structure {}
    }
    public void testOptionsInferenceFromInstanceField() {
        Class[] classes = { TestInterfaceWithInstance.class, TestInterfaceWithInstance.TestStructure.class };
        String[] desc = { "interface", "structure from interface" };
        for (int i=0;i < classes.length;i++) {
            assertEquals("Wrong options found for " + desc[i]
                         + " which provides an instance", 
                         TestInterfaceWithInstance.TEST_OPTS,
                         Native.getLibraryOptions(classes[i]));
            assertEquals("Wrong type mapper found for " + desc[i], 
                         TestInterfaceWithInstance.TEST_MAPPER,
                         Native.getTypeMapper(classes[i]));
            assertEquals("Wrong alignment found for " + desc[i], 
                         TestInterfaceWithInstance.TEST_ALIGNMENT,
                         Native.getStructureAlignment(classes[i]));
            assertEquals("Wrong string encoding found for " + desc[i], 
                         TestInterfaceWithInstance.TEST_ENCODING,
                         Native.getStringEncoding(classes[i]));
        }
    }
    
    public interface TestInterfaceWithOptions extends Library {
        int TEST_ALIGNMENT = Structure.ALIGN_NONE;
        TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        String TEST_ENCODING = "test-encoding";
        Map OPTIONS = new HashMap() { {
            put(OPTION_TYPE_MAPPER, TEST_MAPPER);
            put(OPTION_STRUCTURE_ALIGNMENT, new Integer(TEST_ALIGNMENT));
            put(OPTION_STRING_ENCODING, TEST_ENCODING);
        }};
        abstract class TestStructure extends Structure {}
    }
    public void testOptionsInferenceFromOptionsField() {
        Class[] classes = { TestInterfaceWithOptions.class, TestInterfaceWithOptions.TestStructure.class };
        for (int i=0;i < classes.length;i++) {
            assertEquals("Wrong options found for interface which provides OPTIONS", 
                         TestInterfaceWithOptions.OPTIONS,
                         Native.getLibraryOptions(classes[i]));
            assertEquals("Wrong type mapper found", 
                         TestInterfaceWithOptions.TEST_MAPPER,
                         Native.getTypeMapper(classes[i]));
            assertEquals("Wrong alignment found", 
                         TestInterfaceWithOptions.TEST_ALIGNMENT,
                         Native.getStructureAlignment(classes[i]));
            assertEquals("Wrong encoding found", 
                         TestInterfaceWithOptions.TEST_ENCODING,
                         Native.getStringEncoding(classes[i]));
        }
    }

    public interface TestInterfaceWithTypeMapper extends Library {
        TypeMapper TEST_MAPPER = new DefaultTypeMapper();
        TypeMapper TYPE_MAPPER = TEST_MAPPER;
        abstract class TestStructure extends Structure { }
    }
    public void testOptionsInferenceFromTypeMapperField() {
        assertEquals("Wrong type mapper found for interface which provides TYPE_MAPPER", 
                     TestInterfaceWithTypeMapper.TEST_MAPPER,
                     Native.getTypeMapper(TestInterfaceWithTypeMapper.class));
        assertEquals("Wrong type mapper found for structure from interface which provides TYPE_MAPPER", 
                     TestInterfaceWithTypeMapper.TEST_MAPPER,
                     Native.getTypeMapper(TestInterfaceWithTypeMapper.TestStructure.class));
    }

    public interface TestInterfaceWithAlignment extends Library {
        int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE;
        abstract class TestStructure extends Structure { }
    }
    public void testOptionsInferenceFromAlignmentField() {
        assertEquals("Wrong alignment found for interface which provides STRUCTURE_ALIGNMENT", 
                     Structure.ALIGN_NONE,
                     Native.getStructureAlignment(TestInterfaceWithAlignment.class));
        assertEquals("Wrong alignment found for structure from interface which provides STRUCTURE_ALIGNMENT", 
                     Structure.ALIGN_NONE,
                     Native.getStructureAlignment(TestInterfaceWithAlignment.TestStructure.class));
    }

    public interface TestInterfaceWithEncoding extends Library {
        String STRING_ENCODING = "test-encoding";
        abstract class TestStructure extends Structure { }
    }
    public void testOptionsInferenceFromEncodingField() {
        assertEquals("Wrong encoding found for interface which provides STRING_ENCODING", 
                     TestInterfaceWithEncoding.STRING_ENCODING,
                     Native.getStringEncoding(TestInterfaceWithEncoding.class));
        assertEquals("Wrong encoding found for structure from interface which provides STRING_ENCODING", 
                     TestInterfaceWithEncoding.STRING_ENCODING,
                     Native.getStringEncoding(TestInterfaceWithEncoding.TestStructure.class));
    }

    public void testCharArrayToString() {
        char[] buf = { 'a', 'b', 'c', '\0', 'd', 'e' };
        assertEquals("Wrong String generated", "abc", Native.toString(buf));
    }

    public void testByteArrayToString() {
        byte[] buf = { 'a', 'b', 'c', '\0', 'd', 'e' };
        assertEquals("Wrong String generated", "abc", Native.toString(buf));
    }
    
    @Test
    public final void shouldConvertSequenceToStrings() {
        // given
        final char[] buffer = "ABC\0DEF\0GHI\0\0".toCharArray();

        // when
        final String[] strings = /* Native */N.toStrings(buffer);

        // then
        assertThat(strings, is(new String[] { "ABC", "DEF", "GHI" }));
    }

    public void testToByteArray() {
        final String VALUE = getName();
        byte[] buf = Native.toByteArray(VALUE);
        assertEquals("Wrong byte array length", VALUE.getBytes().length+1, buf.length);
        assertEquals("Missing NUL terminator", (byte)0, buf[buf.length-1]);
        assertEquals("Wrong byte array contents", VALUE, new String(buf, 0, buf.length-1));
    }

    public void testToByteArrayWithEncoding() throws Exception {
        final String ENCODING = "utf8";
        final String VALUE = getName() + UNICODE;
        byte[] buf = Native.toByteArray(VALUE, ENCODING);
        assertEquals("Wrong byte array length", VALUE.getBytes(ENCODING).length+1, buf.length);
        assertEquals("Missing NUL terminator", (byte)0, buf[buf.length-1]);
        assertEquals("Wrong byte array contents", VALUE, new String(buf, 0, buf.length-1, ENCODING));
    }

    public void testToCharArray() {
        final String VALUE = getName() + UNICODE;
        char[] buf = Native.toCharArray(VALUE);
        assertEquals("Wrong char array length", VALUE.length()+1, buf.length);
        assertEquals("Missing NUL terminator", (char)0, buf[buf.length-1]);
        assertEquals("Wrong char array contents: " + new String(buf), VALUE, new String(buf, 0, buf.length-1));
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

    public void testStringReplace() {
        assertEquals("Bad replace", "abcdefg", Native.replace("z", "a", "zbcdefg"));
        assertEquals("Bad replace", "abcdefg", Native.replace("z", "g", "abcdefz"));
        assertEquals("Bad replace", "abcdefg", Native.replace("z", "d", "abczefg"));
        assertEquals("Bad replace", "abcaefa", Native.replace("z", "a", "zbczefz"));
    }

    public void testRemoveTemporaries() throws Exception {
        File dir = Native.getTempDir();
        File tmp = new File(dir, Native.JNA_TMPLIB_PREFIX);
        tmp.delete();
        try {
            assertTrue("Couldn't create temporary file " + tmp, tmp.createNewFile());
            assertTrue("File isn't recognized as unpacked", Native.isUnpacked(tmp));
            Native.markTemporaryFile(tmp);
            Native.removeTemporaryFiles();
            assertFalse("Temporary file still exists", tmp.exists());
        }
        finally {
            tmp.delete();
        }
    }

    private static final String NUL = "\0";
    public void testStringConversion() {
        byte[] buf = (getName() + NUL).getBytes();
        assertEquals("C string improperly converted", getName(), Native.toString(buf));
    }

    public void testStringConversionWithEncoding() throws Exception {
        byte[] buf = (getName() + UNICODE + NUL).getBytes("utf8");
        assertEquals("Encoded C string improperly converted", getName() + UNICODE, Native.toString(buf, "utf8"));
    }

    public void testWideStringConversion() {
        char[] buf = (getName() + NUL).toCharArray();
        assertEquals("Wide C string improperly converted", getName(), Native.toString(buf));
    }

    public void testGetBytes() throws Exception {
        byte[] buf = Native.getBytes(getName() + UNICODE, "utf8");
        assertEquals("Incorrect native bytes from Java String", getName() + UNICODE, new String(buf, "utf8"));
    }

    public void testGetBytesBadEncoding() throws Exception {
        byte[] buf = Native.getBytes(getName(), "unsupported");
        assertEquals("Incorrect fallback bytes with bad encoding",
                     getName(), new String(buf, System.getProperty("file.encoding")));
    }

    public void testFindDirectMappedClassFailure() {
        try {
            Native.findDirectMappedClass(NativeTest.class);
            fail("Expect an exception if native-mapped class can't be found");
        }
        catch(IllegalArgumentException e) {
        }
    }

    /** This method facilitates running tests from a single entry point
        outside of ant (i.e. for androide, WCE, etc.).
    */
    public static void main(String[] args) {
        if (args.length == 0) {
            junit.textui.TestRunner.run(NativeTest.class);
        }
        else {
            if (args.length == 1 && "all".equals(args[0])) {
                args = new String[] {
                    "com.sun.jna.NativeTest",
                    "com.sun.jna.NativeLibraryTest",
                    "com.sun.jna.PointerTest",
                    "com.sun.jna.MemoryTest",
                    "com.sun.jna.LibraryLoadTest", 
                    "com.sun.jna.ArgumentsMarshalTest",
                    "com.sun.jna.ReturnTypesTest",
                    "com.sun.jna.TypeMapperTest", 
                    "com.sun.jna.ByReferenceArgumentsTest",
                    "com.sun.jna.LastErrorTest", 
                    "com.sun.jna.StructureTest",// 1 wce failure (RO fields)
                    "com.sun.jna.StructureByValueTest",
                    "com.sun.jna.UnionTest",
                    "com.sun.jna.IntegerTypeTest", 
                    "com.sun.jna.VMCrashProtectionTest",
                    "com.sun.jna.CallbacksTest", 
                    "com.sun.jna.JNAUnloadTest",
                    "com.sun.jna.DirectTest",
                    "com.sun.jna.DirectArgumentsMarshalTest",
                    "com.sun.jna.DirectByReferenceArgumentsTest",
                    "com.sun.jna.DirectTypeMapperTest",
                    "com.sun.jna.DirectReturnTypesTest",
                    "com.sun.jna.DirectStructureByValueTest",
                    "com.sun.jna.DirectCallbacksTest",
                };
            }
            System.out.println("Test suites: " + args.length);
            for (int i=0;i < args.length;i++) {
                System.out.println("Running tests on class " + args[i]);
                try {
                    junit.textui.TestRunner.run(Class.forName(args[i]));
                }
                catch(Throwable e) {
                    e.printStackTrace();
                }
            }
            try { Thread.sleep(300000); } catch(Exception e) { }
        }
    }
}
