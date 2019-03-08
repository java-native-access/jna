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

import java.lang.reflect.Method;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;

/** Provides mapping from simple method names to w32 stdcall-decorated names
 * where the name suffix is "@" followed by the number of bytes popped by
 * the called function.<p>
 * NOTE: if you use custom type mapping for primitive types, you may need to
 * override {@link #getArgumentNativeStackSize(Class)}.
 */
public class StdCallFunctionMapper implements FunctionMapper {
    /** Override this to handle any custom class mappings.
     * @param cls Java class of a parameter
     * @return number of native bytes used for this class on the stack
     */
    protected int getArgumentNativeStackSize(Class<?> cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls.isArray()) {
            return Native.POINTER_SIZE;
        }
        try {
            return Native.getNativeSize(cls);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
        }
    }

    /**
     * Convert the given Java method into a decorated {@code stdcall} name, if possible.
     *
     * @param library The {@link NativeLibrary} instance
     * @param method The invoked {@link Method}
     * @return The decorated name
     */
    @Override
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        int pop = 0;
        Class<?>[] argTypes = method.getParameterTypes();
        for (Class<?> cls : argTypes) {
            pop += getArgumentNativeStackSize(cls);
        }

        String decorated = name + "@" + pop;
        int conv = StdCallLibrary.STDCALL_CONVENTION;
        try {
            Function func = library.getFunction(decorated, conv);
            name = func.getName();
        } catch(UnsatisfiedLinkError e) {
            // try with an explicit underscore
            try {
                Function func = library.getFunction("_" + decorated, conv);
                name = func.getName();
            } catch(UnsatisfiedLinkError e2) {
                // not found; let caller try undecorated version
            }
        }

        return name;
    }
}