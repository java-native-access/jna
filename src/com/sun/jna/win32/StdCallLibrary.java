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

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;

/** Interface for w32 stdcall calling convention. */
public interface StdCallLibrary extends Library, StdCall {
    /** Constant identifying the w32 stdcall calling convention. */
    int STDCALL_CONVENTION = Function.ALT_CONVENTION;
    /** Provides auto-lookup of stdcall-decorated names. */
    FunctionMapper FUNCTION_MAPPER = new StdCallFunctionMapper();
    /** Interface defining a callback using the w32 stdcall calling convention.
     */
    interface StdCallCallback extends Callback, StdCall { }
}
