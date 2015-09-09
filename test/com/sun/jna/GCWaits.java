package com.sun.jna;

public interface GCWaits {
    /** Amount of time to wait for GC, in ms */
    int GC_WAIT_TIMEOUT = 5000;
    /** How long to sleep between GC */
    int GC_WAIT_INTERVAL = 10;
    /** Number of times to attempt GC */
    int GC_WAITS = GC_WAIT_TIMEOUT / GC_WAIT_INTERVAL;
}