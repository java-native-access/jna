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
import com.sun.jna.platform.win32.OaIdl.MEMBERIDByReference;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

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

	public UINT GetTypeInfoCount() {
		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(12));
		int count = func.invokeInt(new Object[] { this.getPointer() });

		return new UINT(count);
	}

	public HRESULT GetTypeInfo(
	/* [in] */UINT index,
	/* [out] */ITypeInfo.ByReference pTInfo) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(16));
		PointerByReference ppTInfo = new PointerByReference();
		int hr = func.invokeInt(new Object[] { this.getPointer(), index, ppTInfo });
		pTInfo.setPointer(ppTInfo.getValue());

		return new HRESULT(hr);
	}

	public HRESULT GetTypeInfoType(
	/* [in] */UINT index,
	/* [out] */IntByReference pTKind) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(20));
		int hr = func
				.invokeInt(new Object[] { this.getPointer(), index, pTKind });

		return new HRESULT(hr);
	}

	public HRESULT GetTypeInfoOfGuid(
	/* [in] */GUID guid,
	/* [out] */ITypeInfo.ByReference pTinfo) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(24));
		PointerByReference ppTinfo = new PointerByReference();
		int hr = func.invokeInt(new Object[] { this.getPointer(), guid, ppTinfo });
		pTinfo.setPointer(ppTinfo.getPointer());

		return new HRESULT(hr);
	}

	public HRESULT GetLibAttr(
	/* [out] */TLIBATTR.ByReference ppTLibAttr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(28));
		int hr = func.invokeInt(new Object[] { this.getPointer(), ppTLibAttr });

		return new HRESULT(hr);
	}

	public HRESULT GetTypeComp(
	/* [out] */ITypeComp.ByReference pTComp) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(32));
		PointerByReference ppTComp = new PointerByReference();
		int hr = func.invokeInt(new Object[] { this.getPointer(), ppTComp });
		pTComp.setPointer(ppTComp.getPointer());

		return new HRESULT(hr);
	}

	public HRESULT GetDocumentation(
	/* [in] */int index,
	/* [out] */BSTR pBstrName,
	/* [out] */BSTR pBstrDocString,
	/* [out] */DWORDByReference pdwHelpContext,
	/* [out] */BSTR pBstrHelpFile) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(36));
		int hr = func.invokeInt(new Object[] { this.getPointer(), index,
				pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile });

		return new HRESULT(hr);
	}

	public HRESULT IsName(
	/* [annotation][out][in] */
	WString szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [out] */BOOLByReference pfName) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(40));
		int hr = func.invokeInt(new Object[] { this.getPointer(), szNameBuf,
				lHashVal, pfName });

		return new HRESULT(hr);
	}

	public HRESULT FindName(
	/* [annotation][out][in] */
	WString szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [length_is][size_is][out] */ITypeInfo.ByReference ppTInfo,
	/* [length_is][size_is][out] */MEMBERIDByReference rgMemId,
	/* [out][in] */USHORTByReference pcFound) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(44));
		int hr = func.invokeInt(new Object[] { this.getPointer(), szNameBuf,
				lHashVal, ppTInfo, rgMemId, pcFound });

		return new HRESULT(hr);
	}

	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(48));
		func.invokeInt(new Object[] { this.getPointer(), pTLibAttr });
	}
}
