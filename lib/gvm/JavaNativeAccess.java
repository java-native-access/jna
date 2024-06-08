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

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.graalvm.nativeimage.hosted.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Feature for use at build time on GraalVM, which enables basic support for JNA.
 *
 * <p>This "Feature" implementation is discovered on the class path, via the argument file at native-image.properties.
 * At build-time, the feature is registered with the Native Image compiler.
 *
 * <p>If JNA is detected on the class path, the feature is enabled, and the JNA library is initialized and configured
 * for native access support.
 *
 * <p>Certain features like reflection and JNI access are configured by this feature; to enable static optimized
 * support for JNA, see the {@link SubstrateStaticJNA} feature.
 */
public final class JavaNativeAccess extends AbstractJNAFeature implements Feature {
    static final String NATIVE_LAYOUT = "com.sun.jna.Native";

    @Override
    public String getDescription() {
        return "Enables access to JNA at runtime on SubstrateVM";
    }

    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return access.findClassByName(NATIVE_LAYOUT) != null;
    }

    private void registerCommonTypes() {
        registerJniClass(Callback.class);
        registerJniClass(CallbackReference.class);
        registerJniMethods(
                method(Callback.class, "getCallback", Class.class, Pointer.class, Boolean.class));
        registerJniMethods(
                method(Callback.class, "getFunctionPointer", Callback.class, Boolean.class));
        registerJniMethods(
                method(Callback.class, "getNativeString", Object.class, Boolean.class));
        registerJniMethods(
                method(Callback.class, "initializeThread", Callback.class, CallbackReference.AttachOptions.class));

        registerJniClass(com.sun.jna.CallbackReference.AttachOptions.class);

        registerJniClass(FromNativeConverter.class);
        registerJniMethods(method(FromNativeConverter.class, "nativeType"));

        registerJniClass(IntegerType.class);
        registerJniFields(fields(IntegerType.class, "value"));

        registerJniClass(JNIEnv.class);

        registerJniClass(Native.class);
        registerJniMethods(
                method(Callback.class, "dispose"),
                method(Callback.class, "fromNative", FromNativeConverter.class, Object.class, Method.class),
                method(Callback.class, "fromNative", Class.class, Object.class),
                method(Callback.class, "nativeType", Class.class),
                method(Callback.class, "toNative", ToNativeConverter.class, Object.class),
                method(Callback.class, "open", String.class, Integer.class),
                method(Callback.class, "close", Long.class),
                method(Callback.class, "findSymbol", Long.class, String.class));

        registerJniClass(Native.ffi_callback.class);
        registerJniMethods(method(Native.ffi_callback.class, "invoke", Long.class, Long.class, Long.class));

        registerJniClass(NativeLong.class);

        registerJniClass(NativeMapped.class);
        registerJniMethods(method(NativeMapped.class, "toNative"));

        registerJniClass(Pointer.class);
        registerJniFields(fields(Pointer.class, "peer"));
        registerJniMethods(method(Pointer.class, "<init>", Long.class));

        registerJniClass(PointerType.class);
        registerJniFields(fields(PointerType.class, "pointer"));

        registerJniClass(Structure.class);
        registerJniFields(fields(Structure.class, "memory", "typeInfo"));
        registerJniMethods(
                method(Callback.class, "autoRead"),
                method(Callback.class, "autoWrite"),
                method(Callback.class, "getTypeInfo"),
                method(Callback.class, "getTypeInfo", Object.class),
                method(Callback.class, "newInstance", Class.class),
                method(Callback.class, "newInstance", Class.class, Long.class),
                method(Callback.class, "newInstance", Class.class, Pointer.class));

        registerJniClass(Structure.ByValue.class);
        registerJniClass(Structure.FFIType.class);
        registerJniClass(WString.class);
        registerJniClass(PointerByReference.class);
    }

    private void registerCommonProxies() {
        registerProxyInterfaces(Callback.class);
        registerProxyInterfaces(Library.class);
    }

    private void registerReflectiveAccess() {
        reflectiveClass(
                CallbackProxy.class,
                CallbackReference.class,
                Klass.class,
                Native.class,
                NativeLong.class,
                Structure.class,
                IntByReference.class,
                PointerByReference.class);
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        registerCommonTypes();
        registerCommonProxies();
        registerReflectiveAccess();
    }
}
