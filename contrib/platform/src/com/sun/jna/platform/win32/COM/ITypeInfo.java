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

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeInfo interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeInfo extends IUnknown {

	/**
	 * The Class ByReference.
	 * 
	 * @author wolf.tobias@gmx.net The Class ByReference.
	 */
	public static class ByReference extends ITypeInfo implements
			Structure.ByReference {
	}

	/**
	 * Instantiates a new i type info.
	 */
	public ITypeInfo() {
	}

	/**
	 * Instantiates a new i type info.
	 * 
	 * @param pvInstance
	 *            the pv instance
	 */
	public ITypeInfo(Pointer pvInstance) {
		super(pvInstance);
	}

	/**
	 * Gets the type attr.
	 * 
	 * @param ppTypeAttr
	 *            the pp type attr
	 * @return the hresult
	 */
	public HRESULT GetTypeAttr(
	/* [out] */PointerByReference ppTypeAttr) {

		int hr = this
				._invokeInt(3, new Object[] { this.getPointer(), ppTypeAttr });
		return new HRESULT(hr);
	}

	/**
	 * Gets the type comp.
	 * 
	 * @param ppTComp
	 *            the pp t comp
	 * @return the hresult
	 */
	public HRESULT GetTypeComp(
	/* [out] */PointerByReference ppTComp) {

		int hr = this._invokeInt(4, new Object[] { this.getPointer(), ppTComp });
		return new HRESULT(hr);
	}

	/**
	 * Gets the func desc.
	 * 
	 * @param index
	 *            the index
	 * @param ppFuncDesc
	 *            the pp func desc
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetFuncDesc(
	/* [in] */UINT index,
	/* [out] */PointerByReference ppFuncDesc) {

		int hr = this._invokeInt(5, new Object[] { this.getPointer(), index,
				ppFuncDesc });

		return new HRESULT(hr);
	}

	/**
	 * Gets the var desc.
	 * 
	 * @param index
	 *            the index
	 * @param ppVarDesc
	 *            the pp var desc
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetVarDesc(
	/* [in] */UINT index,
	/* [out] */PointerByReference ppVarDesc) {

		int hr = this._invokeInt(6, new Object[] { this.getPointer(), index,
				ppVarDesc });

		return new HRESULT(hr);
	}

	/**
	 * Gets the names.
	 * 
	 * @param memid
	 *            the memid
	 * @param rgBstrNames
	 *            the rg bstr names
	 * @param cMaxNames
	 *            the c max names
	 * @param pcNames
	 *            the pc names
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetNames(
	/* [in] */MEMBERID memid,
	/* [length_is][size_is][out] */BSTR[] rgBstrNames,
	/* [in] */UINT cMaxNames,
	/* [out] */UINTbyReference pcNames) {

		int hr = this._invokeInt(7, new Object[] { this.getPointer(), memid,
				rgBstrNames, cMaxNames, pcNames });

		return new HRESULT(hr);
	}

	/**
	 * Gets the ref type of impl type.
	 * 
	 * @param index
	 *            the index
	 * @param pRefType
	 *            the ref type
	 * @return the hresult
	 */
	public HRESULT GetRefTypeOfImplType(
	/* [in] */UINT index,
	/* [out] */HREFTYPEbyReference pRefType) {

		int hr = this._invokeInt(8, new Object[] { this.getPointer(), index,
				pRefType });

		return new HRESULT(hr);
	}

	/**
	 * Gets the impl type flags.
	 * 
	 * @param index
	 *            the index
	 * @param pImplTypeFlags
	 *            the impl type flags
	 * @return the hresult
	 */
	public HRESULT GetImplTypeFlags(
	/* [in] */UINT index,
	/* [out] */IntByReference pImplTypeFlags) {

		int hr = this._invokeInt(9, new Object[] { this.getPointer(), index,
				pImplTypeFlags });

		return new HRESULT(hr);
	}

	/**
	 * Gets the i ds of names.
	 * 
	 * @param rgszNames
	 *            the rgsz names
	 * @param cNames
	 *            the c names
	 * @param pMemId
	 *            the mem id
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetIDsOfNames(
	/* [size_is][in] */LPOLESTR[] rgszNames,
	/* [in] */UINT cNames,
	/* [size_is][out] */MEMBERID[] pMemId) {

		int hr = this._invokeInt(10, new Object[] { this.getPointer(), rgszNames,
				cNames, pMemId });

		return new HRESULT(hr);
	}

	/**
	 * Invoke.
	 * 
	 * @param pvInstance
	 *            the pv instance
	 * @param memid
	 *            the memid
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
	public/* [local] */HRESULT Invoke(
	/* [in] */PVOID pvInstance,
	/* [in] */MEMBERID memid,
	/* [in] */WORD wFlags,
	/* [out][in] */DISPPARAMS.ByReference pDispParams,
	/* [out] */VARIANT.ByReference pVarResult,
	/* [out] */EXCEPINFO.ByReference pExcepInfo,
	/* [out] */UINTbyReference puArgErr) {

		int hr = this._invokeInt(11, new Object[] { this.getPointer(), pvInstance,
				memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr });

		return new HRESULT(hr);
	}

	/**
	 * Gets the documentation.
	 * 
	 * @param memid
	 *            the memid
	 * @param pBstrName
	 *            the bstr name
	 * @param pBstrDocString
	 *            the bstr doc string
	 * @param pdwHelpContext
	 *            the pdw help context
	 * @param pBstrHelpFile
	 *            the bstr help file
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetDocumentation(
	/* [in] */MEMBERID memid,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */BSTRByReference pBstrDocString,
	/* [out] */DWORDbyReference pdwHelpContext,
	/* [out] */BSTRByReference pBstrHelpFile) {

		int hr = this._invokeInt(12, new Object[] { this.getPointer(), memid,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	/**
	 * Gets the dll entry.
	 * 
	 * @param memid
	 *            the memid
	 * @param invKind
	 *            the inv kind
	 * @param pBstrDllName
	 *            the bstr dll name
	 * @param pBstrName
	 *            the bstr name
	 * @param pwOrdinal
	 *            the pw ordinal
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetDllEntry(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */BSTRByReference pBstrDllName,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */WORDbyReference pwOrdinal) {

		int hr = this._invokeInt(13, new Object[] { this.getPointer(), memid,
				invKind, pBstrDllName, pBstrName, pwOrdinal });

		return new HRESULT(hr);
	}

	/**
	 * Gets the ref type info.
	 * 
	 * @param hRefType
	 *            the h ref type
	 * @param ppTInfo
	 *            the pp t info
	 * @return the hresult
	 */
	public HRESULT GetRefTypeInfo(
	/* [in] */HREFTYPE hRefType,
	/* [out] */PointerByReference ppTInfo) {

		int hr = this._invokeInt(14, new Object[] { this.getPointer(), hRefType,
				ppTInfo });

		return new HRESULT(hr);
	}

	/**
	 * Address of member.
	 * 
	 * @param memid
	 *            the memid
	 * @param invKind
	 *            the inv kind
	 * @param ppv
	 *            the ppv
	 * @return the hresult
	 */
	public/* [local] */HRESULT AddressOfMember(
	/* [in] */MEMBERID memid,
	/* [in] */INVOKEKIND invKind,
	/* [out] */PointerByReference ppv) {

		int hr = this._invokeInt(15, new Object[] { this.getPointer(), memid,
				invKind, ppv });

		return new HRESULT(hr);
	}

	/**
	 * Creates the instance.
	 * 
	 * @param pUnkOuter
	 *            the unk outer
	 * @param riid
	 *            the riid
	 * @param ppvObj
	 *            the ppv obj
	 * @return the hresult
	 */
	public/* [local] */HRESULT CreateInstance(
	/* [in] */IUnknown pUnkOuter,
	/* [in] */REFIID riid,
	/* [iid_is][out] */PointerByReference ppvObj) {

		int hr = this._invokeInt(16, new Object[] { this.getPointer(), pUnkOuter,
				riid, ppvObj });

		return new HRESULT(hr);
	}

	/**
	 * Gets the mops.
	 * 
	 * @param memid
	 *            the memid
	 * @param pBstrMops
	 *            the bstr mops
	 * @return the hresult
	 */
	public HRESULT GetMops(
	/* [in] */MEMBERID memid,
	/* [out] */BSTRByReference pBstrMops) {

		int hr = this._invokeInt(17, new Object[] { this.getPointer(), memid,
				pBstrMops });

		return new HRESULT(hr);
	}

	/**
	 * Gets the containing type lib.
	 * 
	 * @param ppTLib
	 *            the pp t lib
	 * @param pIndex
	 *            the index
	 * @return the hresult
	 */
	public/* [local] */HRESULT GetContainingTypeLib(
	/* [out] */PointerByReference ppTLib,
	/* [out] */UINTbyReference pIndex) {

		int hr = this._invokeInt(18, new Object[] { this.getPointer(), ppTLib,
				pIndex });

		return new HRESULT(hr);
	}

	/**
	 * Release type attr.
	 * 
	 * @param pTypeAttr
	 *            the type attr
	 */
	public/* [local] */void ReleaseTypeAttr(
	/* [in] */TYPEATTR pTypeAttr) {

		this._invokeInt(19, new Object[] { this.getPointer(), pTypeAttr });
	}

	/**
	 * Release func desc.
	 * 
	 * @param pFuncDesc
	 *            the func desc
	 */
	public/* [local] */void ReleaseFuncDesc(
	/* [in] */FUNCDESC pFuncDesc) {

		this._invokeInt(20, new Object[] { this.getPointer(), pFuncDesc });
	}

	/**
	 * Release var desc.
	 * 
	 * @param pVarDesc
	 *            the var desc
	 */
	public/* [local] */void ReleaseVarDesc(
	/* [in] */VARDESC pVarDesc) {

		this._invokeInt(21, new Object[] { this.getPointer(), pVarDesc });
	}
}
