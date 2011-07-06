/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
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
