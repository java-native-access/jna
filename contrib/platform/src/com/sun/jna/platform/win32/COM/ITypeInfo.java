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

import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeInfo interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface ITypeInfo extends IUnknown {

    public HRESULT GetTypeAttr(
    /* [out] */PointerByReference ppTypeAttr);

    public HRESULT GetTypeComp(
    /* [out] */PointerByReference ppTComp);

    public/* [local] */HRESULT GetFuncDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppFuncDesc);

    public/* [local] */HRESULT GetVarDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppVarDesc);

    public/* [local] */HRESULT GetNames(
    /* [in] */MEMBERID memid,
    /* [length_is][size_is][out] */BSTR[] rgBstrNames,
    /* [in] */UINT cMaxNames,
    /* [out] */UINTByReference pcNames);

    public HRESULT GetRefTypeOfImplType(
    /* [in] */UINT index,
    /* [out] */HREFTYPEByReference pRefType);

    public HRESULT GetImplTypeFlags(
    /* [in] */UINT index,
    /* [out] */IntByReference pImplTypeFlags);

    public/* [local] */HRESULT GetIDsOfNames(
    /* [size_is][in] */LPOLESTR[] rgszNames,
    /* [in] */UINT cNames,
    /* [size_is][out] */MEMBERID[] pMemId);

    public/* [local] */HRESULT Invoke(
    /* [in] */PVOID pvInstance,
    /* [in] */MEMBERID memid,
    /* [in] */WORD wFlags,
    /* [out][in] */DISPPARAMS.ByReference pDispParams,
    /* [out] */VARIANT.ByReference pVarResult,
    /* [out] */EXCEPINFO.ByReference pExcepInfo,
    /* [out] */UINTByReference puArgErr);

    public/* [local] */HRESULT GetDocumentation(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDByReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile);

    public/* [local] */HRESULT GetDllEntry(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */BSTRByReference pBstrDllName,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */WORDByReference pwOrdinal);

    public HRESULT GetRefTypeInfo(
    /* [in] */HREFTYPE hRefType,
    /* [out] */PointerByReference ppTInfo);

    public/* [local] */HRESULT AddressOfMember(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */PointerByReference ppv);

    public/* [local] */HRESULT CreateInstance(
    /* [in] */IUnknown pUnkOuter,
    /* [in] */REFIID riid,
    /* [iid_is][out] */PointerByReference ppvObj);

    public HRESULT GetMops(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrMops);

    public/* [local] */HRESULT GetContainingTypeLib(
    /* [out] */PointerByReference ppTLib,
    /* [out] */UINTByReference pIndex);

    public/* [local] */void ReleaseTypeAttr(
    /* [in] */TYPEATTR pTypeAttr);

    public/* [local] */void ReleaseFuncDesc(
    /* [in] */FUNCDESC pFuncDesc);

    public/* [local] */void ReleaseVarDesc(
    /* [in] */VARDESC pVarDesc);
}
