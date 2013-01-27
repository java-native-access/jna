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
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Wrapper class for the ITypeLib interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeLib extends IUnknown {

	public static class ByReference extends IUnknown implements
			Structure.ByReference {
	}

	public ITypeLib() {
	}

	public ITypeLib(Pointer pvInstance) {
		super(pvInstance);
	}

	public HRESULT GetTypeInfoCount(UINTbyReference pcTInfo) {
		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(0));
		int hr = func
				.invokeInt(new Object[] { this.getPointer(), pcTInfo });

		return new HRESULT(hr);
	}

	public HRESULT GetTypeInfo(
	/* [in] */UINT index,
	/* [out] */ITypeInfo ppTInfo) {

		return new HRESULT(0);
	}

	public HRESULT GetTypeInfoType(
	/* [in] */UINT index,
	/* [out] */int pTKind) {

		return new HRESULT(0);
	}

	public HRESULT GetTypeInfoOfGuid(
	/* [in] */GUID guid,
	/* [out] */ITypeInfo ppTinfo) {

		return new HRESULT(0);
	}

	public HRESULT GetLibAttr(
	/* [out] */TLIBATTR ppTLibAttr) {

		return new HRESULT(0);
	}

	public HRESULT GetTypeComp(
	/* [out] */ITypeComp ppTComp) {

		return new HRESULT(0);
	}

	public HRESULT GetDocumentation(
	/* [in] */int index,
	/* [out] */BSTR pBstrName,
	/* [out] */BSTR pBstrDocString,
	/* [out] */DWORD pdwHelpContext,
	/* [out] */BSTR pBstrHelpFile) {

		return new HRESULT(0);
	}

	public HRESULT IsName(
	/* [annotation][out][in] */
	WString szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [out] */BOOL pfName) {

		return new HRESULT(0);
	}

	public HRESULT FindName(
	/* [annotation][out][in] */
	WString szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [length_is][size_is][out] */ITypeInfo ppTInfo,
	/* [length_is][size_is][out] */MEMBERID rgMemId,
	/* [out][in] */USHORT pcFound) {

		return new HRESULT(0);
	}

	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {

	}
}
