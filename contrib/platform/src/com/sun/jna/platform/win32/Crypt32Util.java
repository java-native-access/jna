/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinCrypt.CRYPTPROTECT_PROMPTSTRUCT;
import com.sun.jna.platform.win32.WinCrypt.DATA_BLOB;
import com.sun.jna.ptr.PointerByReference;

/**
 * Crypt32 utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Crypt32Util {

    /**
     * Protect a blob of data.
     * @param data
     *  Data to protect.
     * @return
     *  Protected data.
     */
    public static byte[] cryptProtectData(byte[] data) {
        return cryptProtectData(data, 0);
    }

    /**
     * Protect a blob of data with optional flags.
     * @param data
     *  Data to protect.
     * @param flags
     *  Optional flags, eg. CRYPTPROTECT_LOCAL_MACHINE | CRYPTPROTECT_UI_FORBIDDEN.
     * @return
     *  Protected data.
     */
    public static byte[] cryptProtectData(byte[] data, int flags) {
        return cryptProtectData(data, null, flags, "", null);
    }

    /**
     * Protect a blob of data.
     * @param data
     *  Data to protect.
     * @param entropy
     *  Optional entropy.
     * @param flags
     *  Optional flags.
     * @param description
     *  Optional description.
     * @param prompt
     *  Prompt structure.
     * @return
     *  Protected bytes.
     */
    public static byte[] cryptProtectData(byte[] data, byte[] entropy, int flags,
            String description, CRYPTPROTECT_PROMPTSTRUCT prompt) {
        DATA_BLOB pDataIn = new DATA_BLOB(data);
        DATA_BLOB pDataProtected = new DATA_BLOB();
        DATA_BLOB pEntropy = (entropy == null) ? null : new DATA_BLOB(entropy);
        try {
            if (! Crypt32.INSTANCE.CryptProtectData(pDataIn, description,
                    pEntropy, null, prompt, flags, pDataProtected)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            return pDataProtected.getData();
        } finally {
            if (pDataProtected.pbData != null) {
                Kernel32Util.freeLocalMemory(pDataProtected.pbData);
            }
        }
    }

    /**
     * Unprotect a blob of data.
     * @param data
     *  Data to unprotect.
     * @return
     *  Unprotected blob of data.
     */
    public static byte[] cryptUnprotectData(byte[] data) {
        return cryptUnprotectData(data, 0);
    }

    /**
     * Unprotect a blob of data.
     * @param data
     *  Data to unprotect.
     * @param flags
     *  Optional flags, eg. CRYPTPROTECT_UI_FORBIDDEN.
     * @return
     *  Unprotected blob of data.
     */
    public static byte[] cryptUnprotectData(byte[] data, int flags) {
        return cryptUnprotectData(data, null, flags, null);
    }

    /**
     * Unprotect a blob of data.
     * @param data
     *  Data to unprotect.
     * @param entropy
     *  Optional entropy.
     * @param flags
     *  Optional flags.
     * @param prompt
     *  Optional prompt structure.
     * @return
     *  Unprotected blob of data.
     */
    public static byte[] cryptUnprotectData(byte[] data, byte[] entropy, int flags,
            CRYPTPROTECT_PROMPTSTRUCT prompt) {
        DATA_BLOB pDataIn = new DATA_BLOB(data);
        DATA_BLOB pDataUnprotected = new DATA_BLOB();
        DATA_BLOB pEntropy = (entropy == null) ? null : new DATA_BLOB(entropy);
        PointerByReference pDescription = new PointerByReference();
        Win32Exception err = null;
        byte[] unProtectedData = null;
        try {
            if (! Crypt32.INSTANCE.CryptUnprotectData(pDataIn, pDescription,
                    pEntropy, null, prompt, flags, pDataUnprotected)) {
                err = new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
                unProtectedData = pDataUnprotected.getData();
            }
        } finally {
            if (pDataUnprotected.pbData != null) {
                try {
                    Kernel32Util.freeLocalMemory(pDataUnprotected.pbData);
                } catch(Win32Exception e) {
                    if (err == null) {
                        err = e;
                    } else {
                        err.addSuppressedReflected(e);
                    }
                }
            }

            if (pDescription.getValue() != null) {
                try {
                    Kernel32Util.freeLocalMemory(pDescription.getValue());
                } catch(Win32Exception e) {
                    if (err == null) {
                        err = e;
                    } else {
                        err.addSuppressedReflected(e);
                    }
                }
            }
        }

        if (err != null) {
            throw err;
        }

        return unProtectedData;
    }

    /**
     * Utility method to call to Crypt32's CertNameToStr that allocates the
     * assigns the required memory for the psz parameter based on the type
     * mapping used, calls to CertNameToStr, and returns the received string.
     *
     * @param dwCertEncodingType The certificate encoding type that was used to
     * encode the name. The message encoding type identifier, contained in the
     * high WORD of this value, is ignored by this function.
     * @param pName A pointer to the CERT_NAME_BLOB structure to be converted.
     * @param dwStrType This parameter specifies the format of the output
     * string. This parameter also specifies other options for the contents of
     * the string.
     * @return Returns the retrieved string.
     */
    public static String CertNameToStr(int dwCertEncodingType, int dwStrType, DATA_BLOB pName) {
        int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;

        // Initialize the signature structure.
        int requiredSize = Crypt32.INSTANCE.CertNameToStr(
                dwCertEncodingType,
                pName,
                dwStrType,
                Pointer.NULL,
                0);

        Memory mem = new Memory(requiredSize * charToBytes);

        // Initialize the signature structure.
        int resultBytes = Crypt32.INSTANCE.CertNameToStr(
                dwCertEncodingType,
                pName,
                dwStrType,
                mem,
                requiredSize);

        assert resultBytes == requiredSize;

        if (Boolean.getBoolean("w32.ascii")) {
            return mem.getString(0);
        } else {
            return mem.getWideString(0);
        }
    }
}