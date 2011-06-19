package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;

public class SP_DEVICE_INTERFACE_DATA extends Structure {

    public static class ByReference extends SP_DEVINFO_DATA implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public SP_DEVICE_INTERFACE_DATA() {
        cbSize = size();
    }

    public SP_DEVICE_INTERFACE_DATA(Pointer memory) {
        useMemory(memory);
        read();
    }

    public int cbSize;
    public GUID InterfaceClassGuid;
    public int Flags;
    public Pointer Reserved;
}