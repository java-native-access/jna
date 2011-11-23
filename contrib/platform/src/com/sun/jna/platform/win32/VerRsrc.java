/* This library is free software; you can redistribute it and/or
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
import com.sun.jna.win32.StdCallLibrary;

/**
 * Interface for the VerRsrc.h header file.
 */
public interface VerRsrc extends StdCallLibrary {

    /**
     * Contains version information for a file. This information is language and code page independent.
     */
    public static class VS_FIXEDFILEINFO extends Structure {

        public static class ByReference extends VS_FIXEDFILEINFO implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public VS_FIXEDFILEINFO() {
        }

        public VS_FIXEDFILEINFO(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * Contains the value 0xFEEF04BD. This is used with the szKey member of the VS_VERSIONINFO structure when
         * searching a file for the VS_FIXEDFILEINFO structure.
         */
        public WinDef.DWORD dwSignature;

        /**
         * The binary version number of this structure. The high-order word of this member contains the major version
         * number, and the low-order word contains the minor version number.
         */
        public WinDef.DWORD dwStrucVersion;

        /**
         * The most significant 32 bits of the file's binary version number. This member is used with dwFileVersionLS to
         * form a 64-bit value used for numeric comparisons.
         */
        public WinDef.DWORD dwFileVersionMS;

        /**
         * The least significant 32 bits of the file's binary version number. This member is used with dwFileVersionMS
         * to form a 64-bit value used for numeric comparisons.
         */
        public WinDef.DWORD dwFileVersionLS;

        /**
         * The most significant 32 bits of the binary version number of the product with which this file was
         * distributed. This member is used with dwProductVersionLS to form a 64-bit value used for numeric comparisons.
         */
        public WinDef.DWORD dwProductVersionMS;

        /**
         * The least significant 32 bits of the binary version number of the product with which this file was
         * distributed. This member is used with dwProductVersionMS to form a 64-bit value used for numeric comparisons.
         */
        public WinDef.DWORD dwProductVersionLS;

        /**
         * Contains a bitmask that specifies the valid bits in dwFileFlags. A bit is valid only if it was defined when
         * the file was created.
         */
        public WinDef.DWORD dwFileFlagsMask;

        /**
         * Contains a bitmask that specifies the Boolean attributes of the file. This member can include one or more of
         * the following values.
         */
        public WinDef.DWORD dwFileFlags;

        /**
         * The operating system for which this file was designed.
         */
        public WinDef.DWORD dwFileOS;

        /**
         * The general type of file.
         */
        public WinDef.DWORD dwFileType;

        /**
         * The function of the file. The possible values depend on the value of dwFileType.
         */
        public WinDef.DWORD dwFileSubtype;

        /**
         * The most significant 32 bits of the file's 64-bit binary creation date and time stamp.
         */
        public WinDef.DWORD dwFileDateMS;

        /**
         * The least significant 32 bits of the file's 64-bit binary creation date and time stamp.
         */
        public WinDef.DWORD dwFileDateLS;
    }
}
