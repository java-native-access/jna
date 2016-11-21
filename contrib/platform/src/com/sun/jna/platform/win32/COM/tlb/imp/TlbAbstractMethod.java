/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.CURRENCY;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.DECIMAL;
import com.sun.jna.platform.win32.OaIdl.ELEMDESC;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEDESC;
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
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbFunction.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public abstract class TlbAbstractMethod extends TlbBase implements Variant {

    protected TypeInfoDoc typeInfoDoc;

    protected String methodName;

    protected String docStr;

    protected short vtableId;

    protected MEMBERID memberid;

    protected short paramCount;

    protected String returnType;

    protected String methodparams = "";

    protected String methodvariables = "";
    
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
        super(index, typeLibUtil, typeInfoUtil);
        this.typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
        this.methodName = typeInfoDoc.getName();
        this.docStr = typeInfoDoc.getDocString();

        // get function values
        this.vtableId = funcDesc.oVft.shortValue();
        this.memberid = funcDesc.memid;
        this.paramCount = funcDesc.cParams.shortValue();
        this.returnType = this.getType(funcDesc);
    }

    public TypeInfoDoc getTypeInfoDoc() {
        return typeInfoDoc;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDocStr() {
        return docStr;
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
            return "int";
        case VT_UINT:
            return UINT.class.getSimpleName();
        case VT_VOID:
            return PVOID.class.getSimpleName();
        case VT_HRESULT:
            return HRESULT.class.getSimpleName();
        case VT_PTR:
            return Pointer.class.getSimpleName();
        case VT_SAFEARRAY:
            return "safearray";
        case VT_CARRAY:
            return "carray";
        case VT_USERDEFINED:
            return "userdefined";
        case VT_LPSTR:
            return LPSTR.class.getSimpleName();
        case VT_LPWSTR:
            return LPWSTR.class.getSimpleName();
        case VT_RECORD:
            return "record";
        case VT_INT_PTR:
            return INT_PTR.class.getSimpleName();
        case VT_UINT_PTR:
            return UINT_PTR.class.getSimpleName();
        case VT_FILETIME:
            return FILETIME.class.getSimpleName();
        case VT_STREAM:
            return "steam";
        case VT_STORAGE:
            return "storage";
        case VT_STREAMED_OBJECT:
            return "steamed_object";
        case VT_STORED_OBJECT:
            return "stored_object";
        case VT_BLOB_OBJECT:
            return "blob_object";
        case VT_CF:
            return "cf";
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
            return "illegal";
            /*
             * case VT_ILLEGALMASKED: return "illegal_masked"; case VT_TYPEMASK:
             * return "typemask";
             */default:
            return null;
        }
    }

    protected String getUserdefinedType(HREFTYPE hreftype) {
        ITypeInfo refTypeInfo = this.typeInfoUtil.getRefTypeInfo(hreftype);
        TypeInfoUtil typeInfoUtil = new TypeInfoUtil(refTypeInfo);
        TypeInfoDoc documentation = typeInfoUtil
                .getDocumentation(OaIdl.MEMBERID_NIL);
        return documentation.getName();
    }

    protected String getType(FUNCDESC funcDesc) {
        ELEMDESC elemDesc = funcDesc.elemdescFunc;
        return this.getType(elemDesc);
    }

    protected String getType(ELEMDESC elemDesc) {
        TYPEDESC _typeDesc = elemDesc.tdesc;
        return this.getType(_typeDesc);
    }

    protected String getType(TYPEDESC typeDesc) {
        VARTYPE vt = typeDesc.vt;
        String type = "not_defined";

        if (vt.intValue() == Variant.VT_PTR) {
            TYPEDESC lptdesc = typeDesc._typedesc.getLptdesc();
            type = this.getType(lptdesc);
        } else if (vt.intValue() == Variant.VT_SAFEARRAY
                || vt.intValue() == Variant.VT_CARRAY) {
            TYPEDESC tdescElem = typeDesc._typedesc.getLpadesc().tdescElem;
            type = this.getType(tdescElem);
        } else if (vt.intValue() == Variant.VT_USERDEFINED) {
            HREFTYPE hreftype = typeDesc._typedesc.hreftype;
            type = this.getUserdefinedType(hreftype);
        } else {
            type = this.getVarType(vt);
        }

        return type;
    }

    protected String replaceJavaKeyword(String name) {
        if (name.equals("final"))
            return "_" + name;
        else if (name.equals("default"))
            return "_" + name;
        else if (name.equals("case"))
            return "_" + name;
        else if (name.equals("char"))
            return "_" + name;
        else if (name.equals("private"))
            return "_" + name;
        else if (name.equals("default"))
            return "_" + name;
        else
            return name;
    }
}
