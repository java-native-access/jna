/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

public class GCWaits {
    private GCWaits() {}

    public static void gcRun() {
        try {
            Thread.sleep(GC_WAIT_INTERVAL);
        } catch (InterruptedException ex) {
            return;
        }
        System.gc();
        System.runFinalization();
        Memory.purge();
    }

    /** Amount of time to wait for GC, in ms */
    public static final int GC_WAIT_TIMEOUT = 5000;
    /** How long to sleep between GC */
    public static final int GC_WAIT_INTERVAL = 10;
    /** Number of times to attempt GC */
    public static final int GC_WAITS = GC_WAIT_TIMEOUT / GC_WAIT_INTERVAL;
}