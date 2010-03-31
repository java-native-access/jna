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
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.W32API.HRESULT;

/**
 * Win32 exception.
 */
public class Win32Exception extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private HRESULT _hr;
    
    /**
     * Returns the error code of the error.
     * @return
     *  Error code.
     */
    public HRESULT getHR() {
    	return _hr;
    }

    /**
     * New Win32 exception from HRESULT.
     * @param hr
     *  HRESULT
     */
    public Win32Exception(HRESULT hr) {
        super(Kernel32Util.formatMessageFromHR(hr));
        _hr = hr;    	
    }

    /**
     * New Win32 exception from an error code, usually obtained from GetLastError.
     * @param code
     *  Error code.
     */
    public Win32Exception(int code) {
    	this(W32Errors.HRESULT_FROM_WIN32(code));
    }
}
