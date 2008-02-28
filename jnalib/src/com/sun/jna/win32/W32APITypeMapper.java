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
package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
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
    
    public static final TypeMapper UNICODE = new W32APITypeMapper(true);
    public static final TypeMapper ASCII = new W32APITypeMapper(false);
    
    protected W32APITypeMapper(boolean unicode) {
        if (unicode) {
            TypeConverter stringConverter = new TypeConverter() {
                public Object toNative(Object value, ToNativeContext context) {
                    if (value == null)
                        return null;
                    if (value instanceof String[]) {
                        return new StringArray((String[])value, true);
                    }
                    return new WString(value.toString());
                }
                public Object fromNative(Object value, FromNativeContext context) {
                    if (value == null)
                        return null;
                    return value.toString();
                }
                public Class nativeType() {
                    return WString.class;
                }
            };
            addTypeConverter(String.class, stringConverter);
            addToNativeConverter(String[].class, stringConverter);
        }
        TypeConverter booleanConverter = new TypeConverter() {
            public Object toNative(Object value, ToNativeContext context) {
                return new Integer(Boolean.TRUE.equals(value) ? 1 : 0);
            }
            public Object fromNative(Object value, FromNativeContext context) {
                return ((Integer)value).intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
            public Class nativeType() {
                // BOOL is 32-bit int
                return Integer.class;
            }
        };
        addTypeConverter(Boolean.class, booleanConverter);
    }
}
