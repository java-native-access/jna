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

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import com.sun.jna.Callback;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

/** Provides mapping from simple method names to w32 stdcall-decorated names
 * where the name suffix is "@" followed by the number of bytes popped by
 * the called function.<p>
 * NOTE: if you use custom type mapping, you may need to override 
 * {@link #getArgumentNativeStackSize(Class)}.
 */
public class StdCallFunctionMapper implements FunctionMapper {
    /** Override this to handle any custom class mappings. */
    protected int getArgumentNativeStackSize(Class cls) {
        if (cls == byte.class || cls == Byte.class) return 1;
        if (cls == char.class || cls == Character.class) return Native.WCHAR_SIZE;
        if (cls == short.class || cls == Short.class) return 2; 
        if (cls == int.class || cls == Integer.class) return 4;
        if (cls == long.class || cls == Long.class) return 8;
        if (cls == float.class || cls == Float.class) return 4;
        if (cls == double.class || cls == Double.class) return 8;
        if (NativeLong.class.isAssignableFrom(cls)) return Native.LONG_SIZE;
        if (Pointer.class.isAssignableFrom(cls)
            || Callback.class.isAssignableFrom(cls)
            || Structure.class.isAssignableFrom(cls)
            || String.class == cls
            || WString.class == cls
            || cls.isArray()
            || ByteBuffer.class.isAssignableFrom(cls)) {
            return Pointer.SIZE;
        }
        throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
    }
    /** Convert the given Java method into a decorated stdcall name,
     * if possible.
     */
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        int pop = 0;
        Class[] argTypes = method.getParameterTypes();
        for (int i=0;i < argTypes.length;i++) {
            pop += getArgumentNativeStackSize(argTypes[i]);
        }
        String decorated = name + "@" + pop;
        try {
            name = library.getFunction(decorated, StdCallLibrary.STDCALL_CONVENTION).getName();
        }
        catch(UnsatisfiedLinkError e) {
            // not found; let caller try undecorated version
        }
        return name;
    }
}