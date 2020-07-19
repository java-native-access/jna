/* Copyright (c) 2020 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.unix.aix;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Special treatment of shared objects inside AIX archive libraries and
 * 32/64-bit variants preclude loading within the library interfaces.
 * <p>
 * Package private as this should only be called by AIX libraries.
 */
final class SharedObjectLoader {

    private SharedObjectLoader() {
    }

    static Perfstat getPerfstatInstance() {
        Map<String, Object> options = getOptions();
        try {
            return Native.load("/usr/lib/libperfstat.a(shr_64.o)", Perfstat.class, options);
        } catch (UnsatisfiedLinkError e) {
            // failed 64 bit, try 32 bit
        }
        return Native.load("/usr/lib/libperfstat.a(shr.o)", Perfstat.class, options);
    }

    private static Map<String, Object> getOptions() {
        int RTLD_MEMBER = 0x40000; // allows "lib.a(obj.o)" syntax
        int RTLD_GLOBAL = 0x10000;
        int RTLD_LAZY = 0x4;
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Library.OPTION_OPEN_FLAGS, RTLD_MEMBER | RTLD_GLOBAL | RTLD_LAZY);
        return Collections.unmodifiableMap(options);
    }
}
