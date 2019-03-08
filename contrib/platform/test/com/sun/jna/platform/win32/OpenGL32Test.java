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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI.PIXELFORMATDESCRIPTOR;

/**
 * @author drrobison@openroadsconsulting.com
 */
public class OpenGL32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OpenGL32Test.class);
    }

    public void testGetStringGLVersion() {
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

        // create the OpenGL context
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        String glString = OpenGL32.INSTANCE.glGetString(GL.GL_VERSION);
        System.out.println("GL_VERSION="+glString);
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        assertNotNull("Could not get GL_VERSION", glString);
    }

    public void testGetStringGLRenderer() {
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

        // create the OpenGL context
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        String glString = OpenGL32.INSTANCE.glGetString(GL.GL_RENDERER);
        System.out.println("GL_RENDERER="+glString);
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        //assertNotNull("Could not get GL_RENDERER", glString);
    }

    public void testGetStringGLVendor() {
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

        // create the OpenGL context
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        String glString = OpenGL32.INSTANCE.glGetString(GL.GL_VENDOR);
        System.out.println("GL_VENDOR="+glString);
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        assertNotNull("Could not get GL_VENDOR", glString);
    }

    public void testGetStringGLExtensions() {
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

        // create the OpenGL context
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        String glString = OpenGL32.INSTANCE.glGetString(GL.GL_EXTENSIONS);
        System.out.println("GL_EXTENSIONS="+glString);
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        assertNotNull("Could not get GL_EXTENSIONS", glString);
    }

    public void testGetCurrentContext() {
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

        // create the OpenGL context
        WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hdc);
        OpenGL32.INSTANCE.wglMakeCurrent(hdc, hGLRC);
        WinDef.HGLRC currentContext = OpenGL32.INSTANCE.wglGetCurrentContext();
        OpenGL32.INSTANCE.wglDeleteContext(hGLRC);

        // destroy the window
        User32.INSTANCE.ReleaseDC(hWnd, hdc);
        User32Util.destroyWindow(hWnd);

        assertNotNull("Could not get current context", currentContext);
    }
}
