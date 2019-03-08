/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
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

    public void testLoadLibraryMethods() throws Exception {
        Class<?>[][] params = {
                { Class.class },
                { Class.class, Map.class },
                { String.class, Class.class },
                { String.class, Class.class, Map.class }
        };

        StringBuilder signature = new StringBuilder(Long.SIZE);
        for (Class<?>[] paramTypes : params) {
            signature.setLength(0);
            signature.append('(');
            for (Class<?> p : paramTypes) {
                signature.append(Native.getSignature(p));
            }
            signature.append(')');

            try {
                Method m = Native.class.getMethod("load", paramTypes);
                Class<?> returnType = m.getReturnType();
                signature.append(Native.getSignature(returnType));
                assertSame("Mismatched return type for signature=" + signature, Library.class, returnType);
//                System.out.println("===>" + m.getName() + ": " + signature);
            } catch(NoSuchMethodError err) {
                fail("No method for signature=" + signature);
            }
        }
    }

    public void testLongStringGeneration() {
        StringBuilder buf = new StringBuilder();
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

    public void testToStringList() {
        List<String> expected = Arrays.asList(getClass().getPackage().getName(), getClass().getSimpleName(), "testToStringList");
        StringBuilder sb = new StringBuilder();
        for (String value : expected) {
            sb.append(value).append('\0');
        }
        sb.append('\0');

        List<String> actual = Native.toStringList(sb.toString().toCharArray());
        assertEquals("Mismatched result size", expected.size(), actual.size());
        for (int index = 0; index < expected.size(); index++) {
            String expValue = expected.get(index);
            String actValue = actual.get(index);
            assertEquals("Mismatched value at index #" + index, expValue, actValue);
        }
    }

    public void testDefaultStringEncoding() throws Exception {
        final String UNICODE = "\u0444\u043b\u0441\u0432\u0443";
        final String UNICODEZ = UNICODE + "\0more stuff";
        byte[] nativeEnc = Native.getBytes(UNICODE);
        byte[] expected = UNICODE.getBytes(Native.DEFAULT_ENCODING);
        for (int i=0;i < Math.min(nativeEnc.length, expected.length);i++) {
            assertEquals("Improperly encoded at " + i,
                         expected[i], nativeEnc[i]);
        }
        assertEquals("Wrong number of encoded characters", expected.length, nativeEnc.length);
        String result = Native.toString(nativeEnc);
        // The native encoding might not support our test string; the result
        // will then be all '?'
        if (!result.matches("^\\?+$")) {
            assertEquals("Improperly decoded", UNICODE, result);
        }
        // When the native encoding doesn't support our test string, we can only
        // usefully compare the lengths.
        assertEquals("Should truncate bytes at NUL terminator",
                UNICODE.length(), Native.toString(UNICODEZ.getBytes(Native.DEFAULT_ENCODING)).length());
    }

    public void testCustomizeDefaultStringEncoding() {
        Properties oldprops = (Properties)System.getProperties().clone();
        String encoding = null;
        // Choose a charset that is not the default encoding so we can actually
        // tell we changed it.
        for (String charset : Charset.availableCharsets().keySet()) {
            if (!charset.equals(Native.DEFAULT_ENCODING)) {
                encoding = charset;
                break;
            }
        }
        assertNotNull("No available encodings other than the default!?", encoding);
        try {
            System.setProperty("jna.encoding", encoding);
            assertEquals("Default encoding should match jna.encoding setting", encoding, Native.getDefaultStringEncoding());
        }
        finally {
            System.setProperties(oldprops);
        }
    }

    public void testSizeof() {
        assertEquals("Wrong bool size", 1, Native.BOOL_SIZE);
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
        final TestLib lib = Native.load("testlib", TestLib.class);
        final TestLib synchlib = (TestLib)Native.synchronizedLibrary(lib);
        final TestLib.VoidCallback cb = new TestLib.VoidCallback() {
            @Override
            public void callback() {
                lockHeld[0] = Thread.holdsLock(nlib);
            }
        };
        Thread t0 = new Thread() {
            @Override
            public void run() {
                lib.callVoidCallback(cb);
            }
        };
        t0.start();
        t0.join();
        assertFalse("NativeLibrary lock should not be held during native call to normal library",
                   lockHeld[0]);

        Thread t1 = new Thread() {
            @Override
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
            @Override
            protected List<String> getFieldOrder() {
                return Collections.<String>emptyList();
            }
        }
    }

    public void testFindInterfaceClass() throws Exception {
        Class<?> interfaceClass = TestInterface.class;
        Class<?> cls = TestInterface.InnerTestClass.class;
        Class<?> subClass = TestInterface.InnerTestClass.InnerSubclass.class;
        Class<?> callbackClass = TestInterface.InnerTestClass.TestCallback.class;
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
        Map<String, Object> TEST_OPTS = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;    // we're not serializing it

            {
                put(OPTION_CLASSLOADER, TestInterfaceWithInstance.class.getClassLoader());
                put(OPTION_TYPE_MAPPER, TEST_MAPPER);
                put(OPTION_STRUCTURE_ALIGNMENT, Integer.valueOf(TEST_ALIGNMENT));
                put(OPTION_STRING_ENCODING, TEST_ENCODING);
            }
        };
        TestInterfaceWithInstance ARBITRARY = Native.load("testlib", TestInterfaceWithInstance.class, TEST_OPTS);
        abstract class TestStructure extends Structure {}
    }
    public void testOptionsInferenceFromInstanceField() {
        Class<?>[] classes = { TestInterfaceWithInstance.class, TestInterfaceWithInstance.TestStructure.class };
        String[] desc = { "interface", "structure from interface" };
        for (int i=0;i < classes.length;i++) {
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
        Map<String, Object> OPTIONS = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;    // we're not serializing it

            {
                put(OPTION_TYPE_MAPPER, TEST_MAPPER);
                put(OPTION_STRUCTURE_ALIGNMENT, Integer.valueOf(TEST_ALIGNMENT));
                put(OPTION_STRING_ENCODING, TEST_ENCODING);
            }
        };
        abstract class TestStructure extends Structure {}
    }
    public void testOptionsInferenceFromOptionsField() {
        Class<?>[] classes = { TestInterfaceWithOptions.class, TestInterfaceWithOptions.TestStructure.class };
        for (Class<?> cls : classes) {
            assertEquals("Wrong type mapper found", TestInterfaceWithOptions.TEST_MAPPER, Native.getTypeMapper(cls));
            assertEquals("Wrong alignment found", TestInterfaceWithOptions.TEST_ALIGNMENT, Native.getStructureAlignment(cls));
            assertEquals("Wrong encoding found", TestInterfaceWithOptions.TEST_ENCODING, Native.getStringEncoding(cls));
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

    public interface OptionsBase extends Library {
        int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE;
        TypeMapper TYPE_MAPPER = new DefaultTypeMapper();
        class TypeMappedStructure extends Structure {
            public String stringField;
            @Override
            protected List <String>getFieldOrder() {
                return Arrays.asList("stringField");
            }
        }
    }
    public interface OptionsSubclass extends OptionsBase, Library {
        TypeMapper _MAPPER = new DefaultTypeMapper();
        Map<String, ?> _OPTIONS = Collections.singletonMap(Library.OPTION_TYPE_MAPPER, _MAPPER);
        OptionsSubclass INSTANCE = Native.load("testlib", OptionsSubclass.class, _OPTIONS);
    }
    public void testStructureOptionsInference() {
        Structure s = new OptionsBase.TypeMappedStructure();
        assertEquals("Wrong structure alignment for base structure",
                     Structure.ALIGN_NONE, Native.getStructureAlignment(s.getClass()));
        assertEquals("Wrong type mapper for base structure", OptionsBase.TYPE_MAPPER, s.getTypeMapper());
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

    public void testToByteArrayWithCharset() throws Exception {
        final Charset CHARSET = Charset.forName("UTF-8");
        final String VALUE = getName() + UNICODE;
        byte[] buf = Native.toByteArray(VALUE, CHARSET);
        assertEquals("Wrong byte array length", VALUE.getBytes(CHARSET).length+1, buf.length);
        assertEquals("Missing NUL terminator", (byte)0, buf[buf.length-1]);
        assertEquals("Wrong byte array contents", VALUE, new String(buf, 0, buf.length-1, CHARSET));
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

    public void testStringConversionWithCharset() throws Exception {
        final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
        byte[] buf = (getName() + UNICODE + NUL).getBytes(CHARSET_UTF8);
        assertEquals("Encoded C string improperly converted", getName() + UNICODE, Native.toString(buf, CHARSET_UTF8));
    }

    public void testWideStringConversion() {
        char[] buf = (getName() + NUL).toCharArray();
        assertEquals("Wide C string improperly converted", getName(), Native.toString(buf));
    }

    public void testGetBytes() throws Exception {
        byte[] buf = Native.getBytes(getName() + UNICODE, "utf8");
        assertEquals("Incorrect native bytes from Java String", getName() + UNICODE, new String(buf, "utf8"));
    }

    public void testGetBytesWithCharset() throws Exception {
        final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
        byte[] buf = Native.getBytes(getName() + UNICODE, CHARSET_UTF8);
        assertEquals("Incorrect native bytes from Java String", getName() + UNICODE, new String(buf, CHARSET_UTF8));
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
                    junit.textui.TestRunner.run((Class<? extends TestCase>) Class.forName(args[i]));
                } catch(Throwable e) {
                    e.printStackTrace();
                }
            }
            try { Thread.sleep(300000); } catch(Exception e) { }
        }
    }

    public void testVersionComparison() {
        assertTrue("Equal version", Native.isCompatibleVersion("5.1.0", "5.1.0"));
        assertTrue("New revision", Native.isCompatibleVersion("5.2.0", "5.2.1"));
        assertTrue("New minor provided, older minor expected", Native.isCompatibleVersion("5.1.0", "5.10.0"));
        assertFalse("Old minor provided, new minor expected", Native.isCompatibleVersion("5.10.0", "5.1.0"));
        assertFalse("Different major (expected < provided)", Native.isCompatibleVersion("4.0.0", "5.0.0"));
        assertFalse("Different major (expected > provided)", Native.isCompatibleVersion("5.0.0", "4.0.0"));
    }
}
