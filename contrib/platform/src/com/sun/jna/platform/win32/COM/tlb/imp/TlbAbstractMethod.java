/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.OaIdl.CURRENCY;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.DECIMAL;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WTypes.LPWSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.UCHAR;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.CLSID;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbFunction.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public abstract class TlbAbstractMethod extends TlbBase implements Variant {

    /**
     * Instantiates a new tlb function.
     * 
     * @param index
     *            the index
     * @param typeLibUtil
     *            the type lib util
     * @param funcDesc
     *            the func desc
     * @param typeInfoUtil
     *            the type info util
     */
    public TlbAbstractMethod(int index, TypeLibUtil typeLibUtil,
	    FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
	super(index, typeLibUtil);
    }

    /**
     * Gets the var type.
     * 
     * @param vt
     *            the vt
     * @return the var type
     */
    protected String getVarType(VARTYPE vt) {
	switch (vt.intValue()) {
	case VT_EMPTY:
	    return "";
	case VT_NULL:
	    return "null";
	case VT_I2:
	    return "short";
	case VT_I4:
	    return "int";
	case VT_R4:
	    return "float";
	case VT_R8:
	    return "double";
	case VT_CY:
	    return CURRENCY.class.getSimpleName();
	case VT_DATE:
	    return DATE.class.getSimpleName();
	case VT_BSTR:
	    return BSTR.class.getSimpleName();
	case VT_DISPATCH:
	    return IDispatch.class.getSimpleName();
	case VT_ERROR:
	    return SCODE.class.getSimpleName();
	case VT_BOOL:
	    return BOOL.class.getSimpleName();
	case VT_VARIANT:
	    return VARIANT.class.getSimpleName();
	case VT_UNKNOWN:
	    return IUnknown.class.getSimpleName();
	case VT_DECIMAL:
	    return DECIMAL.class.getSimpleName();
	case VT_I1:
	    return CHAR.class.getSimpleName();
	case VT_UI1:
	    return UCHAR.class.getSimpleName();
	case VT_UI2:
	    return USHORT.class.getSimpleName();
	case VT_UI4:
	    return UINT.class.getSimpleName();
	case VT_I8:
	    return LONG.class.getSimpleName();
	case VT_UI8:
	    return ULONG.class.getSimpleName();
	case VT_INT:
	    return "";
	case VT_UINT:
	    return "";
	case VT_VOID:
	    return PVOID.class.getSimpleName();
	case VT_HRESULT:
	    return HRESULT.class.getSimpleName();
	case VT_PTR:
	    return Pointer.class.getSimpleName();
	case VT_SAFEARRAY:
	    return "";
	case VT_CARRAY:
	    return "";
	case VT_USERDEFINED:
	    return "";
	case VT_LPSTR:
	    return LPSTR.class.getSimpleName();
	case VT_LPWSTR:
	    return LPWSTR.class.getSimpleName();
	case VT_RECORD:
	    return "";
	case VT_INT_PTR:
	    return INT_PTR.class.getSimpleName();
	case VT_UINT_PTR:
	    return UINT_PTR.class.getSimpleName();
	case VT_FILETIME:
	    return FILETIME.class.getSimpleName();
	case VT_STREAM:
	    return "";
	case VT_STORAGE:
	    return "";
	case VT_STREAMED_OBJECT:
	    return "";
	case VT_STORED_OBJECT:
	    return "";
	case VT_BLOB_OBJECT:
	    return "";
	case VT_CF:
	    return "";
	case VT_CLSID:
	    return CLSID.class.getSimpleName();
	case VT_VERSIONED_STREAM:
	    return "";
	    // case VT_BSTR_BLOB:
	    // return "";
	case VT_VECTOR:
	    return "";
	case VT_ARRAY:
	    return "";
	case VT_BYREF:
	    return PVOID.class.getSimpleName();
	case VT_RESERVED:
	    return "";
	case VT_ILLEGAL:
	    return "";
	    // case VT_ILLEGALMASKED:
	    // return "";
	    // case VT_TYPEMASK:
	    // return "";
	default:
	    return null;
	}
    }
}
