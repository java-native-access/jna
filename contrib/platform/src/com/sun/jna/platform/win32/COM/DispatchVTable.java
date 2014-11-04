package com.sun.jna.platform.win32.COM;

import java.util.ArrayList;
import java.util.Arrays;
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

public class DispatchVTable extends UnknownVTable {
	public static class ByReference extends DispatchVTable implements Structure.ByReference {
	}

	public GetTypeInfoCountCallback GetTypeInfoCountCallback;
	public GetTypeInfoCallback GetTypeInfoCallback;
	public GetIDsOfNamesCallback GetIDsOfNamesCallback;
	public InvokeCallback InvokeCallback;

	@Override
	protected List<String> getFieldOrder() {
		List<String> s = super.getFieldOrder();
		List<String> t = Arrays.asList(new String[] { "GetTypeInfoCountCallback", "GetTypeInfoCallback",
				"GetIDsOfNamesCallback", "InvokeCallback" });
		List<String> a = new ArrayList<String>();
		a.addAll(s);
		a.addAll(t);
		return a;
	}

	public static interface GetTypeInfoCountCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, UINTByReference pctinfo);
	}

	public static interface GetTypeInfoCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, UINT iTInfo, LCID lcid, PointerByReference ppTInfo);
	}

	public static interface GetIDsOfNamesCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid,
				DISPIDByReference rgDispId);
	}

	public static interface InvokeCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, DISPID dispIdMember, REFIID.ByValue riid, LCID lcid, WORD wFlags,
				DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
				IntByReference puArgErr);
	}
}
