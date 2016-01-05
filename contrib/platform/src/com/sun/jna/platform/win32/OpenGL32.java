/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
