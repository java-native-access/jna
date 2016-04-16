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

import java.sql.Date;
import java.util.Map;

import org.junit.Test;

/**
 * @author Lyor Goldstein
 */
public class LibCTest extends AbstractUnixTestSupport {
    public LibCTest() {
        super();
    }

    @Test
    public void testGetenv() {
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> ee : env.entrySet()) {
            String name = ee.getKey();
            String expected = ee.getValue();
            String actual = LibC.INSTANCE.getenv(name);
            assertEquals(name, expected, actual);
        }
    }

    @Test
    public void testSetenv() {
        String name = getCurrentTestName();
        try {
            String expected = new Date(System.currentTimeMillis()).toString();
            assertSuccessResult("setenv", LibC.INSTANCE.setenv(name, expected, 1));
            assertEquals("Mismatched values", expected, LibC.INSTANCE.getenv(name));
            assertSuccessResult("unsetenv", LibC.INSTANCE.unsetenv(name));
        } finally {
            LibC.INSTANCE.unsetenv(name);
        }
    }
    
    @Test
    public void testGetLoadAvg() {
        double[] loadavg = new double[3];
        int retval = LibC.INSTANCE.getloadavg(loadavg, 3);
        assertEquals(retval, 3);
        assertTrue(loadavg[0] >= 0);
        assertTrue(loadavg[1] >= 0);
        assertTrue(loadavg[2] >= 0);
    }
}
