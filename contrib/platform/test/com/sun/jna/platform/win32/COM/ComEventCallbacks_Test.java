/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.AbstractWin32TestSupport;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOLByReference;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.junit.Assert;

public class ComEventCallbacks_Test {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private final CLSID CLSID_InternetExplorer = new CLSID("{0002DF01-0000-0000-C000-000000000046}");
    private final IID IID_IConnectionPointContainer = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
    private final IID IID_DWebBrowserEvents2 = new IID("{34A715A0-6587-11D0-924A-0020AFC7AC4D}");
    private final REFIID niid = new REFIID(Guid.IID_NULL);
    private final LCID lcid = new LCID(0x0409); // LCID for english locale
    private final WinDef.WORD methodFlags = new WinDef.WORD(OleAuto.DISPATCH_METHOD);
    private final WinDef.WORD propertyPutFlags = new WinDef.WORD(OleAuto.DISPATCH_PROPERTYPUT);

    private final DISPIDByReference dispIdVisible = new DISPIDByReference();
    private final DISPIDByReference dispIdQuit = new DISPIDByReference();
    private final DISPIDByReference dispIdNavigate = new DISPIDByReference();

    private PointerByReference ieApp;
    private Dispatch ieDispatch;



    @Before
    public void before() {
        AbstractWin32TestSupport.killProcessByName("iexplore.exe");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ex) {}

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
        hr = ieDispatch.GetIDsOfNames(new REFIID(Guid.IID_NULL), new WString[]{new WString("Visible")}, 1, lcid, dispIdVisible);
        COMUtils.checkRC(hr);
        hr = ieDispatch.GetIDsOfNames(new REFIID(Guid.IID_NULL), new WString[]{new WString("Navigate")}, 1, lcid, dispIdNavigate);
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
    public void queryInterface_ConnectionPointContainer() {
        Unknown unk = new Unknown(ieApp.getValue());
        PointerByReference ppCpc = new PointerByReference();
        HRESULT hr = unk.QueryInterface(new REFIID(IID_IConnectionPointContainer), ppCpc);
        COMUtils.checkRC(hr);
        // On success the returned pointer must not be null
        Assert.assertNotNull(ppCpc.getPointer());
    }

    @Test
    public void FindConnectionPoint() {
        // query for ConnectionPointContainer
        Unknown unk = new Unknown(ieApp.getValue());
        PointerByReference ppCpc = new PointerByReference();
        HRESULT hr = unk.QueryInterface(new REFIID(IID_IConnectionPointContainer), ppCpc);
        COMUtils.checkRC(hr);
        ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

        // find connection point for DWebBrowserEvents2
        REFIID riid = new REFIID(IID_DWebBrowserEvents2);
        PointerByReference ppCp = new PointerByReference();
        hr = cpc.FindConnectionPoint(riid, ppCp);
        COMUtils.checkRC(hr);

        // On success the returned pointer must not be null
        Assert.assertNotNull(ppCpc.getPointer());
    }

    @Test
    public void GetConnectionInterface() {
        // query for ConnectionPointContainer
        Unknown unk = new Unknown(this.ieApp.getValue());
        PointerByReference ppCpc = new PointerByReference();
        HRESULT hr = unk.QueryInterface(new REFIID(IID_IConnectionPointContainer), ppCpc);
        COMUtils.checkRC(hr);
        ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

        // find connection point for DWebBrowserEvents2
        REFIID riid = new REFIID(IID_DWebBrowserEvents2);
        PointerByReference ppCp = new PointerByReference();
        hr = cpc.FindConnectionPoint(riid, ppCp);
        COMUtils.checkRC(hr);
        ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());

        IID cp_iid = new IID();
        hr = cp.GetConnectionInterface(cp_iid);
        COMUtils.checkRC(hr);

        Assert.assertEquals(IID_DWebBrowserEvents2, cp_iid);
    }

    class DWebBrowserEvents2_Listener implements IDispatchCallback {

        private final int DISPID_NavigateComplete2 = 0x000000fc;
        private final int DISPID_BeforeNavigate2 = 0x000000fa;

        public DispatchListener listener = new DispatchListener(this);

        @Override
        public Pointer getPointer() {
            return this.listener.getPointer();
        }

        //------------------------ IDispatch ------------------------------
        @Override
        public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
            return new HRESULT(WinError.E_NOTIMPL);
        }

        @Override
        public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
            return new HRESULT(WinError.E_NOTIMPL);
        }

        @Override
        public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames, LCID lcid, DISPIDByReference rgDispId) {
            return new HRESULT(WinError.E_NOTIMPL);
        }

        public volatile boolean blockNavigation = false;
        public volatile boolean navigateComplete2Called = false;
        public volatile String navigateComplete2String = null;

        @Override
        public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid,
                WORD wFlags, DISPPARAMS.ByReference pDispParams,
                VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
                IntByReference puArgErr) {

            VARIANT[] arguments = pDispParams.getArgs();

            try {
                switch (dispIdMember.intValue()) {
                    case DISPID_NavigateComplete2:
                        navigateComplete2Called = true;
                        // URL ist passed as VARIANT$ByReference
                        VARIANT urlByRef = arguments[0];
                        navigateComplete2String = ((VARIANT) urlByRef.getValue()).stringValue();
                        break;
                    case DISPID_BeforeNavigate2:
                        VARIANT Cancel = arguments[0];
                        VARIANT Headers = arguments[1];
                        VARIANT PostData = arguments[2];
                        VARIANT TargetFrameName = arguments[3];
                        VARIANT Flags = arguments[4];
                        VARIANT URL = arguments[5];
                        VARIANT pDisp = arguments[6];
                        VARIANT_BOOLByReference cancelValue = ((VARIANT_BOOLByReference) Cancel.getValue());
                        if (blockNavigation) {
                            cancelValue.setValue(Variant.VARIANT_TRUE);
                        }
                        break;
                }
            } catch (Throwable ex) {
                ex.printStackTrace(System.out);
                System.out.println(ex);
            }

            return new HRESULT(WinError.E_NOTIMPL);
        }

        //------------------------ IUnknown ------------------------------
        public volatile boolean QueryInterface_called = false;

        @Override
        public HRESULT QueryInterface(REFIID refiid, PointerByReference ppvObject) {
            this.QueryInterface_called = true;
            if (null == ppvObject) {
                return new HRESULT(WinError.E_POINTER);
            }

            if (refiid.getValue().equals(IID_DWebBrowserEvents2)) {
                ppvObject.setValue(this.getPointer());
                return WinError.S_OK;
            }

            if (refiid.getValue().equals(Unknown.IID_IUNKNOWN)) {
                ppvObject.setValue(this.getPointer());
                return WinError.S_OK;
            }

            if (refiid.getValue().equals(Dispatch.IID_IDISPATCH)) {
                ppvObject.setValue(this.getPointer());
                return WinError.S_OK;
            }

            ppvObject.setValue(Pointer.NULL);
            return new HRESULT(WinError.E_NOINTERFACE);
        }

        @Override
        public int AddRef() {
            return 0;
        }

        @Override
        public int Release() {
            return 0;
        }

    }

    @Test
    public void testComEventCallback() throws InterruptedException {
        VARIANT.ByReference pVarResult = new VARIANT.ByReference();
        IntByReference puArgErr = new IntByReference();
        EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
        HRESULT hr;

        DISPPARAMS.ByReference pDispParams;

        pDispParams = new DISPPARAMS.ByReference();
        pDispParams.setArgs(new VARIANT[] {new VARIANT(true)});
        pDispParams.setRgdispidNamedArgs(new DISPID[] {OaIdl.DISPID_PROPERTYPUT});
        // Visible-Prioperty
        hr = ieDispatch.Invoke(dispIdVisible.getValue(), niid, lcid, propertyPutFlags, pDispParams, null, null, null);
        COMUtils.checkRC(hr);

        // query for ConnectionPointContainer
        Unknown unk = new Unknown(ieApp.getValue());
        PointerByReference ppCpc = new PointerByReference();
        hr = unk.QueryInterface(new REFIID(IID_IConnectionPointContainer), ppCpc);
        COMUtils.checkRC(hr);
        ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

        // find connection point for DWebBrowserEvents2
        REFIID riid = new REFIID(IID_DWebBrowserEvents2);
        PointerByReference ppCp = new PointerByReference();
        hr = cpc.FindConnectionPoint(riid, ppCp);
        COMUtils.checkRC(hr);
        final ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());
        IID cp_iid = new IID();
        hr = cp.GetConnectionInterface(cp_iid);
        COMUtils.checkRC(hr);

        final DWebBrowserEvents2_Listener listener = new DWebBrowserEvents2_Listener();
        final DWORDByReference pdwCookie = new DWORDByReference();
        HRESULT hr1 = cp.Advise(listener, pdwCookie);
        COMUtils.checkRC(hr1);

        // Advise make several callbacks into the object passed in - at this
        // point QueryInterface must have be called multiple times
        Assert.assertTrue(listener.QueryInterface_called);

        // Call Navigate with URL https://github.com/java-native-access/jna
        String navigateURL = "https://github.com/java-native-access/jna";
        String blockedURL = "http://www.google.de";

        VARIANT[] arguments = new VARIANT[] {new VARIANT(navigateURL)};
        pDispParams = new DISPPARAMS.ByReference();
        pDispParams.setArgs(arguments);
        hr = ieDispatch.Invoke(dispIdNavigate.getValue(), niid, lcid, methodFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr, pExcepInfo, puArgErr);

        for (int i = 0; i < 10; i++) {
            if (listener.navigateComplete2Called) {
                break;
            }
            Thread.sleep(1000);
        }
        OleAuto.INSTANCE.VariantClear(arguments[0]);

        // At this point the call to Navigate before should be complete
        Assert.assertTrue(listener.navigateComplete2Called);
        // Navidate complete should have brought us to github
        Assert.assertEquals(navigateURL, listener.navigateComplete2String);

        listener.navigateComplete2Called = false;
        listener.navigateComplete2String = null;
        listener.blockNavigation = true;

        arguments = new VARIANT[]{new VARIANT(blockedURL)};
        pDispParams = new DISPPARAMS.ByReference();
        pDispParams.setArgs(arguments);
        hr = ieDispatch.Invoke(dispIdNavigate.getValue(), niid, lcid, methodFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr, pExcepInfo, puArgErr);

        // wait 10 seconds to ensure navigation won't happen
        for (int i = 0; i < 10; i++) {
            if (listener.navigateComplete2Called) {
                break;
            }
            Thread.sleep(1000);
        }
        OleAuto.INSTANCE.VariantClear(arguments[0]);

        // Naviation will be blocked - so NavigateComplete can't be called
        Assert.assertFalse("NavigateComplete Handler was called although it should be blocked", listener.navigateComplete2Called);
    }

}
