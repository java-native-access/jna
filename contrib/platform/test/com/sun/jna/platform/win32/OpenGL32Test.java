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
