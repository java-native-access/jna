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
 * Interface for the Winioctl.h header file.
 */
public interface Winioctl extends StdCallLibrary {

    /**
     * Retrieves the device type, device number, and, for a partitionable device, the partition number of a device.
     */
    int IOCTL_STORAGE_GET_DEVICE_NUMBER = 0x2D1080;

    /**
     * Contains information about a device. This structure is used by the IOCTL_STORAGE_GET_DEVICE_NUMBER control code.
     */
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
            super(memory);
            read();
        }

        /**
         * The type of device. Values from 0 through 32,767 are reserved for use by Microsoft. Values from 32,768
         * through 65,535 are reserved for use by other vendors.
         */
        public int DeviceType;

        /**
         * The number of this device.
         */
        public int DeviceNumber;

        /**
         * The partition number of the device, if the device can be partitioned. Otherwise, this member is -1.
         */
        public int PartitionNumber;
    }
}
