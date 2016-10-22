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
