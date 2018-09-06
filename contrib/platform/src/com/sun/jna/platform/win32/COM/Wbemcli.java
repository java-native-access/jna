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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * This header is used by Remote Desktop Services. It contains programming
 * interfaces for enumerating and querying Common Information Model (CIM)
 * objects.
 */
public interface Wbemcli {

    public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 0x00000010;
    public static final int WBEM_FLAG_FORWARD_ONLY = 0x00000020;
    public static final int WBEM_INFINITE = 0xFFFFFFFF;

    // Non-error constants
    // https://docs.microsoft.com/en-us/windows/desktop/wmisdk/wmi-non-error-constants
    public static final int WBEM_S_NO_ERROR = 0x0;
    public static final int WBEM_S_FALSE = 0x1;
    public static final int WBEM_S_TIMEDOUT = 0x40004;
    public static final int WBEM_S_NO_MORE_DATA = 0x40005;

    // Error constants
    // https://docs.microsoft.com/en-us/windows/desktop/wmisdk/wmi-error-constants
    public static final int WBEM_E_INVALID_NAMESPACE = 0x8004100e;
    public static final int WBEM_E_INVALID_CLASS = 0x80041010;
    public static final int WBEM_E_INVALID_QUERY = 0x80041017;

    // CIM Types
    public static final int CIM_ILLEGAL = 0xfff;
    public static final int CIM_EMPTY = 0;
    public static final int CIM_SINT8 = 16;
    public static final int CIM_UINT8 = 17;
    public static final int CIM_SINT16 = 2;
    public static final int CIM_UINT16 = 18;
    public static final int CIM_SINT32 = 3;
    public static final int CIM_UINT32 = 19;
    public static final int CIM_SINT64 = 20;
    public static final int CIM_UINT64 = 21;
    public static final int CIM_REAL32 = 4;
    public static final int CIM_REAL64 = 5;
    public static final int CIM_BOOLEAN = 11;
    public static final int CIM_STRING = 8;
    public static final int CIM_DATETIME = 101;
    public static final int CIM_REFERENCE = 102;
    public static final int CIM_CHAR16 = 103;
    public static final int CIM_OBJECT = 13;
    public static final int CIM_FLAG_ARRAY = 0x2000;

    /**
     * Contains and manipulates both WMI class definitions and class object
     * instances.
     */
    class IWbemClassObject extends Unknown {

        public IWbemClassObject() {
        }

        public IWbemClassObject(Pointer pvInstance) {
            super(pvInstance);
        }

        public HRESULT Get(WString wszName, int lFlags, VARIANT.ByReference pVal, IntByReference pType,
                IntByReference plFlavor) {
            // Get is 5th method of IWbemClassObjectVtbl in WbemCli.h
            return (HRESULT) _invokeNativeObject(4,
                    new Object[] { getPointer(), wszName, lFlags, pVal, pType, plFlavor }, HRESULT.class);
        }

        public HRESULT Get(String wszName, int lFlags, VARIANT.ByReference pVal, IntByReference pType,
                IntByReference plFlavor) {
            return Get(wszName == null ? null : new WString(wszName), lFlags, pVal, pType, plFlavor);
        }
    }

    /**
     * Used to enumerate Common Information Model (CIM) objects.
     */
    class IEnumWbemClassObject extends Unknown {

        public IEnumWbemClassObject() {
        }

        public IEnumWbemClassObject(Pointer pvInstance) {
            super(pvInstance);
        }

        public HRESULT Next(int lTimeOut, int uCount, Pointer[] ppObjects, IntByReference puReturned) {
            // Next is 5th method of IEnumWbemClassObjectVtbl in
            // WbemCli.h
            return (HRESULT) _invokeNativeObject(4,
                    new Object[] { getPointer(), lTimeOut, uCount, ppObjects, puReturned }, HRESULT.class);
        }

        public IWbemClassObject[] Next(int lTimeOut, int uCount) {
            Pointer[] resultArray = new Pointer[uCount];
            IntByReference resultCount = new IntByReference();
            HRESULT result = Next(lTimeOut, uCount, resultArray, resultCount);
            COMUtils.checkRC(result);
            IWbemClassObject[] returnValue = new IWbemClassObject[resultCount.getValue()];
            for (int i = 0; i < resultCount.getValue(); i++) {
                returnValue[i] = new IWbemClassObject(resultArray[i]);
            }
            return returnValue;
        }
    }

    /**
     * Used to obtain the initial namespace pointer to the IWbemServices
     * interface for WMI on a specific host computer.
     */
    class IWbemLocator extends Unknown {

        public static final CLSID CLSID_WbemLocator = new CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
        public static final GUID IID_IWbemLocator = new GUID("dc12a687-737f-11cf-884d-00aa004b2e24");

        public IWbemLocator() {
        }

        private IWbemLocator(Pointer pvInstance) {
            super(pvInstance);
        }

        public static IWbemLocator create() {
            PointerByReference pbr = new PointerByReference();

            HRESULT hres = Ole32.INSTANCE.CoCreateInstance(CLSID_WbemLocator, null, WTypes.CLSCTX_INPROC_SERVER,
                    IID_IWbemLocator, pbr);
            if (COMUtils.FAILED(hres)) {
                return null;
            }

            return new IWbemLocator(pbr.getValue());
        }

        public HRESULT ConnectServer(BSTR strNetworkResource, BSTR strUser, BSTR strPassword, BSTR strLocale,
                int lSecurityFlags, BSTR strAuthority, IWbemContext pCtx, PointerByReference ppNamespace) {
            // ConnectServier is 4th method of IWbemLocatorVtbl in WbemCli.h
            return (HRESULT) _invokeNativeObject(3, new Object[] { getPointer(), strNetworkResource, strUser,
                    strPassword, strLocale, lSecurityFlags, strAuthority, pCtx, ppNamespace }, HRESULT.class);
        }

        public IWbemServices ConnectServer(String strNetworkResource, String strUser, String strPassword,
                String strLocale, int lSecurityFlags, String strAuthority, IWbemContext pCtx) {
            BSTR strNetworkResourceBSTR = OleAuto.INSTANCE.SysAllocString(strNetworkResource);
            BSTR strUserBSTR = OleAuto.INSTANCE.SysAllocString(strUser);
            BSTR strPasswordBSTR = OleAuto.INSTANCE.SysAllocString(strPassword);
            BSTR strLocaleBSTR = OleAuto.INSTANCE.SysAllocString(strLocale);
            BSTR strAuthorityBSTR = OleAuto.INSTANCE.SysAllocString(strAuthority);

            PointerByReference pbr = new PointerByReference();

            try {
                HRESULT result = ConnectServer(strNetworkResourceBSTR, strUserBSTR, strPasswordBSTR, strLocaleBSTR,
                        lSecurityFlags, strAuthorityBSTR, pCtx, pbr);

                COMUtils.checkRC(result);

                return new IWbemServices(pbr.getValue());
            } finally {
                OleAuto.INSTANCE.SysFreeString(strNetworkResourceBSTR);
                OleAuto.INSTANCE.SysFreeString(strUserBSTR);
                OleAuto.INSTANCE.SysFreeString(strPasswordBSTR);
                OleAuto.INSTANCE.SysFreeString(strLocaleBSTR);
                OleAuto.INSTANCE.SysFreeString(strAuthorityBSTR);
            }
        }
    }

    /**
     * Used by clients and providers to access WMI services. The interface is
     * implemented by WMI and WMI providers, and is the primary WMI interface.
     */
    class IWbemServices extends Unknown {

        public IWbemServices() {
        }

        public IWbemServices(Pointer pvInstance) {
            super(pvInstance);
        }

        public HRESULT ExecQuery(BSTR strQueryLanguage, BSTR strQuery, int lFlags, IWbemContext pCtx,
                PointerByReference ppEnum) {
            // ExecQuery is 21st method of IWbemServicesVtbl in WbemCli.h
            return (HRESULT) _invokeNativeObject(20,
                    new Object[] { getPointer(), strQueryLanguage, strQuery, lFlags, pCtx, ppEnum }, HRESULT.class);
        }

        public IEnumWbemClassObject ExecQuery(String strQueryLanguage, String strQuery, int lFlags, IWbemContext pCtx) {
            BSTR strQueryLanguageBSTR = OleAuto.INSTANCE.SysAllocString(strQueryLanguage);
            BSTR strQueryBSTR = OleAuto.INSTANCE.SysAllocString(strQuery);
            try {
                PointerByReference pbr = new PointerByReference();

                HRESULT res = ExecQuery(strQueryLanguageBSTR, strQueryBSTR, lFlags, pCtx, pbr);

                COMUtils.checkRC(res);

                return new IEnumWbemClassObject(pbr.getValue());
            } finally {
                OleAuto.INSTANCE.SysFreeString(strQueryLanguageBSTR);
                OleAuto.INSTANCE.SysFreeString(strQueryBSTR);
            }
        }
    }

    /**
     * Optionally used to communicate additional context information to
     * providers when submitting IWbemServices calls to WMI
     */
    class IWbemContext extends Unknown {

        public IWbemContext() {
        }

        public IWbemContext(Pointer pvInstance) {
            super(pvInstance);
        }
    }
}
