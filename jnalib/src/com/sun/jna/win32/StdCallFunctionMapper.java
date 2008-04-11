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
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sun.jna.Callback;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.WString;

/** Provides mapping from simple method names to w32 stdcall-decorated names
 * where the name suffix is "@" followed by the number of bytes popped by
 * the called function.<p>
 * NOTE: if you use custom type mapping for primitive types, you may need to 
 * override {@link #getArgumentNativeStackSize(Class)}.
 */
public class StdCallFunctionMapper implements FunctionMapper {
    /** Override this to handle any custom class mappings. */
    protected int getArgumentNativeStackSize(Class cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls.isArray()) {
            return Pointer.SIZE;
        }
        try {
            return Native.getNativeSize(cls);
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
        }
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
        int conv = StdCallLibrary.STDCALL_CONVENTION;
        try {
            name = library.getFunction(decorated, conv).getName();

        }
        catch(UnsatisfiedLinkError e) {
            // try with an explicit underscore
            try {
                name = library.getFunction("_" + decorated, conv).getName();
            }
            catch(UnsatisfiedLinkError e2) {
                // not found; let caller try undecorated version
            }
        }
        return name;
    }
}