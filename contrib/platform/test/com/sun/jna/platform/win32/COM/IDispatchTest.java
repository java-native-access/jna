/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class IDispatchTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    /** The Constant LOCALE_SYSTEM_DEFAULT. */
    public final static LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE
            .GetSystemDefaultLCID();

    private Dispatch createIDispatch() {
        try {
            PointerByReference pDispatch = new PointerByReference();

            // Get CLSID for Shell.Application...
            CLSID.ByReference clsid = new CLSID.ByReference();
            HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID("Shell.Application",
                    clsid);

            if (W32Errors.FAILED(hr)) {
                Ole32.INSTANCE.CoUninitialize();
                COMUtils.checkRC(hr);
            }

            hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
                    WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, pDispatch);

            if (W32Errors.FAILED(hr)) {
                COMUtils.checkRC(hr);
            }

            return new Dispatch(pDispatch.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void setUp() throws Exception {
        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

        if (W32Errors.FAILED(hr)) {
            this.tearDown();
            throw new COMException("CoInitialize() failed");
        }
    }

    public void testGetTypeInfoCount() {
        Dispatch dispatch = this.createIDispatch();

        UINTByReference pctinfo = new UINTByReference();
        dispatch.GetTypeInfoCount(pctinfo);

        int intValue = pctinfo.getValue().intValue();
        assertEquals(1, intValue);
    }

    public void testGetTypeInfo() {
        Dispatch dispatch = this.createIDispatch();

        PointerByReference ppTInfo = new PointerByReference();
        HRESULT hr = dispatch.GetTypeInfo(new UINT(0), LOCALE_SYSTEM_DEFAULT, ppTInfo);

        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
    }

    public void testGetIDsOfNames() {
        Dispatch dispatch = this.createIDispatch();

        WString[] ptName = new WString[] { new WString("Application") };
        DISPIDByReference pdispID = new DISPIDByReference();

        HRESULT hr = dispatch.GetIDsOfNames(new REFIID(Guid.IID_NULL), ptName, 1, LOCALE_SYSTEM_DEFAULT, pdispID);
        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
    }

    public void testInvoke() {

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Ole32.INSTANCE.CoUninitialize();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IDispatchTest.class);
    }
}
