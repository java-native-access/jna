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
 * Interface for the Winioctl.h header file.
 */
public interface Winioctl {

	// Devices
	public int FILE_DEVICE_BEEP                 = 0x00000001;
	public int FILE_DEVICE_CD_ROM               = 0x00000002;
	public int FILE_DEVICE_CD_ROM_FILE_SYSTEM   = 0x00000003;
	public int FILE_DEVICE_CONTROLLER           = 0x00000004;
	public int FILE_DEVICE_DATALINK             = 0x00000005;
	public int FILE_DEVICE_DFS                  = 0x00000006;
	public int FILE_DEVICE_DISK                 = 0x00000007;
	public int FILE_DEVICE_DISK_FILE_SYSTEM     = 0x00000008;
	public int FILE_DEVICE_FILE_SYSTEM          = 0x00000009;
	public int FILE_DEVICE_INPORT_PORT          = 0x0000000a;
	public int FILE_DEVICE_KEYBOARD             = 0x0000000b;
	public int FILE_DEVICE_MAILSLOT             = 0x0000000c;
	public int FILE_DEVICE_MIDI_IN              = 0x0000000d;
	public int FILE_DEVICE_MIDI_OUT             = 0x0000000e;
	public int FILE_DEVICE_MOUSE                = 0x0000000f;
	public int FILE_DEVICE_MULTI_UNC_PROVIDER   = 0x00000010;
	public int FILE_DEVICE_NAMED_PIPE           = 0x00000011;
	public int FILE_DEVICE_NETWORK              = 0x00000012;
	public int FILE_DEVICE_NETWORK_BROWSER      = 0x00000013;
	public int FILE_DEVICE_NETWORK_FILE_SYSTEM  = 0x00000014;
	public int FILE_DEVICE_NULL                 = 0x00000015;
	public int FILE_DEVICE_PARALLEL_PORT        = 0x00000016;
	public int FILE_DEVICE_PHYSICAL_NETCARD     = 0x00000017;
	public int FILE_DEVICE_PRINTER              = 0x00000018;
	public int FILE_DEVICE_SCANNER              = 0x00000019;
	public int FILE_DEVICE_SERIAL_MOUSE_PORT    = 0x0000001a;
	public int FILE_DEVICE_SERIAL_PORT          = 0x0000001b;
	public int FILE_DEVICE_SCREEN               = 0x0000001c;
	public int FILE_DEVICE_SOUND                = 0x0000001d;
	public int FILE_DEVICE_STREAMS              = 0x0000001e;
	public int FILE_DEVICE_TAPE                 = 0x0000001f;
	public int FILE_DEVICE_TAPE_FILE_SYSTEM     = 0x00000020;
	public int FILE_DEVICE_TRANSPORT            = 0x00000021;
	public int FILE_DEVICE_UNKNOWN              = 0x00000022;
	public int FILE_DEVICE_VIDEO                = 0x00000023;
	public int FILE_DEVICE_VIRTUAL_DISK         = 0x00000024;
	public int FILE_DEVICE_WAVE_IN              = 0x00000025;
	public int FILE_DEVICE_WAVE_OUT             = 0x00000026;
	public int FILE_DEVICE_8042_PORT            = 0x00000027;
	public int FILE_DEVICE_NETWORK_REDIRECTOR   = 0x00000028;
	public int FILE_DEVICE_BATTERY              = 0x00000029;
	public int FILE_DEVICE_BUS_EXTENDER         = 0x0000002a;
	public int FILE_DEVICE_MODEM                = 0x0000002b;
	public int FILE_DEVICE_VDM                  = 0x0000002c;
	public int FILE_DEVICE_MASS_STORAGE         = 0x0000002d;
	public int FILE_DEVICE_SMB                  = 0x0000002e;
	public int FILE_DEVICE_KS                   = 0x0000002f;
	public int FILE_DEVICE_CHANGER              = 0x00000030;
	public int FILE_DEVICE_SMARTCARD            = 0x00000031;
	public int FILE_DEVICE_ACPI                 = 0x00000032;
	public int FILE_DEVICE_DVD                  = 0x00000033;
	public int FILE_DEVICE_FULLSCREEN_VIDEO     = 0x00000034;
	public int FILE_DEVICE_DFS_FILE_SYSTEM      = 0x00000035;
	public int FILE_DEVICE_DFS_VOLUME           = 0x00000036;
	public int FILE_DEVICE_SERENUM              = 0x00000037;
	public int FILE_DEVICE_TERMSRV              = 0x00000038;
	public int FILE_DEVICE_KSEC                 = 0x00000039;
	public int FILE_DEVICE_FIPS                 = 0x0000003A;
	public int FILE_DEVICE_INFINIBAND           = 0x0000003B;
	public int FILE_DEVICE_VMBUS                = 0x0000003E;
	public int FILE_DEVICE_CRYPT_PROVIDER       = 0x0000003F;
	public int FILE_DEVICE_WPD                  = 0x00000040;
	public int FILE_DEVICE_BLUETOOTH            = 0x00000041;
	public int FILE_DEVICE_MT_COMPOSITE         = 0x00000042;
	public int FILE_DEVICE_MT_TRANSPORT         = 0x00000043;
	public int FILE_DEVICE_BIOMETRIC            = 0x00000044;
	public int FILE_DEVICE_PMI                  = 0x00000045;
	public int FILE_DEVICE_EHSTOR               = 0x00000046;
	public int FILE_DEVICE_DEVAPI               = 0x00000047;
	public int FILE_DEVICE_GPIO                 = 0x00000048;
	public int FILE_DEVICE_USBEX                = 0x00000049;
	public int FILE_DEVICE_CONSOLE              = 0x00000050;
	public int FILE_DEVICE_NFP                  = 0x00000051;
	public int FILE_DEVICE_SYSENV               = 0x00000052;
	public int FILE_DEVICE_VIRTUAL_BLOCK        = 0x00000053;
	public int FILE_DEVICE_POINT_OF_SERVICE     = 0x00000054;

	// Functions
	public int FSCTL_GET_COMPRESSION            = 15;
	public int FSCTL_SET_COMPRESSION            = 16;
	public int FSCTL_SET_REPARSE_POINT          = 41;
	public int FSCTL_GET_REPARSE_POINT          = 42;
	public int FSCTL_DELETE_REPARSE_POINT       = 43;

	// Methods
	public int METHOD_BUFFERED                  = 0;
	public int METHOD_IN_DIRECT                 = 1;
	public int METHOD_OUT_DIRECT                = 2;
	public int METHOD_NEITHER                   = 3;

	// Access
	public int FILE_ANY_ACCESS                  = 0;
	public int FILE_SPECIAL_ACCESS              = FILE_ANY_ACCESS;
	public int FILE_READ_ACCESS                 = 0x0001;    // file & pipe
	public int FILE_WRITE_ACCESS                = 0x0002;    // file & pipe

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
