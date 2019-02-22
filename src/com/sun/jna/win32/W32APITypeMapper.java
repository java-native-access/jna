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
package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;

/** Provide standard conversion for W32 API types.  This comprises the
 * following native types:
 * <ul>
 * <li>Unicode or ASCII/MBCS strings and arrays of string, as appropriate
 * <li>BOOL
 * </ul>
 * @author twall
 */
public class W32APITypeMapper extends DefaultTypeMapper {
    /** Standard TypeMapper to use the unicode version of a w32 API. */
    public static final TypeMapper UNICODE = new W32APITypeMapper(true);
    /** Standard TypeMapper to use the ASCII/MBCS version of a w32 API. */
    public static final TypeMapper ASCII = new W32APITypeMapper(false);
    /** Default TypeMapper to use - depends on the value of {@code w32.ascii} system property */
    public static final TypeMapper DEFAULT = Boolean.getBoolean("w32.ascii") ? ASCII : UNICODE;

    protected W32APITypeMapper(boolean unicode) {
        if (unicode) {
            TypeConverter stringConverter = new TypeConverter() {
                @Override
                public Object toNative(Object value, ToNativeContext context) {
                    if (value == null)
                        return null;
                    if (value instanceof String[]) {
                        return new StringArray((String[])value, true);
                    }
                    return new WString(value.toString());
                }
                @Override
                public Object fromNative(Object value, FromNativeContext context) {
                    if (value == null)
                        return null;
                    return value.toString();
                }
                @Override
                public Class<?> nativeType() {
                    return WString.class;
                }
            };
            addTypeConverter(String.class, stringConverter);
            addToNativeConverter(String[].class, stringConverter);
        }
        TypeConverter booleanConverter = new TypeConverter() {
            @Override
            public Object toNative(Object value, ToNativeContext context) {
                return Integer.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            }
            @Override
            public Object fromNative(Object value, FromNativeContext context) {
                return ((Integer)value).intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            @Override
            public Class<?> nativeType() {
                // BOOL is 32-bit int
                return Integer.class;
            }
        };
        addTypeConverter(Boolean.class, booleanConverter);
    }
}
