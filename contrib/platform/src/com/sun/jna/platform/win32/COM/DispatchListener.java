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

import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class DispatchListener extends Structure {
    public static final List<String> FIELDS = createFieldsOrder("vtbl");
	public DispatchListener(IDispatchCallback callback) {
		this.vtbl = this.constructVTable();
		this.initVTable(callback);
		super.write();
	}
	public DispatchVTable.ByReference vtbl;

	@Override
	protected List<String> getFieldOrder() {
		return FIELDS;
	}

	protected DispatchVTable.ByReference constructVTable() {
		return new DispatchVTable.ByReference();
	}

	protected void initVTable(final IDispatchCallback callback) {
		this.vtbl.QueryInterfaceCallback = new DispatchVTable.QueryInterfaceCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID refid, PointerByReference ppvObject) {
				return callback.QueryInterface(refid, ppvObject);
			}
		};
		this.vtbl.AddRefCallback = new DispatchVTable.AddRefCallback() {
			@Override
			public int invoke(Pointer thisPointer) {
				return callback.AddRef();
			}
		};
		this.vtbl.ReleaseCallback = new DispatchVTable.ReleaseCallback() {
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
			public HRESULT invoke(Pointer thisPointer, REFIID riid, WString[] rgszNames, int cNames, LCID lcid,
					DISPIDByReference rgDispId) {
				return callback.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
			}
		};
		this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, DISPID dispIdMember, REFIID riid, LCID lcid, WORD wFlags,
					DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
		            IntByReference puArgErr) {

				return callback.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
			}
		};

	}

}
