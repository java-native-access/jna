/* The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 and 
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
 * Interface for the Winioctl.h header file.
 */
public interface Winioctl {

    /**
     * Retrieves the device type, device number, and, for a partitionable device, the partition number of a device.
     */
    int IOCTL_STORAGE_GET_DEVICE_NUMBER = 0x2D1080;

    /**
     * Contains information about a device. This structure is used by the IOCTL_STORAGE_GET_DEVICE_NUMBER control code.
     */
    public static class STORAGE_DEVICE_NUMBER extends Structure {

		public static class ByReference extends STORAGE_DEVICE_NUMBER implements
				Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

		public static final List<String> FIELDS = createFieldsOrder("DeviceType", "DeviceNumber", "PartitionNumber");

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

        public STORAGE_DEVICE_NUMBER() {
            super();
        }

        public STORAGE_DEVICE_NUMBER(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
