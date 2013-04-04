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
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLbyReference;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeLib interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeLib extends IUnknown {

	/**
	 * The Class ByReference.
	 * 
	 * @author wolf.tobias@gmx.net The Class ByReference.
	 */
	public static class ByReference extends ITypeLib implements
			Structure.ByReference {
	}

	/**
	 * Instantiates a new i type lib.
	 */
	public ITypeLib() {
	}

	/**
	 * Instantiates a new i type lib.
	 * 
	 * @param pvInstance
	 *            the pv instance
	 */
	public ITypeLib(Pointer pvInstance) {
		super(pvInstance);
	}

	/**
	 * Gets the type info count.
	 * 
	 * @return the uint
	 */
	public UINT GetTypeInfoCount() {
		int count = this._invokeInt(3, new Object[] { this.getPointer() });
		return new UINT(count);
	}

	/**
	 * Gets the type info.
	 * 
	 * @param index
	 *            the index
	 * @param pTInfo
	 *            the t info
	 * @return the hresult
	 */
	public HRESULT GetTypeInfo(
	/* [in] */UINT index,
	/* [out] */ITypeInfo.ByReference pTInfo) {

		PointerByReference ppTInfo = new PointerByReference();
		int hr = this._invokeInt(4, new Object[] { this.getPointer(), index,
				ppTInfo });
		pTInfo.setPointer(ppTInfo.getValue());

		return new HRESULT(hr);
	}

	/**
	 * Gets the type info type.
	 * 
	 * @param index
	 *            the index
	 * @param pTKind
	 *            the t kind
	 * @return the hresult
	 */
	public HRESULT GetTypeInfoType(
	/* [in] */UINT index,
	/* [out] */TYPEKIND.ByReference pTKind) {

		int hr = this._invokeInt(5, new Object[] { this.getPointer(), index,
				pTKind });

		return new HRESULT(hr);
	}

	/**
	 * Gets the type info of guid.
	 * 
	 * @param guid
	 *            the guid
	 * @param pTinfo
	 *            the tinfo
	 * @return the hresult
	 */
	public HRESULT GetTypeInfoOfGuid(
	/* [in] */GUID guid,
	/* [out] */ITypeInfo pTinfo) {

		PointerByReference ppTinfo = new PointerByReference();
		int hr = this._invokeInt(6, new Object[] { this.getPointer(), guid,
				ppTinfo });
		pTinfo.setPointer(ppTinfo.getPointer());

		return new HRESULT(hr);
	}

	/**
	 * Gets the lib attr.
	 * 
	 * @param ppTLibAttr
	 *            the pp t lib attr
	 * @return the hresult
	 */
	public HRESULT GetLibAttr(
	/* [out] */TLIBATTR.ByReference ppTLibAttr) {

		int hr = this
				._invokeInt(7, new Object[] { this.getPointer(), ppTLibAttr });
		return new HRESULT(hr);
	}

	/**
	 * Gets the type comp.
	 * 
	 * @param pTComp
	 *            the t comp
	 * @return the hresult
	 */
	public HRESULT GetTypeComp(
	/* [out] */ITypeComp.ByReference pTComp) {

		PointerByReference ppTComp = new PointerByReference();
		int hr = this._invokeInt(8, new Object[] { this.getPointer(), ppTComp });
		pTComp.setPointer(ppTComp.getPointer());

		return new HRESULT(hr);
	}

	/**
	 * Gets the documentation.
	 * 
	 * @param index
	 *            the index
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
	public HRESULT GetDocumentation(
	/* [in] */int index,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */BSTRByReference pBstrDocString,
	/* [out] */DWORDbyReference pdwHelpContext,
	/* [out] */BSTRByReference pBstrHelpFile) {

		int hr = this._invokeInt(9, new Object[] { this.getPointer(), index,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	/**
	 * Checks if is name.
	 * 
	 * @param szNameBuf
	 *            the sz name buf
	 * @param lHashVal
	 *            the l hash val
	 * @param pfName
	 *            the pf name
	 * @return the hresult
	 */
	public HRESULT IsName(
	/* [annotation][out][in] */
	LPOLESTR szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [out] */BOOLbyReference pfName) {

		int hr = this._invokeInt(10, new Object[] { this.getPointer(), szNameBuf,
				lHashVal, pfName });

		return new HRESULT(hr);
	}

	/**
	 * Find name.
	 * 
	 * @param szNameBuf
	 *            the sz name buf
	 * @param lHashVal
	 *            the l hash val
	 * @param ppTInfo
	 *            the pp t info
	 * @param rgMemId
	 *            the rg mem id
	 * @param pcFound
	 *            the pc found
	 * @return the hresult
	 */
	public HRESULT FindName(
	/* [annotation][out][in] */
	BSTRByReference szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [length_is][size_is][out] */ITypeInfo[] ppTInfo,
	/* [length_is][size_is][out] */MEMBERID[] rgMemId,
	/* [out][in] */USHORTbyReference pcFound) {

		int hr = this._invokeInt(11, new Object[] { this.getPointer(), szNameBuf,
				lHashVal, ppTInfo, rgMemId, pcFound });

		return new HRESULT(hr);
	}

	/**
	 * Release t lib attr.
	 * 
	 * @param pTLibAttr
	 *            the t lib attr
	 */
	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
		this._invokeInt(12, new Object[] { this.getPointer(), pTLibAttr });
	}
}
