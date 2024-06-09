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
    private static final String JNA_LIB_NAME = "jnidispatch";
    private static final String JNA_LINK_NAME = "jnidispatch";
    private static final String JNA_NATIVE_LAYOUT = "com_sun_jna_Native";

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

        nativeLibraries.preregisterUninitializedBuiltinLibrary(JNA_LINK_NAME);
        platformLibraries.addBuiltinPkgNativePrefix(JNA_NATIVE_LAYOUT);

        var accessImpl = (BeforeAnalysisAccessImpl) access;
        accessImpl.getNativeLibraries().addStaticJniLibrary(JNA_LIB_NAME);

        var jniDispatch = NativeLibrary.getInstance(JNA_LIB_BASE_NAME);
        nativeLibraries.loadLibraryAbsolute(jniDispatch.getFile());
    }
}
