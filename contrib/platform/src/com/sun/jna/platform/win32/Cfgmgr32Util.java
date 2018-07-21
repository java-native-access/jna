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

import java.io.UnsupportedEncodingException;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * Cfgmgr32 utility API.
 * 
 * @author widdis[at]gmail[dot]com
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
     * Utility method to call Cfgmgr32's CM_Get_Device_ID that allocates the
     * required memory for the Buffer parameter based on the type mapping used,
     * calls to CM_Get_Device_ID, and returns the received string.
     * 
     * @param devInst
     *            Caller-supplied device instance handle that is bound to the
     *            local machine.
     * @return The device instance ID string.
     * @throws UnsupportedEncodingException
     */
    public static String CM_Get_Device_ID(int devInst) throws UnsupportedEncodingException {
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;

        // Get Device ID character count
        IntByReference pulLen = new IntByReference();
        Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);

        // Zero and Fetch the buffer
        Memory buffer = new Memory(pulLen.getValue() * charToBytes);
        buffer.clear();
        int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        // In the unlikely event the device id changes this might not be big
        // enough, try again. This happens rarely enough one retry should be
        // sufficient.
        if (ret == Cfgmgr32.CR_BUFFER_SMALL) {
            Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
            buffer = new Memory(pulLen.getValue() * charToBytes);
            buffer.clear();
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        }
        // If we still aren't successful throw an exception
        if (ret != Cfgmgr32.CR_SUCCESS) {
            throw new Cfgmgr32Exception(ret);
        }
        // Convert buffer to Java String (may include a null). Since we've
        // cleared it we can rely on trim()
        String deviceId;
        if (charToBytes == 1) {
            deviceId = new String(buffer.getByteArray(0, pulLen.getValue()), "US-ASCII");
        } else {
            deviceId = new String(buffer.getCharArray(0, pulLen.getValue()));
        }
        return deviceId.trim();
    }
}