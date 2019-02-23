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

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

/**
 * Encapsulates lookup of W32 API UNICODE/ASCII functions.
 */
public class W32APIFunctionMapper implements FunctionMapper {
    public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
    public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);
    private final String suffix;
    protected W32APIFunctionMapper(boolean unicode) {
        this.suffix = unicode ? "W" : "A";
    }
    /**
     * Looks up the method name by adding a "W" or "A" suffix as appropriate.
     */
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        if (!name.endsWith("W") && !name.endsWith("A")) {
            try {
                name = library.getFunction(name + suffix, StdCallLibrary.STDCALL_CONVENTION).getName();
            }
            catch(UnsatisfiedLinkError e) {
                // ignore and let caller use undecorated name
            }
        }
        return name;
    }
}
