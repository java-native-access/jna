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

import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class IUnknownTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private Unknown createIUnknown() {
        try {
            PointerByReference pUnknown = new PointerByReference();

            // Get CLSID for Word.Application...
            CLSID.ByReference clsid = new CLSID.ByReference();
            HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID("Shell.Application", clsid);

            if (W32Errors.FAILED(hr)) {
                Ole32.INSTANCE.CoUninitialize();
                COMUtils.checkRC(hr);
            }

            hr = Ole32.INSTANCE.CoCreateInstance(clsid, null, WTypes.CLSCTX_SERVER,
                    IUnknown.IID_IUNKNOWN, pUnknown);

            if (W32Errors.FAILED(hr)) {
                COMUtils.checkRC(hr);
            }

            return new Unknown(pUnknown.getValue());
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
    
    public void testQueryInterface() {
        Unknown iUnknown = this.createIUnknown();
        PointerByReference ppvObject = new PointerByReference();
        iUnknown.QueryInterface(new REFIID(IUnknown.IID_IUNKNOWN), ppvObject);

        assertTrue("ppvObject:" + ppvObject.toString(), ppvObject != null);
    }

    public void testAddRef() {
        Unknown iUnknown = this.createIUnknown();
        int addRef = iUnknown.AddRef();
        assertEquals(2, addRef);
    }

    public void testRelease() {
        Unknown iUnknown = this.createIUnknown();
        int release = iUnknown.Release();
        
        assertEquals(0, release);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Ole32.INSTANCE.CoUninitialize();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IUnknownTest.class);
    }
}
