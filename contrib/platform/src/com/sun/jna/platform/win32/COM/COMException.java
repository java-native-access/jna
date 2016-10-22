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
import com.sun.jna.ptr.IntByReference;

// TODO: Auto-generated Javadoc
/**
 * Exception class for all COM related classes.
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** The p excep info. */
    private EXCEPINFO pExcepInfo;

    /** The pu arg err. */
    private IntByReference puArgErr;

    private int uArgErr;

    /**
     * Instantiates a new automation exception.
     */
    public COMException() {
        super();
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
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     */
    public COMException(String message) {
        super(message);
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     * @param pExcepInfo
     *            the excep info
     * @param puArgErr
     *            the pu arg err
     */
    public COMException(String message, EXCEPINFO pExcepInfo,
            IntByReference puArgErr) {
        super(message + " (puArgErr=" + (null==puArgErr?"":puArgErr.getValue()) + ")");
        this.pExcepInfo = pExcepInfo;
        this.puArgErr = puArgErr;
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param cause
     *            the cause
     */
    public COMException(Throwable cause) {
        super(cause);
    }

    /**
     * Gets the excep info.
     *
     * @return the excep info
     */
    public EXCEPINFO getExcepInfo() {
        return pExcepInfo;
    }

    /**
     * Gets the arg err.
     *
     * @return the arg err
     */
    public IntByReference getArgErr() {
        return puArgErr;
    }

    public int getuArgErr() {
        return uArgErr;
    }

    public void setuArgErr(int uArgErr) {
        this.uArgErr = uArgErr;
    }
}
