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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
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
        assert COMUtils.comIsInitialized() : "COM not initialized";
        
        init(useActiveInstance, clsid, dwClsContext);
    }

    public COMBindingBaseObject(String progId, boolean useActiveInstance,
            int dwClsContext) throws COMException {
        assert COMUtils.comIsInitialized() : "COM not initialized";

        CLSID.ByReference clsid = new CLSID.ByReference();
        HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);

        COMUtils.checkRC(hr);
        
        init(useActiveInstance, clsid, dwClsContext);
    }

    public COMBindingBaseObject(String progId, boolean useActiveInstance)
            throws COMException {
        this(progId, useActiveInstance, WTypes.CLSCTX_SERVER);
    }

    private void init(boolean useActiveInstance, GUID clsid, int dwClsContext) throws COMException {
        HRESULT hr;
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
        
        COMUtils.checkRC(hr);
        
        this.iDispatch = new Dispatch(this.pDispatch.getValue());
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
        if (this.iDispatch != null) {
            this.iDispatch.Release();
        }
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
            dp.setRgdispidNamedArgs(new DISPID[] {OaIdl.DISPID_PROPERTYPUT});
        }

        // Build DISPPARAMS
        if (_argsLen > 0) {
            dp.setArgs(_args);

            // write 'DISPPARAMS' structure to memory
            dp.write();
        }

        // Apply "fix" according to
        // https://www.delphitools.info/2013/04/30/gaining-visual-basic-ole-super-powers/
        // https://msdn.microsoft.com/en-us/library/windows/desktop/ms221486(v=vs.85).aspx
        //
        // Summary: there are methods in the word typelibrary that require both
        // PROPERTYGET _and_ METHOD to be set. With only one of these set the call
        // fails.
        //
        // The article from delphitools argues, that automation compatible libraries
        // need to be compatible with VisualBasic which does not distingish methods
        // and property getters and will set both flags always.
        //
        // The MSDN article advises this behaviour: "[...] Some languages cannot 
        // distinguish between retrieving a property and calling a method. In this 
        //case, you should set the flags DISPATCH_PROPERTYGET and DISPATCH_METHOD.
        // [...]"))
        //
        // This was found when trying to bind InchesToPoints from the _Application 
        // dispatch interface of the MS Word 15 type library
        //
        // The signature according the ITypeLib Viewer (OLE/COM Object Viewer):
        // [id(0x00000172), helpcontext(0x09700172)]
        // single InchesToPoints([in] single Inches);

        final int finalNType;
        if (nType == OleAuto.DISPATCH_METHOD || nType == OleAuto.DISPATCH_PROPERTYGET) {
            finalNType = OleAuto.DISPATCH_METHOD | OleAuto.DISPATCH_PROPERTYGET;
        } else {
            finalNType = nType;
        }

        // Make the call!
        HRESULT hr = pDisp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT,
                new WinDef.WORD(finalNType), dp, pvResult, pExcepInfo, puArgErr);

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
        COMUtils.checkRC(hr);
    }
}
