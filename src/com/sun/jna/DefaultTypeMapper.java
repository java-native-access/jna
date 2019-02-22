/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Provide custom mappings to and from native types.  The default lookup
 * checks classes corresponding to converters in the order added; if the
 * class to be converted is an instance of the converter's registered class,
 * the converter will be used.<p>
 * Derived classes should install additional converters using
 * {@link #addToNativeConverter}
 * and/or {@link #addFromNativeConverter} in the default constructor.  Classes
 * for primitive types will automatically register for the corresponding
 * Object type and vice versa (i.e. you don't have to register both
 * <code>int.class</code> and <code>Integer.class</code>).
 * If you want different mapping behavior than the default, simply override
 * {@link #getToNativeConverter} and {@link #getFromNativeConverter}.
 * @see Library#OPTION_TYPE_MAPPER
 */
public class DefaultTypeMapper implements TypeMapper {
    private static class Entry {
        public Class<?> type;
        public Object converter;
        public Entry(Class<?> type, Object converter) {
            this.type = type;
            this.converter = converter;
        }
    }

    private List<Entry> toNativeConverters = new ArrayList<Entry>();
    private List<Entry> fromNativeConverters = new ArrayList<Entry>();

    private Class<?> getAltClass(Class<?> cls) {
        if (cls == Boolean.class) {
            return boolean.class;
        } else if (cls == boolean.class) {
            return Boolean.class;
        } else if (cls == Byte.class) {
            return byte.class;
        } else if (cls == byte.class) {
            return Byte.class;
        } else if (cls == Character.class) {
            return char.class;
        } else if (cls == char.class) {
            return Character.class;
        } else if (cls == Short.class) {
            return short.class;
        } else if (cls == short.class) {
            return Short.class;
        } else if (cls == Integer.class) {
            return int.class;
        } else if (cls == int.class) {
            return Integer.class;
        } else if (cls == Long.class) {
            return long.class;
        } else if (cls == long.class) {
            return Long.class;
        } else if (cls == Float.class) {
            return float.class;
        } else if (cls == float.class) {
            return Float.class;
        } else if (cls == Double.class) {
            return double.class;
        } else if (cls == double.class) {
            return Double.class;
        }
        return null;
    }
    /** Add a {@link ToNativeConverter} to define the conversion into a native
     * type from arguments of the given Java type.  Converters are
     * checked for in the order added.
     * @param cls Java class requiring conversion
     * @param converter {@link ToNativeConverter} to transform an object of
     * the given Java class into its native-compatible form.
     */
    public void addToNativeConverter(Class<?> cls, ToNativeConverter converter) {
        toNativeConverters.add(new Entry(cls, converter));
        Class<?> alt = getAltClass(cls);
        if (alt != null) {
            toNativeConverters.add(new Entry(alt, converter));
        }
    }
    /**
     * Add a {@link FromNativeConverter} to convert a native result type into the
     * given Java type.  Converters are checked for in the order added.
     *
     * @param cls Java class for the Java representation of a native type.
     * @param converter {@link FromNativeConverter} to transform a
     * native-compatible type into its Java equivalent.
     */
    public void addFromNativeConverter(Class<?> cls, FromNativeConverter converter) {
        fromNativeConverters.add(new Entry(cls, converter));
        Class<?> alt = getAltClass(cls);
        if (alt != null) {
            fromNativeConverters.add(new Entry(alt, converter));
        }
    }

    /**
     * Add a {@link TypeConverter} to provide bidirectional mapping between
     * a native and Java type.
     *
     * @param cls Java class representation for a native type
     * @param converter {@link TypeConverter} to translate between native and
     * Java types.
     */
    public void addTypeConverter(Class<?> cls, TypeConverter converter) {
        addFromNativeConverter(cls, converter);
        addToNativeConverter(cls, converter);
    }

    private Object lookupConverter(Class<?> javaClass, Collection<? extends Entry> converters) {
        for (Entry entry : converters) {
            if (entry.type.isAssignableFrom(javaClass)) {
                return entry.converter;
            }
        }
        return null;
    }

    @Override
    public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
        return (FromNativeConverter)lookupConverter(javaType, fromNativeConverters);
    }

    @Override
    public ToNativeConverter getToNativeConverter(Class<?> javaType) {
        return (ToNativeConverter)lookupConverter(javaType, toNativeConverters);
    }
}
