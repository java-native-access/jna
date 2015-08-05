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
import com.sun.jna.platform.win32.WinNT.HANDLE;

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
        
        int hr = Kernel32.INSTANCE.GetLastError();
        if (hr == WinError.ERROR_SUCCESS) {
            fail(message + " failed with unknown reason code");
        } else {
            fail(message + " failed: hr=" + hr + " - 0x" + Integer.toHexString(hr));
        }
    }
    
    /**
     * Checks if the status code is ERROR_SUCCESS
     * @param message Message to display if code is an error
     * @param statusCode Status code to check
     * @param showHex If status code is not error success then show it as HEX
     */
    public static final void assertErrorSuccess(String message, int statusCode, boolean showHex) {
        if (showHex) {
            if (statusCode != WinError.ERROR_SUCCESS) {
                fail(message + " - failed - hr=0x" + Integer.toHexString(statusCode));
            }
        } else {
            assertEquals(message, WinError.ERROR_SUCCESS, statusCode);
        }
    }
    
    /**
     * Makes sure that the handle argument is not {@code null} or {@link WinBase#INVALID_HANDLE_VALUE}.
     * If invalid handle detected, then it invokes {@link Kernel32#GetLastError()}
     * in order to display the error code
     * @param message Message to display if bad handle
     * @param handle The {@link HANDLE} to test
     * @return The same as the input handle if good handle - otherwise does
     * not return and throws an assertion error
     */
    public static final HANDLE assertValidHandle(String message, HANDLE handle) {
        if ((handle == null) || WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr == WinError.ERROR_SUCCESS) {
                fail(message + " failed with unknown reason code");
            } else {
                fail(message + " failed: hr=" + hr + " - 0x" + Integer.toHexString(hr));
            }
        }

        return handle;
    }
}
