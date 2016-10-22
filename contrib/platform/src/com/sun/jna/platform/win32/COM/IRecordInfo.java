/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
