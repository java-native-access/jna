/* Copyright (c) 2013 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class PlatformTest extends TestCase {
    
    public void testOSPrefix() {
        assertEquals("Wrong resource path", "win32-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWS,
                                                             "x86", "Windows", false));
        assertEquals("Wrong resource path Windows/i386", "win32-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWS,
                                                             "i386", "Windows", false));
        assertEquals("Wrong resource path Windows CE/arm", "w32ce-arm",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWSCE,
                                                             "arm", "Windows CE", false));
        assertEquals("Wrong resource path Mac/x86", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "x86", "Darwin", false));
        assertEquals("Wrong resource path Mac/x86", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "i386", "Darwin", false));
        assertEquals("Wrong resource path Mac/x86_64", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "x86_64", "Mac", false));
        assertEquals("Wrong resource path Solaris/sparc", "sunos-sparc",
                     Platform.getNativeLibraryResourcePrefix(Platform.SOLARIS,
                                                             "sparc", "Solaris", false));
        assertEquals("Wrong resource path SunOS/sparcv9", "sunos-sparcv9",
                     Platform.getNativeLibraryResourcePrefix(Platform.SOLARIS,
                                                             "sparcv9", "SunOS", false));
        assertEquals("Wrong resource path Linux/i386", "linux-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "i386", "Linux/Gnu", false));
        assertEquals("Wrong resource path Linux/x86", "linux-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "x86", "Linux", false));
        assertEquals("Wrong resource path Linux/x86", "linux-x86-64",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "x86_64", "Linux", false));
        assertEquals("Wrong resource path Linux/x86", "linux-x86-64",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "amd64", "Linux", false));
        assertEquals("Wrong resource path Linux/ppc", "linux-ppc",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "powerpc", "Linux", false));
        assertEquals("Wrong resource path Linux/sparcv9", "linux-sparcv9",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "sparcv9", "Linux", false));
        assertEquals("Wrong resource path Linux/arm (hardfloat)", "linux-arm",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "arm", "Linux/Gnu", false));
        assertEquals("Wrong resource path Linux/arm (softfloat)", "linux-armel",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "arm", "Linux/Gnu", true));
        assertEquals("Wrong resource path OpenBSD/x86", "openbsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.OPENBSD,
                                                             "x86", "OpenBSD", false));
        assertEquals("Wrong resource path FreeBSD/x86", "freebsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.FREEBSD,
                                                             "x86", "FreeBSD", false));
        assertEquals("Wrong resource path GNU/kFreeBSD/x86", "kfreebsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.KFREEBSD,
                                                             "x86", "GNU/kFreeBSD", false));
        assertEquals("Wrong resource path NetBSD/x86", "netbsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.NETBSD,
                                                             "x86", "NetBSD", false));
        assertEquals("Wrong resource path Linux/armv7l (android)", "android-arm",
                     Platform.getNativeLibraryResourcePrefix(Platform.ANDROID,
                                                             "armv7l", "Linux", false));
        
        assertEquals("Wrong resource path other/other", "name-ppc",
                     Platform.getNativeLibraryResourcePrefix(Platform.UNSPECIFIED,
                                                             "PowerPC", "Name Of System", false));
        
    }

    public void testSystemProperty() {
        String demoOverride = "demoOverride";
        assertFalse(demoOverride.equals(Platform.getNativeLibraryResourcePrefix()));
        
        System.setProperty("jna.prefix", demoOverride);
        assertTrue(demoOverride.equals(Platform.getNativeLibraryResourcePrefix()));
        
        System.clearProperty("jna.prefix");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PlatformTest.class);
    }
}
