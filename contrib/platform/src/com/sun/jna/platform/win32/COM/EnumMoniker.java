/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
