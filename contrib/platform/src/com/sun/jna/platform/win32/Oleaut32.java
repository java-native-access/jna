/*
 * Copyright (c) 2010 Digital Rapids Corp., All rights reserved.
 */

/* Copyright (c) 2010 Timothy Wall, All Rights Reserved
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
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Oleaut32.dll Interface.
 * @author scott.palmer
 */
public interface Oleaut32 extends StdCallLibrary {
	
    Oleaut32 INSTANCE = (Oleaut32) Native.loadLibrary(
			"Oleaut32", Oleaut32.class, W32APIOptions.UNICODE_OPTIONS);

    /**
     * This function allocates a new string and copies the passed string into it. 
     * @param sz 
     * 	Null-terminated UNICODE string to copy. 
     * @return
     *  Null if there is insufficient memory or if a null pointer is passed in.
     */
    Pointer SysAllocString(String sz);
    
    /**
     * This function frees a string allocated previously by SysAllocString, 
     * SysAllocStringByteLen, SysReAllocString, SysAllocStringLen, or 
     * SysReAllocStringLen.
     * @param bstr
     *  Unicode string that was allocated previously, or NULL. Setting this parameter 
     *  to NULL causes the function to simply return.
     */
    void SysFreeString(Pointer bstr);
}
