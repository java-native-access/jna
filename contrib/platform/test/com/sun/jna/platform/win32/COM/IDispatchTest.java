/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
