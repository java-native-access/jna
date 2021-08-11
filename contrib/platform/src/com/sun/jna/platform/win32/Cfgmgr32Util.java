/* Copyright (c) 2018, 2021 Daniel Widdis, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Cfgmgr32 utility API.
 */
public abstract class Cfgmgr32Util {
    @SuppressWarnings("serial")
    public static class Cfgmgr32Exception extends RuntimeException {
        private final int errorCode;

        public Cfgmgr32Exception(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * Utility method to call Cfgmgr32's CM_Get_Device_ID_Size, allocates the
     * required memory for the Buffer parameter based on the type mapping used,
     * calls to CM_Get_Device_ID, and returns the received string.
     *
     * @param devInst
     *            Caller-supplied device instance handle that is bound to the local
     *            machine.
     * @return The device instance ID string.
     * @throws Cfgmgr32Exception
     */
    public static String CM_Get_Device_ID(int devInst) throws Cfgmgr32Exception {
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;

        // Get Device ID character count
        IntByReference pulLen = new IntByReference();
        int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
        if (ret != Cfgmgr32.CR_SUCCESS) {
            throw new Cfgmgr32Exception(ret);
        }

        // Add one to length to allow null terminator
        Memory buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
        // Zero the buffer (including the extra character)
        buffer.clear();
        // Fetch the buffer specifying only the current length
        ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        // In the unlikely event the device id changes this might not be big
        // enough, try again. This happens rarely enough one retry should be
        // sufficient.
        if (ret == Cfgmgr32.CR_BUFFER_SMALL) {
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
            if (ret != Cfgmgr32.CR_SUCCESS) {
                throw new Cfgmgr32Exception(ret);
            }
            buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
            buffer.clear();
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        }
        // If we still aren't successful throw an exception
        if (ret != Cfgmgr32.CR_SUCCESS) {
            throw new Cfgmgr32Exception(ret);
        }
        // Convert buffer to Java String
        if (charToBytes == 1) {
            return buffer.getString(0);
        } else {
            return buffer.getWideString(0);
        }
    }

    /**
     * Utility method to call Cfgmgr32's CM_Get_DevNode_Registry_Property that
     * allocates the required memory for the Buffer parameter, and returns values of
     * the appropriate type.
     *
     * @param devInst
     *            Caller-supplied device instance handle that is bound to the local
     *            machine.
     * @param ulProperty
     *            A {@code CM_DRP_}-prefixed constant value that identifies the
     *            device property to be obtained from the registry. These constants
     *            are defined in Cfgmgr32.h.
     * @return An {@link Object} containing the specified registry property for the
     *         device.
     *         <p>
     *         If the property is of type {@link WinNT#REG_SZ}, a
     *         {@link java.lang.String} is returned.
     *         <p>
     *         If the property is of type {@link WinNT#REG_MULTI_SZ}, an array of
     *         {@link java.lang.String} is returned.
     *         <p>
     *         If the property is of type {@link WinNT#REG_DWORD}, an
     *         {@link java.lang.Integer} is returned.
     *         <p>
     *         If the property is of type {@link WinNT#REG_BINARY}, an array of
     *         {@link java.lang.Byte} is returned.
     *         <p>
     *         If no value exists for this property (error
     *         {@link Cfgmgr32#CR_NO_SUCH_VALUE}), returns {@code null}.
     * @throws Cfgmgr32Exception
     *             on any errors other than {@link Cfgmgr32#CR_NO_SUCH_VALUE}
     */
    public static Object CM_Get_DevNode_Registry_Property(int devInst, int ulProperty) throws Cfgmgr32Exception {

        // Get byte count and type
        IntByReference size = new IntByReference();
        IntByReference type = new IntByReference();
        int ret = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(devInst, ulProperty, type, null, size, 0);
        // If this property does not exist return null
        if (ret == Cfgmgr32.CR_NO_SUCH_VALUE) {
            return null;
        }
        // If successful in retrieving type and size, should fail with CR_BUFFER_SMALL,
        // otherwise throw an exception
        if (ret != Cfgmgr32.CR_BUFFER_SMALL) {
            throw new Cfgmgr32Exception(ret);
        }

        // It is possible to have a valid value with registry data type, but 0 size.
        // Leave the memory buffer null in that case
        Memory buffer = null;
        if (size.getValue() > 0) {
            buffer = new Memory(size.getValue());
            ret = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(devInst, ulProperty, type, buffer, size, 0);
            if (ret != Cfgmgr32.CR_SUCCESS) {
                throw new Cfgmgr32Exception(ret);
            }
        }

        // Get the appropriate type of data from the buffer
        switch (type.getValue()) {
            case WinNT.REG_SZ:
                // Convert buffer to Java String
                if (buffer == null) {
                    return "";
                }
                return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? buffer.getWideString(0)
                        : buffer.getString(0);
            case WinNT.REG_MULTI_SZ:
                // Convert buffer to String Array
                if (buffer == null) {
                    return new String[0];
                }
                return Advapi32Util.regMultiSzBufferToStringArray(buffer);
            case WinNT.REG_DWORD:
                // Convert buffer to int
                if (buffer == null) {
                    return 0;
                }
                return buffer.getInt(0);
            case WinNT.REG_NONE:
                return null;
            default:
                // Intended for WinNT.REG_BINARY but safe default for any data
                if (buffer == null) {
                    return new byte[0];
                }
                // Convert buffer to array of bytes
                return buffer.getByteArray(0, (int) buffer.size());
        }
    }
}
