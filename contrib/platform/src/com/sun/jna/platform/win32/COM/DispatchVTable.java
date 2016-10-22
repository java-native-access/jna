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
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public class DispatchVTable extends Structure {
	public static class ByReference extends DispatchVTable implements Structure.ByReference {
	}

	public static final List<String> FIELDS = createFieldsOrder(
	        "QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback",
	        "GetTypeInfoCountCallback", "GetTypeInfoCallback",
            "GetIDsOfNamesCallback", "InvokeCallback");

	public QueryInterfaceCallback QueryInterfaceCallback;
	public AddRefCallback AddRefCallback;
	public ReleaseCallback ReleaseCallback;
	public GetTypeInfoCountCallback GetTypeInfoCountCallback;
	public GetTypeInfoCallback GetTypeInfoCallback;
	public GetIDsOfNamesCallback GetIDsOfNamesCallback;
	public InvokeCallback InvokeCallback;

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

	public static interface GetTypeInfoCountCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, UINTByReference pctinfo);
	}

	public static interface GetTypeInfoCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, UINT iTInfo, LCID lcid, PointerByReference ppTInfo);
	}

	public static interface GetIDsOfNamesCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, REFIID riid, WString[] rgszNames, int cNames, LCID lcid,
				DISPIDByReference rgDispId);
	}

	public static interface InvokeCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, DISPID dispIdMember, REFIID riid, LCID lcid, WORD wFlags,
				DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
				IntByReference puArgErr);
	}
}
