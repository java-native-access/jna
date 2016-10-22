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
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public class UnknownVTable extends Structure {
	public static class ByReference extends UnknownVTable implements Structure.ByReference {
	}

	public static final List<String> FIELDS = createFieldsOrder("QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback");
	public QueryInterfaceCallback QueryInterfaceCallback;
	public AddRefCallback AddRefCallback;
	public ReleaseCallback ReleaseCallback;

	@Override
	protected List<String> getFieldOrder() {
		return FIELDS;
	}

	public static interface QueryInterfaceCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, REFIID refid, PointerByReference ppvObject);
	}

	public static interface AddRefCallback extends StdCallLibrary.StdCallCallback {
		int invoke(Pointer thisPointer);
	}

	public static interface ReleaseCallback extends StdCallLibrary.StdCallCallback {
		int invoke(Pointer thisPointer);
	}
}
