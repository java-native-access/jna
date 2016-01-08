/* Copyright (c) 2013 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

import junit.framework.TestCase;

//@SuppressWarnings("unused")
public class PlatformTest extends TestCase {
    
    public void testOSPrefix() {
        assertEquals("Wrong resource path", "win32-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWS,
                                                             "x86", "Windows"));
        assertEquals("Wrong resource path Windows/i386", "win32-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWS,
                                                             "i386", "Windows"));
        assertEquals("Wrong resource path Windows CE/arm", "w32ce-arm",
                     Platform.getNativeLibraryResourcePrefix(Platform.WINDOWSCE,
                                                             "arm", "Windows CE"));
        assertEquals("Wrong resource path Mac/x86", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "x86", "Darwin"));
        assertEquals("Wrong resource path Mac/x86", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "i386", "Darwin"));
        assertEquals("Wrong resource path Mac/x86_64", "darwin",
                     Platform.getNativeLibraryResourcePrefix(Platform.MAC,
                                                             "x86_64", "Mac"));
        assertEquals("Wrong resource path Solaris/sparc", "sunos-sparc",
                     Platform.getNativeLibraryResourcePrefix(Platform.SOLARIS,
                                                             "sparc", "Solaris"));
        assertEquals("Wrong resource path SunOS/sparcv9", "sunos-sparcv9",
                     Platform.getNativeLibraryResourcePrefix(Platform.SOLARIS,
                                                             "sparcv9", "SunOS"));
        assertEquals("Wrong resource path Linux/i386", "linux-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "i386", "Linux/Gnu"));
        assertEquals("Wrong resource path Linux/x86", "linux-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "x86", "Linux"));
        assertEquals("Wrong resource path Linux/x86", "linux-x86-64",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "x86_64", "Linux"));
        assertEquals("Wrong resource path Linux/x86", "linux-x86-64",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "amd64", "Linux"));
        assertEquals("Wrong resource path Linux/ppc", "linux-ppc",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "powerpc", "Linux"));
        assertEquals("Wrong resource path Linux/sparcv9", "linux-sparcv9",
                     Platform.getNativeLibraryResourcePrefix(Platform.LINUX,
                                                             "sparcv9", "Linux"));
        assertEquals("Wrong resource path OpenBSD/x86", "openbsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.OPENBSD,
                                                             "x86", "OpenBSD"));
        assertEquals("Wrong resource path FreeBSD/x86", "freebsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.FREEBSD,
                                                             "x86", "FreeBSD"));
        assertEquals("Wrong resource path GNU/kFreeBSD/x86", "kfreebsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.KFREEBSD,
                                                             "x86", "GNU/kFreeBSD"));
        assertEquals("Wrong resource path NetBSD/x86", "netbsd-x86",
                     Platform.getNativeLibraryResourcePrefix(Platform.NETBSD,
                                                             "x86", "NetBSD"));
        assertEquals("Wrong resource path Linux/armv7l (android)", "android-arm",
                     Platform.getNativeLibraryResourcePrefix(Platform.ANDROID,
                                                             "armv7l", "Linux"));
        
        assertEquals("Wrong resource path other/other", "name-ppc",
                     Platform.getNativeLibraryResourcePrefix(Platform.UNSPECIFIED,
                                                             "PowerPC", "Name Of System"));
        
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(PlatformTest.class);
    }
}
