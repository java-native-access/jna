/* Copyright (c) 2007-20013 Timothy Wall, All Rights Reserved
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

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.UUID;

import junit.framework.TestCase;

public class LibraryLoadTest extends TestCase implements Paths {

    private class TestLoader extends URLClassLoader {
        public TestLoader(File path) throws MalformedURLException {
            super(new URL[] { path.toURI().toURL(), },
                  new CloverLoader());
        }
    }

    public void testLoadJNALibrary() {
        assertTrue("Pointer size should never be zero", Native.POINTER_SIZE > 0);
    }

    public void testLoadJAWT() {
        if (!Platform.HAS_AWT || !Platform.HAS_JAWT) return;

        if (GraphicsEnvironment.isHeadless()) return;

        // Encapsulate in a separate class to avoid class loading issues where
        // AWT is unavailable
        AWT.loadJAWT(getName());
    }

    public void testLoadAWTAfterJNA() {
        if (!Platform.HAS_AWT) return;

        if (GraphicsEnvironment.isHeadless()) return;

        if (Native.POINTER_SIZE > 0) {
            Toolkit.getDefaultToolkit();
        }
    }

    public void testExtractFromResourcePath() throws Exception {
        // doesn't actually load the resource
        assertNotNull(Native.extractFromResourcePath("testlib-path", new TestLoader(new File(TESTPATH))));
    }

    public void testExtractFromResourcePathWithNullClassLoader() throws Exception {
        // doesn't actually load the resource
        assertNotNull(Native.extractFromResourcePath("/com/sun/jna/LibraryLoadTest.class", null));
    }

    private static final String LIBC_SELECTION_LIBRARY = "jna-libc-selection-test";
    private static final String LIBC_GLIBC_MARKER = "glibc variant";
    private static final String LIBC_MUSL_MARKER = "musl variant";
    private static final String LIBC_LEGACY_MARKER = "legacy variant";

    private File createLibcSelectionClasspath() throws IOException {
        File root = new File(Native.getTempDir(), "jna-libc-selection-" + UUID.randomUUID());

        assertTrue("Could not create test classpath directory", root.mkdirs());

        return root;
    }

    private void writeLibcSelectionResource(File root, String prefix, String marker) throws IOException {
        String libraryName = NativeLibrary.mapSharedLibraryName(LIBC_SELECTION_LIBRARY);
        File resource = new File(new File(root, prefix), libraryName);

        assertTrue("Could not create test resource directory",
                resource.getParentFile().exists() || resource.getParentFile().mkdirs());

        FileOutputStream output = new FileOutputStream(resource);

        try {
            output.write(marker.getBytes("UTF-8"));
        } finally {
            output.close();
        }
    }

    private String readLibcSelectionMarker(File resource) throws IOException {
        FileInputStream input = new FileInputStream(resource);
        try {
            byte[] buffer = new byte[64];
            int length = input.read(buffer);
            return length < 0 ? "" : new String(buffer, 0, length, "UTF-8");
        } finally {
            input.close();
        }
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    private URLClassLoader createLibcSelectionLoader(File root) throws MalformedURLException {
        return new URLClassLoader(new URL[] { root.toURI().toURL() }, new CloverLoader());
    }

    private void assertLibcSelection(File root, Platform.LibC libc, String expectedMarker) throws Exception {
        URLClassLoader loader = createLibcSelectionLoader(root);

        try {
            File extracted = Native.extractFromResourcePath(LIBC_SELECTION_LIBRARY, loader, libc);

            assertEquals(expectedMarker, readLibcSelectionMarker(extracted));
        } finally {
            loader.close();
        }
    }

    private void assertLibcSelectionFails(File root, Platform.LibC libc) throws Exception {
        URLClassLoader loader = createLibcSelectionLoader(root);

        try {
            try {
                Native.extractFromResourcePath(LIBC_SELECTION_LIBRARY, loader, libc);

                fail("Expected IOException when no compatible resource exists for libc " + libc);
            } catch (IOException expected) {
                // No compatible resource is available.
            }
        } finally {
            loader.close();
        }
    }

    public void testExtractPrefersMuslVariant() throws Exception {
        File root = createLibcSelectionClasspath();
        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-musl", LIBC_MUSL_MARKER);

            assertLibcSelection(root, Platform.LibC.MUSL, LIBC_MUSL_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractPrefersGlibcVariant() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-musl", LIBC_MUSL_MARKER);

            assertLibcSelection(root, Platform.LibC.GLIBC, LIBC_GLIBC_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractFallsBackToLegacyResource() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX, LIBC_LEGACY_MARKER);

            assertLibcSelection(root, Platform.LibC.MUSL, LIBC_LEGACY_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractSpecificVariantPrecedesLegacyResource() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-musl", LIBC_MUSL_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX, LIBC_LEGACY_MARKER);

            assertLibcSelection(root, Platform.LibC.MUSL, LIBC_MUSL_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractFallsBackToLegacyWhenRequestedVariantIsMissing() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            // A GLIBC variant must not be selected for a MUSL request.
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX, LIBC_LEGACY_MARKER);

            assertLibcSelection(root, Platform.LibC.MUSL, LIBC_LEGACY_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractUnknownLibcFallsBackToLegacyResource() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-musl", LIBC_MUSL_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX, LIBC_LEGACY_MARKER);

            assertLibcSelection(root, Platform.LibC.UNKNOWN, LIBC_LEGACY_MARKER);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractUnknownLibcDoesNotSelectVariant() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-musl", LIBC_MUSL_MARKER);
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);

            assertLibcSelectionFails(root, Platform.LibC.UNKNOWN);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testExtractDoesNotSelectOtherLibcVariant() throws Exception {
        File root = createLibcSelectionClasspath();

        try {
            // Only GLIBC is available; MUSL must not select it.
            writeLibcSelectionResource(root, Platform.RESOURCE_PREFIX + "-glibc", LIBC_GLIBC_MARKER);

            assertLibcSelectionFails(root, Platform.LibC.MUSL);
        } finally {
            deleteRecursively(root);
        }
    }

    public void testLoadFromJNALibraryPath() {
        // Tests are already configured to load from this path
        NativeLibrary.getInstance("testlib");
    }

    public void testLoadFromCustomPath() throws MalformedURLException {
        NativeLibrary.addSearchPath("testlib-path", TESTPATH);
        NativeLibrary.getInstance("testlib-path", new TestLoader(new File(".")));
    }

    public void testLoadFromClasspath() throws MalformedURLException {
        NativeLibrary.getInstance("testlib-path", new TestLoader(new File(TESTPATH)));
    }

    public void testLoadFromClasspathAbsolute() throws MalformedURLException {
        String name = NativeLibrary.mapSharedLibraryName("testlib-path");
        NativeLibrary.getInstance("/" + name, new TestLoader(new File(TESTPATH)));
    }

    public void testLoadFromJar() throws MalformedURLException {
        NativeLibrary.getInstance("testlib-jar", new TestLoader(new File(TESTJAR2)));
        NativeLibrary.getInstance("testlib-jar", new TestLoader(new File(TESTJAR3)));
    }

    public void testLoadFromJarAbsolute() throws MalformedURLException {
        String name = NativeLibrary.mapSharedLibraryName("testlib-jar");
        NativeLibrary.getInstance("/" + name, new TestLoader(new File(TESTJAR)));
    }

    public void testLoadExplicitAbsolutePath() throws MalformedURLException {
        // windows requires ".dll" suffix
        String name = "testlib-truncated" + (Platform.isWindows() ? ".dll" : "");
        NativeLibrary.getInstance(new File(TESTPATH, name).getAbsolutePath());
    }

    public static interface CLibrary extends Library {
        int wcslen(WString wstr);
        int strlen(String str);
        int atol(String str);

        Pointer getpwuid(int uid);
        int geteuid();
    }

    private Object load() {
        return Native.load(Platform.C_LIBRARY_NAME, CLibrary.class);
    }

    public void testLoadProcess() {
        Native.load(CLibrary.class);
    }

    public void testLoadProcessWithOptions() {
        Native.load(CLibrary.class, Collections.EMPTY_MAP);
    }

    public void testLoadCLibrary() {
        load();
    }

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

    public void testLoadLibraryWithUnicodeName() throws Exception {
        File tmpdir = Native.getTempDir();
        String libName = NativeLibrary.mapSharedLibraryName("testlib");
        File src = new File(TESTPATH, libName);
        assertTrue("Expected JNA native library at " + src + " is missing", src.exists());

        final String UNICODE = "\u0444\u043b\u0441\u0432\u0443";

        String newLibName = libName.replace("testlib", UNICODE);
        File dst = new File(tmpdir, newLibName);
        copy(src, dst);
        try {
            NativeLibrary.getInstance(UNICODE, new TestLoader(tmpdir));
            dst.deleteOnExit();
        }
        catch(UnsatisfiedLinkError e) {
            fail("Library '" + newLibName + "' at " + dst + " could not be loaded: " + e);
        }
    }

    public void testLoadLibraryWithLongName() throws Exception {
        File tmpdir = Native.getTempDir();
        String libName = NativeLibrary.mapSharedLibraryName("testlib");
        File src = new File(TESTPATH, libName);
        assertTrue("Expected JNA native library at " + src + " is missing", src.exists());

        for (int i=0;i < 16;i++) {
            tmpdir = new File(tmpdir, "subdir0123456789");
            tmpdir.deleteOnExit();
        }

        final String NAME = getName();
        String newLibName = libName.replace("testlib", NAME);
        tmpdir.mkdirs();
        File dst = new File(tmpdir, newLibName);
        copy(src, dst);
        try {
            NativeLibrary.getInstance(NAME, new TestLoader(tmpdir));
            dst.deleteOnExit();
        }
        catch(UnsatisfiedLinkError e) {
            fail("Library '" + newLibName + "' at " + dst + " could not be loaded: " + e);
        }
    }

    public void testLoadFrameworkLibrary() {
        if (Platform.isMac()) {
            final String PATH = "/System/Library/Frameworks/CoreServices.framework";
            try {
                NativeLibrary lib = NativeLibrary.getInstance("CoreServices");
                assertNotNull("CoreServices not found", lib);
            }
            catch(UnsatisfiedLinkError e) {
                failCoreServices("Should search /System/Library/Frameworks: ", e, PATH);
            }
        }
    }

    public void testLoadFrameworkLibraryAbsolute() {
        if (Platform.isMac()) {
            final String PATH = "/System/Library/Frameworks/CoreServices";
            final String FRAMEWORK = PATH + ".framework";
            try {
                NativeLibrary lib = NativeLibrary.getInstance(PATH);
                assertNotNull("CoreServices not found", lib);
            }
            catch(UnsatisfiedLinkError e) {
                failCoreServices("Should try FRAMEWORK.framework/FRAMEWORK if the absolute framework (truncated) path given exists: ", e, FRAMEWORK);
            }
        }
    }

    public void testLoadFrameworkLibraryAbsoluteFull() {
        if (Platform.isMac()) {
            final String PATH = "/System/Library/Frameworks/CoreServices.framework/CoreServices";
            try {
                NativeLibrary lib = NativeLibrary.getInstance(PATH);
                assertNotNull("CoreServices not found", lib);
            }
            catch(UnsatisfiedLinkError e) {
                failCoreServices("Should try FRAMEWORK verbatim if the absolute path given exists: ", e, PATH);
            }
        }
    }

    private void failCoreServices(String message, UnsatisfiedLinkError e, String expectedPath) {
        if (!new File(expectedPath).exists()) {
            message = "CoreServices not present on this setup, expected at " + expectedPath + "\n" + message;
        }
        fail(message + e);
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

    public interface TestLib2 extends Library {
        int dependentReturnFalse();
    }

    // Only desktop windows provides an altered search path, looking for
    // dependent libraries in the same directory as the original
    public void testLoadDependentLibraryWithAlteredSearchPath() {
        try {
            TestLib2 lib = Native.load("testlib2", TestLib2.class);
            lib.dependentReturnFalse();
        }
        catch(UnsatisfiedLinkError e) {
            // failure expected on anything but windows
            if (Platform.isWindows() && !Platform.isWindowsCE()) {
                fail("Failed to load dependent libraries: " + e);
            }
        }
    }

    // Ubuntu bug when arch-specific libc is active
    // Only fails on *some* functions
    public void testLoadProperCLibraryVersion() {
        if (Platform.isWindows()) return;

        CLibrary lib = Native.load("c", CLibrary.class);
        assertNotNull("Couldn't get current user",
                      lib.getpwuid(lib.geteuid()));
    }

    private static class AWT {
        public static void loadJAWT(String name) {
            Frame f = new Frame(name);
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
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LibraryLoadTest.class);
    }
}
