/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

/** Exception representing a non-zero error code returned in either
    <code><a href="http://www.opengroup.org/onlinepubs/009695399/functions/errno.html">errno</a></code> or <code><a href="http://msdn.microsoft.com/en-us/library/ms679360(VS.85).aspx">GetLastError()</a></code>.
*/
public class LastErrorException extends RuntimeException {
    private static String formatMessage(int code) {
        return Platform.isWindows()
            ?"GetLastError() returned " + code
            :"errno was " + code;
    }
    private static String parseMessage(String m) {
        try {
            return formatMessage(Integer.parseInt(m));
        }
        catch(NumberFormatException e) {
            return m;
        }
    }
    public int errorCode;
    public LastErrorException(String msg) {
        super(parseMessage(msg));
        try {
            this.errorCode = Integer.parseInt(msg);
        }
        catch(NumberFormatException e) {
            this.errorCode = -1;
        }
    }
    public LastErrorException(int code) {
        super(formatMessage(code));
        this.errorCode = code;
    }
}