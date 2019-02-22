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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;

/** Provides type conversion for instances of {@link NativeMapped}. */
public class NativeMappedConverter implements TypeConverter {
    private static final Map<Class<?>, Reference<NativeMappedConverter>> converters =
            new WeakHashMap<Class<?>, Reference<NativeMappedConverter>>();
    private final Class<?> type;
    private final Class<?> nativeType;
    private final NativeMapped instance;

    public static NativeMappedConverter getInstance(Class<?> cls) {
        synchronized(converters) {
            Reference<NativeMappedConverter> r = converters.get(cls);
            NativeMappedConverter nmc = r != null ? r.get() : null;
            if (nmc == null) {
                nmc = new NativeMappedConverter(cls);
                converters.put(cls, new SoftReference<NativeMappedConverter>(nmc));
            }
            return nmc;
        }
    }

    public NativeMappedConverter(Class<?> type) {
        if (!NativeMapped.class.isAssignableFrom(type))
            throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
        this.type = type;
        this.instance = defaultValue();
        this.nativeType = instance.nativeType();
    }

    public NativeMapped defaultValue() {
        if (type.isEnum()) {
            return (NativeMapped) type.getEnumConstants()[0];
        }

        return (NativeMapped) Klass.newInstance(type);
    }

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return instance.fromNative(nativeValue, context);
    }

    @Override
    public Class<?> nativeType() {
        return nativeType;
    }

    @Override
    public Object toNative(Object value, ToNativeContext context) {
        if (value == null) {
            if (Pointer.class.isAssignableFrom(nativeType)) {
                return null;
            }
            value = defaultValue();
        }
        return ((NativeMapped)value).toNative();
    }
}