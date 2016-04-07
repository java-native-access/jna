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
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class UnknownListener extends Structure {
    public static final List<String> FIELDS = createFieldsOrder("vtbl");
    public UnknownVTable.ByReference vtbl;

	public UnknownListener(IUnknownCallback callback) {
		this.vtbl = this.constructVTable();
		this.initVTable(callback);
		super.write();
	}

	@Override
	protected List<String> getFieldOrder() {
		return FIELDS;
	}

	protected UnknownVTable.ByReference constructVTable() {
		return new UnknownVTable.ByReference();
	}

	protected void initVTable(final IUnknownCallback callback) {
		this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() {
			@Override
			public HRESULT invoke(Pointer thisPointer, REFIID refid, PointerByReference ppvObject) {
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
	}

}
