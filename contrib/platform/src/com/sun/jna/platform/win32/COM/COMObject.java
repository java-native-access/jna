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
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VariantArg;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Helper class to provide basic COM support.
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMObject {

    public final static LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE
        .GetUserDefaultLCID();
    public final static LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE
        .GetSystemDefaultLCID();

    protected IUnknown iUnknown;

    protected IDispatch iDispatch;

    private PointerByReference pDispatch = new PointerByReference();

    private PointerByReference pUnknown = new PointerByReference();

    public COMObject(IDispatch iDispatch) {
        this.iDispatch = iDispatch;
    }

    /**
     * Instantiates a new cOM object.
     *
     * @param progId
     *            the prog id
     * @param useActiveInstance
     *            the use active instance
     * @throws COMException
     *             the automation exception
     */
    public COMObject(String progId, boolean useActiveInstance)
        throws COMException {

        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED);

        if (COMUtils.FAILED(hr)) {
            this.release();
            throw new COMException("CoInitializeEx() failed: " + Kernel32Util.formatMessage(hr));
        }

        CLSID clsid = new CLSID();
        hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);

        if (COMUtils.FAILED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CLSIDFromProgID() failed: " + Kernel32Util.formatMessage(hr));
        }

        if (useActiveInstance) {
            hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, this.pUnknown);

            if (COMUtils.SUCCEEDED(hr)) {
                this.iUnknown = new IUnknown(this.pUnknown.getValue());
                hr = iUnknown.QueryInterface(IDispatch.IID_IDispatch,
                                             this.pDispatch);
            } else {
                hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
                                                     WTypes.CLSCTX_SERVER, IDispatch.IID_IDispatch,
                                                     this.pDispatch);
            }
        } else {
            hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
                                                 WTypes.CLSCTX_SERVER, IDispatch.IID_IDispatch,
                                                 this.pDispatch);
        }

        if (COMUtils.FAILED(hr)) {
            throw new COMException("COM object with ProgID '" + progId
                                   + "' and CLSID " + clsid.toGuidString() + " not registered properly!");
        }

        this.iDispatch = new IDispatch(this.pDispatch.getValue());
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
                                IDispatch pDisp, String name, VARIANT[] pArgs) throws COMException {

        if (pDisp == null)
            throw new COMException("pDisp (IDispatch) parameter is null!");

        WString[] ptName = new WString[] { new WString(name) };
        DISPPARAMS dp = new DISPPARAMS();
        DISPIDByReference pdispID = new DISPIDByReference();
        VariantArg.ByReference variantArg = new VariantArg.ByReference();
        variantArg.variantArg = pArgs;
        EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
        IntByReference puArgErr = new IntByReference();

        // Get DISPID for name passed...
        HRESULT hr = pDisp.GetIDsOfNames(Guid.IID_NULL, ptName, 1,
                                         LOCALE_USER_DEFAULT, pdispID);

        COMUtils.checkAutoRC(hr);

        // Handle special-case for property-puts!
        if (nType == OleAuto.DISPATCH_PROPERTYPUT) {
            dp.cNamedArgs = new UINT(pArgs.length);
            dp.rgdispidNamedArgs = new DISPIDByReference(
                                                         OaIdl.DISPID_PROPERTYPUT);
        }

        // Build DISPPARAMS
        if ((pArgs != null) && (pArgs.length > 0)) {
            dp.cArgs = new UINT(pArgs.length);
            dp.rgvarg = variantArg;

            // write 'DISPPARAMS' structure to memory
            dp.write();
        }

        // Make the call!
        hr = pDisp.Invoke(pdispID.getValue(), Guid.IID_NULL,
                          LOCALE_SYSTEM_DEFAULT, new DISPID(nType), dp, pvResult,
                          pExcepInfo, puArgErr);

        COMUtils.checkAutoRC(hr, pExcepInfo, puArgErr);
        return hr;
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
                                IDispatch pDisp, String name, VARIANT pArg) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, name,
                              new VARIANT[] { pArg });
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
                                IDispatch pDisp, String name) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, name, (VARIANT[]) null);
    }

    protected void checkFailed(HRESULT hr) {
        COMUtils.checkAutoRC(hr, null, null);
    }

    public IDispatch getIDispatch() {
        return iDispatch;
    }

    public PointerByReference getIDispatchPointer() {
        return pDispatch;
    }

    public IUnknown getIUnknown() {
        return iUnknown;
    }

    public PointerByReference getIUnknownPointer() {
        return pUnknown;
    }

    public void release() {
        if (this.iDispatch != null)
            this.iDispatch.Release();

        Ole32.INSTANCE.CoUninitialize();
    }
}
