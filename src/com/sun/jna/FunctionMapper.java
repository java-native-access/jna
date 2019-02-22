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
package com.sun.jna;

import java.lang.reflect.Method;

/** Provides mapping of Java method names to native function names.
 * An instance of this interface may be provided to
 * {@link Native#load(String, Class, java.util.Map)} as an entry in
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
