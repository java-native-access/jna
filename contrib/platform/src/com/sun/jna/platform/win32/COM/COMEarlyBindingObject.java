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
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Helper class to provide basic COM support.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMEarlyBindingObject extends COMBindingBaseObject implements
        IDispatch {

    public COMEarlyBindingObject(CLSID clsid, boolean useActiveInstance,
            int dwClsContext) {
        super(clsid, useActiveInstance, dwClsContext);
    }

    protected String getStringProperty(DISPID dispId) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), dispId);

        return result.getValue().toString();
    }

    protected void setProperty(DISPID dispId, boolean value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                dispId, new VARIANT(value));
    }

    @Override
    public HRESULT QueryInterface(REFIID riid, PointerByReference ppvObject) {
        return this.getIDispatch().QueryInterface(riid, ppvObject);
    }

    @Override
    public int AddRef() {
        return this.getIDispatch().AddRef();
    }

    @Override
    public int Release() {
        return this.getIDispatch().Release();
    }

    @Override
    public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
        return this.getIDispatch().GetTypeInfoCount(pctinfo);
    }

    @Override
    public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid,
            PointerByReference ppTInfo) {
        return this.getIDispatch().GetTypeInfo(iTInfo, lcid, ppTInfo);
    }

    @Override
    public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames,
            LCID lcid, DISPIDByReference rgDispId) {
        return this.getIDispatch().GetIDsOfNames(riid, rgszNames, cNames, lcid,
                rgDispId);
    }

    @Override
    public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid,
            WORD wFlags, DISPPARAMS.ByReference pDispParams,
            VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
            IntByReference puArgErr) {
        return this.getIDispatch().Invoke(dispIdMember, riid, lcid, wFlags,
                pDispParams, pVarResult, pExcepInfo, puArgErr);
    }
}
