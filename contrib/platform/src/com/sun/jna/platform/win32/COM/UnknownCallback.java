package com.sun.jna.platform.win32.COM;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class UnknownCallback extends Structure implements IUnknown {

	public UnknownVTable.ByReference vtbl;

	public UnknownCallback() {
		this.vtbl = new UnknownVTable.ByReference();
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
		super.write();
	}

	public HRESULT QueryInterface(REFIID.ByValue refid, PointerByReference ppvObject) {
		String s = refid.toGuidString();
		HRESULT E_ = new HRESULT(0x80004002);
		return E_;
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
