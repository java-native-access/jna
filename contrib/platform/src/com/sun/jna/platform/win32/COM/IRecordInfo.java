package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.LPCOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class IRecordInfo extends IUnknown {

	public final static IID IID_IRecordInfo = new IID(
			"{0000002F-0000-0000-C000-000000000046}");

	public IRecordInfo() {
	}

	public IRecordInfo(Pointer pvInstance) {
		super(pvInstance);
	}

	public HRESULT RecordInit(/* [out] */PVOID pvNew) {
		return null;
	}

	public HRESULT RecordClear(
	/* [in] */PVOID pvExisting) {
		return null;
	}

	public HRESULT RecordCopy(
	/* [in] */PVOID pvExisting,
	/* [out] */PVOID pvNew) {
		return null;
	}

	public HRESULT GetGuid(
	/* [out] */GUID pguid) {
		return null;
	}

	public HRESULT GetName(
	/* [out] */BSTR pbstrName) {
		return null;
	}

	public HRESULT GetSize(
	/* [out] */ULONG pcbSize) {
		return null;
	}

	public HRESULT GetTypeInfo(
	/* [out] */ITypeInfo ppTypeInfo) {
		return null;
	}

	public HRESULT GetField(
	/* [in] */PVOID pvData,
	/* [in] */LPCOLESTR szFieldName,
	/* [out] */VARIANT pvarField) {
		return null;
	}

	public HRESULT GetFieldNoCopy(
	/* [in] */PVOID pvData,
	/* [in] */LPCOLESTR szFieldName,
	/* [out] */VARIANT pvarField,
	/* [out] */PVOID ppvDataCArray) {
		return null;
	}

	public HRESULT PutField(
	/* [in] */ULONG wFlags,
	/* [out][in] */PVOID pvData,
	/* [in] */LPCOLESTR szFieldName,
	/* [in] */VARIANT pvarField) {
		return null;
	}

	public HRESULT PutFieldNoCopy(
	/* [in] */ULONG wFlags,
	/* [out][in] */PVOID pvData,
	/* [in] */LPCOLESTR szFieldName,
	/* [in] */VARIANT pvarField) {
		return null;
	}

	public HRESULT GetFieldNames(
	/* [out][in] */ULONG pcNames,
	/* [length_is][size_is][out] */BSTR rgBstrNames) {
		return null;
	}

	public BOOL IsMatchingType(
	/* [in] */IRecordInfo pRecordInfo) {
		return null;
	}

	public PVOID RecordCreate() {
		return null;
	}

	public HRESULT RecordCreateCopy(
	/* [in] */PVOID pvSource,
	/* [out] */PVOID ppvDest) {
		return null;
	}

	public HRESULT RecordDestroy(
	/* [in] */PVOID pvRecord) {
		return null;
	}
}
