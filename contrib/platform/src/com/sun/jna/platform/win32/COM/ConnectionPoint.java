package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class ConnectionPoint extends Unknown implements IConnectionPoint {

	public ConnectionPoint(Pointer pointer) {
		super(pointer);
	}

	HRESULT GetConnectionInterface(IID iid) {
		final int vTableId = 3;
		return (HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), iid }, HRESULT.class);
	}

	void GetConnectionPointContainer() {
		final int vTableId = 4;

	}

	@Override
	public HRESULT Advise(UnknownCallback pUnkSink, DWORDByReference pdwCookie) {
		final int vTableId = 5;

		return (HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), pUnkSink.getPointer(),
				pdwCookie }, HRESULT.class);
	}

	void Unadvise() {
		final int vTableId = 6;

	}

	void EnumConnections() {
		final int vTableId = 7;
	}
}
