package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.structures.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.structures.SP_DEVINFO_DATA;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface SetupApi extends StdCallLibrary {

    SetupApi INSTANCE = (SetupApi)
        Native.loadLibrary("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);

    public static GUID GUID_DEVINTERFACE_DISK = new GUID(new byte[]
    { 
        0x07, 0x63, (byte) 0xf5, 0x53, (byte) 0xbf, (byte) 0xb6, (byte) 0xd0, 0x11,
        (byte) 0x94, (byte) 0xf2, 0x00, (byte) 0xa0, (byte) 0xc9, (byte) 0x1e, (byte) 0xfb, (byte) 0x8b
    });

    public int DIGCF_DEFAULT = 0x1;
    public int DIGCF_PRESENT = 0x2;
    public int DIGCF_ALLCLASSES = 0x4;
    public int DIGCF_PROFILE = 0x8;
    public int DIGCF_DEVICEINTERFACE = 0x10;

    public int SPDRP_REMOVAL_POLICY = 0x0000001F;
    public int CM_DEVCAP_REMOVABLE = 0x00000004;

    WinNT.HANDLE SetupDiGetClassDevs(GUID.ByReference classGuid, Pointer enumerator, Pointer hwndParent, int flags);

    boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE hDevInfo);

    boolean SetupDiEnumDeviceInterfaces(WinNT.HANDLE hDevInfo, Pointer devInfo, GUID.ByReference interfaceClassGuid,
           int memberIndex, SP_DEVICE_INTERFACE_DATA.ByReference deviceInterfaceData);

    boolean SetupDiGetDeviceInterfaceDetail(WinNT.HANDLE hDevInfo, SP_DEVICE_INTERFACE_DATA.ByReference deviceInterfaceData,
           Pointer deviceInterfaceDetailData, int deviceInterfaceDetailDataSize,
           IntByReference requiredSize, SP_DEVINFO_DATA.ByReference deviceInfoData);

    boolean SetupDiGetDeviceRegistryProperty(WinNT.HANDLE DeviceInfoSet, SP_DEVINFO_DATA.ByReference DeviceInfoData, int Property,
            IntByReference PropertyRegDataType, Pointer PropertyBuffer, int PropertyBufferSize,
            IntByReference RequiredSize);
}