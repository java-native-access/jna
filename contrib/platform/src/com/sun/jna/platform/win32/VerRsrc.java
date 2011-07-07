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

public interface VerRsrc extends StdCallLibrary {

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
            useMemory(memory);
            read();
        }

        public int Signature;
        public int StrucVersion;
        public int FileVersionMS;
        public int FileVersionLS;
        public int ProductVersionMS;
        public int ProductVersionLS;
        public int FileFlagsMask;
        public int FileFlags;
        public int FileOS;
        public int FileType;
        public int FileSubtype;
        public int FileDateMS;
        public int FileDateLS;
    }
}
