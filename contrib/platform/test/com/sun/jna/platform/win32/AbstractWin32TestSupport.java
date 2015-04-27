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

import com.sun.jna.platform.AbstractPlatformTestSupport;

/**
 * @author lgoldstein
 */
public abstract class AbstractWin32TestSupport extends AbstractPlatformTestSupport {
    protected AbstractWin32TestSupport() {
        super();
    }

    /**
     * Checks if the API call result is {@code true}. If not, then calls
     * {@link Kernel32#GetLastError()} and fails with the error code.
     * <B>Note:</B> if the error code is {@link WinError#ERROR_SUCCESS}
     * then an <I>&quot;unknown reason code&quot;</I> is reported
     * @param message Message to display if call failed
     * @param result The API call result
     */
    public static final void assertCallSucceeded(String message, boolean result) {
        if (result) {
            return;
        }
        
        int hr=Kernel32.INSTANCE.GetLastError();
        if (hr == WinError.ERROR_SUCCESS) {
            fail(message + " failed with unknown reason code");
        } else {
            fail(message + " failed: hr=0x" + Integer.toHexString(hr));
        }
    }
}
