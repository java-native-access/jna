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
import com.sun.jna.platform.win32.OaIdl.BINDPTR;
import com.sun.jna.platform.win32.OaIdl.DESCKIND;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Wrapper class for the ITypeComp interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeComp extends IUnknown {

	public static class ByReference extends ITypeComp implements
			Structure.ByReference {
	}

	public ITypeComp() {
	}

	public ITypeComp(Pointer pvInstance) {
		super(pvInstance);
	}

	public HRESULT Bind(
	/* [annotation][in] */
	WString szName,
	/* [in] */ULONG lHashVal,
	/* [in] */WORD wFlags,
	/* [out] */ITypeInfo.ByReference ppTInfo,
	/* [out] */DESCKIND.ByReference pDescKind,
	/* [out] */BINDPTR.ByReference pBindPtr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(12));
		int hr = func.invokeInt(new Object[] { this.getPointer(), szName,
				lHashVal, wFlags, ppTInfo, pDescKind, pBindPtr });

		return new HRESULT(hr);
	}

	public HRESULT BindType(
	/* [annotation][in] */
	WString szName,
	/* [in] */ULONG lHashVal,
	/* [out] */ITypeInfo.ByReference ppTInfo,
	/* [out] */ITypeComp.ByReference ppTComp) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(16));
		int hr = func.invokeInt(new Object[] { this.getPointer(), szName,
				lHashVal, ppTInfo, ppTComp });

		return new HRESULT(hr);
	}
}
