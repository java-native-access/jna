/* Copyright (c) 2016 Minoru Sakamoto, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Minoru Sakamoto
 */
public interface EvtVariant {

    /**
     * Contains event data or property values.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385611(v=vs.85).aspx
     */
    public static class EVT_VARIANT extends Structure {

        public field1_union field1;

        public static class field1_union extends Union {

            /** A Boolean value. */
            public int BooleanVal;

            /** A signed 8-bit integer value. */
            public byte SByteVal;

            /** A signed 16-bit integer value. */
            public short Int16Val;

            /** A signed 32-bit integer value. */
            public int Int32Val;

            /** A signed 64-bit integer value. */
            public long Int64Val;

            /** An unsigned 8-bit integer value. */
            public byte ByteVal;

            /** An unsigned 16-bit integer value. */
            public char UInt16Val;

            /** An unsigned 32-bit integer value. */
            public int UInt32Val;

            /** An unsigned 64-bit integer value. */
            public long UInt64Val;

            /** A single precision real value. */
            public float SingleVal;

            /** A double precision real value. */
            public double DoubleVal;

            /** An 8-byte FILETIME value. */
            public long FileTimeVal;

            /** A SYSTEMTIME value. */
            public WinBase.SYSTEMTIME.ByReference SysTimeVal;

            /** A 16-byte GUID value. */
            public Guid.GUID.ByReference GuidVal;

            /** A null-terminated Unicode string. */
            public WinDef.CHARByReference StringVal;

            /** A null-terminated ANSI string value. */
            public Pointer AnsiStringVal;

            /** A pointer to a hexadecimal binary value. */
            public Pointer BinaryVal;

            /** A 4-byte ASCII value. A security identifier (SID) structure that uniquely identifies a user or group. */
            public Pointer SidVal;

            /**
             * A pointer address. The size of the address (4 bytes or 8 bytes) depends on whether the provider ran on
             * a 32-bit or 64-bit operating system.
             */
            public BaseTSD.SIZE_T SizeTVal;

            /** An EVT_HANDLE value. */
            public WinNT.HANDLE EvtHandleVal;

            /** A pointer to an array of Boolean values. */
            public IntByReference BooleanArr;

            /** A pointer to an array of signed 8-bit values. */
            public Pointer SByteArr;

            /** A pointer to an array of signed 16-bit values. */
            public ShortByReference Int16Arr;

            /** A pointer to an array of signed 32-bit values. */
            public IntByReference Int32Arr;

            /** A pointer to an array of signed 64-bit values. */
            public LongByReference Int64Arr;

            /** A pointer to an array of unsigned 8-bit values. */
            public Pointer ByteArr;

            /** A pointer to an array of unsigned 16-bit values. */
            public ShortByReference UInt16Arr;

            /** A pointer to an array of unsigned 32-bit values. */
            public IntByReference UInt32Arr;

            /** A pointer to an array of unsigned 64-bit values. */
            public LongByReference UInt64Arr;

            /** A pointer to an array of single precision real values. */
            public FloatByReference SingleArr;

            /** A pointer to an array of double precision real values. */
            public DoubleByReference DoubleArr;

            /** A pointer to an array of FILETIME values. */
            public WinBase.FILETIME.ByReference FileTimeArr;

            /** A pointer to an array of SYSTEMTIME values. */
            public WinBase.SYSTEMTIME.ByReference SysTimeArr;

            /** A pointer to an array of GUID values. */
            public Guid.GUID.ByReference GuidArr;

            /** A pointer to an array of null-terminated Unicode strings. */
            public PointerByReference StringArr;

            /** A pointer to an array of null-terminated ANSI strings. */
            public PointerByReference AnsiStringArr;

            /** A pointer to an array of 4-byte ASCII values. */
            public PointerByReference SidArr;

            /** A pointer to an array of size_t values. */
            public PointerByReference SizeTArr;

            /** An XML string value. */
            public char XmlVal;

            /** A pointer to an array of XML string values. */
            public WinDef.CHARByReference XmlValArr;

            public void use(Pointer m) {
                useMemory(m, 0);
            }

            public field1_union() {
                super();
            }

            public field1_union(Pointer peer) {
                super(peer);
            }

            protected field1_union newInstance() {
                return new field1_union();
            }

            protected ByReference newByReference() {
                return new ByReference();
            }

            protected ByValue newByValue() {
                return new ByValue();
            }

            public static class ByReference extends field1_union implements Structure.ByReference {
                public ByReference(Pointer p) {
                    super(p);
                }

                public ByReference() {
                    super();
                }

            }

            public static class ByValue extends field1_union implements Structure.ByValue {

            }
        }

        /**
         * The number of elements in the array of values. Use Count if the Type member has
         * the EVT_VARIANT_TYPE_ARRAY flag set.
         */
        public int Count;

        /**
         * A flag that specifies the data type of the variant. For possible values, see
         * the {@link Winevt.EVT_VARIANT_TYPE} enumeration.
         * The variant contains an array of values, if the EVT_VARIANT_TYPE_ARRAY flag is set. The members that end in
         * "Arr" contain arrays of values. For example, you would use the StringArr member to access the variant data
         * if the type is EvtVarTypeString and the EVT_VARIANT_TYPE_ARRAY flag is set.
         * You can use the {@link Winevt#EVT_VARIANT_TYPE_MASK} constant to mask out the array bit to determine
         * the variant's type.
         */
        public int Type;

        public EVT_VARIANT() {
            super(W32APITypeMapper.DEFAULT);
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList("field1", "Count", "Type");
        }

        public EVT_VARIANT(Pointer peer) {
            super(peer, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
        }

        protected EVT_VARIANT newInstance() {
            return new EVT_VARIANT();
        }

        public void use(Pointer m) {
            useMemory(m, 0);
        }

        protected ByReference newByReference() {
            return new ByReference();
        }

        protected ByValue newByValue() {
            return new ByValue();
        }

        public static class ByReference extends EVT_VARIANT implements Structure.ByReference {
            public ByReference(Pointer p) {
                super(p);
            }

            public ByReference() {
                super();
            }
        }

        public static class ByValue extends EVT_VARIANT implements Structure.ByValue {

        }
    }
}
