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
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class ConnectionPoint extends Unknown implements IConnectionPoint {

	public ConnectionPoint(Pointer pointer) {
		super(pointer);
	}

	@Override
	public HRESULT GetConnectionInterface(IID iid) {
		final int vTableId = 3;
		return (HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), iid }, HRESULT.class);
	}

	void GetConnectionPointContainer() {
		final int vTableId = 4;

	}

	@Override
	public HRESULT Advise(IUnknownCallback pUnkSink, DWORDByReference pdwCookie) {
		final int vTableId = 5;

		return (HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), pUnkSink.getPointer(),
				pdwCookie }, HRESULT.class);
	}

	@Override
	public HRESULT Unadvise(DWORD dwCookie) {
		final int vTableId = 6;
		
		return (HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), dwCookie }, HRESULT.class);
	}

	void EnumConnections() {
		final int vTableId = 7;
	}
}
