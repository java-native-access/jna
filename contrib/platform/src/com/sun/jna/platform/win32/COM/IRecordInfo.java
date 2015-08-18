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

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the IRecordInfo interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface IRecordInfo extends IUnknown {

    public final static IID IID_IRecordInfo = new IID(
            "{0000002F-0000-0000-C000-000000000046}");

    public HRESULT RecordInit(/* [out] */PVOID pvNew);

    public HRESULT RecordClear(
    /* [in] */PVOID pvExisting);

    public HRESULT RecordCopy(/* [in] */PVOID pvExisting,
    /* [out] */PVOID pvNew);

    public HRESULT GetGuid(
    /* [out] */GUID pguid);

    public HRESULT GetName(
    /* [out] */BSTR pbstrName);

    public HRESULT GetSize(
    /* [out] */ULONG pcbSize);

    public HRESULT GetTypeInfo(
    /* [out] */ITypeInfo ppTypeInfo);

    public HRESULT GetField(
    /* [in] */PVOID pvData,
    /* [in] */WString szFieldName,
    /* [out] */VARIANT pvarField);

    public HRESULT GetFieldNoCopy(
    /* [in] */PVOID pvData,
    /* [in] */WString szFieldName,
    /* [out] */VARIANT pvarField,
    /* [out] */PVOID ppvDataCArray);

    public HRESULT PutField(
    /* [in] */ULONG wFlags,
    /* [out][in] */PVOID pvData,
    /* [in] */WString szFieldName,
    /* [in] */VARIANT pvarField);

    public HRESULT PutFieldNoCopy(
    /* [in] */ULONG wFlags,
    /* [out][in] */PVOID pvData,
    /* [in] */WString szFieldName,
    /* [in] */VARIANT pvarField);

    public HRESULT GetFieldNames(
    /* [out][in] */ULONG pcNames,
    /* [length_is][size_is][out] */BSTR rgBstrNames);

    public BOOL IsMatchingType(
    /* [in] */IRecordInfo pRecordInfo);

    public PVOID RecordCreate();

    public HRESULT RecordCreateCopy(
    /* [in] */PVOID pvSource,
    /* [out] */PVOID ppvDest);

    public HRESULT RecordDestroy(
    /* [in] */PVOID pvRecord);
}
