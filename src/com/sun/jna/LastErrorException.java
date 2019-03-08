/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

/**
 * Exception representing a non-zero error code returned in either
 * <code><a href="http://www.opengroup.org/onlinepubs/009695399/functions/errno.html">errno</a></code>
 * or <code><a href="http://msdn.microsoft.com/en-us/library/ms679360(VS.85).aspx">GetLastError()</a></code>.
*/
public class LastErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int errorCode;

    private static String formatMessage(int code) {
        return Platform.isWindows()
            ? "GetLastError() returned " + code
            : "errno was " + code;
    }

    private static String parseMessage(String m) {
        try {
            return formatMessage(Integer.parseInt(m));
        } catch(NumberFormatException e) {
            return m;
        }
    }

    /**
     * @return The reported error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    public LastErrorException(String msg) {
        super(parseMessage(msg.trim()));
        try {
            if (msg.startsWith("[")) {
                msg = msg.substring(1, msg.indexOf("]"));
            }
            this.errorCode = Integer.parseInt(msg);
        } catch(NumberFormatException e) {
            this.errorCode = -1;
        }
    }

    public LastErrorException(int code) {
        this(code, formatMessage(code));
    }

    protected LastErrorException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }
}