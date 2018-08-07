/* Copyright (c) 2017 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.LibCAPI;

/**
 * <I>libc</I> API
 * 
 * @author Daniel Widdis
 */
public interface LibC extends LibCAPI, Library {
    String NAME = "c";
    LibC INSTANCE = Native.load(NAME, LibC.class);

    @FieldOrder({ "uptime", "loads", "totalram", "freeram", "sharedram", "bufferram", "totalswap", "freeswap", "procs",
            "totalhigh", "freehigh", "mem_unit", "_f" })
    class Sysinfo extends Structure {
        public NativeLong uptime; // Seconds since boot
        // 1, 5, and 15 minute load averages
        public NativeLong[] loads = new NativeLong[3];
        public NativeLong totalram; // Total usable main memory size
        public NativeLong freeram; // Available memory size
        public NativeLong sharedram; // Amount of shared memory
        public NativeLong bufferram; // Memory used by buffers
        public NativeLong totalswap; // Total swap space size
        public NativeLong freeswap; // swap space still available
        public short procs; // Number of current processes
        public NativeLong totalhigh; // Total high memory size
        public NativeLong freehigh; // Available high memory size
        public int mem_unit; // Memory unit size in bytes
        // Padding is based on the size of NativeLong. When the size is 4 we
        // need 8 bytes of padding. When this size is 8, zero padding is needed
        // but we are not allowed to declare an array of size zero. It's safe to
        // declare extra padding in the structure; these bytes simply will not
        // be written on 64-bit systems.
        public byte[] _f = new byte[8]; // padding to 64 bytes
    }

    /**
     * sysinfo() provides a simple way of getting overall system statistics.
     * This is more portable than reading /dev/kmem.
     * 
     * @param info
     *            A Sysinfo structure which will be populated
     * @return On success, zero is returned. On error, -1 is returned, and errno
     *         is set appropriately.
     */
    int sysinfo(Sysinfo info);
}
