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

public interface Winioctl extends StdCallLibrary {

    public static class STORAGE_DEVICE_NUMBER extends Structure {

        public static class ByReference extends STORAGE_DEVICE_NUMBER implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public STORAGE_DEVICE_NUMBER() {
        }

        public STORAGE_DEVICE_NUMBER(Pointer memory) {
            useMemory(memory);
            read();
        }

        public int DeviceType;
        public int DeviceNumber;
        public int PartitionNumber;
    }
}
