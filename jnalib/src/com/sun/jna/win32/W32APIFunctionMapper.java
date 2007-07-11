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

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

/** Encapsulates lookup of W32 API UNICODE/ASCII functions. */
public class W32APIFunctionMapper implements FunctionMapper {
    public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
    public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);
    private final String suffix;
    protected W32APIFunctionMapper(boolean unicode) {
        this.suffix = unicode ? "W" : "A";
    }
    /** Looks up the method name by adding a "W" or "A" suffix as appropriate. 
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
