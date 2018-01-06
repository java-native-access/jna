/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.LastErrorException;
import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.EnumKey;
import com.sun.jna.platform.win32.Advapi32Util.InfoKey;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;

/**
 * The Class COMUtils.
 * 
 * @author wolf.tobias@gmx.net The Class COMUtils.
 */
public abstract class COMUtils {

    /** The Constant CO_E_NOTINITIALIZED. */
    public static final int S_OK = 0;
    public static final int S_FALSE = 1;
    public static final int E_UNEXPECTED=0x8000FFFF;

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
        return hr >= 0;
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
        return hr < 0;
    }

    /**
     * Throw new exception.
     * 
     * @param hr
     *            the hr
     */
    public static void checkRC(HRESULT hr) {
        if (FAILED(hr)) {
            String formatMessage;
            try {
                formatMessage = Kernel32Util.formatMessage(hr) + "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
            } catch (LastErrorException ex) {
                // throws if HRESULT can't be resolved
                formatMessage = "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
            }
            throw new COMException(formatMessage, hr);
        }
    }

    /**
     * Check status of HRESULT if it indicates a failed call a COMInvokeException
     * is reaised.
     * 
     * <p>The string members of the pExcepInfo are freed in this call and can't
     * be used afterwards. The structure is not freeed, as it is expected, that
     * is allocated via the Memory object of JNA.</p>
     * 
     * @param hr
     *            the hr
     * @param pExcepInfo
     *            the excep info, it is expected
     * @param puArgErr
     *            the pu arg err
     */
    public static void checkRC(HRESULT hr, EXCEPINFO pExcepInfo,
            IntByReference puArgErr) {
        
        COMException resultException = null;
        
        if (FAILED(hr)) {
            StringBuilder formatMessage = new StringBuilder();

            Integer errorArg = null;
            Integer wCode = null;
            Integer scode = null;
            String description = null;
            String helpFile = null;
            Integer helpCtx = null;
            String source = null;
            
            if(puArgErr != null) {
                errorArg = puArgErr.getValue();
            }

            try {
                formatMessage.append(Kernel32Util.formatMessage(hr));
            } catch (LastErrorException ex) {
                // throws if HRESULT can't be resolved
            }
            
            formatMessage.append("(HRESULT: ");
            formatMessage.append(Integer.toHexString(hr.intValue()));
            formatMessage.append(")");
            
            if(pExcepInfo != null) {
                wCode = pExcepInfo.wCode.intValue();
                scode = pExcepInfo.scode.intValue();
                helpCtx = pExcepInfo.dwHelpContext.intValue();
                
                if(pExcepInfo.bstrSource != null) {
                    source = pExcepInfo.bstrSource.getValue();
                    formatMessage.append("\nSource:      ");
                    formatMessage.append(source);
                }
                if(pExcepInfo.bstrDescription != null) {
                    description = pExcepInfo.bstrDescription.getValue();
                    formatMessage.append("\nDescription: ");
                    formatMessage.append(description);
                }
                if(pExcepInfo.bstrHelpFile != null) {
                    helpFile = pExcepInfo.bstrHelpFile.getValue();
                }
            }
            
            throw new COMInvokeException(
                    formatMessage.toString(), 
                    hr, 
                    errorArg,
                    description,
                    helpCtx,
                    helpFile,
                    scode,
                    source,
                    wCode
            );
        }
        
        if(pExcepInfo != null) {
            if(pExcepInfo.bstrSource != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrSource);
            }
            if(pExcepInfo.bstrDescription != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrDescription);
            }
            if(pExcepInfo.bstrHelpFile != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrHelpFile);
            }
        }
        
        if(resultException != null) {
            throw resultException;
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
                    "CLSID", WinNT.KEY_READ);
            // open subkey
            InfoKey infoKey = Advapi32Util.registryQueryInfoKey(
                    phkResult.getValue(), WinNT.KEY_READ);

            for (int i = 0; i < infoKey.lpcSubKeys.getValue(); i++) {
                EnumKey enumKey = Advapi32Util.registryRegEnumKey(
                        phkResult.getValue(), i);
                subKey = Native.toString(enumKey.lpName);

                COMInfo comInfo = new COMInfo(subKey);

                phkResult2 = Advapi32Util.registryGetKey(phkResult.getValue(),
                        subKey, WinNT.KEY_READ);
                InfoKey infoKey2 = Advapi32Util.registryQueryInfoKey(
                        phkResult2.getValue(), WinNT.KEY_READ);

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
     * Check if COM was initialized correctly. The initialization status is not changed!
     *
     * <p>This is a debug function, not for normal usage!</p>
     * 
     * @return whether COM has been initialized
     */
    public static boolean comIsInitialized() {
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        if (hr.equals(W32Errors.S_OK)) {
            // User failed - uninitialize again and return false
            Ole32.INSTANCE.CoUninitialize();
            return false;
        } else if (hr.equals(W32Errors.S_FALSE)) {
            // OK Variant 1 - User initialized COM with same threading module as
            // in this check. According to MSDN CoUninitialize needs to be called
            // in this case.
            Ole32.INSTANCE.CoUninitialize();
            return true;
        } else if (hr.intValue() == W32Errors.RPC_E_CHANGED_MODE) {
            return true;
        }
        // If another result than the checked ones above happens handling is
        // delegated to the "normal" COM exception handling and a COMException
        // will be raised.
        COMUtils.checkRC(hr);
        // The return will not be met, as COMUtils#checkRC will raise an exception
        return false;
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
