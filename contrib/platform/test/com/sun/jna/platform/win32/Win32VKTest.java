/*
 * Copyright (c) 2019 Keve Müller
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0.
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
package com.sun.jna.platform.win32;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test for Win32VK enumeration.
 */
public final class Win32VKTest extends TestCase {
    public static final void main(final String[] args) {
        junit.textui.TestRunner.run(User32UtilTest.class);
    }

    /**
     * All valid codes 0..255 map to a enumeration value and its code matches.
     */
    @Test
    public void testContinuity() {
        for (int code = 0; code < 256; code++) {
            Win32VK vk = Win32VK.fromValue(code);
            assertEquals(code, vk.code);
        }
    }

    /**
     * Assert that every value starts with VK_ and special VK_UNASSIGNED_XX and
     * VK_RESERVED_XX enumeration values match their respective code value.
     */
    @Test
    public void testNaming() {
        for (Win32VK vk : Win32VK.values()) {
            assertTrue(vk.name().startsWith("VK_"));
            if (vk.name().startsWith("VK_UNASSIGNED_")) {
                int nameValue = Integer.valueOf(vk.name().substring(14), 16).intValue();
                assertEquals(vk.code, nameValue);
            } else if (vk.name().startsWith("VK_RESERVED_")) {
                int nameValue = Integer.valueOf(vk.name().substring(12), 16).intValue();
                assertEquals(vk.code, nameValue);
            }
        }
    }

    /**
     * Assert that the introducedVersion is matching well known numbers.
     */
    @Test
    public void testVersion() {
        for (Win32VK vk : Win32VK.values()) {
            switch (vk.introducedVersion) {
                case 0:
                case 0x0400:
                case 0x0500:
                case 0x0604:
                    break;
                default:
                    fail("Unexpected version 0x" + Integer.toHexString(vk.introducedVersion));
            }
        }
    }

    /**
     * Assert that every illegal codes throw corresponding exception.
     */
    public void testNoSuchElement() {
        try {
            Win32VK.valueOf("NO_SUCH_VK");
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            Win32VK.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            // pass
        }

        try {
            Win32VK.fromValue(-1);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            Win32VK.fromValue(256);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

}