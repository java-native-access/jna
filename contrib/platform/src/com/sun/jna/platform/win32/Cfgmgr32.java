/* Copyright (c) 2018 Daniel Widdis, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

/**
 * Windows Cfgmgr32.
 *
 * @author widdis[at]gmail[dot]com
 */
public interface Cfgmgr32 extends Library {
    Cfgmgr32 INSTANCE = Native.load("Cfgmgr32", Cfgmgr32.class, W32APIOptions.DEFAULT_OPTIONS);

    int CR_SUCCESS = 0;
    int CR_DEFAULT = 0x00000001;
    int CR_OUT_OF_MEMORY = 0x00000002;
    int CR_INVALID_POINTER = 0x00000003;
    int CR_INVALID_FLAG = 0x00000004;
    int CR_INVALID_DEVNODE = 0x00000005;
    int CR_INVALID_DEVINST = CR_INVALID_DEVNODE;
    int CR_INVALID_RES_DES = 0x00000006;
    int CR_INVALID_LOG_CONF = 0x00000007;
    int CR_INVALID_ARBITRATOR = 0x00000008;
    int CR_INVALID_NODELIST = 0x00000009;
    int CR_DEVNODE_HAS_REQS = 0x0000000A;
    int CR_DEVINST_HAS_REQS = CR_DEVNODE_HAS_REQS;
    int CR_INVALID_RESOURCEID = 0x0000000B;
    int CR_DLVXD_NOT_FOUND = 0x0000000C;   // WIN 95 ONLY
    int CR_NO_SUCH_DEVNODE = 0x0000000D;
    int CR_NO_SUCH_DEVINST = CR_NO_SUCH_DEVNODE;
    int CR_NO_MORE_LOG_CONF = 0x0000000E;
    int CR_NO_MORE_RES_DES = 0x0000000F;
    int CR_ALREADY_SUCH_DEVNODE = 0x00000010;
    int CR_ALREADY_SUCH_DEVINST = CR_ALREADY_SUCH_DEVNODE;
    int CR_INVALID_RANGE_LIST = 0x00000011;
    int CR_INVALID_RANGE = 0x00000012;
    int CR_FAILURE = 0x00000013;
    int CR_NO_SUCH_LOGICAL_DEV = 0x00000014;
    int CR_CREATE_BLOCKED = 0x00000015;
    int CR_NOT_SYSTEM_VM = 0x00000016;   // WIN 95 ONLY
    int CR_REMOVE_VETOED = 0x00000017;
    int CR_APM_VETOED = 0x00000018;
    int CR_INVALID_LOAD_TYPE = 0x00000019;
    int CR_BUFFER_SMALL = 0x0000001A;
    int CR_NO_ARBITRATOR = 0x0000001B;
    int CR_NO_REGISTRY_HANDLE = 0x0000001C;
    int CR_REGISTRY_ERROR = 0x0000001D;
    int CR_INVALID_DEVICE_ID = 0x0000001E;
    int CR_INVALID_DATA = 0x0000001F;
    int CR_INVALID_API = 0x00000020;
    int CR_DEVLOADER_NOT_READY = 0x00000021;
    int CR_NEED_RESTART = 0x00000022;
    int CR_NO_MORE_HW_PROFILES = 0x00000023;
    int CR_DEVICE_NOT_THERE = 0x00000024;
    int CR_NO_SUCH_VALUE = 0x00000025;
    int CR_WRONG_TYPE = 0x00000026;
    int CR_INVALID_PRIORITY = 0x00000027;
    int CR_NOT_DISABLEABLE = 0x00000028;
    int CR_FREE_RESOURCES = 0x00000029;
    int CR_QUERY_VETOED = 0x0000002A;
    int CR_CANT_SHARE_IRQ = 0x0000002B;
    int CR_NO_DEPENDENT = 0x0000002C;
    int CR_SAME_RESOURCES = 0x0000002D;
    int CR_NO_SUCH_REGISTRY_KEY = 0x0000002E;
    int CR_INVALID_MACHINENAME = 0x0000002F;   // NT ONLY
    int CR_REMOTE_COMM_FAILURE = 0x00000030;   // NT ONLY
    int CR_MACHINE_UNAVAILABLE = 0x00000031;   // NT ONLY
    int CR_NO_CM_SERVICES = 0x00000032;   // NT ONLY
    int CR_ACCESS_DENIED = 0x00000033;   // NT ONLY
    int CR_CALL_NOT_IMPLEMENTED = 0x00000034;
    int CR_INVALID_PROPERTY = 0x00000035;
    int CR_DEVICE_INTERFACE_ACTIVE = 0x00000036;
    int CR_NO_SUCH_DEVICE_INTERFACE = 0x00000037;
    int CR_INVALID_REFERENCE_STRING = 0x00000038;
    int CR_INVALID_CONFLICT_LIST = 0x00000039;
    int CR_INVALID_INDEX = 0x0000003A;
    int CR_INVALID_STRUCTURE_SIZE = 0x0000003B;
    int NUM_CR_RESULTS = 0x0000003C;

    int CM_LOCATE_DEVNODE_NORMAL = 0;
    int CM_LOCATE_DEVNODE_PHANTOM = 1;
    int CM_LOCATE_DEVNODE_CANCELREMOVE = 2;
    int CM_LOCATE_DEVNODE_NOVALIDATION = 4;
    int CM_LOCATE_DEVNODE_BITS = 7;

    int CM_DRP_DEVICEDESC = 0x00000001; // DeviceDesc REG_SZ property (RW)
    int CM_DRP_HARDWAREID = 0x00000002; // HardwareID REG_MULTI_SZ property (RW)
    int CM_DRP_COMPATIBLEIDS = 0x00000003; // CompatibleIDs REG_MULTI_SZ property (RW)
    int CM_DRP_SERVICE = 0x00000005; // Service REG_SZ property (RW)
    int CM_DRP_CLASS = 0x00000008; // Class REG_SZ property (RW)
    int CM_DRP_CLASSGUID = 0x00000009; // ClassGUID REG_SZ property (RW)
    int CM_DRP_DRIVER = 0x0000000A; // Driver REG_SZ property (RW)
    int CM_DRP_CONFIGFLAGS = 0x0000000B; // ConfigFlags REG_DWORD property (RW)
    int CM_DRP_MFG = 0x0000000C; // Mfg REG_SZ property (RW)
    int CM_DRP_FRIENDLYNAME = 0x0000000D; // FriendlyName REG_SZ property (RW)
    int CM_DRP_LOCATION_INFORMATION = 0x0000000E; // LocationInformation REG_SZ property (RW)
    int CM_DRP_PHYSICAL_DEVICE_OBJECT_NAME = 0x0000000F; // PhysicalDeviceObjectName REG_SZ property (R)
    int CM_DRP_CAPABILITIES = 0x00000010; // Capabilities REG_DWORD property (R)
    int CM_DRP_UI_NUMBER = 0x00000011; // UiNumber REG_DWORD property (R)
    int CM_DRP_UPPERFILTERS = 0x00000012; // UpperFilters REG_MULTI_SZ property (RW)
    int CM_DRP_LOWERFILTERS = 0x00000013; // LowerFilters REG_MULTI_SZ property (RW)
    int CM_DRP_BUSTYPEGUID = 0x00000014; // Bus Type Guid, GUID, (R)
    int CM_DRP_LEGACYBUSTYPE = 0x00000015; // Legacy bus type, INTERFACE_TYPE, (R)
    int CM_DRP_BUSNUMBER = 0x00000016; // Bus Number, DWORD, (R)
    int CM_DRP_ENUMERATOR_NAME = 0x00000017; // Enumerator Name REG_SZ property (R)
    int CM_DRP_SECURITY = 0x00000018; // Security - Device override (RW)
    int CM_DRP_SECURITY_SDS = 0x00000019; // Security - Device override (RW)
    int CM_DRP_DEVTYPE = 0x0000001A; // Device Type - Device override (RW)
    int CM_DRP_EXCLUSIVE = 0x0000001B; // Exclusivity - Device override (RW)
    int CM_DRP_CHARACTERISTICS = 0x0000001C; // Characteristics - Device Override (RW)
    int CM_DRP_ADDRESS = 0x0000001D; // Device Address (R)
    int CM_DRP_UI_NUMBER_DESC_FORMAT = 0x0000001E; // UINumberDescFormat REG_SZ property (RW)
    int CM_DRP_DEVICE_POWER_DATA = 0x0000001F; // CM_POWER_DATA REG_BINARY property (R)
    int CM_DRP_REMOVAL_POLICY = 0x00000020; // CM_DEVICE_REMOVAL_POLICY REG_DWORD (R)
    int CM_DRP_REMOVAL_POLICY_HW_DEFAULT = 0x00000021; // CM_DRP_REMOVAL_POLICY_HW_DEFAULT REG_DWORD (R)
    int CM_DRP_REMOVAL_POLICY_OVERRIDE = 0x00000022; // CM_DRP_REMOVAL_POLICY_OVERRIDE REG_DWORD (RW)
    int CM_DRP_INSTALL_STATE = 0x00000023; // CM_DRP_INSTALL_STATE REG_DWORD (R)
    int CM_DRP_LOCATION_PATHS = 0x00000024; // CM_DRP_LOCATION_PATHS REG_MULTI_SZ (R)
    int CM_DRP_BASE_CONTAINERID = 0x00000025; // Base ContainerID REG_SZ property (R)

    /**
     * The CM_Locate_DevNode function obtains a device instance handle to the
     * device node that is associated with a specified device instance ID on the
     * local machine.
     *
     * @param pdnDevInst
     *            A pointer to a device instance handle that CM_Locate_DevNode
     *            retrieves. The retrieved handle is bound to the local machine.
     * @param pDeviceID
     *            A pointer to a NULL-terminated string representing a device
     *            instance ID. If this value is NULL, or if it points to a
     *            zero-length string, the function retrieves a device instance
     *            handle to the device at the root of the device tree. *
     * @param ulFlags
     *            A variable of ULONG type that supplies one of the following
     *            flag values that apply if the caller supplies a device
     *            instance identifier: CM_LOCATE_DEVNODE_NORMAL,
     *            CM_LOCATE_DEVNODE_PHANTOM, CM_LOCATE_DEVNODE_CANCELREMOVE, or
     *            CM_LOCATE_DEVNODE_NOVALIDATION
     * @return If the operation succeeds, CM_Locate_DevNode returns CR_SUCCESS.
     *         Otherwise, the function returns one of the CR_Xxx error codes
     *         that are defined in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_locate_devnodea">
     *      CM_Locate_DevNode</A>
     */
    int CM_Locate_DevNode(IntByReference pdnDevInst, String pDeviceID, int ulFlags);

    /**
     * The CM_Get_Parent function obtains a device instance handle to the parent
     * node of a specified device node (devnode) in the local machine's device
     * tree.
     *
     * @param pdnDevInst
     *            Caller-supplied pointer to the device instance handle to the
     *            parent node that this function retrieves. The retrieved handle
     *            is bound to the local machine.
     * @param dnDevInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns CR_SUCCESS.
     *         Otherwise, it returns one of the CR_-prefixed error codes defined
     *         in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_get_parent">
     *      CM_Get_Parent</A>
     */
    int CM_Get_Parent(IntByReference pdnDevInst, int dnDevInst, int ulFlags);

    /**
     * The CM_Get_Child function is used to retrieve a device instance handle to
     * the first child node of a specified device node (devnode) in the local
     * machine's device tree.
     *
     * @param pdnDevInst
     *            Caller-supplied pointer to the device instance handle to the
     *            child node that this function retrieves. The retrieved handle
     *            is bound to the local machine.
     * @param dnDevInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns CR_SUCCESS.
     *         Otherwise, it returns one of the CR_-prefixed error codes defined
     *         in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_get_child">
     *      CM_Get_Child</A>
     */
    int CM_Get_Child(IntByReference pdnDevInst, int dnDevInst, int ulFlags);

    /**
     * The CM_Get_Sibling function obtains a device instance handle to the next
     * sibling node of a specified device node (devnode) in the local machine's
     * device tree.
     *
     * @param pdnDevInst
     *            Caller-supplied pointer to the device instance handle to the
     *            sibling node that this function retrieves. The retrieved
     *            handle is bound to the local machine.
     * @param dnDevInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns CR_SUCCESS.
     *         Otherwise, it returns one of the CR_-prefixed error codes defined
     *         in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_get_sibling">
     *      CM_Get_Sibling</A>
     */
    int CM_Get_Sibling(IntByReference pdnDevInst, int dnDevInst, int ulFlags);

    /**
     * The CM_Get_Device_ID function retrieves the device instance ID for a
     * specified device instance on the local machine.
     *
     * @param devInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param Buffer
     *            Address of a buffer to receive a device instance ID string.
     *            The required buffer size can be obtained by calling
     *            CM_Get_Device_ID_Size, then incrementing the received value to
     *            allow room for the string's terminating NULL.
     * @param BufferLen
     *            Caller-supplied length, in characters, of the buffer specified
     *            by Buffer.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns CR_SUCCESS.
     *         Otherwise, it returns one of the CR_-prefixed error codes defined
     *         in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_get_device_idw">
     *      CM_Get_Device_ID</A>
     */
    int CM_Get_Device_ID(int devInst, Pointer Buffer, int BufferLen, int ulFlags);

    /**
     * The CM_Get_Device_ID_Size function retrieves the buffer size required to
     * hold a device instance ID for a device instance on the local machine.
     *
     * @param pulLen
     *            Receives a value representing the required buffer size, in
     *            characters.
     * @param dnDevInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns CR_SUCCESS.
     *         Otherwise, it returns one of the CR_-prefixed error codes defined
     *         in Cfgmgr32.h.
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/cfgmgr32/nf-cfgmgr32-cm_get_device_id_size">
     *      CM_Get_Device_ID_Size</A>
     */
    int CM_Get_Device_ID_Size(IntByReference pulLen, int dnDevInst, int ulFlags);

    /**
     * The CM_Get_DevNode_Registry_Property function retrieves a specified device
     * property from the registry.
     *
     * @param dnDevInst
     *            A caller-supplied device instance handle that is bound to the
     *            local machine.
     * @param ulProperty
     *            A {@code CM_DRP_}-prefixed constant value that identifies the
     *            device property to be obtained from the registry. These constants
     *            are defined in Cfgmgr32.h.
     * @param pulRegDataType
     *            Optional, can be {@code null}. A pointer to a location that
     *            receives the registry data type, specified as a
     *            {@code REG_}-prefixed constant defined in Winnt.h.
     * @param buffer
     *            Optional, can be {@code null}. A pointer to a caller-supplied
     *            buffer that receives the requested device property. If this value
     *            is {@code null}, the function supplies only the length of the
     *            requested data in the address pointed to by {@code pulLength}.
     * @param pulLength
     *            A pointer to a {@code ULONG} variable into which the function
     *            stores the length, in bytes, of the requested device property.
     *            <p>
     *            If the Buffer parameter is set to {@code null}, the ULONG variable
     *            must be set to zero.
     *            <p>
     *            If the Buffer parameter is not set to {@code null}, the
     *            {@code ULONG} variable must be set to the length, in bytes, of the
     *            caller-supplied buffer.
     * @param ulFlags
     *            Not used, must be zero.
     * @return If the operation succeeds, the function returns {@code CR_SUCCESS}.
     *         Otherwise, it returns one of the {@code CR_}-prefixed error codes
     *         that are defined in Cfgmgr32.h.
     */
    int CM_Get_DevNode_Registry_Property(int dnDevInst, int ulProperty, IntByReference pulRegDataType, Pointer buffer,
            IntByReference pulLength, int ulFlags);
}
