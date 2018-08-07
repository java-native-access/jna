/* Copyright (c) 2017 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.linux;

import org.junit.Test;

import com.sun.jna.platform.linux.LibC.Sysinfo;

import junit.framework.TestCase;

/**
 * Exercise the {@link LibC} class.
 *
 * @author widdis@gmail.com
 */
public class LibCTest extends TestCase {

    @Test
    public void testSysinfo() {
        Sysinfo info = new Sysinfo();
        assertEquals(0, LibC.INSTANCE.sysinfo(info));

        // Get loadavg for comparison (rounds to nearest hundredth)
        double[] loadavg = new double[3];
        LibC.INSTANCE.getloadavg(loadavg, 3);
        // Sysinfo loadavg longs must be divided by 2^16
        for (int i = 0; i < 3; i++) {
            assertEquals(loadavg[i], info.loads[i].longValue() / (double) (1 << 16), 0.02);
        }
        assertTrue(info.uptime.longValue() > 0);
        assertTrue(info.totalram.longValue() > 0);
        assertTrue(info.freeram.longValue() <= info.totalram.longValue());
        assertTrue(info.freeswap.longValue() <= info.totalswap.longValue());
    }
}
