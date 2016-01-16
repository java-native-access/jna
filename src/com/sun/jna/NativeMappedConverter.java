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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
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
        try {
            return (NativeMapped)type.newInstance();
        } catch (InstantiationException e) {
            String msg = "Can't create an instance of " + type
                + ", requires a no-arg constructor: " + e;
            throw new IllegalArgumentException(msg);
        } catch (IllegalAccessException e) {
            String msg = "Not allowed to create an instance of " + type
                + ", requires a public, no-arg constructor: " + e;
            throw new IllegalArgumentException(msg);
        }
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