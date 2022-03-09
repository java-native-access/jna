/* Copyright (c) 2022 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Native;

/**
 * Exception encapsulating {@code Kstat2} Error Return Values, defined as
 * {@code kstat2_status} values in {@code kstat2.h}
 */
public class Kstat2StatusException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int kstat2Status;

    /**
     * New exception from {@code kstat2_status}
     *
     * @param ks
     *            The return value
     */
    public Kstat2StatusException(int ks) {
        this(ks, formatMessage(ks));
    }

    /**
     * New exception from {@code kstat2_status} with specified message
     *
     * @param ks
     *            The return value
     * @param msg
     *            The exception message
     */
    protected Kstat2StatusException(int ks, String msg) {
        super(msg);
        this.kstat2Status = ks;
    }

    /**
     * @return the Kstat2Status code
     */
    public int getKstat2Status() {
        return kstat2Status;
    }

    private static String formatMessage(int ks) {
        String status = Kstat2.INSTANCE.kstat2_status_string(ks);
        if (ks == Kstat2.KSTAT2_S_SYS_FAIL) {
            status += " (errno=" + Native.getLastError() + ")";
        }
        return "Kstat2Status error code " + ks + ": " + status;
    }
}
