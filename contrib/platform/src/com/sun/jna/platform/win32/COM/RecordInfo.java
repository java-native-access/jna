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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
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
public class RecordInfo extends Unknown implements IRecordInfo {

    public static class ByReference extends RecordInfo implements
            Structure.ByReference {
    }

    /**
     * Instantiates a new i record info.
     */
    public RecordInfo() {
    }

    /**
     * Instantiates a new i record info.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public RecordInfo(Pointer pvInstance) {
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
