/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

public class TlbParameterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TlbParameterNotFoundException() {
        super();
    }

    public TlbParameterNotFoundException(String msg) {
        super(msg);
    }

    public TlbParameterNotFoundException(Throwable cause) {
        super(cause);
    }

    public TlbParameterNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
