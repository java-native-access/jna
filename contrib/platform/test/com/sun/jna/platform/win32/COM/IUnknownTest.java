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
