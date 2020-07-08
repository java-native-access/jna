/*
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

import com.sun.jna.win32.W32APITypeMapper;

import junit.framework.TestCase;

public class ShlwapiTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ShlwapiTest.class);
    }

    public void testPathIsUNC() {
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\path1\\path2"));
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("acme\\\\path4\\\\path5"));
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("\\path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("c:\\path1"));
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) { // UNC is only available on UNICODE API
            assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\path1\\path2"));
            assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\path1"));
            assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\"));
            assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\c:\\path1"));
        }
    }
}
