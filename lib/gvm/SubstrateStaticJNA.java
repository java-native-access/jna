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

import java.io.File;
import java.io.IOException;

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
 */
public final class SubstrateStaticJNA extends AbstractJNAFeature {
    /**
     * Name for the JNI Dispatch native library used for registration with Native Image.
     */
    private static final String JNA_LIB_NAME = "jnidispatch";

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
     * Returns the name of the static JNI Dispatch library for the current platform. On UNIX-based systems,
     * {@link #JNI_DISPATCH_UNIX_NAME} is used; on Windows, {@link #JNI_DISPATCH_WINDOWS_NAME} is returned instead.
     *
     * @see #getStaticLibraryResource
     */
    private static String getStaticLibraryFileName() {
        if (Platform.includedIn(Platform.WINDOWS.class)) return JNI_DISPATCH_WINDOWS_NAME;
        if (Platform.includedIn(Platform.LINUX.class)) return JNI_DISPATCH_UNIX_NAME;
        if (Platform.includedIn(Platform.LINUX.class)) return JNI_DISPATCH_UNIX_NAME;

        // If the current platform is not in the Platform class, this code would not run at all
        throw new UnsupportedOperationException("Current platform does not support static linking");
    }

    /**
     * Returns the full path to the static JNI Dispatch library embedded in the JAR, accounting for platform-specific
     * library names.
     *
     * @see #getStaticLibraryFileName()
     */
    private static String getStaticLibraryResource() {
        //
        return "/com/sun/jna/" + com.sun.jna.Platform.RESOURCE_PREFIX + "/" + getStaticLibraryFileName();
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
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        var nativeLibraries = NativeLibrarySupport.singleton();
        var platformLibraries = PlatformNativeLibrarySupport.singleton();

        // Register as a built-in library with Native Image and set the name prefix used by native symbols
        nativeLibraries.preregisterUninitializedBuiltinLibrary(JNA_LINK_NAME);
        platformLibraries.addBuiltinPkgNativePrefix(JNA_NATIVE_LAYOUT);

        // WARNING: the static JNI linking feature is unstable and may be removed in the future;
        // this code uses the access implementation directly in order to register the static library
        var accessImpl = (BeforeAnalysisAccessImpl) access;
        accessImpl.getNativeLibraries().addStaticJniLibrary(JNA_LIB_NAME);

        // Unpack the static library from resources so Native Image can use it
        File extractedLib;
        try {
            extractedLib = Native.extractFromResourcePath(getStaticLibraryResource(), Native.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract native dispatch library from resources", e);
        }

        // TODO(@sgammon): this does not seem like the correct method to call here, consider updating
        //                 the Native Image library path to include the extracted library instead
        //                 (see `accessImpl.getNativeLibraries().getLibraryPaths()`)
        // nativeLibraries.loadLibraryAbsolute(extractedLib);
    }
}
