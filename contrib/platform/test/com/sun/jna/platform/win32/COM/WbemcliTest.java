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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.sun.jna.platform.win32.Variant.VT_ARRAY;
import static com.sun.jna.platform.win32.Variant.VT_BSTR;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.COM.Wbemcli.IEnumWbemClassObject;
import com.sun.jna.platform.win32.COM.Wbemcli.IWbemClassObject;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for Wbemcli and WbemcliUti methods and classes used to query WMI.
 * Also tests some methods of Ole32.
 */
public class WbemcliTest {

    /**
     * Properties to retrieve from Win32_Process
     */
    enum ProcessProperty {
        PROCESSID, // UINT32
        WORKINGSETSIZE, // UINT64
        CREATIONDATE, // DATETIME
        EXECUTIONSTATE, // UINT16, Always NULL
        COMMANDLINE; // STRING
    }

    /**
     * Properties to retrieve from Win32_OperatingSystem
     */
    enum OperatingSystemProperty {
        FOREGROUNDAPPLICATIONBOOST, // UINT8
        OSTYPE, // UINT16
        PRIMARY; // BOOLEAN
    }

    @Before
    public void initCom() {
        assertEquals(COMUtils.S_OK, Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED).intValue());
        assertEquals(COMUtils.S_OK,
                Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, Ole32.RPC_C_AUTHN_LEVEL_DEFAULT,
                        Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE, null).intValue());
    }

    @After
    public void unInitCom() {
        Ole32.INSTANCE.CoUninitialize();
    }

    @Test
    public void testWmiExceptions() {
        WmiQuery<ProcessProperty> processQuery = new WmiQuery<ProcessProperty>("Win32_Process", ProcessProperty.class);
        try {
            // This query should take more than 0 ms
            processQuery.execute(0);
            // Highly unlikely to get this far, but if we do, no failure
            System.err.println("Warning: Win32_Process WMI query returned in 0 ms. This is unusual.");
        } catch (TimeoutException expected) {
            assertEquals("No results after 0 ms.", expected.getMessage());
        }

        // Invalid class
        processQuery.setWmiClassName("Win32_ClassDoesNotExist");
        try {
            processQuery.execute();
            fail("Win32_ClassDoesNotExist does not exist.");
        } catch (COMException expected) {
            assertEquals(Wbemcli.WBEM_E_INVALID_CLASS, expected.getHresult().intValue());
        }

        // Valid class but properties don't match the class
        processQuery.setWmiClassName("Win32_OperatingSystem");
        try {
            processQuery.execute();
            fail("Properties in the process enum aren't in Win32_OperatingSystem");
        } catch (COMException expected) {
            assertEquals(Wbemcli.WBEM_E_INVALID_QUERY, expected.getHresult().intValue());
        }

        // Invalid namespace
        processQuery.setNameSpace("Invalid");
        try {
            processQuery.execute();
            fail("This is an invalid namespace.");
        } catch (COMException expected) {
            assertEquals(Wbemcli.WBEM_E_INVALID_NAMESPACE, expected.getHresult().intValue());
        }
    }

    @Test
    public void testNamespace() {
        assertTrue(WbemcliUtil.hasNamespace(WbemcliUtil.DEFAULT_NAMESPACE));
        assertFalse(WbemcliUtil.hasNamespace("Name Space"));
    }

    @Test
    public void testWmiProcesses() {
        WmiQuery<ProcessProperty> processQuery = new WmiQuery<ProcessProperty>("Win32_Process", ProcessProperty.class);

        WmiResult<ProcessProperty> processes = processQuery.execute();
        // There has to be at least one process (this one!)
        assertTrue(processes.getResultCount() > 0);
        int lastProcessIndex = processes.getResultCount() - 1;

        // PID is UINT32 = VT_I4
        assertEquals(Wbemcli.CIM_UINT32, processes.getCIMType(ProcessProperty.PROCESSID));
        assertEquals(Variant.VT_I4, processes.getVtType(ProcessProperty.PROCESSID));
        assertTrue((Integer) processes.getValue(ProcessProperty.PROCESSID, lastProcessIndex) >= 0);

        // WSS is UINT64 = STRING
        assertEquals(Wbemcli.CIM_UINT64, processes.getCIMType(ProcessProperty.WORKINGSETSIZE));
        assertEquals(Variant.VT_BSTR, processes.getVtType(ProcessProperty.WORKINGSETSIZE));
        String wssStr = (String) processes.getValue(ProcessProperty.WORKINGSETSIZE, lastProcessIndex);
        assertTrue(Long.parseLong(wssStr) > 0);

        // EXECUTIONSTATE is UINT16 but is always null
        assertEquals(Wbemcli.CIM_UINT16, processes.getCIMType(ProcessProperty.EXECUTIONSTATE));
        assertEquals(Variant.VT_NULL, processes.getVtType(ProcessProperty.EXECUTIONSTATE));
        Object state = processes.getValue(ProcessProperty.EXECUTIONSTATE, lastProcessIndex);
        assertNull(state);

        // CreationDate is DATETIME = STRING
        // and be in CIM_DATETIME format yyyymmddhhmmss.mmmmmm+zzz
        assertEquals(Wbemcli.CIM_DATETIME, processes.getCIMType(ProcessProperty.CREATIONDATE));
        assertEquals(Variant.VT_BSTR, processes.getVtType(ProcessProperty.CREATIONDATE));
        String cdate = (String) processes.getValue(ProcessProperty.CREATIONDATE, lastProcessIndex);

        assertEquals(25, cdate.length());
        assertEquals('.', cdate.charAt(14));
        assertTrue(Integer.parseInt(cdate.substring(0, 4)) > 1970);
        assertTrue(Integer.parseInt(cdate.substring(4, 6)) <= 12);
        assertTrue(Integer.parseInt(cdate.substring(6, 8)) <= 31);
    }


    @Test
    public void testShowProperties() {
        Wbemcli.IWbemServices svc = null;
        IEnumWbemClassObject enumRes = null;
        Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
        IntByReference pType = new IntByReference();
        IntByReference plFlavor = new IntByReference();
        int resultCount = 0;
        try {
            svc = WbemcliUtil.connectServer(WbemcliUtil.DEFAULT_NAMESPACE);
            enumRes = svc.ExecQuery("WQL", "SELECT * FROM Win32_Process WHERE COMMANDLINE IS NOT NULL", Wbemcli.WBEM_FLAG_FORWARD_ONLY, null);
            while(true) {
                IWbemClassObject[] results = enumRes.Next(Wbemcli.WBEM_INFINITE, 100);
                if(results.length == 0) {
                    break;
                }

                for(IWbemClassObject iwco: results) {
                    resultCount++;
                    Set<String> names = new HashSet<String>(Arrays.asList(iwco.GetNames(null, 0, null)));
                    assertTrue(names.contains("CommandLine"));
                    assertTrue(names.contains("ProcessId"));
                    assertTrue(names.contains("WorkingSetSize"));
                    assertTrue(names.contains("ExecutionState"));
                    assertTrue(names.contains("CreationDate"));
                    try {
                        // COMMANDLINE is STRING = VT_BSTR
                        iwco.Get("COMMANDLINE", 0, pVal, pType, plFlavor);
                        assertEquals(Wbemcli.CIM_STRING, pType.getValue());
                        assertEquals(Variant.VT_BSTR, pVal.getVarType().intValue());
                        String commandline = pVal.stringValue();
                        assertTrue(commandline != null && (!commandline.isEmpty()));
                        OleAuto.INSTANCE.VariantClear(pVal);

                        // PID is UINT32 = VT_I4
                        iwco.Get("PROCESSID", 0, pVal, pType, plFlavor);
                        assertEquals(Wbemcli.CIM_UINT32, pType.getValue());
                        assertEquals(Variant.VT_I4, pVal.getVarType().intValue());
                        long processId = pVal.longValue();
                        assertTrue(processId >= 0);
                        OleAuto.INSTANCE.VariantClear(pVal);

                        // WSS is UINT64 = STRING
                        iwco.Get("WORKINGSETSIZE", 0, pVal, pType, plFlavor);
                        assertEquals(Wbemcli.CIM_UINT64, pType.getValue());
                        assertEquals(Variant.VT_BSTR, pVal.getVarType().intValue());
                        String workingSetSizeString = pVal.stringValue();
                        long workingSetSize = Long.parseLong(workingSetSizeString);
                        assertTrue(workingSetSize > 0);
                        OleAuto.INSTANCE.VariantClear(pVal);

                        // EXECUTIONSTATE is UINT16 but is always null
                        iwco.Get("EXECUTIONSTATE", 0, pVal, pType, plFlavor);
                        assertEquals(Wbemcli.CIM_UINT16, pType.getValue());
                        assertEquals(Variant.VT_NULL, pVal.getVarType().intValue());
                        OleAuto.INSTANCE.VariantClear(pVal);

                        // CreationDate is DATETIME = STRING
                        // and be in CIM_DATETIME format yyyymmddhhmmss.mmmmmm+zzz
                        iwco.Get("CREATIONDATE", 0, pVal, pType, plFlavor);
                        String cdate = pVal.stringValue();
                        assertEquals(Wbemcli.CIM_DATETIME, pType.getValue());
                        assertEquals(Variant.VT_BSTR, pVal.getVarType().intValue());
                        assertEquals(25, cdate.length());
                        assertEquals('.', cdate.charAt(14));
                        int year = Integer.parseInt(cdate.substring(0, 4));
                        int month = Integer.parseInt(cdate.substring(4, 6));
                        int day = Integer.parseInt(cdate.substring(6, 8));
                        int hour = Integer.parseInt(cdate.substring(8, 10));
                        int minute = Integer.parseInt(cdate.substring(10, 12));
                        int second = Integer.parseInt(cdate.substring(12, 14));
                        assertTrue(year > 1970);
                        assertTrue(month >= 1 && month <= 12);
                        assertTrue(day >= 1 && day <= 31);
                        assertTrue(hour >= 0 && hour <= 23);
                        assertTrue(minute >= 0 && minute <= 59);
                        assertTrue(second >= 0 && second <= 59);
                        OleAuto.INSTANCE.VariantClear(pVal);

//                        System.out.printf("% 6d\t% 10d\t%04d-%02d-%02dT%02d:%02d:%02d\t%s%n", processId, workingSetSize, year, month, day, hour, minute, second, commandline);
                    } finally {
                        iwco.Release();
                    }
                }
            }
        } finally {
            if (svc != null) svc.Release();
            if (enumRes != null) enumRes.Release();
        }

        // It is expected, that we can read at least one result (our process)
        assertTrue(resultCount > 0);
    }

    @Test
    public void testWmiOperatingSystem() {
        WmiQuery<OperatingSystemProperty> operatingSystemQuery = new WmiQuery<OperatingSystemProperty>("Win32_OperatingSystem",
                OperatingSystemProperty.class);

        WmiResult<OperatingSystemProperty> os = operatingSystemQuery.execute();
        // There has to be at least one os (this one!)
        assertTrue(os.getResultCount() > 0);

        // ForegroundApplicationBoost is UINT8 = VT_UI1
        assertEquals(Wbemcli.CIM_UINT8, os.getCIMType(OperatingSystemProperty.FOREGROUNDAPPLICATIONBOOST));
        assertEquals(Variant.VT_UI1, os.getVtType(OperatingSystemProperty.FOREGROUNDAPPLICATIONBOOST));
        assertTrue((Byte) os.getValue(OperatingSystemProperty.FOREGROUNDAPPLICATIONBOOST, 0) >= 0);

        // OSTYPE is UINT16 = VT_I4
        assertEquals(Wbemcli.CIM_UINT16, os.getCIMType(OperatingSystemProperty.OSTYPE));
        assertEquals(Variant.VT_I4, os.getVtType(OperatingSystemProperty.OSTYPE));
        assertTrue((Integer) os.getValue(OperatingSystemProperty.OSTYPE, 0) >= 0);

        // PRIMARY is BOOLEAN = VT_BOOL
        assertEquals(Wbemcli.CIM_BOOLEAN, os.getCIMType(OperatingSystemProperty.PRIMARY));
        assertEquals(Variant.VT_BOOL, os.getVtType(OperatingSystemProperty.PRIMARY));
        assertNotNull(os.getValue(OperatingSystemProperty.PRIMARY, 0));
    }

    enum Win32_DiskDrive_Values {
        CAPTION,
        CAPABILITIES
    }

    @Test
    public void testUnsupportedValues() {
        WmiQuery<Win32_DiskDrive_Values> serialNumberQuery = new WmiQuery<Win32_DiskDrive_Values>("Win32_DiskDrive", Win32_DiskDrive_Values.class);
        WmiResult<Win32_DiskDrive_Values> result = serialNumberQuery.execute();
        assertTrue(result.getResultCount() > 0);
        for (int i = 0; i < result.getResultCount(); i++) {
            assertNotNull(result.getValue(Win32_DiskDrive_Values.CAPTION, i));
            // Capabilities are represented by a SAFEARRAY, this not supported
            // in the simplified API
            assertNull(result.getValue(Win32_DiskDrive_Values.CAPABILITIES, i));
        }
    }

    @Test
    public void testIWbemClassObjectGetQualifierSet() {

        Wbemcli.IWbemServices svc = null;
        Wbemcli.IEnumWbemClassObject enumRes = null;
        Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
        IntByReference pType = new IntByReference();
        IntByReference plFlavor = new IntByReference();

        boolean foundWin32_Process = false;
        try {
            svc = connectServerEnglishLocale(WbemcliUtil.DEFAULT_NAMESPACE);
            enumRes = svc.ExecQuery(
                    "WQL",
                    "SELECT * FROM meta_class",
                    Wbemcli.WBEM_FLAG_FORWARD_ONLY | Wbemcli.WBEM_FLAG_USE_AMENDED_QUALIFIERS, null);

            while (true) {
                Wbemcli.IWbemClassObject[] results = enumRes.Next(Wbemcli.WBEM_INFINITE, 1);
                if (results.length == 0) {
                    break;
                }

                Wbemcli.IWbemClassObject classObject = results[0];
                Variant.VARIANT.ByReference pQualifierVal = new Variant.VARIANT.ByReference();

                COMUtils.checkRC(classObject.Get("__CLASS", 0, pVal, pType, plFlavor));
                String className = pVal.stringValue();
                if(! className.equals("Win32_Process")) {
                    continue;
                }
                foundWin32_Process = true;
                OleAuto.INSTANCE.VariantClear(pVal);

                COMUtils.checkRC(classObject.Get("__SUPERCLASS", 0, pVal, pType, plFlavor));
                Object baseClass = pVal.getValue();
                OleAuto.INSTANCE.VariantClear(pVal);
                assertEquals("CIM_Process", baseClass.toString());

                String[] propertyNames = classObject.GetNames(null, 0, pQualifierVal);
                assertTrue(Arrays.asList(propertyNames).contains("ProcessId"));

                Wbemcli.IWbemQualifierSet classQualifiersSet = classObject.GetQualifierSet();
                String[] classQualifiersNames = classQualifiersSet.GetNames();
                assertTrue(Arrays.asList(classQualifiersNames).contains("DisplayName"));
                String classDisplayName = classQualifiersSet.Get("DisplayName");
                assertEquals("Processes", classDisplayName);

                Wbemcli.IWbemQualifierSet propertyQualifiersSet = classObject.GetPropertyQualifierSet("ProcessId");
                String[] propertyQualifierNames = propertyQualifiersSet.GetNames();

                assertTrue(Arrays.asList(propertyQualifierNames).contains("DisplayName"));
                String propertyDisplayName = propertyQualifiersSet.Get("DisplayName");
                assertEquals("Process Id", propertyDisplayName);

                assertTrue(Arrays.asList(propertyQualifierNames).contains("CIMTYPE"));
                String propertyCIMTYPE = propertyQualifiersSet.Get("CIMTYPE");
                assertEquals("uint32", propertyCIMTYPE);

                classObject.Release();
            }
        } finally {
            if (svc != null) svc.Release();
            if (enumRes != null) enumRes.Release();
        }
        assertTrue(foundWin32_Process);
    }

    @Test
    public void testIWbemContextSetValue() {
        long currentPid = Kernel32.INSTANCE.GetCurrentProcessId();
        String objectPath = String.format("\\\\.\\%s:Win32_Process.Handle=\"%d\"", WbemcliUtil.DEFAULT_NAMESPACE, currentPid);

        // This context object retrieves only parts of a WMI instance.
        Wbemcli.IWbemContext pctxDrive = new Wbemcli.IWbemContext().create();
        pctxDrive.SetValue("__GET_EXTENSIONS", 0, true);
        pctxDrive.SetValue("__GET_EXT_CLIENT_REQUEST", 0, true);

        // Create a safe array of just one property to retrieve.
        SAFEARRAY psaProperties = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_BSTR), 1);
        OleAuto.INSTANCE.SafeArrayLock(psaProperties);
        try {
            WTypes.BSTR strPropertyBSTR = OleAuto.INSTANCE.SysAllocString("ProcessId");
            try {
                psaProperties.putElement(strPropertyBSTR, 0);
            } finally {
                OleAuto.INSTANCE.SysFreeString(strPropertyBSTR);
            }
        } finally {
            OleAuto.INSTANCE.SafeArrayUnlock(psaProperties);
        }

        Variant.VARIANT.ByReference vPropertyList = new Variant.VARIANT.ByReference();
        vPropertyList.setVarType((short) (VT_ARRAY | VT_BSTR));
        vPropertyList.setValue(psaProperties);
        pctxDrive.SetValue("__GET_EXT_PROPERTIES", 0, vPropertyList);
        psaProperties.destroy();

        Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();
        Wbemcli.IWbemServices svc = null;
        try {
            svc = WbemcliUtil.connectServer(WbemcliUtil.DEFAULT_NAMESPACE);
            Wbemcli.IWbemClassObject classObject = svc.GetObject(objectPath, Wbemcli.WBEM_FLAG_RETURN_WBEM_COMPLETE, pctxDrive);
            // The properties "Handle" and "PropertyId" must have the same values with different types.
            COMUtils.checkRC(classObject.Get("ProcessId", 0, pVal, null, null));
        }
        finally {
            if (svc != null) svc.Release();
        }
        assertEquals(currentPid, pVal.longValue());
    }

    @Test
    public void testWmiObject() {
        Wbemcli.IWbemServices svc = null;
        Variant.VARIANT.ByReference pVal = new Variant.VARIANT.ByReference();

        String className = "StdRegProv";
        String methodName = "GetStringValue";

        IWbemClassObject regClass = null;
        IWbemClassObject method = null;
        IWbemClassObject instance = null;
        IWbemClassObject result = null;

        try {
            svc = WbemcliUtil.connectServer("ROOT\\Default");

            regClass = svc.GetObject(className, 0, null);
            method = regClass.GetMethod(methodName);
            instance = method.SpawnInstance();

            instance.Put("sSubKeyName", "SOFTWARE\\Microsoft\\Windows\\CurrentVersion");
            instance.Put("sValueName", "CommonFilesDir");

            result = svc.ExecMethod(className, methodName, 0, null, instance);

            result.Get("sValue", 0, pVal, null, null);
            assertEquals(Variant.VT_BSTR, pVal.getVarType().intValue());
            assertTrue(!pVal.stringValue().isEmpty());
            OleAuto.INSTANCE.VariantClear(pVal);
        } finally {
            if (svc != null) svc.Release();
            if (result != null) result.Release();
            if (method != null) method.Release();
            if (instance != null) instance.Release();
            if (regClass != null) regClass.Release();
        }
    }

    /**
     * Copy from WbemcliUtil#connectServer with American English selected as
     * locale.
     */
    private static Wbemcli.IWbemServices connectServerEnglishLocale(String namespace) {
        Wbemcli.IWbemLocator loc = Wbemcli.IWbemLocator.create();
        if (loc == null) {
            throw new COMException("Failed to create WbemLocator object.");
        }

        Wbemcli.IWbemServices services = loc.ConnectServer(namespace, null, null, "MS_409", 0, null, null);
        loc.Release();

        WinNT.HRESULT hres = Ole32.INSTANCE.CoSetProxyBlanket(services, Ole32.RPC_C_AUTHN_WINNT, Ole32.RPC_C_AUTHZ_NONE, null,
            Ole32.RPC_C_AUTHN_LEVEL_CALL, Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE);
        if (COMUtils.FAILED(hres)) {
            services.Release();
            throw new COMException("Could not set proxy blanket.", hres);
        }
        return services;
    }
}
