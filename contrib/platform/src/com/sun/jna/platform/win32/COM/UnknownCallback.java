package com.sun.jna.platform.win32.COM;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class UnknownCallback extends Structure implements IUnknownCallback {

	public UnknownVTable.ByReference vtbl;

	public UnknownCallback() {
		this.vtbl = this.constructVTable();
		this.initVTable();
		super.write();
	}

	protected UnknownVTable.ByReference constructVTable() {
		return new UnknownVTable.ByReference();
	}
	
	protected void initVTable() {
		this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID.ByValue refid, PointerByReference ppvObject) {
				return UnknownCallback.this.QueryInterface(refid, ppvObject);
			}
		};
		this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return UnknownCallback.this.AddRef();
			}
		};
		this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return UnknownCallback.this.Release();
			}
		};
	}
	
	public HRESULT QueryInterface(REFIID.ByValue refid, PointerByReference ppvObject) {
		if (null==ppvObject) {
			return new HRESULT(WinError.E_POINTER);
		}

		if (new Guid.IID(refid.getPointer()).equals(Unknown.IID_IUNKNOWN)) {
			ppvObject.setValue(this.getPointer());
			return WinError.S_OK;
		}
		
		return new HRESULT(WinError.E_NOINTERFACE);
	}

	public int AddRef() {
		return 0;
	}

	public int Release() {
		return 0;
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { "vtbl" });
	}


}
