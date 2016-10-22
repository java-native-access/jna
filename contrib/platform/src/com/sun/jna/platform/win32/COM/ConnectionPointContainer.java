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
