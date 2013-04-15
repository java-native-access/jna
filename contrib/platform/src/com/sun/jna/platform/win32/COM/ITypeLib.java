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
public interface ITypeLib extends IUnknown {

	@VTABLE_ID(3)
	public UINT GetTypeInfoCount();

	@VTABLE_ID(4)
	public HRESULT GetTypeInfo(
	/* [in] */UINT index,
	/* [out] */PointerByReference pTInfo);

	@VTABLE_ID(5)
	public HRESULT GetTypeInfoType(
	/* [in] */UINT index,
	/* [out] */TYPEKIND.ByReference pTKind);

	@VTABLE_ID(6)
	public HRESULT GetTypeInfoOfGuid(
	/* [in] */GUID guid,
	/* [out] */PointerByReference pTinfo);

	@VTABLE_ID(7)
	public HRESULT GetLibAttr(
	/* [out] */PointerByReference ppTLibAttr);

	@VTABLE_ID(8)
	public HRESULT GetTypeComp(
	/* [out] */PointerByReference ppTComp);

	@VTABLE_ID(9)
	public HRESULT GetDocumentation(
	/* [in] */int index,
	/* [out] */BSTRByReference pBstrName,
	/* [out] */BSTRByReference pBstrDocString,
	/* [out] */DWORDbyReference pdwHelpContext,
	/* [out] */BSTRByReference pBstrHelpFile);

	@VTABLE_ID(10)
	public HRESULT IsName(
	/* [annotation][out][in] */
	LPOLESTR szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [out] */BOOLbyReference pfName);

	@VTABLE_ID(11)
	public HRESULT FindName(
	/* [annotation][out][in] */
	BSTRByReference szNameBuf,
	/* [in] */ULONG lHashVal,
	/* [length_is][size_is][out] */ITypeInfo[] ppTInfo,
	/* [length_is][size_is][out] */MEMBERID[] rgMemId,
	/* [out][in] */USHORTbyReference pcFound);

	@VTABLE_ID(12)
	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr);
}
