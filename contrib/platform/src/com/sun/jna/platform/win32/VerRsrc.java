/* The contents of this file is dual-licensed under 2 
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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Interface for the VerRsrc.h header file.
 */
public interface VerRsrc {

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

        public static final List<String> FIELDS = createFieldsOrder(
                "dwSignature", "dwStrucVersion",
                "dwFileVersionMS", "dwFileVersionLS",
                "dwProductVersionMS", "dwProductVersionLS",
                "dwFileFlagsMask", "dwFileFlags", "dwFileOS",
                "dwFileType", "dwFileSubtype",
                "dwFileDateMS", "dwFileDateLS");

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

        public VS_FIXEDFILEINFO() {
            super();
        }

        public VS_FIXEDFILEINFO(Pointer memory) {
            super(memory);
            read();
        }

        public int getFileVersionMajor() {
            return dwFileVersionMS.intValue() >>> 16;
        }

        public int getFileVersionMinor() {
            return dwFileVersionMS.intValue() & 0xffff;
        }

        public int getFileVersionRevision() {
            return dwFileVersionLS.intValue() >>> 16;
        }

        public int getFileVersionBuild() {
            return dwFileVersionLS.intValue() & 0xffff;
        }

        public int getProductVersionMajor() {
            return dwProductVersionMS.intValue() >>> 16;
        }

        public int getProductVersionMinor() {
            return dwProductVersionMS.intValue() & 0xffff;
        }

        public int getProductVersionRevision() {
            return dwProductVersionLS.intValue() >>> 16;
        }

        public int getProductVersionBuild() {
            return dwProductVersionLS.intValue() & 0xffff;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
