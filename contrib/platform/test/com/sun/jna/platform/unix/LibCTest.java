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
import com.sun.jna.Platform;
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
        if (Platform.isAIX()) {
            System.out.println("Skip testGetLoadAvg - getloadavg is not implemented on AIX");
            return;
        }
        double[] loadavg = new double[3];
        int retval = LibC.INSTANCE.getloadavg(loadavg, 3);
        assertEquals(retval, 3);
        assertTrue(loadavg[0] >= 0);
        assertTrue(loadavg[1] >= 0);
        assertTrue(loadavg[2] >= 0);
    }

    @Test
    public void testGethostnameGetdomainname() {
        // This needs visual inspection ...
        byte[] buffer = new byte[256];
        LibC.INSTANCE.gethostname(buffer, buffer.length);
        String hostname = Native.toString(buffer);
        System.out.println("Hostname: " + hostname);
        assertNotNull(hostname);
        LibC.INSTANCE.getdomainname(buffer, buffer.length);
        String domainname = Native.toString(buffer);
        System.out.println("Domainname: " + domainname);
        assertNotNull(domainname);
    }
}
