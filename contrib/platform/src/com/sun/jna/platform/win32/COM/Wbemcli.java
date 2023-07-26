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
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdlUtil;
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

    public static final int WBEM_FLAG_RETURN_WBEM_COMPLETE = 0x00000000;
    public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 0x00000010;
    public static final int WBEM_FLAG_FORWARD_ONLY = 0x00000020;
    public static final int WBEM_FLAG_NO_ERROR_OBJECT = 0x00000040;
    public static final int WBEM_FLAG_SEND_STATUS = 0x00000080;
    public static final int WBEM_FLAG_ENSURE_LOCATABLE = 0x00000100;
    public static final int WBEM_FLAG_DIRECT_READ = 0x00000200;
    public static final int WBEM_MASK_RESERVED_FLAGS = 0x0001F000;
    public static final int WBEM_FLAG_USE_AMENDED_QUALIFIERS = 0x00020000;
    public static final int WBEM_FLAG_STRONG_VALIDATION = 0x00100000;
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

    public interface WBEM_CONDITION_FLAG_TYPE {
        public static final int WBEM_FLAG_ALWAYS = 0;
        public static final int WBEM_FLAG_ONLY_IF_TRUE = 0x1;
        public static final int WBEM_FLAG_ONLY_IF_FALSE = 0x2;
        public static final int WBEM_FLAG_ONLY_IF_IDENTICAL = 0x3;
        public static final int WBEM_MASK_PRIMARY_CONDITION = 0x3;
        public static final int WBEM_FLAG_KEYS_ONLY = 0x4;
        public static final int WBEM_FLAG_REFS_ONLY = 0x8;
        public static final int WBEM_FLAG_LOCAL_ONLY = 0x10;
        public static final int WBEM_FLAG_PROPAGATED_ONLY = 0x20;
        public static final int WBEM_FLAG_SYSTEM_ONLY = 0x30;
        public static final int WBEM_FLAG_NONSYSTEM_ONLY = 0x40;
        public static final int WBEM_MASK_CONDITION_ORIGIN = 0x70;
        public static final int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 0x100;
        public static final int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 0x200;
        public static final int WBEM_MASK_CLASS_CONDITION = 0x300;
    }

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

        public HRESULT GetMethod(String wszName, int lFlags, PointerByReference ppInSignature, PointerByReference ppOutSignature) {
            return GetMethod(wszName == null ? null : new WString(wszName), lFlags, ppInSignature, ppOutSignature);
        }

        public HRESULT GetMethod(WString wszName, int lFlags, PointerByReference ppInSignature, PointerByReference ppOutSignature) {
            // 20th method in IWbemClassObjectVtbl
            return (HRESULT) _invokeNativeObject(19,
                new Object[]{ getPointer(), wszName, lFlags, ppInSignature, ppOutSignature}, HRESULT.class);
        }

        public IWbemClassObject GetMethod(String wszName) {
            PointerByReference ppInSignature = new PointerByReference();
            HRESULT res = GetMethod(wszName, 0, ppInSignature, null);
            COMUtils.checkRC(res);
            return new IWbemClassObject(ppInSignature.getValue());
        }

        public HRESULT GetNames(String wszQualifierName, int lFlags, VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
            return GetNames(wszQualifierName == null ? null : new WString(wszQualifierName), lFlags, pQualifierVal, pNames);
        }

        public HRESULT GetNames(WString wszQualifierName, int lFlags, VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
            // 8th method in IWbemClassObjectVtbl
            return (HRESULT) _invokeNativeObject(7,
                new Object[]{ getPointer(), wszQualifierName, lFlags, pQualifierVal, pNames}, HRESULT.class);
        }

        public String[] GetNames(String wszQualifierName, int lFlags, VARIANT.ByReference pQualifierVal) {
            PointerByReference pbr = new PointerByReference();
            COMUtils.checkRC(GetNames(wszQualifierName, lFlags, pQualifierVal, pbr));
            Object[] nameObjects = (Object[]) OaIdlUtil.toPrimitiveArray(new SAFEARRAY(pbr.getValue()), true);
            String[] names = new String[nameObjects.length];
            for(int i = 0; i < nameObjects.length; i++) {
                names[i] = (String) nameObjects[i];
            }
            return names;
        }

        public HRESULT GetQualifierSet(PointerByReference ppQualSet) {
            // Get is the fourth method of IWbemClassObjectVtbl in WbemCli.h :
            return (HRESULT) _invokeNativeObject(3,
                    new Object[] { getPointer(), ppQualSet }, HRESULT.class);
        }

        public IWbemQualifierSet GetQualifierSet() {
            PointerByReference ppQualSet = new PointerByReference();
            HRESULT hr = GetQualifierSet(ppQualSet);
            COMUtils.checkRC(hr);
            IWbemQualifierSet qualifier = new IWbemQualifierSet(ppQualSet.getValue());

            return qualifier;
        }

        /*
        // https://docs.microsoft.com/en-us/windows/win32/api/wbemcli/nf-wbemcli-iwbemclassobject-getpropertyqualifierset
        HRESULT GetPropertyQualifierSet(
            [in]  LPCWSTR           wszProperty,
            [out] IWbemQualifierSet **ppQualSet
            );
         */
        public HRESULT GetPropertyQualifierSet(WString wszProperty, PointerByReference ppQualSet) {
            // Get is 12th method of IWbemClassObjectVtbl in WbemCli.h :
            return (HRESULT) _invokeNativeObject(11,
                    new Object[] { getPointer(), wszProperty, ppQualSet }, HRESULT.class);
        }

        public IWbemQualifierSet GetPropertyQualifierSet(String strProperty) {
            WString wszProperty = new WString(strProperty);
            PointerByReference ppQualSet = new PointerByReference();

            COMUtils.checkRC(GetPropertyQualifierSet(wszProperty, ppQualSet));
            IWbemQualifierSet qualifier  = new IWbemQualifierSet(ppQualSet.getValue());
            return qualifier;
        }

        public HRESULT Put(String wszName, int lFlags, Variant.VARIANT pVal, int Type) {
            return Put(wszName == null ? null : new WString(wszName), lFlags, pVal, Type);
        }

        public HRESULT Put(WString wszName, int lFlags, Variant.VARIANT pVal, int Type) {
            // 6th method in IWbemClassObjectVtbl
            return (HRESULT) _invokeNativeObject(5,
                new Object[]{ getPointer(), wszName, lFlags, pVal, Type}, HRESULT.class);
        }

        public void Put(String wszName, String pValue) {
            Variant.VARIANT aVariant = new Variant.VARIANT();
            BSTR strValue = OleAuto.INSTANCE.SysAllocString(pValue);
            try {
                aVariant.setValue(Variant.VT_BSTR, strValue);
                HRESULT res = Put(wszName, 0, aVariant, 0);
                COMUtils.checkRC(res);
            }
            finally {
                OleAuto.INSTANCE.VariantClear(aVariant);
            }
        }

        public HRESULT SpawnInstance(int lFlags, PointerByReference ppNewInstance) {
            // 16th method in IWbemClassObjectVtbl
            return (HRESULT) _invokeNativeObject(15,
                new Object[]{ getPointer(), lFlags, ppNewInstance}, HRESULT.class);
        }

        public IWbemClassObject SpawnInstance() {
            PointerByReference ppNewInstance = new PointerByReference();
            HRESULT res = SpawnInstance(0, ppNewInstance);
            COMUtils.checkRC(res);
            return new IWbemClassObject(ppNewInstance.getValue());
        }
    }

    class IWbemQualifierSet extends Unknown {
        public IWbemQualifierSet(Pointer pvInstance) {
            super(pvInstance);
        }

        public HRESULT Get(WString wszName, int lFlags, VARIANT.ByReference pVal, IntByReference plFlavor) {
            return (HRESULT) _invokeNativeObject(3,
                    new Object[] { getPointer(), wszName, lFlags, pVal, plFlavor }, HRESULT.class);
        }

        public String Get(String wszName) {
            WString wszNameStr = new WString(wszName);
            Variant.VARIANT.ByReference pQualifierVal = new Variant.VARIANT.ByReference();
            HRESULT hres = Get(wszNameStr, 0, pQualifierVal, null);
            if(hres.intValue() == 0x80041002) {
                // This error for some classes only.
                return null;
            }
            int qualifierInt = pQualifierVal.getVarType().intValue();
            switch(qualifierInt) {
                case Wbemcli.CIM_BOOLEAN:
                    return String.valueOf(pQualifierVal.booleanValue());
                case Wbemcli.CIM_STRING:
                    return pQualifierVal.stringValue();
            }
            return null;
        }

        public HRESULT GetNames(int lFlags, PointerByReference pNames) {
            return (HRESULT) _invokeNativeObject(6,
                    new Object[] { getPointer(), lFlags, pNames }, HRESULT.class);
        }

        public String[] GetNames() {
            PointerByReference pbr = new PointerByReference();
            COMUtils.checkRC(GetNames(0, pbr));
            Object[] nameObjects = (Object[]) OaIdlUtil.toPrimitiveArray(new SAFEARRAY(pbr.getValue()), true);
            String[] qualifierNames = new String[nameObjects.length];
            for(int i = 0; i < nameObjects.length; i++) {
                qualifierNames[i] = (String) nameObjects[i];
            }
            return qualifierNames;
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
            return (HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), strNetworkResource, strUser,
                strPassword, strLocale, lSecurityFlags, strAuthority, pCtx, ppNamespace}, HRESULT.class);
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

        public HRESULT ExecMethod(BSTR strObjectPath, BSTR strMethodName, int lFlags, IWbemContext pCtx,
                Pointer pInParams, PointerByReference ppOutParams, PointerByReference ppCallResult) {
            // ExecMethod is 25st method of IWbemServicesVtbl in WbemCli.h
            return (HRESULT) _invokeNativeObject(24,
                    new Object[] { getPointer(), strObjectPath, strMethodName, lFlags, pCtx, pInParams, ppOutParams, ppCallResult }, HRESULT.class);
        }

        public IWbemClassObject ExecMethod(String strObjectPath, String strMethodName, int lFlags, IWbemContext pCtx,
                IWbemClassObject inParams) {
            BSTR strObjectPathBSTR = OleAuto.INSTANCE.SysAllocString(strObjectPath);
            BSTR strMethodNameBSTR = OleAuto.INSTANCE.SysAllocString(strMethodName);
            try {
                PointerByReference ppOutParams = new PointerByReference();

                HRESULT res = ExecMethod(strObjectPathBSTR, strMethodNameBSTR, lFlags, pCtx, inParams.getPointer(), ppOutParams, null);

                COMUtils.checkRC(res);

                return new IWbemClassObject(ppOutParams.getValue());
            } finally {
                OleAuto.INSTANCE.SysFreeString(strObjectPathBSTR);
                OleAuto.INSTANCE.SysFreeString(strMethodNameBSTR);
            }
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

        public HRESULT GetObject(BSTR strObjectPath, int lFlags, IWbemContext pCtx,
                                 PointerByReference ppObject, PointerByReference ppCallResult) {
            // GetObject is the 7th method of IWbemServicesVtbl in WbemCli.h
            return (HRESULT) _invokeNativeObject(6,
                    new Object[] { getPointer(), strObjectPath, lFlags, pCtx, ppObject, ppCallResult}, HRESULT.class);
        }

        public IWbemClassObject GetObject(String strObjectPath, int lFlags, IWbemContext pCtx) {
            BSTR strObjectPathBSTR = OleAuto.INSTANCE.SysAllocString(strObjectPath);
            try {
                PointerByReference ppObject = new PointerByReference();
                HRESULT res = GetObject(strObjectPathBSTR, lFlags, pCtx, ppObject, null);
                COMUtils.checkRC(res);
                return new IWbemClassObject(ppObject.getValue());
            } finally {
                OleAuto.INSTANCE.SysFreeString(strObjectPathBSTR);
            }
        }
    }

    /**
     * Optionally used to communicate additional context information to
     * providers when submitting IWbemServices calls to WMI
     */
    class IWbemContext extends Unknown {
        public static final CLSID CLSID_WbemContext  = new CLSID("674B6698-EE92-11D0-AD71-00C04FD8FDFF");
        public static final GUID IID_IWbemContext  = new GUID("44aca674-e8fc-11d0-a07c-00c04fb68820");

        public IWbemContext() {
        }

        public static IWbemContext create() {
            PointerByReference pbr = new PointerByReference();

            HRESULT hres = Ole32.INSTANCE.CoCreateInstance(CLSID_WbemContext, null, WTypes.CLSCTX_INPROC_SERVER,
                    IID_IWbemContext, pbr);
            if (COMUtils.FAILED(hres)) {
                return null;
            }

            return new IWbemContext(pbr.getValue());
        }

        public IWbemContext(Pointer pvInstance) {
            super(pvInstance);
        }

        public void SetValue(String wszName, int lFlag, Variant.VARIANT pValue) {
            BSTR wszNameBSTR = OleAuto.INSTANCE.SysAllocString(wszName);
            try {
                // SetValue is the 9th method of IWbemContextVtbl in WbemCli.h
                HRESULT res = (HRESULT) _invokeNativeObject(8,
                        new Object[] { getPointer(), wszNameBSTR, lFlag, pValue}, HRESULT.class);
                COMUtils.checkRC(res);
            } finally {
                OleAuto.INSTANCE.SysFreeString(wszNameBSTR);
            }
        }

        public void SetValue(String wszName, int lFlag, boolean pValue) {
            Variant.VARIANT aVariant = new Variant.VARIANT();
            aVariant.setValue(Variant.VT_BOOL, pValue ? Variant.VARIANT_TRUE : Variant.VARIANT_FALSE);
            SetValue(wszName, lFlag, aVariant);
            OleAuto.INSTANCE.VariantClear(aVariant);
        }

        public void SetValue(String wszName, int lFlag, String pValue) {
            Variant.VARIANT aVariant = new Variant.VARIANT();
            BSTR strValue = OleAuto.INSTANCE.SysAllocString(pValue);
            try {
                aVariant.setValue(Variant.VT_LPSTR, strValue);
                SetValue(wszName, lFlag, aVariant);
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(strValue);
            }
        }
    }
}
