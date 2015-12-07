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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Kernel32;
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
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Helper class to provide basic COM support.
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMBindingBaseObject extends COMInvoker {

    /** The Constant LOCALE_USER_DEFAULT. */
    public final static LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE
            .GetUserDefaultLCID();

    /** The Constant LOCALE_SYSTEM_DEFAULT. */
    public final static LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE
            .GetSystemDefaultLCID();

    /** The i unknown. */
    private IUnknown iUnknown;

    /** The i dispatch. */
    private IDispatch iDispatch;

    /** IDispatch interface reference. */
    private PointerByReference pDispatch = new PointerByReference();

    /** IUnknown interface reference. */
    private PointerByReference pUnknown = new PointerByReference();

    public COMBindingBaseObject(IDispatch dispatch) {
        // transfer the value
        this.iDispatch = dispatch;
    }

    public COMBindingBaseObject(CLSID clsid, boolean useActiveInstance) {
        this(clsid, useActiveInstance, WTypes.CLSCTX_SERVER);
    }

    public COMBindingBaseObject(CLSID clsid, boolean useActiveInstance,
            int dwClsContext) {
        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);

        if (COMUtils.FAILED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CoInitialize() failed!");
        }

        if (useActiveInstance) {
            hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, this.pUnknown);

            if (COMUtils.SUCCEEDED(hr)) {
                this.iUnknown = new Unknown(this.pUnknown.getValue());
                hr = iUnknown.QueryInterface(new REFIID( IDispatch.IID_IDISPATCH),
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

        this.iDispatch = new Dispatch(this.pDispatch.getValue());
    }

    public COMBindingBaseObject(String progId, boolean useActiveInstance,
            int dwClsContext) throws COMException {
        // Initialize COM for this thread...
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);

        if (COMUtils.FAILED(hr)) {
            this.release();
            throw new COMException("CoInitialize() failed!");
        }

        // Get CLSID for Word.Application...
        CLSID.ByReference clsid = new CLSID.ByReference();
        hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);

        if (COMUtils.FAILED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("CLSIDFromProgID() failed!");
        }

        if (useActiveInstance) {
            hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, this.pUnknown);

            if (COMUtils.SUCCEEDED(hr)) {
                this.iUnknown = new Unknown(this.pUnknown.getValue());
                hr = iUnknown.QueryInterface(new REFIID(IDispatch.IID_IDISPATCH),
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
            throw new COMException("COM object with ProgID '" + progId
                    + "' and CLSID " + clsid.toGuidString()
                    + " not registered properly!");
        }

        this.iDispatch = new Dispatch(this.pDispatch.getValue());
    }

    public COMBindingBaseObject(String progId, boolean useActiveInstance)
            throws COMException {
        this(progId, useActiveInstance, WTypes.CLSCTX_SERVER);
    }

    /**
     * Gets the i dispatch.
     *
     * @return the i dispatch
     */
    public IDispatch getIDispatch() {
        return iDispatch;
    }

    /**
     * Gets the i dispatch pointer.
     *
     * @return the i dispatch pointer
     */
    public PointerByReference getIDispatchPointer() {
        return pDispatch;
    }

    /**
     * Gets the i unknown.
     *
     * @return the i unknown
     */
    public IUnknown getIUnknown() {
        return iUnknown;
    }

    /**
     * Gets the i unknown pointer.
     *
     * @return the i unknown pointer
     */
    public PointerByReference getIUnknownPointer() {
        return pUnknown;
    }

    /**
     * Release.
     */
    public void release() {
        if (this.iDispatch != null)
            this.iDispatch.Release();

        Ole32.INSTANCE.CoUninitialize();
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, String name, VARIANT[] pArgs) throws COMException {

        if (pDisp == null)
            throw new COMException("pDisp (IDispatch) parameter is null!");

        // variable declaration
        WString[] ptName = new WString[] { new WString(name) };
        DISPIDByReference pdispID = new DISPIDByReference();

        // Get DISPID for name passed...
        HRESULT hr = pDisp.GetIDsOfNames(new REFIID(Guid.IID_NULL), ptName, 1,
                LOCALE_USER_DEFAULT, pdispID);

        COMUtils.checkRC(hr);

        return this
                .oleMethod(nType, pvResult, pDisp, pdispID.getValue(), pArgs);
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, DISPID dispId, VARIANT[] pArgs)
            throws COMException {

        if (pDisp == null)
            throw new COMException("pDisp (IDispatch) parameter is null!");

        // variable declaration
        int _argsLen = 0;
        VARIANT[] _args = null;
        DISPPARAMS.ByReference dp = new DISPPARAMS.ByReference();
        EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
        IntByReference puArgErr = new IntByReference();

        // make parameter reverse ordering as expected by COM runtime
        if ((pArgs != null) && (pArgs.length > 0)) {
            _argsLen = pArgs.length;
            _args = new VARIANT[_argsLen];

            int revCount = _argsLen;
            for (int i = 0; i < _argsLen; i++) {
                _args[i] = pArgs[--revCount];
            }
        }

        // Handle special-case for property-puts!
        if (nType == OleAuto.DISPATCH_PROPERTYPUT) {
            dp.cNamedArgs = new UINT(_argsLen);
            dp.rgdispidNamedArgs = new DISPIDByReference(
                    OaIdl.DISPID_PROPERTYPUT);
        }

        // Build DISPPARAMS
        if (_argsLen > 0) {
            dp.cArgs = new UINT(_args.length);
            // make pointer of variant array
            dp.rgvarg = new VariantArg.ByReference(_args);

            // write 'DISPPARAMS' structure to memory
            dp.write();
        }

        // Make the call!
        HRESULT hr = pDisp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT,
                new WinDef.WORD(nType), dp, pvResult, pExcepInfo, puArgErr);

        COMUtils.checkRC(hr, pExcepInfo, puArgErr);
        return hr;
    }

    /**
     * Ole method.
     *
     * @param nType
     *            the n type
     * @param pvResult
     *            the pv result
     * @param pDisp
     *            the disp
     * @param name
     *            the name
     * @param pArg
     *            the arg
     * @return the hresult
     * @throws COMException
     *             the cOM exception
     */
    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, String name, VARIANT pArg) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, name,
                new VARIANT[] { pArg });
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, DISPID dispId, VARIANT pArg) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, dispId,
                new VARIANT[] { pArg });
    }

    /**
     * Ole method.
     *
     * @param nType
     *            the n type
     * @param pvResult
     *            the pv result
     * @param pDisp
     *            the disp
     * @param name
     *            the name
     * @return the hresult
     * @throws COMException
     *             the cOM exception
     */
    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, String name) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, name, (VARIANT[]) null);
    }

    protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
            IDispatch pDisp, DISPID dispId) throws COMException {

        return this.oleMethod(nType, pvResult, pDisp, dispId, (VARIANT[]) null);
    }

    /**
     * Check failed.
     *
     * @param hr
     *            the hr
     */
    protected void checkFailed(HRESULT hr) {
        COMUtils.checkRC(hr, null, null);
    }
}
