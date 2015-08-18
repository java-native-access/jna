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
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer extends Unknown implements
		IConnectionPointContainer {
	
	public ConnectionPointContainer(Pointer pointer) {
		super(pointer);
	}

	public HRESULT EnumConnectionPoints() {
		// I think the magic number here is worked out by counting the number of
		// methods in the full interface, as this inherits IUnknown, which
		// has 3 methods, we start here at 3 (0 indexed).
		final int vTableId = 3;
		
		
//		return (HRESULT) this._invokeNativeObject(3,
//				new Object[] { this.getPointer(), riid, ppCP }, HRESULT.class);
		throw new UnsupportedOperationException();
	}

	@Override
	public HRESULT FindConnectionPoint(REFIID riid, PointerByReference ppCP) {
		// I think the magic number here is worked out by counting the number of
		// methods in the full interface,
		// this as this inherits IUnknown, which has 3 methods, we have here 4.
		// second in this class
		final int vTableId = 4;
		return (HRESULT) this._invokeNativeObject(vTableId,
				new Object[] { this.getPointer(), riid, ppCP }, HRESULT.class);
	}

}
