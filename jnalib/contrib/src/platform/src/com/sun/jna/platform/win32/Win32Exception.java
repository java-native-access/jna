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
