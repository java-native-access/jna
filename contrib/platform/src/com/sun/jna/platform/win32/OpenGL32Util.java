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

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HGLRCByReference;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI.PIXELFORMATDESCRIPTOR;

/**
 * opengl32 utility API.
 */
public abstract class OpenGL32Util {

    /**
     * Return a procedure function pointer
     * @param procName the procedure name
     * @return the function
     */
    public static Function wglGetProcAddress(String procName) {
        Pointer funcPointer = OpenGL32.INSTANCE.wglGetProcAddress("wglEnumGpusNV");
        return (funcPointer == null) ? null : Function.getFunction(funcPointer);
    }

    /**
     * Count GPUs
     * @return the number of available GPUs
     */
    public static int countGpusNV() {
        // create a dummy window
        HWND hWnd = User32Util.createWindow("Message", null, 0, 0, 0, 0, 0, null, null, null, null);
        HDC hdc = User32.INSTANCE.GetDC(hWnd);

        // set a compatible pixel format
        PIXELFORMATDESCRIPTOR.ByReference pfd = new PIXELFORMATDESCRIPTOR.ByReference();
        pfd.nVersion = 1;
        pfd.dwFlags = WinGDI.PFD_DRAW_TO_WINDOW | WinGDI.PFD_SUPPORT_OPENGL | WinGDI.PFD_DOUBLEBUFFER;
        pfd.iPixelType = WinGDI.PFD_TYPE_RGBA;
        pfd.cColorBits = 24;
        pfd.cDepthBits = 16;
        pfd.iLayerType = WinGDI.PFD_MAIN_PLANE;
        GDI32.INSTANCE.SetPixelFormat(hdc, GDI32.INSTANCE.ChoosePixelFormat(hdc, pfd), pfd);

        // create the OpenGL context to get function address
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        Pointer funcPointer = OpenGL32.INSTANCE.wglGetProcAddress("wglEnumGpusNV");
        Function fncEnumGpusNV = (funcPointer == null) ? null : Function.getFunction(funcPointer);
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        // abort if the nVidia extensions are not present
        if (fncEnumGpusNV == null) return 0;

        // enumerate nVidia adapters
        HGLRCByReference hGPU = new HGLRCByReference();
        for (int i = 0; i < 16; i++) {
            Boolean ok = (Boolean) fncEnumGpusNV.invoke(Boolean.class, new Object[] { Integer.valueOf(i), hGPU, });
            if (!ok.booleanValue()) return i;
        }

        return 0;
    }
}
