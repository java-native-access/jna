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

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.MEMBERIDByReference;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
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
	/* [out] */TYPEATTR.ByReference pTypeAttr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(12));
		int hr = func.invokeInt(new Object[] { this.getPointer(), pTypeAttr });
		pTypeAttr.read();

		return new HRESULT(hr);
	}

	public HRESULT GetTypeComp(
	/* [out] */ITypeComp.ByReference pTComp) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(16));
		PointerByReference ppTComp = new PointerByReference();
		int hr = func.invokeInt(new Object[] { this.getPointer(), ppTComp });
		pTComp.setPointer(ppTComp.getValue());

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetFuncDesc(
	/* [in] */UINT index,
	/* [out] */FUNCDESC.ByReference pFuncDesc) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(20));
		int hr = func.invokeInt(new Object[] { this.getPointer(), index,
				pFuncDesc });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetVarDesc(
	/* [in] */UINT index,
	/* [out] */VARDESC.ByReference pVarDesc) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(24));
		int hr = func.invokeInt(new Object[] { this.getPointer(), index,
				pVarDesc });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetNames(
	/* [in] */MEMBERID memid,
	/* [length_is][size_is][out] */BSTR[] rgBstrNames,
	/* [in] */UINT cMaxNames,
	/* [out] */UINTByReference pcNames) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(28));
		int hr = func.invokeInt(new Object[] { this.getPointer(), memid,
				rgBstrNames, cMaxNames, pcNames });

		return new HRESULT(hr);
	}

	public HRESULT GetRefTypeOfImplType(
	/* [in] */UINT index,
	/* [out] */HREFTYPEByReference pRefType) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(32));
		int hr = func.invokeInt(new Object[] { this.getPointer(), index,
				pRefType });

		return new HRESULT(hr);
	}

	public HRESULT GetImplTypeFlags(
	/* [in] */UINT index,
	/* [out] */IntByReference pImplTypeFlags) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(36));
		int hr = func.invokeInt(new Object[] { this.getPointer(), index,
				pImplTypeFlags });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetIDsOfNames(
	/* [size_is][in] */WString[] rgszNames,
	/* [in] */UINT cNames,
	/* [size_is][out] */MEMBERID[] pMemId) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(40));
		int hr = func.invokeInt(new Object[] { this.getPointer(), rgszNames,
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
	/* [out] */UINTByReference puArgErr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(44));
		int hr = func.invokeInt(new Object[] { this.getPointer(), pvInstance,
				memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetDocumentation(
	/* [in] */MEMBERID memid,
	/* [out] */BSTR pBstrName,
	/* [out] */BSTR pBstrDocString,
	/* [out] */DWORDByReference pdwHelpContext,
	/* [out] */BSTR pBstrHelpFile) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(48));
		int hr = func.invokeInt(new Object[] { this.getPointer(), memid,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetDllEntry(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */BSTR pBstrDllName,
	/* [out] */BSTR pBstrName,
	/* [out] */WORDByReference pwOrdinal) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(52));
		int hr = func.invokeInt(new Object[] { this.getPointer(), memid,
				invKind, pBstrDllName, pBstrName, pwOrdinal });

		return new HRESULT(hr);
	}

	public HRESULT GetRefTypeInfo(
	/* [in] */HREFTYPE hRefType,
	/* [out] */ITypeInfo.ByReference ppTInfo) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(56));
		int hr = func.invokeInt(new Object[] { this.getPointer(), hRefType,
				ppTInfo });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT AddressOfMember(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */PointerByReference ppv) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(60));
		int hr = func.invokeInt(new Object[] { this.getPointer(), memid,
				invKind, ppv });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT CreateInstance(
	/* [in] */IUnknown pUnkOuter,
	/* [in] */REFIID riid,
	/* [iid_is][out] */PointerByReference ppvObj) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(64));
		int hr = func.invokeInt(new Object[] { this.getPointer(), pUnkOuter,
				riid, ppvObj });

		return new HRESULT(hr);
	}

	public HRESULT GetMops(
	/* [in] */MEMBERID memid,
	/* [out] */BSTR pBstrMops) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(68));
		int hr = func.invokeInt(new Object[] { this.getPointer(), memid,
				pBstrMops });

		return new HRESULT(hr);
	}

	public/* [local] */HRESULT GetContainingTypeLib(
	/* [out] */ITypeLib.ByReference pTLib,
	/* [out] */UINTByReference pIndex) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(72));
		PointerByReference ppTLib = new PointerByReference();
		int hr = func.invokeInt(new Object[] { this.getPointer(), ppTLib,
				pIndex });
		pTLib.setPointer(ppTLib.getPointer());

		return new HRESULT(hr);
	}

	public/* [local] */void ReleaseTypeAttr(
	/* [in] */TYPEATTR pTypeAttr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(76));
		func.invokeInt(new Object[] { this.getPointer(), pTypeAttr });
	}

	public/* [local] */void ReleaseFuncDesc(
	/* [in] */FUNCDESC pFuncDesc) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(80));
		func.invokeInt(new Object[] { this.getPointer(), pFuncDesc });
	}

	public/* [local] */void ReleaseVarDesc(
	/* [in] */VARDESC pVarDesc) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(84));
		func.invokeInt(new Object[] { this.getPointer(), pVarDesc });
	}
}
