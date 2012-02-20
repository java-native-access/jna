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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * The interface for the w32 setup API.
 */
public interface SetupApi extends StdCallLibrary {

    SetupApi INSTANCE = (SetupApi)
        Native.loadLibrary("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * The GUID_DEVINTERFACE_DISK device interface class is defined for hard disk storage devices.
     */
    public static Guid.GUID GUID_DEVINTERFACE_DISK = new Guid.GUID(new byte[]
    {
        0x07, 0x63, (byte) 0xf5, 0x53, (byte) 0xbf, (byte) 0xb6, (byte) 0xd0, 0x11,
        (byte) 0x94, (byte) 0xf2, 0x00, (byte) 0xa0, (byte) 0xc9, (byte) 0x1e, (byte) 0xfb, (byte) 0x8b
    });

    /**
     * Return only the device that is associated with the system default device interface, if one is set, for the
     * specified device interface classes.
     */
    public int DIGCF_DEFAULT = 0x1;

    /**
     * Return only devices that are currently present in a system.
     */
    public int DIGCF_PRESENT = 0x2;

    /**
     * Return a list of installed devices for all device setup classes or all device interface classes.
     */
    public int DIGCF_ALLCLASSES = 0x4;

    /**
     * Return only devices that are a part of the current hardware profile.
     */
    public int DIGCF_PROFILE = 0x8;

    /**
     * Return devices that support device interfaces for the specified device interface classes. This flag must be set
     * in the Flags parameter if the Enumerator parameter specifies a device instance ID.
     */
    public int DIGCF_DEVICEINTERFACE = 0x10;

    /**
     * (Windows XP and later) The function retrieves the device's current removal policy as a DWORD that contains one of
     * the CM_REMOVAL_POLICY_Xxx values that are defined in Cfgmgr32.h.
     */
    public int SPDRP_REMOVAL_POLICY = 0x0000001F;

    /**
     * Removable.
     */
    public int CM_DEVCAP_REMOVABLE = 0x00000004;

    /**
     * The SetupDiGetClassDevs function returns a handle to a device information set that contains requested device
     * information elements for a local computer.
     *
     * @param classGuid
     *   A pointer to the GUID for a device setup class or a device interface class. This pointer is optional and can be
     *   NULL. For more information about how to set ClassGuid, see the following Remarks section.
     *
     * @param enumerator
     *   A pointer to a NULL-terminated string that specifies:
     *
     *   An identifier (ID) of a Plug and Play (PnP) enumerator. This ID can either be the value's globally unique
     *   identifier (GUID) or symbolic name. For example, "PCI" can be used to specify the PCI PnP value. Other examples
     *   of symbolic names for PnP values include "USB," "PCMCIA," and "SCSI".
     *
     *   A PnP device instance ID. When specifying a PnP device instance ID, DIGCF_DEVICEINTERFACE must be set in the
     *   Flags parameter.
     *
     *   This pointer is optional and can be NULL. If an enumeration value is not used to select devices, set Enumerator
     *   to NULL.
     *
     * @param hwndParent
     *   A handle to the top-level window to be used for a user interface that is associated with installing a device
     *   instance in the device information set. This handle is optional and can be NULL.
     *
     * @param flags
     *   A variable of type DWORD that specifies control options that filter the device information elements that are
     *   added to the device information set.
     *
     * @return
     *   If the operation succeeds, SetupDiGetClassDevs returns a handle to a device information set that contains all
     *   installed devices that matched the supplied parameters. If the operation fails, the function returns
     *   INVALID_HANDLE_VALUE. To get extended error information, call GetLastError.
     */
    WinNT.HANDLE SetupDiGetClassDevs(Guid.GUID.ByReference classGuid, Pointer enumerator, Pointer hwndParent, int flags);

    /**
     * The SetupDiDestroyDeviceInfoList function deletes a device information set and frees all associated memory.
     *
     * @param hDevInfo A handle to the device information set to delete.
     * @return The function returns TRUE if it is successful. Otherwise, it returns FALSE and the logged error can be
     *   retrieved with a call to GetLastError.
     */
    boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE hDevInfo);

    /**
     * The SetupDiEnumDeviceInterfaces function enumerates the device interfaces that are contained in a device
     * information set.
     *
     * @param hDevInfo
     *   A pointer to a device information set that contains the device interfaces for which to return information. This
     *   handle is typically returned by SetupDiGetClassDevs.
     *
     * @param devInfo
     *   A pointer to an SP_DEVINFO_DATA structure that specifies a device information element in DeviceInfoSet. This
     *   parameter is optional and can be NULL. If this parameter is specified, SetupDiEnumDeviceInterfaces constrains
     *   the enumeration to the interfaces that are supported by the specified device. If this parameter is NULL,
     *   repeated calls to SetupDiEnumDeviceInterfaces return information about the interfaces that are associated with
     *   all the device information elements in DeviceInfoSet. This pointer is typically returned by
     *   SetupDiEnumDeviceInfo.
     *
     * @param interfaceClassGuid
     *   A pointer to a GUID that specifies the device interface class for the requested interface.
     *
     * @param memberIndex
     *   A zero-based index into the list of interfaces in the device information set. The caller should call this
     *   function first with MemberIndex set to zero to obtain the first interface. Then, repeatedly increment
     *   MemberIndex and retrieve an interface until this function fails and GetLastError returns ERROR_NO_MORE_ITEMS.
     *
     *   If DeviceInfoData specifies a particular device, the MemberIndex is relative to only the interfaces exposed by
     *   that device.
     *
     * @param deviceInterfaceData
     *   A pointer to a caller-allocated buffer that contains, on successful return, a completed
     *   SP_DEVICE_INTERFACE_DATA structure that identifies an interface that meets the search parameters. The caller
     *   must set DeviceInterfaceData.cbSize to sizeof(SP_DEVICE_INTERFACE_DATA) before calling this function.
     *
     * @return
     *   SetupDiEnumDeviceInterfaces returns TRUE if the function completed without error. If the function completed
     *   with an error, FALSE is returned and the error code for the failure can be retrieved by calling GetLastError.
     */
    boolean SetupDiEnumDeviceInterfaces(WinNT.HANDLE hDevInfo, Pointer devInfo,
           Guid.GUID.ByReference interfaceClassGuid, int memberIndex,
           SP_DEVICE_INTERFACE_DATA.ByReference deviceInterfaceData);

    /**
     * The SetupDiGetDeviceInterfaceDetail function returns details about a device interface.
     *
     * @param hDevInfo
     *   A pointer to the device information set that contains the interface for which to retrieve details. This handle
     *   is typically returned by SetupDiGetClassDevs.
     *
     * @param deviceInterfaceData
     *   A pointer to an SP_DEVICE_INTERFACE_DATA structure that specifies the interface in DeviceInfoSet for which to
     *   retrieve details. A pointer of this type is typically returned by SetupDiEnumDeviceInterfaces.
     *
     * @param deviceInterfaceDetailData
     *   A pointer to an SP_DEVICE_INTERFACE_DETAIL_DATA structure to receive information about the specified interface.
     *   This parameter is optional and can be NULL. This parameter must be NULL if DeviceInterfaceDetailSize is zero.
     *   If this parameter is specified, the caller must set DeviceInterfaceDetailData.cbSize to
     *   sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA) before calling this function. The cbSize member always contains the
     *   size of the fixed part of the data structure, not a size reflecting the variable-length string at the end.
     *
     * @param deviceInterfaceDetailDataSize
     *   The size of the DeviceInterfaceDetailData buffer. The buffer must be at least
     *   (offsetof(SP_DEVICE_INTERFACE_DETAIL_DATA, DevicePath) + sizeof(TCHAR)) bytes, to contain the fixed part of the
     *   structure and a single NULL to terminate an empty MULTI_SZ string.
     *
     *   This parameter must be zero if DeviceInterfaceDetailData is NULL.
     *
     * @param requiredSize
     *   A pointer to a variable of type DWORD that receives the required size of the DeviceInterfaceDetailData buffer.
     *   This size includes the size of the fixed part of the structure plus the number of bytes required for the
     *   variable-length device path string. This parameter is optional and can be NULL.
     *
     * @param deviceInfoData
     *   A pointer to a buffer that receives information about the device that supports the requested interface. The
     *   caller must set DeviceInfoData.cbSize to sizeof(SP_DEVINFO_DATA). This parameter is optional and can be NULL.
     *
     * @return
     *   SetupDiGetDeviceInterfaceDetail returns TRUE if the function completed without error. If the function completed
     *   with an error, FALSE is returned and the error code for the failure can be retrieved by calling GetLastError.
     */
    boolean SetupDiGetDeviceInterfaceDetail(WinNT.HANDLE hDevInfo,
           SP_DEVICE_INTERFACE_DATA.ByReference deviceInterfaceData, Pointer deviceInterfaceDetailData,
           int deviceInterfaceDetailDataSize, IntByReference requiredSize, SP_DEVINFO_DATA.ByReference deviceInfoData);

    /**
     * The SetupDiGetDeviceRegistryProperty function retrieves a specified Plug and Play device property.
     *
     * @param DeviceInfoSet
     *   A handle to a device information set that contains a device information element that represents the device for
     *   which to retrieve a Plug and Play property.
     *
     * @param DeviceInfoData
     *   A pointer to an SP_DEVINFO_DATA structure that specifies the device information element in DeviceInfoSet.
     *
     * @param Property
     *  Specifies the property to be retrieved.
     *
     * @param PropertyRegDataType
     *   A pointer to a variable that receives the data type of the property that is being retrieved. This is one of the
     *   standard registry data types. This parameter is optional and can be NULL.
     *
     * @param PropertyBuffer
     *   A pointer to a buffer that receives the property that is being retrieved. If this parameter is set to NULL, and
     *   PropertyBufferSize is also set to zero, the function returns the required size for the buffer in RequiredSize.
     *
     * @param PropertyBufferSize
     *   The size, in bytes, of the PropertyBuffer buffer.
     *
     * @param RequiredSize
     *   A pointer to a variable of type DWORD that receives the required size, in bytes, of the PropertyBuffer buffer
     *   that is required to hold the data for the requested property. This parameter is optional and can be NULL.
     *
     * @return
     *   SetupDiGetDeviceRegistryProperty returns TRUE if the call was successful. Otherwise, it returns FALSE and the
     *   logged error can be retrieved by making a call to GetLastError. SetupDiGetDeviceRegistryProperty returns the
     *   ERROR_INVALID_DATA error code if the requested property does not exist for a device or if the property data is
     *   not valid.
     */
    boolean SetupDiGetDeviceRegistryProperty(WinNT.HANDLE DeviceInfoSet, SP_DEVINFO_DATA.ByReference DeviceInfoData,
            int Property, IntByReference PropertyRegDataType, Pointer PropertyBuffer, int PropertyBufferSize,
            IntByReference RequiredSize);

    /**
     * An SP_DEVICE_INTERFACE_DATA structure defines a device interface in a device information set.
     */
    public static class SP_DEVICE_INTERFACE_DATA extends Structure {

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
            super(memory);
            read();
        }

        /**
         * The size, in bytes, of the SP_DEVICE_INTERFACE_DATA structure.
         */
        public int cbSize;

        /**
         * The GUID for the class to which the device interface belongs.
         */
        public Guid.GUID InterfaceClassGuid;

        /**
         * Can be one or more of the following:
         *  SPINT_ACTIVE - The interface is active (enabled).
         *  SPINT_DEFAULT - The interface is the default interface for the device class.
         *  SPINT_REMOVED - The interface is removed.
         */
        public int Flags;

        /**
         * Reserved. Do not use.
         */
        public Pointer Reserved;
    }

    /**
     * An SP_DEVINFO_DATA structure defines a device instance that is a member of a device information set.
     */
    public static class SP_DEVINFO_DATA extends Structure {

        public static class ByReference extends SP_DEVINFO_DATA implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public SP_DEVINFO_DATA() {
            cbSize = size();
        }

        public SP_DEVINFO_DATA(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The size, in bytes, of the SP_DEVINFO_DATA structure.
         */
        public int cbSize;

        /**
         * The GUID of the device's setup class.
         */
        public Guid.GUID InterfaceClassGuid;

        /**
         * An opaque handle to the device instance (also known as a handle to the devnode).
         *
         * Some functions, such as SetupDiXxx functions, take the whole SP_DEVINFO_DATA structure as input to identify a
         * device in a device information set. Other functions, such as CM_Xxx functions like CM_Get_DevNode_Status,
         * take this DevInst handle as input.
         */
        public int DevInst;

        /**
         * Reserved. For internal use only.
         */
        public Pointer Reserved;
    }
}
