/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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
package com.sun.jna.platform.unix;

import com.sun.jna.Native;
import com.sun.jna.platform.AbstractPlatformTestSupport;

/**
 * @author Lyor Goldstein
 */
public abstract class AbstractUnixTestSupport extends AbstractPlatformTestSupport {
    protected AbstractUnixTestSupport() {
        super();
    }

    public static int getErrno() {
        return Native.getLastError();
    }

    /**
     * Checks if result is zero. If not, then throws an assertion error with the {@code errno} value
     *
     * @param message The failure message to prepend if necessary
     * @param result The syscall result code
     */
    public static void assertSuccessResult(String message, int result) {
        if (result != 0) {
            int errno = getErrno();
            fail(message + ": result=" + result + ", errno=" + errno);
        }
    }
}
