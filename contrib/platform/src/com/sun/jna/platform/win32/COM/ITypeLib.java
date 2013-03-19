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
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOLbyReference;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Wrapper class for the ITypeLib interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeLib extends IUnknown {

	public static class ByReference extends ITypeLib implements
			Structure.ByReference {
	}

	public ITypeLib() {
	}

	public ITypeLib(Pointer pvInstance) {
		super(pvInstance);
	}

	public UINT GetTypeInfoCount() {
		int count = this.invoke(4, new Object[] { this.getPointer() });
		return new UINT(count);
	}

	public HRESULT GetTypeInfo(
	/* [in] */UINT index,
	/* [out] */ITypeInfo.ByReference pTInfo) {

		PointerByReference ppTInfo = new PointerByReference();
		int hr = this.invoke(5, new Object[] { this.getPointer(), index,
				ppTInfo });
		pTInfo.setPointer(ppTInfo.getValue());

		return new HRESULT(hr);
	}

	public HRESULT GetTypeInfoType(
	/* [in] */UINT index,
	/* [out] */IntByReference pTKind) {

		int hr = this.invoke(6,
				new Object[] { this.getPointer(), index, pTKind });

		return new HRESULT(hr);
	}

	public HRESULT GetTypeInfoOfGuid(
	/* [in] */GUID guid,
	/* [out] */ITypeInfo pTinfo) {

		PointerByReference ppTinfo = new PointerByReference();
		int hr = this.invoke(7,
				new Object[] { this.getPointer(), guid, ppTinfo });
		pTinfo.setPointer(ppTinfo.getPointer());

		return new HRESULT(hr);
	}

	public HRESULT GetLibAttr(
	/* [out] */TLIBATTR.ByReference ppTLibAttr) {

		int hr = this.invoke(8, new Object[] { this.getPointer(), ppTLibAttr });
		return new HRESULT(hr);
	}

	public HRESULT GetTypeComp(
	/* [out] */ITypeComp.ByReference pTComp) {

		PointerByReference ppTComp = new PointerByReference();
		int hr = this.invoke(9, new Object[] { this.getPointer(), ppTComp });
		pTComp.setPointer(ppTComp.getPointer());

		return new HRESULT(hr);
	}

	public HRESULT GetDocumentation(
	/* [in] */int index,
	/* [out] */BSTR pBstrName,
	/* [out] */BSTR pBstrDocString,
	/* [out] */DWORDbyReference pdwHelpContext,
	/* [out] */BSTR pBstrHelpFile) {

		int hr = this.invoke(10, new Object[] { this.getPointer(), index,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	public HRESULT IsName(
	/* [annotation][out][in] */
	WString szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [out] */BOOLbyReference pfName) {

		int hr = this.invoke(11, new Object[] { this.getPointer(), szNameBuf,
				lHashVal, pfName });

		return new HRESULT(hr);
	}

	public HRESULT FindName(
	/* [out][in] */
	WString szNameBuf,
	/* [in] */long lHashVal,
	/* [out] */ITypeInfo[] ppTInfo,
	/* [out] */MEMBERID[] rgMemId,
	/* [out][in] */short pcFound) {

		int hr = this.invoke(12, new Object[] { this.getPointer(), szNameBuf,
				lHashVal, ppTInfo, rgMemId, pcFound });

		return new HRESULT(hr);
	}

	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
		this.invoke(13, new Object[] { this.getPointer(), pTLibAttr });
	}
}
