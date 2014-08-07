/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32.COM;

import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.EnumKey;
import com.sun.jna.platform.win32.Advapi32Util.InfoKey;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;

// TODO: Auto-generated Javadoc
/**
 * The Class COMUtils.
 * 
 * @author wolf.tobias@gmx.net The Class COMUtils.
 */
public abstract class COMUtils {

    /** The Constant CO_E_NOTINITIALIZED. */
    public static final int S_OK = 0;

    /**
     * Succeeded.
     * 
     * @param hr
     *            the hr
     * @return true, if successful
     */
    public static boolean SUCCEEDED(HRESULT hr) {
        return SUCCEEDED(hr.intValue());
    }

    /**
     * Succeeded.
     * 
     * @param hr
     *            the hr
     * @return true, if successful
     */
    public static boolean SUCCEEDED(int hr) {
        if (hr == S_OK)
            return true;
        else
            return false;
    }

    /**
     * Failed.
     * 
     * @param hr
     *            the hr
     * @return true, if successful
     */
    public static boolean FAILED(HRESULT hr) {
        return FAILED(hr.intValue());
    }

    /**
     * Failed.
     * 
     * @param hr
     *            the hr
     * @return true, if successful
     */
    public static boolean FAILED(int hr) {
        return (hr != S_OK);
    }

    /**
     * Throw new exception.
     * 
     * @param hr
     *            the hr
     */
    public static void checkRC(HRESULT hr) {
        checkRC(hr, null, null);
    }

    /**
     * Throw new exception.
     * 
     * @param hr
     *            the hr
     * @param pExcepInfo
     *            the excep info
     * @param puArgErr
     *            the pu arg err
     */
    public static void checkRC(HRESULT hr, EXCEPINFO pExcepInfo,
            IntByReference puArgErr) {
        if (FAILED(hr)) {
            String formatMessageFromHR = Kernel32Util.formatMessage(hr);
            throw new COMException(formatMessageFromHR, pExcepInfo, puArgErr);
        }
    }

    /**
     * Gets the all com info on system.
     * 
     * @return the all com info on system
     */
    public static ArrayList<COMInfo> getAllCOMInfoOnSystem() {
        HKEYByReference phkResult = new HKEYByReference();
        HKEYByReference phkResult2 = new HKEYByReference();
        String subKey;
        ArrayList<COMInfo> comInfos = new ArrayList<COMUtils.COMInfo>();

        try {
            // open root key
            phkResult = Advapi32Util.registryGetKey(WinReg.HKEY_CLASSES_ROOT,
                    "CLSID", WinNT.KEY_ALL_ACCESS);
            // open subkey
            InfoKey infoKey = Advapi32Util.registryQueryInfoKey(
                    phkResult.getValue(), WinNT.KEY_ALL_ACCESS);

            for (int i = 0; i < infoKey.lpcSubKeys.getValue(); i++) {
                EnumKey enumKey = Advapi32Util.registryRegEnumKey(
                        phkResult.getValue(), i);
                subKey = Native.toString(enumKey.lpName);

                COMInfo comInfo = new COMInfo(subKey);

                phkResult2 = Advapi32Util.registryGetKey(phkResult.getValue(),
                        subKey, WinNT.KEY_ALL_ACCESS);
                InfoKey infoKey2 = Advapi32Util.registryQueryInfoKey(
                        phkResult2.getValue(), WinNT.KEY_ALL_ACCESS);

                for (int y = 0; y < infoKey2.lpcSubKeys.getValue(); y++) {
                    EnumKey enumKey2 = Advapi32Util.registryRegEnumKey(
                            phkResult2.getValue(), y);
                    String subKey2 = Native.toString(enumKey2.lpName);

                    if (subKey2.equals("InprocHandler32")) {
                        comInfo.inprocHandler32 = (String) Advapi32Util
                                .registryGetValue(phkResult2.getValue(),
                                        subKey2, null);
                    } else if (subKey2.equals("InprocServer32")) {
                        comInfo.inprocServer32 = (String) Advapi32Util
                                .registryGetValue(phkResult2.getValue(),
                                        subKey2, null);
                    } else if (subKey2.equals("LocalServer32")) {
                        comInfo.localServer32 = (String) Advapi32Util
                                .registryGetValue(phkResult2.getValue(),
                                        subKey2, null);
                    } else if (subKey2.equals("ProgID")) {
                        comInfo.progID = (String) Advapi32Util
                                .registryGetValue(phkResult2.getValue(),
                                        subKey2, null);
                    } else if (subKey2.equals("TypeLib")) {
                        comInfo.typeLib = (String) Advapi32Util
                                .registryGetValue(phkResult2.getValue(),
                                        subKey2, null);
                    }
                }

                Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
                comInfos.add(comInfo);
            }
        } finally {
            Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
            Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
        }

        return comInfos;
    }

    /**
     * The Class COMInfo.
     * 
     * @author wolf.tobias@gmx.net The Class COMInfo.
     */
    public static class COMInfo {

        /** The clsid. */
        public String clsid;

        /** The inproc handler32. */
        public String inprocHandler32;

        /** The inproc server32. */
        public String inprocServer32;

        /** The local server32. */
        public String localServer32;

        /** The prog id. */
        public String progID;

        /** The type lib. */
        public String typeLib;

        /**
         * Instantiates a new cOM info.
         */
        public COMInfo() {
        }

        /**
         * Instantiates a new cOM info.
         * 
         * @param clsid
         *            the clsid
         */
        public COMInfo(String clsid) {
            this.clsid = clsid;
        }
    }
}
