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

package com.sun.jna;

public interface GCWaits {
    /** Amount of time to wait for GC, in ms */
    int GC_WAIT_TIMEOUT = 5000;
    /** How long to sleep between GC */
    int GC_WAIT_INTERVAL = 10;
    /** Number of times to attempt GC */
    int GC_WAITS = GC_WAIT_TIMEOUT / GC_WAIT_INTERVAL;
}