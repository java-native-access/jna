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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Interface for the Winioctl.h header file.
 *
 * @author Luke Quinane
 */
public interface Winioctl {
    /**
     * Retrieves extended information about the physical disk's geometry: type, number of cylinders, tracks per
     * cylinder, sectors per track, and bytes per sector.
     */
    int IOCTL_DISK_GET_DRIVE_GEOMETRY_EX = 0x700A0;

    /**
     * Retrieves the length of the specified disk, volume, or partition.
     */
    int IOCTL_DISK_GET_LENGTH_INFO = 0x7405C;

    /**
     * Returns the properties of a storage device or adapter.
     */
    int IOCTL_STORAGE_QUERY_PROPERTY = 0x2D1400;

    /**
     * Retrieves information about the types of media supported by a device.
     */
    int IOCTL_STORAGE_GET_MEDIA_TYPES_EX = 0x2D0C04;

    /**
     * Retrieves the device type, device number, and, for a partitionable device, the partition number of a device.
     */
    int IOCTL_STORAGE_GET_DEVICE_NUMBER = 0x2D1080;

    /**
     * Contains information about a device. This structure is used by the IOCTL_STORAGE_GET_DEVICE_NUMBER control code.
     */
    class STORAGE_DEVICE_NUMBER extends Structure {

        public static class ByReference extends STORAGE_DEVICE_NUMBER implements
                Structure.ByReference {
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

        protected List getFieldOrder() {
            return Arrays.asList("DeviceType", "DeviceNumber", "PartitionNumber");
        }
    }

    /**
     * Indicates the properties of a storage device or adapter to retrieve as the input buffer passed to the
     * IOCTL_STORAGE_QUERY_PROPERTY control code.
     */
    class STORAGE_PROPERTY_QUERY extends Structure {
        /**
         * Indicates whether the caller is requesting a device descriptor, an adapter descriptor, a write cache
         * property, a device unique ID (DUID), or the device identifiers
         * provided in the device's SCSI vital product data (VPD) page.
         */
        public /* STORAGE_PROPERTY_ID */ WinDef.DWORD propertyId;

        /**
         * Contains flags indicating the type of query to be performed as enumerated by the STORAGE_QUERY_TYPE
         * enumeration.
         */
        public /* STORAGE_QUERY_TYPE */ WinDef.DWORD queryType;

        /**
         * Contains an array of bytes that can be used to retrieve additional parameters for specific queries.
         */
        public byte[] additionalParameters = new byte[1];

        public static class ByReference extends STORAGE_PROPERTY_QUERY implements Structure.ByReference {
            public ByReference() {
            }
        }

        public STORAGE_PROPERTY_QUERY() {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("propertyId", "queryType", "additionalParameters");
        }
    }

    /**
     * Contains information about the media types supported by a device.
     */
    class GET_MEDIA_TYPES extends Structure {
        /**
         * The type of device. Values from 0 through 32,767 are reserved for use by Microsoft Corporation. Values from
         * 32,768 through 65,535 are reserved for use by other vendors.
         */
        public WinDef.DWORD deviceType;

        /**
         * The number of elements in the MediaInfo array.
         */
        public WinDef.DWORD mediaInfoCount;

        /**
         * A pointer to the first DEVICE_MEDIA_INFO structure in the array. There is one structure for each media type
         * supported by the device.
         */
        public WinDef.DWORD /* DEVICE_MEDIA_INFO */ mediaInfo;

        public static class ByReference extends GET_MEDIA_TYPES implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public GET_MEDIA_TYPES() {
        }

        public GET_MEDIA_TYPES(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("deviceType", "mediaInfoCount", "mediaInfo");
        }
    }

    /**
     * Used in conjunction with the IOCTL_STORAGE_QUERY_PROPERTY control code to retrieve the storage device descriptor
     * data for a device.
     */
    class STORAGE_DEVICE_DESCRIPTOR extends Structure {
        /**
         * Contains the size of this structure, in bytes. The value of this member will change as members are added to
         * the structure.
         */
        public WinDef.DWORD version;

        /**
         * Specifies the total size of the descriptor, in bytes, which may include vendor ID, product ID, product
         * revision, device serial number strings and bus-specific data which are appended to the structure.
         */
        public WinDef.DWORD size;

        /**
         * Specifies the device type as defined by the Small Computer Systems Interface (SCSI) specification.
         */
        public WinDef.BYTE deviceType;

        /**
         * Specifies the device type modifier, if any, as defined by the SCSI specification. If no device type modifier
         * exists, this member is zero.
         */
        public /* BOOLEAN */ WinDef.BYTE deviceTypeModifier;

        /**
         * Indicates when TRUE that the device's media (if any) is removable. If the device has no media, this member
         * should be ignored. When FALSE the device's media is not removable.
         */
        public /* BOOLEAN */ WinDef.BYTE removableMedia;

        /**
         * Indicates when TRUE that the device supports multiple outstanding commands (SCSI tagged queuing or
         * equivalent). When FALSE, the device does not support SCSI-tagged queuing or the equivalent.
         */
        public /* BOOLEAN */ WinDef.BYTE commandQueueing;

        /**
         * Specifies the byte offset from the beginning of the structure to a null-terminated ASCII string that contains
         * the device's vendor ID. If the device has no vendor ID, this member is zero.
         */
        public WinDef.DWORD vendorIdOffset;

        /**
         * Specifies the byte offset from the beginning of the structure to a null-terminated ASCII string that contains
         * the device's product ID. If the device has no product ID, this member is zero.
         */
        public WinDef.DWORD productIdOffset;

        /**
         * Specifies the byte offset from the beginning of the structure to a null-terminated ASCII string that contains
         * the device's product revision string. If the device has no product revision string, this member is zero.
         */
        public WinDef.DWORD productRevisionOffset;

        /**
         * Specifies the byte offset from the beginning of the structure to a null-terminated ASCII string that contains
         * the device's serial number. If the device has no serial number, this member is zero.
         */
        public WinDef.DWORD serialNumberOffset;

        /**
         * Specifies an enumerator value of type STORAGE_BUS_TYPE that indicates the type of bus to which the device is
         * connected. This should be used to interpret the raw device properties at the end of this structure (if any).
         */
        public /*STORAGE_BUS_TYPE*/ WinDef.UCHAR busType;

        /**
         * Indicates the number of bytes of bus-specific data that have been appended to this descriptor.
         */
        public WinDef.DWORD rawPropertiesLength;

        /**
         * Contains an array of length one that serves as a place holder for the first byte of the bus specific property
         * data.
         */
        public byte[] rawDeviceProperties = new byte[1];

        public static class ByReference extends STORAGE_DEVICE_DESCRIPTOR implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public STORAGE_DEVICE_DESCRIPTOR() {
        }

        public STORAGE_DEVICE_DESCRIPTOR(Pointer memory) {
            super(memory);
            read();

            // Allocate the buffer for the raw properties, and re-read
            rawDeviceProperties = new byte[rawPropertiesLength.intValue()];
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("version", "size", "deviceType", "deviceTypeModifier", "removableMedia",
                    "commandQueueing", "vendorIdOffset", "productIdOffset", "productRevisionOffset",
                    "serialNumberOffset", "busType", "rawPropertiesLength", "rawDeviceProperties");
        }
    }

    /**
     * Describes the extended geometry of disk devices and media.
     */
    class DISK_GEOMETRY_EX extends Structure {
        public static class ByReference extends DISK_GEOMETRY_EX implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public DISK_GEOMETRY_EX() {
        }

        public DISK_GEOMETRY_EX(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * A DISK_GEOMETRY structure.
         */
        public DISK_GEOMETRY geometry;

        /**
         * The disk size, in bytes.
         */
        public WinNT.LARGE_INTEGER diskSize;

        /**
         * Any additional data.
         */
        public WinDef.BYTE[] data = new WinDef.BYTE[0x200];

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "disk-geometry-ex[cylinders:%d tracksPerCylinder:%d sectorsPerTrack:%d" +
                    " bytesPerSector:%d] mediaType:%d diskSize:%d",
                    geometry.cylinders.longValue(), geometry.tracksPerCylinder.longValue(),
                    geometry.sectorsPerTrack.longValue(), geometry.bytesPerSector.longValue(),
                    geometry.mediaType.longValue(), diskSize.getValue());
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("geometry", "diskSize", "data");
        }
    }

    /**
     * Describes the geometry of disk devices and media.
     */
    class DISK_GEOMETRY extends Structure {
        public static class ByReference extends DISK_GEOMETRY implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public DISK_GEOMETRY() {
        }

        public DISK_GEOMETRY(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The number of cylinders.
         */
        public WinNT.LONGLONG cylinders;

        /**
         * The type of media.
         */
        public WinDef.DWORD mediaType;

        /**
         * The number of tracks per cylinder.
         */
        public WinDef.DWORD tracksPerCylinder;

        /**
         * The number of sectors per track.
         */
        public WinDef.DWORD sectorsPerTrack;

        /**
         * The number of bytes per sector.
         */
        public WinDef.DWORD bytesPerSector;

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "disk-geometry[cylinders:%d tracksPerCylinder:%d sectorsPerTrack:%d " +
                    "bytesPerSector:%d] mediaType:%d",
                    cylinders.longValue(), tracksPerCylinder.longValue(), sectorsPerTrack.longValue(),
                    bytesPerSector.longValue(), mediaType.longValue());
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("cylinders", "mediaType", "tracksPerCylinder", "sectorsPerTrack", "bytesPerSector");
        }
    }
}
