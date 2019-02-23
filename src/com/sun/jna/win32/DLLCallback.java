/* Copyright (c) 2012 Timothy Wall, All Rights Reserved
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

/** Indicate that the callback needs to appear to be within a DLL.  The
 * effective DLL module handle may be obtained by TODO.
 * Use this interface when your callback must reside within a DLL (hooks set
 * via <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms644990(v=vs.85).aspx">SetWindowsHook</a>,
 * and certain service handlers, for example).
 */
public interface DLLCallback extends Callback {
    /** Total number of DLL callbacks available for allocation. */
    @java.lang.annotation.Native
    int DLL_FPTRS = 16;
}
