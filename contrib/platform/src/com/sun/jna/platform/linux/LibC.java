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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.LibCAPI;

/**
 * LibC structures and functions unique to Linux
 */
public interface LibC extends LibCAPI, Library {
    String NAME = "c";
    LibC INSTANCE = Native.load(NAME, LibC.class);

    @FieldOrder({ "uptime", "loads", "totalram", "freeram", "sharedram", "bufferram", "totalswap", "freeswap", "procs",
            "totalhigh", "freehigh", "mem_unit", "_f" })
    class Sysinfo extends Structure {
        private static final int PADDING_SIZE = 20 - 2 * NativeLong.SIZE - 4;

        public NativeLong uptime; // Seconds since boot
        // 1, 5, and 15 minute load averages, divide by 2^16
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
        // Padding to 64 bytes
        public byte[] _f = new byte[PADDING_SIZE];

        /*
         * getFieldList and getFieldOrder are overridden because PADDING_SIZE
         * might be 0 - that is a GCC only extension and not supported by JNA
         * 
         * The dummy field at the end of the structure is just padding and so if
         * the field is the zero length array, it is stripped from the fields
         * and field order.
         */
        @Override
        protected List<Field> getFieldList() {
            List<Field> fields = new ArrayList<Field>(super.getFieldList());
            if (PADDING_SIZE == 0) {
                Iterator<Field> fieldIterator = fields.iterator();
                while (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    if ("_f".equals(field.getName())) {
                        fieldIterator.remove();
                    }
                }
            }
            return fields;
        }

        @Override
        protected List<String> getFieldOrder() {
            List<String> fieldOrder = new ArrayList<String>(super.getFieldOrder());
            if (PADDING_SIZE > 0) {
                fieldOrder.add("_f");
            }
            return fieldOrder;
        }
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
