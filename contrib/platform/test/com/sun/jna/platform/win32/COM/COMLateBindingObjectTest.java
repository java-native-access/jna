/* Copyright (c) 2021 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.AbstractWin32TestSupport;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class COMLateBindingObjectTest {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private final CLSID CLSID_InternetExplorer = new CLSID("{0002DF01-0000-0000-C000-000000000046}");
    private final LCID lcid = new LCID(0x0409); // LCID for english locale
    private final WinDef.WORD methodFlags = new WinDef.WORD(OleAuto.DISPATCH_METHOD);

    private final REFIID niid = new REFIID(Guid.IID_NULL);
    private final DISPIDByReference dispIdQuit = new DISPIDByReference();

    private PointerByReference ieApp;
    private Dispatch ieDispatch;

    public COMLateBindingObjectTest() {
    }

    @Before
    public void before() {
        AbstractWin32TestSupport.killProcessByName("iexplore.exe");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ex) {
        }

        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED);
        COMUtils.checkRC(hr);

        // Create InternetExplorer object
        ieApp = new PointerByReference();
        hr = Ole32.INSTANCE
            .CoCreateInstance(CLSID_InternetExplorer, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ieApp);
        COMUtils.checkRC(hr);

        ieDispatch = new Dispatch(ieApp.getValue());
        ieDispatch.AddRef();
        hr = ieDispatch.GetIDsOfNames(new REFIID(Guid.IID_NULL), new WString[]{new WString("Quit")}, 1, lcid, dispIdQuit);
        COMUtils.checkRC(hr);
    }

    @After
    public void after() {
        // Shutdown Internet Explorer
        DISPPARAMS.ByReference pDispParams = new DISPPARAMS.ByReference();
        VARIANT.ByReference pVarResult = new VARIANT.ByReference();
        IntByReference puArgErr = new IntByReference();
        EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();

        HRESULT hr = ieDispatch.Invoke(dispIdQuit.getValue(), niid, lcid, methodFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr, pExcepInfo, puArgErr);

        ieDispatch.Release();
        Ole32.INSTANCE.CoUninitialize();
    }

    @Test
    public void testPropertyAccessor() throws InterruptedException {
        final String testString = "Hallo Welt";
        COMLateBindingObject ieBinding = new COMLateBindingObject(ieDispatch);
        ieBinding.setProperty("Visible", true);
        assertTrue(ieBinding.getBooleanProperty("Visible"));
        boolean statusBarInitial = ieBinding.getBooleanProperty("StatusBar");
        ieBinding.setProperty("StatusBar", true);
        assertTrue(ieBinding.getBooleanProperty("StatusBar"));
        ieBinding.setProperty("StatusText", testString);
        assertEquals(testString, ieBinding.getStringProperty("StatusText"));
        ieBinding.setProperty("StatusBar", false);
        assertFalse(ieBinding.getBooleanProperty("StatusBar"));
        ieBinding.setProperty("StatusBar", statusBarInitial);
        ieBinding.setProperty("Visible", false);
        assertFalse(ieBinding.getBooleanProperty("Visible"));
    }
}
