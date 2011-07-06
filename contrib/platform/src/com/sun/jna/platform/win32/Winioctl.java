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
