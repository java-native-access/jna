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

import java.lang.reflect.Method;

/** Provides mapping of Java method names to native function names.  
 * An instance of this interface may be provided to 
 * {@link Native#loadLibrary(String, Class, java.util.Map)} as an entry in
 * the options map with key {@link Library#OPTION_FUNCTION_MAPPER}.
 * <p>
 * There are several circumstances where this option might prove useful.
 * <ul>
 * <li>C preprocessor macros are used to allow C code to refer to a library 
 * function by a different name
 * <li>Generated linker symbols are different than those used in C code.  
 * Windows <code>stdcall</code> functions, for instance, are exported with a 
 * special suffix that describes the stack size of the function arguments
 * (see {@link com.sun.jna.win32.StdCallFunctionMapper}).
 * <li>The naming of the C library methods conflicts horribly with your
 * Java coding standards, or are otherwise hard to follow.  It's generally
 * better to keep the original function names in this case, to avoid confusion
 * about what's actually being called, but the option is available.
 * </ul>
 * 
 * @see Library#OPTION_FUNCTION_MAPPER 
 */
public interface FunctionMapper {
    String getFunctionName(NativeLibrary library, Method method);
}
