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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
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
 * Wrapper class for the IDispatch interface
 * 
 * IDispatch.GetTypeInfoCount 12 IDispatch.GetTypeInfo 16
 * IDispatch.GetIDsOfNames 20 IDispatch.Invoke 24
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class Dispatch extends Unknown implements IDispatch {

    public static class ByReference extends Dispatch implements
            Structure.ByReference {
    }

    public Dispatch() {
    }

    public Dispatch(Pointer pvInstance) {
        super(pvInstance);
    }

    /**
     * Gets the type info count.
     * 
     * @param pctinfo
     *            the pctinfo
     * @return the hresult
     * 
     *         Virtual
     */
    public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
        return (HRESULT) this._invokeNativeObject(3,
                new Object[] { this.getPointer(), pctinfo }, HRESULT.class);
    }

    /**
     * Gets the type info.
     * 
     * @param iTInfo
     *            the i t info
     * @param lcid
     *            the lcid
     * @param ppTInfo
     *            the pp t info
     * @return the hresult
     */
    public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid,
            PointerByReference ppTInfo) {
        return (HRESULT) this._invokeNativeObject(4,
                new Object[] { this.getPointer(), iTInfo, lcid, ppTInfo },
                HRESULT.class);
    }

    /**
     * Gets the ids of names.
     * 
     * @param riid
     *            the riid
     * @param rgszNames
     *            the rgsz names
     * @param cNames
     *            the c names
     * @param lcid
     *            the lcid
     * @param rgDispId
     *            the rg disp id
     * @return the hresult
     */
    public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames,
            LCID lcid, DISPIDByReference rgDispId) {
        return (HRESULT) this._invokeNativeObject(5,
                new Object[] { this.getPointer(), riid, rgszNames, cNames,
                        lcid, rgDispId }, HRESULT.class);
    }

    /**
     * Invoke.
     * 
     * @param dispIdMember
     *            the disp id member
     * @param riid
     *            the riid
     * @param lcid
     *            the lcid
     * @param wFlags
     *            the w flags
     * @param pDispParams
     *            the disp params
     * @param pVarResult
     *            the var result
     * @param pExcepInfo
     *            the excep info
     * @param puArgErr
     *            the pu arg err
     * @return the hresult
     */
    public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid,
            WORD wFlags, DISPPARAMS.ByReference pDispParams,
            VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
            IntByReference puArgErr) {
        return (HRESULT) this
                ._invokeNativeObject(6, new Object[] { this.getPointer(),
                        dispIdMember, riid, lcid, wFlags, pDispParams,
                        pVarResult, pExcepInfo, puArgErr }, HRESULT.class);
    }
}
