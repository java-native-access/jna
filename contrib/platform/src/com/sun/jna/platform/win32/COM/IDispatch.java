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
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDbyReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
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
public interface IDispatch extends IUnknown {

    public final static IID IID_IDispatch = new IID(
            "00020400-0000-0000-C000-000000000046");

    public HRESULT GetTypeInfoCount(UINTbyReference pctinfo);

    public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid,
            PointerByReference ppTInfo);

    public HRESULT GetIDsOfNames(IID riid, WString[] rgszNames, int cNames,
            LCID lcid, DISPIDbyReference rgDispId);

    public HRESULT Invoke(DISPID dispIdMember, IID riid, LCID lcid,
            DISPID wFlags, DISPPARAMS pDispParams,
            VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
            IntByReference puArgErr);
}
