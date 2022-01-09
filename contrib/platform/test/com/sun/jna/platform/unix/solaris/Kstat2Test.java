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

import com.sun.jna.Platform;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2Handle;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2Map;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2MatcherList;
import com.sun.jna.platform.unix.solaris.Kstat2.Kstat2NV;

import junit.framework.TestCase;

/**
 * Exercise the {@link Kstat2} class.
 */
public class Kstat2Test extends TestCase {

    public void testKstat2() {
        if (Platform.isSolaris()) {

            try {
                assertNotNull(Kstat2.INSTANCE);
            } catch (UnsatisfiedLinkError e) {
                // Kstat2 is only available in Solaris 11.4 or later.
                // If the library fails to load, gracefully exit.
                return;
            }

            // Fetch a string and (long) integer using name match
            Kstat2MatcherList matchers = new Kstat2MatcherList();
            try {
                matchers.addMatcher(Kstat2.KSTAT2_M_STRING, "kstat:/system/cpu/0/info");
                Kstat2Handle handle = new Kstat2Handle();
                try {
                    Kstat2Map map = handle.lookupMap("kstat:/system/cpu/0/info");
                    assertTrue(map.getValue("vendor_id") instanceof String);
                    Kstat2NV nv = map.mapGet("vendor_id");
                    assertEquals(Kstat2.KSTAT2_NVVT_STR, nv.type);
                    assertEquals(Kstat2.KSTAT2_NVK_SYS, nv.kind);
                    assertEquals(Kstat2.KSTAT2_NVF_NONE, nv.flags);

                    assertTrue(map.getValue("clock_MHz") instanceof Long);
                    nv = map.mapGet("clock_MHz");
                    assertEquals(Kstat2.KSTAT2_NVVT_INT, nv.type);
                    assertEquals(Kstat2.KSTAT2_NVK_SYS, nv.kind);
                    assertEquals(Kstat2.KSTAT2_NVF_NONE, nv.flags);
                } finally {
                    handle.close();
                }
            } finally {
                matchers.free();
            }

            // Fetch an array of longs using glob match
            matchers = new Kstat2MatcherList();
            try {
                matchers.addMatcher(Kstat2.KSTAT2_M_GLOB, "kstat:/pm/cpu/*/pstate");
                Kstat2Handle handle = new Kstat2Handle(matchers);
                try {
                    Kstat2Map map = handle.lookupMap("kstat:/pm/cpu/0/pstate");
                    assertTrue(map.getValue("supported_frequencies") instanceof long[]);
                    Kstat2NV nv = map.mapGet("supported_frequencies");
                    assertEquals(Kstat2.KSTAT2_NVVT_INTS, nv.type);
                    assertEquals(Kstat2.KSTAT2_NVK_SYS, nv.kind);
                    assertEquals(Kstat2.KSTAT2_NVF_NONE, nv.flags);
                } finally {
                    handle.close();
                }
            } finally {
                matchers.free();
            }
        }
    }
}
