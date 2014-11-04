package com.sun.jna.platform.win32.COM;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Guid.GUID.ByValue;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VARIANT.ByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class DispatchListener extends Structure {
	public DispatchListener(IDispatchCallback callback) {
		this.vtbl = this.constructVTable();
		this.initVTable(callback);
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
	
	protected void initVTable(final IDispatchCallback callback) {
		this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID.ByValue refid, PointerByReference ppvObject) {
				return callback.QueryInterface(refid, ppvObject);
			}
		};
		this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return callback.AddRef();
			}
		};
		this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return callback.Release();
			}
		};
		this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, UINTByReference pctinfo) {
				return callback.GetTypeInfoCount(pctinfo);
			}
		};
		this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
				return callback.GetTypeInfo(iTInfo, lcid, ppTInfo);
			}
		};
		this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid,
					DISPIDByReference rgDispId) {
				return callback.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
			}
		};
		this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback() {
			
			@Override
			public HRESULT invoke(Pointer thisPointer, DISPID dispIdMember, REFIID.ByValue riid, LCID lcid, WORD wFlags,
					DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
		            IntByReference puArgErr) {

				return callback.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
			}
		};
	}

}
