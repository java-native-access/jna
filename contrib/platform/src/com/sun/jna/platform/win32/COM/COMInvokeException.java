/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Exception class for error origination from an COM invoke
 */
public class COMInvokeException extends COMException {
    private static final long serialVersionUID = 1L;

    private final Integer wCode;
    private final String source;
    private final String description;
    private final String helpFile;
    private final Integer helpContext;
    private final Integer scode; 
    private final Integer errorArg;

    /**
     * Instantiates a new automation exception.
     */
    public COMInvokeException() {
        this("", (Throwable) null);
    }

    /**
     * Instantiates a new automation exception.
     *
     * @param message
     *            the message
     */
    public COMInvokeException(String message) {
        this(message, (Throwable) null);
    }


    /**
     * Instantiates a new automation exception.
     *
     * @param cause
     *            the cause
     */
    public COMInvokeException(Throwable cause) {
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
    public COMInvokeException(String message, Throwable cause) {
        super(message, cause);
        this.description = null;
        this.errorArg = null;
        this.helpContext = null;
        this.helpFile = null;
        this.scode = null;
        this.source = null;
        this.wCode = null;
    }

    /**
     * Instantiates a new automation exception.
     * 
     * @param message exception message
     * @param hresult hresult of the invoke call
     * @param errorArg the position of the argument that caused the error
     * @param description The exception description to display. If no
     * description is available, use null.
     * @param helpContext The help context ID.
     * @param helpFile The fully qualified help file path. If no Help is
     * available, use null.
     * @param scode A return value that describes the error. Either this field
     * or wCode (but not both) must be filled in; the other must be set to 0.
     * (16-bit Windows versions only.)
     * @param source The name of the exception source. Typically, this is an
     * application name. This field should be filled in by the implementor of
     * IDispatch.
     * @param wCode The error code. Error codes should be greater than 1000.
     * Either this field or the scode field must be filled in; the other must be
     * set to 0.
     */
    public COMInvokeException(String message, HRESULT hresult, Integer errorArg,
            String description, Integer helpContext, String helpFile,
            Integer scode, String source, Integer wCode) {
        super(formatMessage(hresult, message, errorArg), hresult);
        this.description = description;
        this.errorArg = errorArg;
        this.helpContext = helpContext;
        this.helpFile = helpFile;
        this.scode = scode;
        this.source = source;
        this.wCode = wCode;
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
     * @return The error code. Error codes should be greater than 1000. Either
     * this field or the scode field must be filled in; the other must be set to
     * 0. It is NULL if no exception info was created
     */
    public Integer getWCode() {
        return wCode;
    }

    /**
     * @return The name of the exception source. Typically, this is an
     * application name. This field should be filled in by the implementor of
     * IDispatch. NULL if no exception info was created
     */
    public String getSource() {
        return source;
    }

    /**
     * @return The exception description to display. If no description is
     * available, use null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The fully qualified help file path. If no Help is available, use
     * null.
     */
    public String getHelpFile() {
        return helpFile;
    }

    /**
     * @return The help context ID or NULL if not present
     */
    public Integer getHelpContext() {
        return helpContext;
    }

    /**
     * @return A return value that describes the error. Either this field or
     * wCode (but not both) must be filled in; the other must be set to 0.
     * (16-bit Windows versions only.) NULL if no exception info was created.
     */
    public Integer getScode() {
        return scode;
    }

    private static String formatMessage(HRESULT hresult, String message, Integer errArg) {
        if (hresult.intValue() == WinError.DISP_E_TYPEMISMATCH
                || hresult.intValue() == WinError.DISP_E_PARAMNOTFOUND) {
            return message + " (puArgErr=" + errArg + ")";
        } else {
            return message;
        }
    }
}
