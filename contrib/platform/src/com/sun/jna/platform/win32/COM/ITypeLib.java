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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeLib interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface ITypeLib extends IUnknown {

    public UINT GetTypeInfoCount();

    public HRESULT GetTypeInfo(
    /* [in] */UINT index,
    /* [out] */PointerByReference pTInfo);

    public HRESULT GetTypeInfoType(
    /* [in] */UINT index,
    /* [out] */TYPEKIND.ByReference pTKind);

    public HRESULT GetTypeInfoOfGuid(
    /* [in] */GUID guid,
    /* [out] */PointerByReference pTinfo);

    public HRESULT GetLibAttr(
    /* [out] */PointerByReference ppTLibAttr);

    public HRESULT GetTypeComp(
    /* [out] */PointerByReference ppTComp);

    public HRESULT GetDocumentation(
    /* [in] */int index,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDByReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile);

    public HRESULT IsName(
    /* [annotation][out][in] */
    LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [out] */BOOLByReference pfName);

    public HRESULT FindName(
    /* [annotation][out][in] */
    LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [length_is][size_is][out] */Pointer[] ppTInfo,
    /* [length_is][size_is][out] */MEMBERID[] rgMemId,
    /* [out][in] */USHORTByReference pcFound);

    public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr);
}
