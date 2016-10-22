/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.win32.StdCallLibrary;

/**
 * opengl32.dll Interface.
 */
public interface OpenGL32 extends StdCallLibrary {
    OpenGL32 INSTANCE = Native.loadLibrary("opengl32", OpenGL32.class);

    /**
     * The glGetString function returns a string describing the current OpenGL connection.
     *
     * @param name
     *            One of the following symbolic constants.
     * @return The glGetString function returns a pointer to a static string describing some aspect of the current OpenGL connection.
     */
    String glGetString(int name);

    /**
     * The wglCreateContext function creates a new OpenGL rendering context, which is suitable for drawing on the device
     * referenced by hdc. The rendering context has the same pixel format as the device context.
     *
     * @param windowDC
     *            Handle to a device context for which the function creates a suitable OpenGL rendering context.
     * @return handle to an OpenGL rendering context
     */
    WinDef.HGLRC wglCreateContext(HDC windowDC);

    /**
     * The wglGetCurrentContext function obtains a handle to the current OpenGL rendering context of the calling thread.
     *
     * @return If the calling thread has a current OpenGL rendering context, wglGetCurrentContext returns a
     *         handle to that rendering context. Otherwise, the return value is NULL.
     */
    WinDef.HGLRC wglGetCurrentContext();

    /**
     * The wglMakeCurrent function makes a specified OpenGL rendering context the calling thread's current rendering
     * context. All subsequent OpenGL calls made by the thread are drawn on the device identified by hdc.
     *
     * @param windowDC
     *            Handle to a device context. Subsequent OpenGL calls made by the calling thread are drawn on the
     *            device identified by hdc.
     * @param hglrc
     *            Handle to an OpenGL rendering context that the function sets as the calling thread's rendering context.
     * @return true if successful
     */
    boolean wglMakeCurrent(HDC windowDC, WinDef.HGLRC hglrc);

    /**
     * The wglDeleteContext function deletes a specified OpenGL rendering context.
     *
     * @param hglrc
     *            Handle to an OpenGL rendering context that the function will delete.
     * @return true if successful
     */
    boolean wglDeleteContext(WinDef.HGLRC hglrc);

    /**
     * The wglGetProcAddress function returns the address of an OpenGL extension function for use with the
     * current OpenGL rendering context.
     *
     * @param lpszProc
     *            Points to a null-terminated string that is the name of the extension function.
     *            The name of the extension function must be identical to a corresponding function implemented by OpenGL.
     * @return When the function succeeds, the return value is the address of the extension function.
     */
    Pointer wglGetProcAddress(String lpszProc);
}
