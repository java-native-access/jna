/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import java.awt.Rectangle;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from Windef.h (various macros and types). 
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinDef extends StdCallLibrary {

    int MAX_PATH = 260;

    /**
     * 16-bit unsigned integer.
     */
    public static class WORD extends IntegerType {
        public WORD() {
            this(0);
        }

        public WORD(long value) {
            super(2, value);
        }
    }

    /**
     * 32-bit unsigned integer.
     */
    public static class DWORD extends IntegerType {
        public DWORD() {
            this(0);
        }

        public DWORD(long value) {
            super(4, value);
        }
		
        /**
         * Low WORD.
         * @return
         *  Low WORD.
         */
        public WORD getLow() {
            return new WORD(longValue() & 0xFF);
        }
				
        /**
         * High WORD.
         * @return
         *  High WORD.
         */
        public WORD getHigh() {
            return new WORD((longValue() >> 16) & 0xFF);
        }
    }

    /**
     * 32-bit signed integer.
     */
    public static class LONG extends IntegerType {
        public LONG() {
            this(0);
        }

        public LONG(long value) {
            super(Native.LONG_SIZE, value);
        }
    }

    /**
     * Handle to a device context (DC).
     */
    public static class HDC extends HANDLE {
        public HDC() {

        }

        public HDC(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to an icon.
     */
    public static class HICON extends HANDLE {
        public HICON() {

        }

        public HICON(Pointer p) {
            super(p);
        }
    }
	
    /**
     * Handle to a cursor. 
     */
    public static class HCURSOR extends HICON {
        public HCURSOR() {

        }

        public HCURSOR(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a cursor. 
     */
    public static class HMENU extends HANDLE {
        public HMENU() {

        }

        public HMENU(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a pen.
     */
    public static class HPEN extends HANDLE {
        public HPEN() {

        }

        public HPEN(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a resource. 
     */
    public static class HRSRC extends HANDLE {
        public HRSRC() {

        }

        public HRSRC(Pointer p) {
            super(p);
        }
    }
	
    /**
     * Handle to a palette. 
     */
    public static class HPALETTE extends HANDLE {
        public HPALETTE() {

        }

        public HPALETTE(Pointer p) {
            super(p);
        }
    }
	
    /**
     * Handle to a bitmap.
     */
    public static class HBITMAP extends HANDLE {
        public HBITMAP() {

        }

        public HBITMAP(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a region.
     */
    public static class HRGN extends HANDLE {
        public HRGN() {

        }

        public HRGN(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a window.
     */
    public static class HWND extends HANDLE {
        public HWND() {

        }

        public HWND(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to an instance.
     */
    public static class HINSTANCE extends HANDLE {

    }

    /**
     * Handle to a module. The value is the base address of the module.
     */
    public static class HMODULE extends HINSTANCE {

    }

    /**
     * Handle to a font.
     */
    public static class HFONT extends HANDLE {
        public HFONT() {

        }

        public HFONT(Pointer p) {
            super(p);
        }
    }

    /**
     * Message parameter. 
     */
    public static class LPARAM extends LONG_PTR {
        public LPARAM() {
            this(0);
        }

        public LPARAM(long value) {
            super(value);
        }
    }

    /**
     * Signed result of message processing. 
     */
    public static class LRESULT extends LONG_PTR {
        public LRESULT() {
            this(0);
        }

        public LRESULT(long value) {
            super(value);
        }
    }

    /**
     * Unsigned INT_PTR.
     */
    public static class UINT_PTR extends IntegerType {
        public UINT_PTR() {
            super(Pointer.SIZE);
        }

        public UINT_PTR(long value) {
            super(Pointer.SIZE, value);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
     * Message parameter. 
     */
    public static class WPARAM extends UINT_PTR {
        public WPARAM() {
            this(0);
        }

        public WPARAM(long value) {
            super(value);
        }
    }
	
    public class RECT extends Structure {
        public int left;
        public int top;
        public int right;
        public int bottom;
        
        public Rectangle toRectangle() {
            return new Rectangle(left, top, right-left, bottom-top);
        }
        
        public String toString() {
            return "[(" + left + "," + top + ")(" + right + "," + bottom + ")]";
        }
    }
    
    /**
     * 64-bit unsigned integer.
     */
    public static class ULONGLONG extends IntegerType {
        public ULONGLONG() {
            this(0);
        }

        public ULONGLONG(long value) {
            super(8, value);
        }
    }
	
    /**
     * 64-bit unsigned integer.
     */
    public static class DWORDLONG extends IntegerType {
        public DWORDLONG() {
            this(0);
        }

        public DWORDLONG(long value) {
            super(8, value);
        }
    }
}
