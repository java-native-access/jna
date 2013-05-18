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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.ptr.PointerByReference;

public class IUnknownTest extends TestCase {

    private IDispatch iDispatch = new IDispatch();

    private PointerByReference pDispatch = new PointerByReference();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED);

        if (W32Errors.FAILED(hr)) {
            this.tearDown();
            throw new COMException("CoInitializeEx() failed: " + Kernel32Util.formatMessage(hr));
        }

        // Get CLSID for Word.Application...
        CLSID clsid = new CLSID();
        hr = Ole32.INSTANCE.CLSIDFromProgID("InternetExplorer.Application",
                                            clsid);

        if (W32Errors.FAILED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CLSIDFromProgID() failed: " + Kernel32Util.formatMessage(hr));
        }

        hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
                                             WTypes.CLSCTX_LOCAL_SERVER, IDispatch.IID_IDispatch,
                                             this.pDispatch);

        if (W32Errors.FAILED(hr)) {
            throw new COMException("Internet Explorer not registered properly");
        }

        this.iDispatch = new IDispatch(pDispatch.getPointer());
    }

    public void testQueryInterface() {
        PointerByReference ppvObject = new PointerByReference();
        this.iDispatch.QueryInterface(IDispatch.IID_IDispatch, ppvObject);
        assertNotNull(ppvObject.getValue());
        System.out.println("ppvObject:" + ppvObject.toString());
    }

    public void testAddRef() {
        int addRef = this.iDispatch.AddRef();
        assertEquals("Wrong addRef result", 1, addRef);
    }

    public void testRelease() {
        int release = this.iDispatch.Release();
        assertEquals("Wrong release result", 0, release);
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
