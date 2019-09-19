/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

/**
 * Exception encapsulating {@code IOReturn} I/O Kit Error Return Values, defined
 * as {@code kern_return_t} values in {@code IOKit/IOReturn.h}
 * <p>
 * The return value supplies information in three separate bit fields: the high
 * 6 bits specify the system in which the error occurred, the next 12 bits
 * specify the subsystem, and the final 14 bits specify the error code itself.
 */
public class IOReturnException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int ioReturn;

    /**
     * New exception from {@code kern_return_t}
     *
     * @param kr
     *            The return value
     */
    public IOReturnException(int kr) {
        this(kr, formatMessage(kr));
    }

    /**
     * New exception from {@code kern_return_t} with specified message
     *
     * @param kr
     *            The return value
     * @param msg
     *            The exception message
     */
    protected IOReturnException(int kr, String msg) {
        super(msg);
        this.ioReturn = kr;
    }

    /**
     * @return the IOReturn code
     */
    public int getIOReturnCode() {
        return ioReturn;
    }

    /**
     * The high 6 bits of the return value encode the system.
     *
     * @param kr
     *            The return value
     * @return the system value
     */
    public static int getSystem(int kr) {
        return (kr >> 26) & 0x3f;
    }

    /**
     * The middle 12 bits of the return value encode the subsystem.
     *
     * @param kr
     *            The return value
     * @return the subsystem value
     */
    public static int getSubSystem(int kr) {
        return (kr >> 14) & 0xfff;
    }

    /**
     * The low 14 bits of the return value encode the return code.
     *
     * @param kr
     *            The return value
     * @return the return code
     */
    public static int getCode(int kr) {
        return kr & 0x3fff;
    }

    private static String formatMessage(int kr) {
        return "IOReturn error code: " + kr + " (system=" + getSystem(kr) + ", subSystem=" + getSubSystem(kr)
                + ", code=" + getCode(kr) + ")";
    }
}
