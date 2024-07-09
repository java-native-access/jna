/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
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

import com.oracle.svm.core.jdk.NativeLibrarySupport;
import com.oracle.svm.core.jdk.PlatformNativeLibrarySupport;
import com.oracle.svm.hosted.FeatureImpl.BeforeAnalysisAccessImpl;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.hosted.Feature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

/**
 * Feature for use at build time on GraalVM, which enables static JNI support for JNA.
 *
 * <p>This "Feature" implementation is discovered on the classpath, via the argument file at native-image.properties.
 * At build-time, the feature is registered with the Native Image compiler.
 *
 * <p>If JNA is detected on the classpath, and if static JNI is enabled, the feature is enabled, and the JNA library is
 * initialized and configured for native access support.
 *
 * <p>This class extends the base {@link com.sun.jna.JavaNativeAccess} feature by providing JNA's JNI layer statically,
 * so that no library unpacking step needs to take place.
 *
 * @since 5.15.0
 * @author Sam Gammon (sam@elide.dev)
 * @author Dario Valdespino (dario@elide.dev)
 */
public final class SubstrateStaticJNA extends AbstractJNAFeature {
    /**
     * Name for the FFI native library used during static linking by Native Image.
     */
    private static final String FFI_LINK_NAME = "ffi";

    /**
     * Name for the JNI Dispatch native library used during static linking by Native Image.
     */
    private static final String JNA_LINK_NAME = "jnidispatch";

    /**
     * Name prefix used by native functions from the JNI Dispatch library.
     */
    private static final String JNA_NATIVE_LAYOUT = "com_sun_jna_Native";

    /**
     * Name of the JNI Dispatch static library on UNIX-based platforms.
     */
    private static final String JNI_DISPATCH_UNIX_NAME = "libjnidispatch.a";

    /**
     * Name of the JNI Dispatch static library on Windows.
     */
    private static final String JNI_DISPATCH_WINDOWS_NAME = "jnidispatch.lib";

    /**
     * Name of the FFI static library on UNIX-based platforms.
     */
    private static final String FFI_UNIX_NAME = "libffi.a";

    /**
     * Name of the FFI static library on Windows.
     */
    private static final String FFI_WINDOWS_NAME = "ffi.lib";

    /**
     * Returns the name of the static JNI Dispatch library for the current platform. On UNIX-based systems,
     * {@link #JNI_DISPATCH_UNIX_NAME} is used; on Windows, {@link #JNI_DISPATCH_WINDOWS_NAME} is returned instead.
     *
     * @see #getStaticLibraryResource
     * @return The JNI Dispatch library name for the current platform.
     */
    private static String getStaticLibraryFileName() {
        if (Platform.includedIn(Platform.WINDOWS.class)) return JNI_DISPATCH_WINDOWS_NAME;
        if (Platform.includedIn(Platform.LINUX.class)) return JNI_DISPATCH_UNIX_NAME;
        if (Platform.includedIn(Platform.DARWIN.class)) return JNI_DISPATCH_UNIX_NAME;

        // If the current platform is not in the Platform class, this code would not run at all
        throw new UnsupportedOperationException("Current platform does not support static linking");
    }

    /**
     * Returns the name of the static FFI library for the current platform. On UNIX-based systems,
     * {@link #FFI_UNIX_NAME} is used; on Windows, {@link #FFI_WINDOWS_NAME} is returned instead.
     *
     * @see #getStaticLibraryResource
     * @return The FFI library name for the current platform.
     */
    private static String getFFILibraryFileName() {
        if (Platform.includedIn(Platform.WINDOWS.class)) return FFI_WINDOWS_NAME;
        if (Platform.includedIn(Platform.LINUX.class)) return FFI_UNIX_NAME;
        if (Platform.includedIn(Platform.DARWIN.class)) return FFI_UNIX_NAME;

        // If the current platform is not in the Platform class, this code would not run at all
        throw new UnsupportedOperationException("Current platform does not support static FFI");
    }

    /**
     * Returns the full path to the static JNI Dispatch library embedded in the JAR, accounting for platform-specific
     * library names.
     *
     * @see #getStaticLibraryFileName()
     * @return The JNI Dispatch library resource path for the current platform.
     */
    private static String getStaticLibraryResource() {
        return "/com/sun/jna/" + com.sun.jna.Platform.RESOURCE_PREFIX + "/" + getStaticLibraryFileName();
    }

    /**
     * Returns the full path to the static FFI library which JNA depends on, accounting for platform-specific
     * library names.
     *
     * @see #getFFILibraryFileName()
     * @return The FFI library resource path for the current platform.
     */
    private static String getFFILibraryResource() {
        return "/com/sun/jna/" + com.sun.jna.Platform.RESOURCE_PREFIX + "/" + getFFILibraryFileName();
    }

    /**
     * Extracts a library resource and returns the file it was extracted to.
     *
     * @param resource Resource path for the library to extract.
     * @param filename Expected filename for the library.
     * @return The extracted library file.
     */
    private static File unpackLibrary(String resource, String filename) {
        // Unpack the static library from resources so Native Image can use it
        File extractedLib;
        try {
            extractedLib = Native.extractFromResourcePath(resource, Native.class.getClassLoader());

            // The library is extracted into a file with a `.tmp` name, which will not be picked up by the linker
            // We need to rename it first using the platform-specific convention or the build will fail
            File platformLib = new File(extractedLib.getParentFile(), filename);
            if (!extractedLib.renameTo(platformLib)) throw new IllegalStateException("Renaming extract file failed");
            extractedLib = platformLib;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract native dispatch library from resources", e);
        }
        return extractedLib;
    }

    @Override
    public String getDescription() {
        return "Enables optimized static access to JNA at runtime";
    }

    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return access.findClassByName(JavaNativeAccess.NATIVE_LAYOUT) != null;
    }

    @Override
    public List<Class<? extends Feature>> getRequiredFeatures() {
        return Collections.singletonList(JavaNativeAccess.class);
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        var nativeLibraries = NativeLibrarySupport.singleton();
        var platformLibraries = PlatformNativeLibrarySupport.singleton();

        // Register as a built-in library with Native Image and set the name prefix used by native symbols
        nativeLibraries.preregisterUninitializedBuiltinLibrary(JNA_LINK_NAME);
        platformLibraries.addBuiltinPkgNativePrefix(JNA_NATIVE_LAYOUT);

        // Extract the main JNA library from the platform-specific resource path; next, extract the FFI
        // library it depends on
        unpackLibrary(getFFILibraryResource(), getFFILibraryFileName());
        var extractedLib = unpackLibrary(getStaticLibraryResource(), getStaticLibraryFileName());

        // WARNING: the static JNI linking feature is unstable and may be removed in the future;
        // this code uses the access implementation directly in order to register the static library. We
        // inform the Native Image compiler that JNA depends on `ffi`, so that it forces it to load first
        // when JNA is initialized at image runtime.
        var nativeLibsImpl = ((BeforeAnalysisAccessImpl) access).getNativeLibraries();
        nativeLibsImpl.addStaticNonJniLibrary(FFI_LINK_NAME);
        nativeLibsImpl.addStaticJniLibrary(JNA_LINK_NAME, FFI_LINK_NAME);

        // Enhance the Native Image lib paths so the injected static libraries are available to the linker
        nativeLibsImpl.getLibraryPaths().add(extractedLib.getParentFile().getAbsolutePath());
    }
}
