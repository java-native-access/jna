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

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Ported from Wdm.h.
 * Microsoft Windows DDK.
 * @author dblock[at]dblock.org
 */
public interface Wdm {

    /**
     * The KEY_BASIC_INFORMATION structure defines a subset of
     * the full information that is available for a registry key.
     */
    public static class KEY_BASIC_INFORMATION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("LastWriteTime", "TitleIndex", "NameLength", "Name");

        /**
         * The last time the key or any of its values changed.
         */
        public long LastWriteTime;
        /**
         * Device and intermediate drivers should ignore this member.
         */
        public int TitleIndex;
        /**
         * Specifies the size in bytes of the following name.
         */
        public int NameLength;
        /**
         * A string of Unicode characters naming the key.
         * The string is not null-terminated.
         */
        public char[] Name;

        public KEY_BASIC_INFORMATION() {
            super();
        }

        public KEY_BASIC_INFORMATION(int size) {
            NameLength = size - 16; // write time, title index and name length
            Name = new char[NameLength];
            allocateMemory();
        }

        public KEY_BASIC_INFORMATION(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        /**
         * Name of the key.
         * @return String.
         */
        public String getName() {
            return Native.toString(Name);
        }

        @Override
        public void read() {
            super.read();
            Name = new char[NameLength / 2];
            readField("Name");
        }
    }

    /**
     * The KEY_INFORMATION_CLASS enumeration type represents
     * the type of information to supply about a registry key.
     */
    public abstract class KEY_INFORMATION_CLASS {
        public static final int KeyBasicInformation = 0;
        public static final int KeyNodeInformation = 1;
        public static final int KeyFullInformation = 2;
        public static final int KeyNameInformation = 3;
        public static final int KeyCachedInformation = 4;
        public static final int KeyVirtualizationInformation = 5;
    }
}
