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
