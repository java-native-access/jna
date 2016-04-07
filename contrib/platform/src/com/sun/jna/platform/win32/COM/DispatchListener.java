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
