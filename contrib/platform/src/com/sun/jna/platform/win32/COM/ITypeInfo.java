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
import com.sun.jna.platform.win32.OaIdl.HREFTYPEbyReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDbyReference;
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

    @VTABLE_ID(3)
    public HRESULT GetTypeAttr(
    /* [out] */PointerByReference ppTypeAttr);

    @VTABLE_ID(4)
    public HRESULT GetTypeComp(
    /* [out] */PointerByReference ppTComp);

    @VTABLE_ID(5)
    public/* [local] */HRESULT GetFuncDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppFuncDesc);

    @VTABLE_ID(6)
    public/* [local] */HRESULT GetVarDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppVarDesc);

    @VTABLE_ID(7)
    public/* [local] */HRESULT GetNames(
    /* [in] */MEMBERID memid,
    /* [length_is][size_is][out] */BSTR[] rgBstrNames,
    /* [in] */UINT cMaxNames,
    /* [out] */UINTbyReference pcNames);

    @VTABLE_ID(8)
    public HRESULT GetRefTypeOfImplType(
    /* [in] */UINT index,
    /* [out] */HREFTYPEbyReference pRefType);

    @VTABLE_ID(9)
    public HRESULT GetImplTypeFlags(
    /* [in] */UINT index,
    /* [out] */IntByReference pImplTypeFlags);

    @VTABLE_ID(10)
    public/* [local] */HRESULT GetIDsOfNames(
    /* [size_is][in] */LPOLESTR[] rgszNames,
    /* [in] */UINT cNames,
    /* [size_is][out] */MEMBERID[] pMemId);

    @VTABLE_ID(11)
    public/* [local] */HRESULT Invoke(
    /* [in] */PVOID pvInstance,
    /* [in] */MEMBERID memid,
    /* [in] */WORD wFlags,
    /* [out][in] */DISPPARAMS.ByReference pDispParams,
    /* [out] */VARIANT.ByReference pVarResult,
    /* [out] */EXCEPINFO.ByReference pExcepInfo,
    /* [out] */UINTbyReference puArgErr);

    @VTABLE_ID(12)
    public/* [local] */HRESULT GetDocumentation(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDbyReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile);

    @VTABLE_ID(13)
    public/* [local] */HRESULT GetDllEntry(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */BSTRByReference pBstrDllName,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */WORDbyReference pwOrdinal);

    @VTABLE_ID(14)
    public HRESULT GetRefTypeInfo(
    /* [in] */HREFTYPE hRefType,
    /* [out] */PointerByReference ppTInfo);

    @VTABLE_ID(15)
    public/* [local] */HRESULT AddressOfMember(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */PointerByReference ppv);

    @VTABLE_ID(16)
    public/* [local] */HRESULT CreateInstance(
    /* [in] */IUnknown pUnkOuter,
    /* [in] */REFIID riid,
    /* [iid_is][out] */PointerByReference ppvObj);

    @VTABLE_ID(17)
    public HRESULT GetMops(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrMops);

    @VTABLE_ID(18)
    public/* [local] */HRESULT GetContainingTypeLib(
    /* [out] */PointerByReference ppTLib,
    /* [out] */UINTbyReference pIndex);

    @VTABLE_ID(19)
    public/* [local] */void ReleaseTypeAttr(
    /* [in] */TYPEATTR pTypeAttr);

    @VTABLE_ID(20)
    public/* [local] */void ReleaseFuncDesc(
    /* [in] */FUNCDESC pFuncDesc);

    @VTABLE_ID(21)
    public/* [local] */void ReleaseVarDesc(
    /* [in] */VARDESC pVarDesc);
}
