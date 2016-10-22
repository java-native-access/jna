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
