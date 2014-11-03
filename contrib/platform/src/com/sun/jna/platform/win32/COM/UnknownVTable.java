package com.sun.jna.platform.win32.COM;

import java.util.Arrays;
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

	public QueryInterfaceCallback QueryInterfaceCallback;
	public AddRefCallback AddRefCallback;
	public ReleaseCallback ReleaseCallback;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { "QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback" });
	}

	public static interface QueryInterfaceCallback extends StdCallLibrary.StdCallCallback {
		WinNT.HRESULT invoke(Pointer thisPointer, REFIID.ByValue refid, PointerByReference ppvObject);
	}

	public static interface AddRefCallback extends StdCallLibrary.StdCallCallback {
		int invoke(Pointer thisPointer);
	}

	public static interface ReleaseCallback extends StdCallLibrary.StdCallCallback {
		int invoke(Pointer thisPointer);
	}
}
