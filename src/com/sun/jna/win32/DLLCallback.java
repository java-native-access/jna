/* Copyright (c) 2012 Timothy Wall, All Rights Reserved
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

/** Indicate that the callback needs to appear to be within a DLL.  The
 * effective DLL module handle may be obtained by TODO.
 * Use this interface when your callback must reside within a DLL (hooks set
 * via <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms644990(v=vs.85).aspx">SetWindowsHook</a>,
 * and certain service handlers, for example). 
 */
public interface DLLCallback extends Callback {  
    /** Total number of DLL callbacks available for allocation. */
    int DLL_FPTRS = 16;
}
