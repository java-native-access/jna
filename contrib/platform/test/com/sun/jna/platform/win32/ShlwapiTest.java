package com.sun.jna.platform.win32;

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
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\path1\\path2"));
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\path1"));
        assertEquals(true, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\UNC\\"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("\\path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("c:\\path1"));
        assertEquals(false, Shlwapi.INSTANCE.PathIsUNC("\\\\?\\c:\\path1"));
    }
}
