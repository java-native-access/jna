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
import com.sun.jna.Structure;
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

/**
 * Wrapper class for the ITypeInfo interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeInfo extends IUnknown {

	public static class ByReference extends ITypeInfo implements
			Structure.ByReference {
	}

	public ITypeInfo() {
	}

	public ITypeInfo(Pointer pvInstance) {
		super(pvInstance);
	}

	public HRESULT GetTypeAttr(
	/* [out] */PointerByReference ppTypeAttr) {

		int hr = this._invoke(3, new Object[] { this.getPointer(), ppTypeAttr });
		return new HRESULT(hr);
	}

	public HRESULT GetTypeComp(
	/* [out] */PointerByReference ppTComp) {

		int hr = this._invoke(4, new Object[] { this.getPointer(), ppTComp });
		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetFuncDesc(
	/* [in] */UINT index,
	/* [out] */PointerByReference ppFuncDesc) {

		int hr = this._invoke(5, new Object[] { this.getPointer(), index,
				ppFuncDesc });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetVarDesc(
	/* [in] */UINT index,
	/* [out] */PointerByReference ppVarDesc) {

		int hr = this._invoke(6, new Object[] { this.getPointer(), index,
				ppVarDesc });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetNames(
	/* [in] */MEMBERID memid,
	/* [length_is][size_is][out] */BSTR[] rgBstrNames,
	/* [in] */UINT cMaxNames,
	/* [out] */UINTbyReference pcNames) {

		int hr = this._invoke(7, new Object[] { this.getPointer(), memid,
				rgBstrNames, cMaxNames, pcNames });

		return new HRESULT(hr);
	}

	public HRESULT GetRefTypeOfImplType(
	/* [in] */UINT index,
	/* [out] */HREFTYPEbyReference pRefType) {

		int hr = this._invoke(8, new Object[] { this.getPointer(), index,
				pRefType });

		return new HRESULT(hr);
	}

	public HRESULT GetImplTypeFlags(
	/* [in] */UINT index,
	/* [out] */IntByReference pImplTypeFlags) {

		int hr = this._invoke(9, new Object[] { this.getPointer(), index,
				pImplTypeFlags });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetIDsOfNames(
	/* [size_is][in] */LPOLESTR[] rgszNames,
	/* [in] */UINT cNames,
	/* [size_is][out] */MEMBERID[] pMemId) {

		int hr = this._invoke(10, new Object[] { this.getPointer(), rgszNames,
				cNames, pMemId });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT Invoke(
	/* [in] */PVOID pvInstance,
	/* [in] */MEMBERID memid,
	/* [in] */WORD wFlags,
	/* [out][in] */DISPPARAMS.ByReference pDispParams,
	/* [out] */VARIANT.ByReference pVarResult,
	/* [out] */EXCEPINFO.ByReference pExcepInfo,
	/* [out] */UINTbyReference puArgErr) {

		int hr = this._invoke(11, new Object[] { this.getPointer(), pvInstance,
				memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetDocumentation(
	/* [in] */MEMBERID memid,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */BSTRByReference pBstrDocString,
	/* [out] */DWORDbyReference pdwHelpContext,
	/* [out] */BSTRByReference pBstrHelpFile) {

		int hr = this._invoke(12, new Object[] { this.getPointer(), memid,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetDllEntry(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */BSTRByReference pBstrDllName,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */WORDbyReference pwOrdinal) {

		int hr = this._invoke(13, new Object[] { this.getPointer(), memid,
				invKind, pBstrDllName, pBstrName, pwOrdinal });

		return new HRESULT(hr);
	}

	public HRESULT GetRefTypeInfo(
	/* [in] */HREFTYPE hRefType,
	/* [out] */PointerByReference ppTInfo) {

		int hr = this._invoke(14, new Object[] { this.getPointer(), hRefType,
				ppTInfo });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT AddressOfMember(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */PointerByReference ppv) {

		int hr = this._invoke(15, new Object[] { this.getPointer(), memid,
				invKind, ppv });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT CreateInstance(
	/* [in] */IUnknown pUnkOuter,
	/* [in] */REFIID riid,
	/* [iid_is][out] */PointerByReference ppvObj) {

		int hr = this._invoke(16, new Object[] { this.getPointer(), pUnkOuter,
				riid, ppvObj });

		return new HRESULT(hr);
	}

	public HRESULT GetMops(
	/* [in] */MEMBERID memid,
	/* [out] */BSTRByReference pBstrMops) {

		int hr = this._invoke(17, new Object[] { this.getPointer(), memid,
				pBstrMops });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetContainingTypeLib(
	/* [out] */PointerByReference ppTLib,
	/* [out] */UINTbyReference pIndex) {

		int hr = this._invoke(18, new Object[] { this.getPointer(), ppTLib,
				pIndex });

		return new HRESULT(hr);
	}

	public/* [local] */void ReleaseTypeAttr(
	/* [in] */TYPEATTR pTypeAttr) {

		this._invoke(19, new Object[] { this.getPointer(), pTypeAttr });
	}

	public/* [local] */void ReleaseFuncDesc(
	/* [in] */FUNCDESC pFuncDesc) {

		this._invoke(20, new Object[] { this.getPointer(), pFuncDesc });
	}

	public/* [local] */void ReleaseVarDesc(
	/* [in] */VARDESC pVarDesc) {

		this._invoke(21, new Object[] { this.getPointer(), pVarDesc });
	}
}
