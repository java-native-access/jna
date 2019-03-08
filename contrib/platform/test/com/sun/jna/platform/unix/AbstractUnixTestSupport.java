/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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
