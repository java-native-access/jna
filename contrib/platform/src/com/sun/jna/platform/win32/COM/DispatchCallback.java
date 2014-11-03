package com.sun.jna.platform.win32.COM;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class DispatchCallback extends Structure implements IDispatch {
	public DispatchCallback() {
		this.vtbl = this.constructVTable();
		this.initVTable();
		super.write();
	}

	public DispatchVTable.ByReference vtbl;
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { "vtbl" });
	}
	
	protected DispatchVTable.ByReference constructVTable() {
		return new DispatchVTable.ByReference();
	}
	
	protected void initVTable() {
		this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID.ByValue refid, PointerByReference ppvObject) {
				return DispatchCallback.this.QueryInterface(refid, ppvObject);
			}
		};
		this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return DispatchCallback.this.AddRef();
			}
		};
		this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return DispatchCallback.this.Release();
			}
		};
		this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, UINTByReference pctinfo) {
				return DispatchCallback.this.GetTypeInfoCount(pctinfo);
			}
		};
		this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
				return DispatchCallback.this.GetTypeInfo(iTInfo, lcid, ppTInfo);
			}
		};
		this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid,
					DISPIDByReference rgDispId) {
				return DispatchCallback.this.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
			}
		};
	}

	//------------------------ IDispatch ------------------------------
	@Override
	public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
		// TODO not implemented
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
		// TODO not implemented
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetIDsOfNames(REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid, DISPIDByReference rgDispId) {
		// TODO not implemented
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT Invoke(DISPID dispIdMember, IID riid, LCID lcid, DISPID wFlags, DISPPARAMS pDispParams,
			com.sun.jna.platform.win32.Variant.VARIANT.ByReference pVarResult,
			com.sun.jna.platform.win32.OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
		// TODO not implemented
		return new HRESULT(WinError.E_NOTIMPL);
	}
	
	
	//------------------------ IUnknown ------------------------------
	@Override
	public HRESULT QueryInterface(REFIID.ByValue refid, PointerByReference ppvObject) {
		if (null==ppvObject) {
			return new HRESULT(WinError.E_POINTER);
		}

		if (new Guid.IID(refid.getPointer()).equals(Unknown.IID_IUNKNOWN)) {
			ppvObject.setValue(this.getPointer());
			return WinError.S_OK;
		}
		
		if (new Guid.IID(refid.getPointer()).equals(Dispatch.IID_IDISPATCH)) {
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

}
