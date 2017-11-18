/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.LastErrorException;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Win32 exception.
 * @author dblock[at]dblock[dot]org
 */
public class Win32Exception extends LastErrorException {

    private static final long serialVersionUID = 1L;

    private HRESULT _hr;

    /**
     * Returns the error code of the error.
     * @return HRESULT value
     */
    public HRESULT getHR() {
    	return _hr;
    }

    /**
     * New Win32 exception from an error code, usually obtained from {@code GetLastError.}
     * @param code Error code.
     */
    public Win32Exception(int code) {
    	this(code, W32Errors.HRESULT_FROM_WIN32(code));
    }


    /**
     * New Win32 exception from HRESULT.
     * @param hr HRESULT
     */
    public Win32Exception(HRESULT hr) {
        this(W32Errors.HRESULT_CODE(hr.intValue()), hr);
    }

    protected Win32Exception(int code, HRESULT hr) {
        this(code, hr, Kernel32Util.formatMessage(hr));
    }

    protected Win32Exception(int code, HRESULT hr, String msg) {
        super(code, msg);
        _hr = hr;
    }
    
    private static Method addSuppressedMethod = null;
    static {
        try {
            addSuppressedMethod = Throwable.class.getMethod("addSuppressed", Throwable.class);
        } catch (NoSuchMethodException ex) {
            // This is the case for JDK < 7
        } catch (SecurityException ex) {
            Logger.getLogger(Win32Exception.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", ex);
        }
    }
    
    void addSuppressedReflected(Throwable exception) {
        if(addSuppressedMethod == null) {
            // Make this a NOOP on an unsupported JDK
            return;
        }
        try {
            addSuppressedMethod.invoke(this, exception);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Failed to call addSuppressedMethod", ex);
        }
    }
}
