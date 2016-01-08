/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class EnumMoniker extends Unknown implements IEnumMoniker {

	public EnumMoniker(Pointer pointer) {
		super(pointer);
	}

	// The magic number values for (vTableId) below, are worked out by
	// counting the number of methods in the full interface (0 indexed), as this
	// inherits IUnknown, which has 3 methods, we start here at 3.

	@Override
	public HRESULT Next(ULONG celt, PointerByReference rgelt, ULONGByReference pceltFetched) {
		final int vTableId = 3;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), celt,
				rgelt, pceltFetched }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT Skip(ULONG celt) {
		final int vTableId = 4;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), celt },
				WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT Reset() {
		final int vTableId = 5;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), },
				WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT Clone(PointerByReference ppenum) {
		final int vTableId = 6;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId,
				new Object[] { this.getPointer(), ppenum }, WinNT.HRESULT.class);

		return hr;
	}
}
