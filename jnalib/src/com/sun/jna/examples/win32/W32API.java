/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.examples.win32;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/** Base type for most W32 API libraries.  Provides standard options
 * for unicode/ASCII mappings.  Set the system property <code>w32.ascii</code>
 * to <code>true</code> to default to the ASCII mappings.
 */
public interface W32API extends StdCallLibrary, W32Errors {
    
    /** Standard options to use the unicode version of a w32 API. */
    Map UNICODE_OPTIONS = new HashMap() {
        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    };
    /** Standard options to use the ASCII/MBCS version of a w32 API. */
    Map ASCII_OPTIONS = new HashMap() {
        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
        }
    };
    Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;
    
    public static class HANDLE extends PointerType { 
        public HANDLE() { }
        public HANDLE(Pointer p) { super(p); }
    };
    
    public static class HDC extends HANDLE { }
    public static class HICON extends HANDLE { }
    public static class HBITMAP extends HANDLE { }
    public static class HRGN extends HANDLE { }
    public static class HWND extends HANDLE { }
    public static class HINSTANCE extends HANDLE { }
    public static class HMODULE extends HINSTANCE { }
    
    HANDLE INVALID_HANDLE_VALUE = new HANDLE(Pointer.PM1) {
        public void setPointer(Pointer p) { 
            throw new UnsupportedOperationException("Immutable reference");
        }
    };
    
    public static class LONG_PTR extends IntegerType { 
    	public LONG_PTR() { this(0); }
    	public LONG_PTR(long value) { super(Pointer.SIZE, value); }
    }
    public static class LPARAM extends LONG_PTR { 
    	public LPARAM() { this(0); }
    	public LPARAM(long value) { super(value); }
    } 
    public static class LRESULT extends LONG_PTR { 
    	public LRESULT() { this(0); }
    	public LRESULT(long value) { super(value); }
    } 
    public static class ULONG_PTR extends IntegerType {
    	public ULONG_PTR() { super(Pointer.SIZE); }
    	public ULONG_PTR(long value) { super(Pointer.SIZE, value); }
    }
}
