/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Guid.GUID;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Ole32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Ole32UtilTest.class);
    }

    public void testGenerateGUID() {
        GUID guid1 = Ole32Util.generateGUID();
        GUID guid2 = Ole32Util.generateGUID();
        assertTrue(guid1 != guid2);
        assertTrue(Ole32Util.getStringFromGUID(guid1) != Ole32Util.getStringFromGUID(guid2));
    }

    public void testGetStringFromGUID() {
        assertEquals("{00000000-0000-0000-0000-000000000000}", Ole32Util.getStringFromGUID(
                new GUID()));
        assertFalse("{00000000-0000-0000-0000-000000000000}" == Ole32Util.getStringFromGUID(
                Ole32Util.generateGUID()));
    }

    public void testGetGUIDFromString() {
        GUID lpiid = Ole32Util.getGUIDFromString("{13709620-C279-11CE-A49E-444553540000}");
        assertEquals(0x13709620, lpiid.Data1);
        assertEquals(0xFFFFC279, lpiid.Data2);
        assertEquals(0x11CE, lpiid.Data3);
        assertEquals(0xFFFFFFA4, lpiid.Data4[0]);
        assertEquals(0xFFFFFF9E, lpiid.Data4[1]);
        assertEquals(0x44, lpiid.Data4[2]);
        assertEquals(0x45, lpiid.Data4[3]);
        assertEquals(0x53, lpiid.Data4[4]);
        assertEquals(0x54, lpiid.Data4[5]);
        assertEquals(0, lpiid.Data4[6]);
        assertEquals(0, lpiid.Data4[7]);
    }

    public void testGetGUIDToFromString() {
        GUID guid = Ole32Util.generateGUID();
        assertEquals(guid, Ole32Util.getGUIDFromString(
                Ole32Util.getStringFromGUID(guid)));
    }
}
