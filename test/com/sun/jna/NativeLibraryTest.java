/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import com.sun.jna.win32.W32APIOptions;

import junit.framework.TestCase;

public class NativeLibraryTest extends TestCase {

    public static interface TestLibrary extends Library {
        int callCount();
    }

    public void testMapSharedLibraryName() {
        final Object[][] MAPPINGS = {
            { Platform.MAC, "lib", ".dylib" },
            { Platform.LINUX, "lib", ".so" },
            { Platform.WINDOWS, "", ".dll" },
            { Platform.SOLARIS, "lib", ".so" },
            { Platform.FREEBSD, "lib", ".so" },
            { Platform.OPENBSD, "lib", ".so" },
            { Platform.WINDOWSCE, "", ".dll" },
            { Platform.AIX, "lib", ".a" },
            { Platform.ANDROID, "lib", ".so" },
            { Platform.GNU, "lib", ".so" },
            { Platform.KFREEBSD, "lib", ".so" },
            { Platform.NETBSD, "lib", ".so" },
        };
        for (int i=0;i < MAPPINGS.length;i++) {
            int osType = ((Integer)MAPPINGS[i][0]).intValue();
            if (osType == Platform.getOSType()) {
                assertEquals("Wrong shared library name mapping",
                             MAPPINGS[i][1] + "testlib" + MAPPINGS[i][2],
                             NativeLibrary.mapSharedLibraryName("testlib"));
            }
        }
    }

    public void testGCNativeLibrary() throws Exception {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        Reference<NativeLibrary> ref = new WeakReference<>(lib);
        lib = null;
        System.gc();
        long start = System.currentTimeMillis();
        while (ref.get() != null) {
            Thread.sleep(10);
            if ((System.currentTimeMillis() - start) > 5000L)
                break;
        }
        assertNull("Library not GC'd", ref.get());
    }

    public void testAvoidDuplicateLoads() throws Exception {
        // This test basicly tests whether unloading works. It relies on the
        // runtime to unload the library when dlclose is called. This is not
        // required by POSIX and dt time of writing macOS is known to be flaky
        // in that regard.
        //
        // This test causes false test failures
        if (!Platform.isMac()) {
            NativeLibrary.disposeAll();
            Thread.sleep(2);

            TestLibrary lib = Native.load("testlib", TestLibrary.class);
            assertEquals("Library should be newly loaded after explicit dispose of all native libraries",
                    1, lib.callCount());
            if (lib.callCount() <= 1) {
                fail("Library should not be reloaded without dispose");
            }
        }
    }

    public void testUseSingleLibraryInstance() {
        TestLibrary lib = Native.load("testlib", TestLibrary.class);
        int count = lib.callCount();
        TestLibrary lib2 = Native.load("testlib", TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Interfaces should share a library instance",
                     count + 1, count2);
    }

    public void testAliasLibraryFilename() {
        TestLibrary lib = Native.load("testlib", TestLibrary.class);
        int count = lib.callCount();
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        TestLibrary lib2 = Native.load(nl.getFile().getName(), TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Simple filename load not aliased", count + 1, count2);
    }

    public void testAliasLibraryFullPath() {
        TestLibrary lib = Native.load("testlib", TestLibrary.class);
        int count = lib.callCount();
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        TestLibrary lib2 = Native.load(nl.getFile().getAbsolutePath(), TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Full pathname load not aliased", count + 1, count2);
    }

    public void testAliasSimpleLibraryName() throws Exception {
        NativeLibrary nl = NativeLibrary.getInstance("testlib");
        File file = nl.getFile();
        Reference<NativeLibrary> ref = new WeakReference<>(nl);
        nl = null;
        System.gc();
        long start = System.currentTimeMillis();
        while (ref.get() != null) {
            Thread.sleep(10);
            if ((System.currentTimeMillis() - start) > 5000L) {
                fail("Timed out waiting for library to be GC'd");
            }
        }
        TestLibrary lib = Native.load(file.getAbsolutePath(), TestLibrary.class);
        int count = lib.callCount();
        TestLibrary lib2 = Native.load("testlib", TestLibrary.class);
        int count2 = lib2.callCount();
        assertEquals("Simple library name not aliased", count + 1, count2);
    }

    public void testRejectNullFunctionName() {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        try {
            Function f = lib.getFunction(null);
            fail("Function must have a name: " + f);
        } catch(NullPointerException e) {
            // expected
        }
    }

    public void testIncludeSymbolNameInLookupError() {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        try {
            lib.getGlobalVariableAddress(getName());
            fail("Non-existent global variable lookup should fail");
        }
        catch(UnsatisfiedLinkError e) {
            assertTrue("Expect symbol name in error message: " + e.getMessage(), e.getMessage().indexOf(getName()) != -1);
        }
    }

    public void testFunctionHoldsLibraryReference() throws Exception {
        NativeLibrary lib = NativeLibrary.getInstance("testlib");
        Reference<NativeLibrary> ref = new WeakReference<>(lib);
        Function f = lib.getFunction("callCount");
        lib = null;
        System.gc();
        for (long start = System.currentTimeMillis(); (ref.get() != null) && ((System.currentTimeMillis() - start) < 2000L); ) {
            Thread.sleep(10);
        }
        assertNotNull("Library GC'd when it should not be", ref.get());
        f.invokeInt(new Object[0]);
        f = null;
        System.gc();
        for (long start = System.currentTimeMillis(); (ref.get() != null) && ((System.currentTimeMillis() - start) < 5000L); ) {
            Thread.sleep(10);
        }
        assertNull("Library not GC'd", ref.get());
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
        assertEquals("Latest versioned library not found when unversioned requested for path=" + dir,
                lib1_1.getCanonicalPath(),
                NativeLibrary.matchLibrary(name, Collections.singletonList(dir.getCanonicalPath())));
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
        assertEquals("Library with similar prefix should be ignored for path=" + dir,
                lib0.getCanonicalPath(),
                NativeLibrary.matchLibrary(name, Collections.singletonList(dir.getCanonicalPath())));
    }

    public void testParseVersion() throws Exception {
        String[] VERSIONS = {
            "1",
            "1.2",
            "1.2.3",
            "1.2.3.4",};
        double[] EXPECTED = {
            1, 1.02, 1.0203, 1.020304,};
        for (int i = 0; i < VERSIONS.length; i++) {
            assertEquals("Badly parsed version", EXPECTED[i], NativeLibrary.parseVersion(VERSIONS[i]), 0.0000001);
        }
    }

    // XFAIL on android
    public void testGetProcess() {
        if (Platform.isAndroid()) {
            fail("dlopen(NULL) segfaults on Android");
        }
        NativeLibrary process = NativeLibrary.getProcess();
        // Access a common C library function
        process.getFunction("printf");
    }

    public void testLoadFoundationFramework() {
        if (!Platform.isMac()) {
            return;
        }
        assertNotNull(NativeLibrary.getInstance("Foundation"));
    }

    public void testMatchSystemFramework() {
        if (!Platform.isMac()) {
            return;
        }

        assertEquals("Wrong framework mapping", 1,
                NativeLibrary.matchFramework("/System/Library/Frameworks/Foundation.framework/Foundation").length);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/Foundation.framework/Foundation",
                NativeLibrary.matchFramework("/System/Library/Frameworks/Foundation.framework/Foundation")[0]);

        assertEquals("Wrong framework mapping", 1,
                NativeLibrary.matchFramework("/System/Library/Frameworks/Foundation").length);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/Foundation.framework/Foundation",
                NativeLibrary.matchFramework("/System/Library/Frameworks/Foundation")[0]);
    }

    public void testMatchOptionalFrameworkExists() {
        if (!Platform.isMac()) {
            return;
        }

        if(!new File("/System/Library/Frameworks/QuickTime.framework").exists()) {
            return;
        }

        assertEquals("Wrong framework mapping", 1,
                NativeLibrary.matchFramework("QuickTime").length);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/QuickTime.framework/QuickTime",
                NativeLibrary.matchFramework("QuickTime")[0]);

        assertEquals("Wrong framework mapping", 1,
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime").length);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/QuickTime.framework/Versions/Current/QuickTime",
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime")[0]);
    }

    public void testMatchOptionalFrameworkNotFound() {
        if (!Platform.isMac()) {
            return;
        }

        if(new File(System.getProperty("user.home") + "/Library/Frameworks/QuickTime.framework").exists()) {
            return;
        }
        if(new File("/Library/Frameworks/QuickTime.framework").exists()) {
            return;
        }
        if(new File("/System/Library/Frameworks/QuickTime.framework").exists()) {
            return;
        }

        assertEquals("Wrong framework mapping", 3,
                NativeLibrary.matchFramework("QuickTime").length);
        assertEquals("Wrong framework mapping", System.getProperty("user.home") + "/Library/Frameworks/QuickTime.framework/QuickTime",
                NativeLibrary.matchFramework("QuickTime")[0]);
        assertEquals("Wrong framework mapping", "/Library/Frameworks/QuickTime.framework/QuickTime",
                NativeLibrary.matchFramework("QuickTime")[1]);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/QuickTime.framework/QuickTime",
                NativeLibrary.matchFramework("QuickTime")[2]);

        assertEquals("Wrong framework mapping", 3,
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime").length);
        assertEquals("Wrong framework mapping", System.getProperty("user.home") + "/Library/Frameworks/QuickTime.framework/Versions/Current/QuickTime",
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime")[0]);
        assertEquals("Wrong framework mapping", "/Library/Frameworks/QuickTime.framework/Versions/Current/QuickTime",
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime")[1]);
        assertEquals("Wrong framework mapping", "/System/Library/Frameworks/QuickTime.framework/Versions/Current/QuickTime",
                NativeLibrary.matchFramework("QuickTime.framework/Versions/Current/QuickTime")[2]);
    }

    public void testLoadLibraryWithOptions() {
        Native.load("testlib", TestLibrary.class, Collections.singletonMap(Library.OPTION_OPEN_FLAGS, Integer.valueOf(-1)));
    }

    public interface Kernel32 {
        int GetLastError();
        void SetLastError(int code);
    }
    public void testInterceptLastError() {
        if (!Platform.isWindows()) {
            return;
        }
        NativeLibrary kernel32 = NativeLibrary.getInstance("kernel32", W32APIOptions.DEFAULT_OPTIONS);
        Function get = kernel32.getFunction("GetLastError");
        Function set = kernel32.getFunction("SetLastError");
        assertEquals("SetLastError should not be customized", Function.class, set.getClass());
        assertTrue("GetLastError should be a Function", Function.class.isAssignableFrom(get.getClass()));
        assertTrue("GetLastError should be a customized Function", get.getClass() != Function.class);
        final int EXPECTED = 42;
        set.invokeVoid(new Object[] { Integer.valueOf(EXPECTED) });
        assertEquals("Wrong error", EXPECTED, get.invokeInt(null));
    }

    public void testCleanupOnLoadError() throws Exception {
        int previousTempFileCount = Native.getTempDir().listFiles().length;
        try {
            NativeLibrary.getInstance("disfunct", Collections.singletonMap(Library.OPTION_CLASSLOADER, new DisfunctClassLoader()));
            fail("Expected NativeLibrary.getInstance() to fail with an UnsatisfiedLinkError here.");
        } catch(UnsatisfiedLinkError e) {
            int currentTempFileCount = Native.getTempDir().listFiles().length;
            assertEquals("Extracted native library should be cleaned up again. Number of files in temp directory:", previousTempFileCount, currentTempFileCount);
        }
    }

    // returns unloadable "shared library" on any input
    private class DisfunctClassLoader extends ClassLoader {
        @Override
        public URL getResource(String name) {
            try {
                return new URL("jar", "", name);
            } catch(MalformedURLException e) {
                fail("Could not even create disfunct library URL: " + e.getMessage());
                return null;
            }
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NativeLibraryTest.class);
    }
}
