/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.wince;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinNT;

/** Definition <code>coredll.dll</code>.
    Add other win32 interface mappings as needed.
 */
public interface CoreDLL extends WinNT {

    CoreDLL INSTANCE = (CoreDLL)
        Native.loadLibrary("coredll", CoreDLL.class, 
                           W32APIOptions.UNICODE_OPTIONS);

}
