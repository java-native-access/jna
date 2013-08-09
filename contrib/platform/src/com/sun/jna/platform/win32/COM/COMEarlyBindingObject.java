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


import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Helper class to provide basic COM support.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMEarlyBindingObject extends Dispatch {

    /** IDispatch interface reference. */
    private PointerByReference pDispatch = new PointerByReference();

    /** IUnknown interface reference. */
    private PointerByReference pUnknown = new PointerByReference();

    public COMEarlyBindingObject(CLSID clsid, boolean useActiveInstance,
            int dwClsContext) {
        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

        if (COMUtils.FAILED(hr)) {
            this.Release();
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CoInitialize() failed!");
        }

        if (COMUtils.FAILED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CLSIDFromProgID() failed!");
        }

        if (useActiveInstance) {
            hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, this.pUnknown);

            if (COMUtils.SUCCEEDED(hr)) {
                hr = this.QueryInterface(IDispatch.IID_IDISPATCH,
                        this.pDispatch);
            } else {
                hr = Ole32.INSTANCE.CoCreateInstance(clsid, null, dwClsContext,
                        IDispatch.IID_IDISPATCH, this.pDispatch);
            }
        } else {
            hr = Ole32.INSTANCE.CoCreateInstance(clsid, null, dwClsContext,
                    IDispatch.IID_IDISPATCH, this.pDispatch);
        }

        if (COMUtils.FAILED(hr)) {
            throw new COMException("COM object with CLSID "
                    + clsid.toGuidString() + " not registered properly!");
        }

        this.setPointer(this.pDispatch.getValue());
    }
}
