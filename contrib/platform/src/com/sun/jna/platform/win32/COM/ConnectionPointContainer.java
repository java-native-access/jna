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
