/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Exception class for all COM related classes.
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final EXCEPINFO pExcepInfo;

    private final Integer errorArg;

    private final HRESULT hresult;

    /**
     * Instantiates a new automation exception.
     */
    public COMException() {
        this("", (Throwable) null);
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     */
    public COMException(String message) {
        this(message, (Throwable) null);
    }


    /**
     * Instantiates a new automation exception.
     *
     * @param cause
     *            the cause
     */
    public COMException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public COMException(String message, Throwable cause) {
        super(message, cause);
        this.errorArg = null;
        this.hresult = null;
        this.pExcepInfo = null;
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     * @param hresult
     *            HRESULT that lead to the creation of the COMException
     */
    public COMException(String message, HRESULT hresult) {
        this(message, null, null, hresult);
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     * @param pExcepInfo
     *            the excep info
     * @param argErr
     *            the errorArg
     * @param hresult
     *            HRESULT that lead to the creation of the COMException
     */
    public COMException(String message, EXCEPINFO pExcepInfo,
            Integer argErr, HRESULT hresult) {
        super(formatMessage(message, argErr));
        this.pExcepInfo = pExcepInfo;
        this.errorArg = argErr;
        this.hresult = hresult;
    }

    /**
     * @return retrieve EXCEPINFO if present
     */
    public EXCEPINFO getExcepInfo() {
        return pExcepInfo;
    }

    /**
     * Gets the arg err.
     *
     * @return the arg err
     */
    public Integer getErrorArg() {
        return errorArg;
    }

    /**
     * @return the HRESULT that lead to thie COMException or NULL if the COMException as not directly caused by a native call
     */
    public HRESULT getHresult() {
        return hresult;
    }

    /**
     * @param errorCode
     * @return true if the exception has an associated HRESULT and that HRESULT
     * matches the supplied error code
     */
    public boolean matchesErrorCode(int errorCode) {
        return hresult != null && hresult.intValue() == errorCode;
    }

    private static String formatMessage(String message, Integer errArg) {
        if(errArg != null) {
            return message + " (puArgErr=" + errArg + ")";
        } else {
            return message;
        }
    }
}
