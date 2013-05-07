/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Wrapper class for the IRecordInfo interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class IRecordInfo extends IUnknown {

	/** The Constant IID_IRecordInfo. */
	public final static IID IID_IRecordInfo = new IID(
			"{0000002F-0000-0000-C000-000000000046}");

	/**
	 * Instantiates a new i record info.
	 */
	public IRecordInfo() {
	}

	/**
	 * Instantiates a new i record info.
	 * 
	 * @param pvInstance
	 *            the pv instance
	 */
	public IRecordInfo(Pointer pvInstance) {
		super(pvInstance);
	}

	/**
	 * Record init.
	 * 
	 * @param pvNew
	 *            the pv new
	 * @return the hresult
	 */
	public HRESULT RecordInit(/* [out] */PVOID pvNew) {
		return null;
	}

	/**
	 * Record clear.
	 * 
	 * @param pvExisting
	 *            the pv existing
	 * @return the hresult
	 */
	public HRESULT RecordClear(
	/* [in] */PVOID pvExisting) {
		return null;
	}

	/**
	 * Record copy.
	 * 
	 * @param pvExisting
	 *            the pv existing
	 * @param pvNew
	 *            the pv new
	 * @return the hresult
	 */
	public HRESULT RecordCopy(
	/* [in] */PVOID pvExisting,
	/* [out] */PVOID pvNew) {
		return null;
	}

	/**
	 * Gets the guid.
	 * 
	 * @param pguid
	 *            the pguid
	 * @return the hresult
	 */
	public HRESULT GetGuid(
	/* [out] */GUID pguid) {
		return null;
	}

	/**
	 * Gets the name.
	 * 
	 * @param pbstrName
	 *            the pbstr name
	 * @return the hresult
	 */
	public HRESULT GetName(
	/* [out] */BSTR pbstrName) {
		return null;
	}

	/**
	 * Gets the size.
	 * 
	 * @param pcbSize
	 *            the pcb size
	 * @return the hresult
	 */
	public HRESULT GetSize(
	/* [out] */ULONG pcbSize) {
		return null;
	}

	/**
	 * Gets the type info.
	 * 
	 * @param ppTypeInfo
	 *            the pp type info
	 * @return the hresult
	 */
	public HRESULT GetTypeInfo(
	/* [out] */ITypeInfo ppTypeInfo) {
		return null;
	}

	/**
	 * Gets the field.
	 * 
	 * @param pvData
	 *            the pv data
	 * @param szFieldName
	 *            the sz field name
	 * @param pvarField
	 *            the pvar field
	 * @return the hresult
	 */
	public HRESULT GetField(
	/* [in] */PVOID pvData,
	/* [in] */WString szFieldName,
	/* [out] */VARIANT pvarField) {
		return null;
	}

	/**
	 * Gets the field no copy.
	 * 
	 * @param pvData
	 *            the pv data
	 * @param szFieldName
	 *            the sz field name
	 * @param pvarField
	 *            the pvar field
	 * @param ppvDataCArray
	 *            the ppv data c array
	 * @return the hresult
	 */
	public HRESULT GetFieldNoCopy(
	/* [in] */PVOID pvData,
	/* [in] */WString szFieldName,
	/* [out] */VARIANT pvarField,
	/* [out] */PVOID ppvDataCArray) {
		return null;
	}

	/**
	 * Put field.
	 * 
	 * @param wFlags
	 *            the w flags
	 * @param pvData
	 *            the pv data
	 * @param szFieldName
	 *            the sz field name
	 * @param pvarField
	 *            the pvar field
	 * @return the hresult
	 */
	public HRESULT PutField(
	/* [in] */ULONG wFlags,
	/* [out][in] */PVOID pvData,
	/* [in] */WString szFieldName,
	/* [in] */VARIANT pvarField) {
		return null;
	}

	/**
	 * Put field no copy.
	 * 
	 * @param wFlags
	 *            the w flags
	 * @param pvData
	 *            the pv data
	 * @param szFieldName
	 *            the sz field name
	 * @param pvarField
	 *            the pvar field
	 * @return the hresult
	 */
	public HRESULT PutFieldNoCopy(
	/* [in] */ULONG wFlags,
	/* [out][in] */PVOID pvData,
	/* [in] */WString szFieldName,
	/* [in] */VARIANT pvarField) {
		return null;
	}

	/**
	 * Gets the field names.
	 * 
	 * @param pcNames
	 *            the pc names
	 * @param rgBstrNames
	 *            the rg bstr names
	 * @return the hresult
	 */
	public HRESULT GetFieldNames(
	/* [out][in] */ULONG pcNames,
	/* [length_is][size_is][out] */BSTR rgBstrNames) {
		return null;
	}

	/**
	 * Checks if is matching type.
	 * 
	 * @param pRecordInfo
	 *            the record info
	 * @return the bool
	 */
	public BOOL IsMatchingType(
	/* [in] */IRecordInfo pRecordInfo) {
		return null;
	}

	/**
	 * Record create.
	 * 
	 * @return the pvoid
	 */
	public PVOID RecordCreate() {
		return null;
	}

	/**
	 * Record create copy.
	 * 
	 * @param pvSource
	 *            the pv source
	 * @param ppvDest
	 *            the ppv dest
	 * @return the hresult
	 */
	public HRESULT RecordCreateCopy(
	/* [in] */PVOID pvSource,
	/* [out] */PVOID ppvDest) {
		return null;
	}

	/**
	 * Record destroy.
	 * 
	 * @param pvRecord
	 *            the pv record
	 * @return the hresult
	 */
	public HRESULT RecordDestroy(
	/* [in] */PVOID pvRecord) {
		return null;
	}
}
