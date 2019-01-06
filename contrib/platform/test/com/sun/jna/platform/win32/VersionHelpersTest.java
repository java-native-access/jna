package com.sun.jna.platform.win32;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionHelpersTest {
    @Test
    public void testVersionHelpers() {
        // All windows versions should be higher than version 0.0!
        assertTrue(VersionHelpers.IsWindowsVersionOrGreater(0, 0, 0));
        // All windows versions should be lower than version Short.MAX_VALUE!
        assertFalse(VersionHelpers.IsWindowsVersionOrGreater(Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE));
        // These tests in order should be true until false; once false never
        // true again
        boolean lastVersionTest = true;
        boolean versionTest = VersionHelpers.IsWindowsXPOrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsXPSP1OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsXPSP2OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsXPSP3OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsVistaOrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsVistaSP1OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindowsVistaSP2OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindows7OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindows7SP1OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindows8OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindows8Point1OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);

        lastVersionTest = versionTest;
        versionTest = VersionHelpers.IsWindows10OrGreater();
        assertTrue((lastVersionTest == versionTest) || !versionTest);
    }
}

