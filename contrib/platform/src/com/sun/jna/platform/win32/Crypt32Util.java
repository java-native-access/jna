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
                        err.addSuppressed(e);
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
                        err.addSuppressed(e);
                    }
                }
            }
        }

        if (err != null) {
            throw err;
        }

        return unProtectedData;
    }
}