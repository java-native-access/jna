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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable extends Unknown implements IRunningObjectTable {

	public static class ByReference extends RunningObjectTable implements Structure.ByReference {
	}

	public RunningObjectTable() {
	}

	public RunningObjectTable(Pointer pointer) {
		super(pointer);
	}

	// The magic number values for (vTableId) below, are worked out by
	// counting the number of methods in the full interface (0 indexed), as this
	// inherits IUnknown, which has 3 methods, we start here at 3.

	@Override
	public HRESULT Register(DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, DWORDByReference pdwRegister) {
		final int vTableId = 3;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				grfFlags, punkObject, pmkObjectName, pdwRegister }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT Revoke(DWORD dwRegister) {
		final int vTableId = 4;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				dwRegister }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT IsRunning(Pointer pmkObjectName) {
		final int vTableId = 5;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				pmkObjectName }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject) {
		final int vTableId = 6;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				pmkObjectName, ppunkObject }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT NoteChangeTime(DWORD dwRegister, FILETIME pfiletime) {
		final int vTableId = 7;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				dwRegister, pfiletime }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT GetTimeOfLastChange(Pointer pmkObjectName, FILETIME.ByReference pfiletime) {
		final int vTableId = 8;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				pmkObjectName, pfiletime }, WinNT.HRESULT.class);

		return hr;
	}

	@Override
	public HRESULT EnumRunning(PointerByReference ppenumMoniker) {
		final int vTableId = 9;

		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(),
				ppenumMoniker }, WinNT.HRESULT.class);

		return hr;
	}

}
