/*
 * This library is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 */
package com.sun.jna;

/** Provide simplified platform information. */
public final class Platform {
    private static final int UNSPECIFIED = -1;
    private static final int MAC = 0;
    private static final int LINUX = 1;
    private static final int WINDOWS = 2;
    private static final int SOLARIS = 3;
    private static final int FREEBSD = 4;
    private static final int osType;
    
    static {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            osType = LINUX;
        } 
        else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            osType = MAC;
        }
        else if (osName.startsWith("Windows")) {
            osType = WINDOWS;
        }
        else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            osType = SOLARIS;
        }
        else if (osName.startsWith("FreeBSD")) {
            osType = FREEBSD;
        }
        else {
            osType = UNSPECIFIED;
        }
    }
    private Platform() { }
    public static final boolean isMac() {
        return osType == MAC;
    }
    public static final boolean isLinux() {
        return osType == LINUX;
    }
    public static final boolean isWindows() {
        return osType == WINDOWS;
    }
    public static final boolean isSolaris() {
        return osType == SOLARIS;
    }
    public static final boolean isFreeBSD() {
        return osType == FREEBSD;
    }
    public static final boolean isX11() {
        // TODO: check FS or do some other X11-specific test
        return !Platform.isWindows() && !Platform.isMac();
    }
}
