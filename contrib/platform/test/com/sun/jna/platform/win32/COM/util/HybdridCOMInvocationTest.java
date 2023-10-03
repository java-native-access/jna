/*
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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;

/**
 * In the word COM bindings it was determined, that some methods can't be called
 * with only wFlags OleAuto.DISPATCH_METHOD or OleAuto.DISPATCH_PROPERTYGET.
 *
 * For these methods both flags need to be set.
 *
 * https://www.delphitools.info/2013/04/30/gaining-visual-basic-ole-super-powers/
 * https://msdn.microsoft.com/en-us/library/windows/desktop/ms221486(v=vs.85).aspx
 *
 * A sample function is InchesToPoints from thw word typelibrary
 */
public class HybdridCOMInvocationTest {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private static final Logger LOG = Logger.getLogger(HybdridCOMInvocationTest.class.getName());

    private static final String CLSID_WORD_STRING = "{000209FF-0000-0000-C000-000000000046}";
    private static final String IID_APPLICATION_STRING = "{00020970-0000-0000-C000-000000000046}";
    private static final GUID CLSID_WORD = new GUID(CLSID_WORD_STRING);
    private static final IID IID_APPLICATION = new IID(new GUID(IID_APPLICATION_STRING));

    private boolean initialized = false;

    @After
    public void tearDown() throws Exception {
        if(initialized) {
            Ole32.INSTANCE.CoUninitialize();
            initialized = false;
        }
    }

    @Before
    public void setUp() throws Exception {
        // Initialize COM for this thread...
        // Check that FileSystemObject is registered in the registry
        Assume.assumeTrue("Could not find registration", checkCOMRegistered(CLSID_WORD_STRING));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
    }

    @Test
    public void testOfficeInvocationProblemCOMUtil() {
        ObjectFactory fact = new ObjectFactory();
        Application app;
        try {
            app = fact.createObject(Application.class);
        } catch (COMException ex)  {
            LOG.log(Level.INFO, "HybdridCOMInvocationTest test was not run, MS Word object could not be instantiated.", ex);
            return;
        }
        // If this fails: remember: floats are not exact, if this happens replace
        // with a range check
        assertThat(app.InchesToPoints(1F), is(72.0f));
        fact.disposeAll();
    }

    @Test
    public void testOfficeInvocationProblemCOMBindingObject() {
        WordApplication app;
        try {
            app = new WordApplication(false);
        } catch (COMException ex)  {
            LOG.log(Level.INFO, "HybdridCOMInvocationTest test was not run, MS Word object could not be instantiated.", ex);
            return;
        }
        assertThat(app.InchesToPoints(1F), is(72.0f));
    }


    public void testOfficeInvocationDemonstration() {
        // THIS IS NOT A TEST
        //
        // This reproduces the problem by using the dispatch directly.

        PointerByReference pDispatch = new PointerByReference();

        HRESULT hr = Ole32.INSTANCE.CoCreateInstance(CLSID_WORD, null,
                WTypes.CLSCTX_SERVER, IID_APPLICATION, pDispatch);

        if(! COMUtils.SUCCEEDED(hr)) {
            LOG.log(Level.INFO, "HybdridCOMInvocationTest test was not run, MS Word object could not be instantiated.");
            return;
        }

        Dispatch dp = new Dispatch(pDispatch.getValue());

        // DispID of InchesToPoints
        DISPID dispId = new OaIdl.DISPID(0x00000172);
        // Interface _Application of MS Word type library
        WinDef.LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE.GetSystemDefaultLCID();
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
        IntByReference puArgErr = new IntByReference();

        WORD wFlagsMethod = new WinDef.WORD(OleAuto.DISPATCH_METHOD);
        WORD wFlagsGet = new WinDef.WORD(OleAuto.DISPATCH_PROPERTYGET);
        WORD wFlagsCombined = new WinDef.WORD(OleAuto.DISPATCH_METHOD | OleAuto.DISPATCH_PROPERTYGET);

        OleAuto.DISPPARAMS.ByReference pDispParams = new OleAuto.DISPPARAMS.ByReference();
        VARIANT[] params = new VARIANT[] {new VARIANT(1f)};
        pDispParams.setArgs(params);

        // Call InchesToPoints as a method
        hr = dp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, wFlagsMethod, pDispParams, result, pExcepInfo, puArgErr);
        assertTrue(COMUtils.FAILED(hr));

        // Call InchesToPoints as a property getter
        hr = dp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, wFlagsGet, pDispParams, result, pExcepInfo, puArgErr);
        assertTrue(COMUtils.FAILED(hr));

        // Call InchesToPoints as a hybrid
        hr = dp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, wFlagsCombined, pDispParams, result, pExcepInfo, puArgErr);
        assertTrue(COMUtils.SUCCEEDED(hr));

        assertEquals(72.0f, result.floatValue(), 0.1d);
    }

    @ComObject(clsId = CLSID_WORD_STRING)
    public static interface Application extends IDispatch, _Application {
    }

    @ComInterface(iid = IID_APPLICATION_STRING)
    public static interface _Application {
        @ComMethod
        Float InchesToPoints(Float value);
    }

    public static class WordApplication extends COMLateBindingObject {

        public WordApplication(boolean useActiveInstance) {
            super(new CLSID(CLSID_WORD), useActiveInstance);
        }

        public Float InchesToPoints(Float value) {
            VARIANT.ByReference pvResult = new VARIANT.ByReference();
            this.oleMethod(OleAuto.DISPATCH_METHOD , pvResult, "InchesToPoints", new VARIANT[] {new VARIANT(value)});
            return pvResult.floatValue();
        }
    }
}
