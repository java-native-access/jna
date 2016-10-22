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
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeInfo interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TypeInfo extends Unknown implements ITypeInfo {

    public static class ByReference extends TypeInfo implements
            Structure.ByReference {
    }

    /**
     * Instantiates a new i type info.
     */
    public TypeInfo() {
    }

    /**
     * Instantiates a new i type info.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public TypeInfo(Pointer pvInstance) {
        super(pvInstance);
    }

    /**
     * Gets the type attr.
     * 
     * @param ppTypeAttr
     *            the pp type attr
     * @return the hresult
     */
    public HRESULT GetTypeAttr(
    /* [out] */PointerByReference ppTypeAttr) {

        return (HRESULT) this._invokeNativeObject(3,
                new Object[] { this.getPointer(), ppTypeAttr }, HRESULT.class);
    }

    /**
     * Gets the type comp.
     * 
     * @param ppTComp
     *            the pp t comp
     * @return the hresult
     */
    public HRESULT GetTypeComp(
    /* [out] */PointerByReference ppTComp) {

        return (HRESULT) this._invokeNativeObject(4,
                new Object[] { this.getPointer(), ppTComp }, HRESULT.class);
    }

    /**
     * Gets the func desc.
     * 
     * @param index
     *            the index
     * @param ppFuncDesc
     *            the pp func desc
     * @return the hresult
     */
    public/* [local] */HRESULT GetFuncDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppFuncDesc) {

        return (HRESULT) this._invokeNativeObject(5,
                new Object[] { this.getPointer(), index, ppFuncDesc },
                HRESULT.class);
    }

    /**
     * Gets the var desc.
     * 
     * @param index
     *            the index
     * @param ppVarDesc
     *            the pp var desc
     * @return the hresult
     */
    public/* [local] */HRESULT GetVarDesc(
    /* [in] */UINT index,
    /* [out] */PointerByReference ppVarDesc) {

        return (HRESULT) this._invokeNativeObject(6,
                new Object[] { this.getPointer(), index, ppVarDesc },
                HRESULT.class);
    }

    /**
     * Gets the names.
     * 
     * @param memid
     *            the memid
     * @param rgBstrNames
     *            the rg bstr names
     * @param cMaxNames
     *            the c max names
     * @param pcNames
     *            the pc names
     * @return the hresult
     */
    public/* [local] */HRESULT GetNames(
    /* [in] */MEMBERID memid,
    /* [length_is][size_is][out] */BSTR[] rgBstrNames,
    /* [in] */UINT cMaxNames,
    /* [out] */UINTByReference pcNames) {

        return (HRESULT) this._invokeNativeObject(7,
                new Object[] { this.getPointer(), memid, rgBstrNames,
                        cMaxNames, pcNames }, HRESULT.class);
    }

    /**
     * Gets the ref type of impl type.
     * 
     * @param index
     *            the index
     * @param pRefType
     *            the ref type
     * @return the hresult
     */
    public HRESULT GetRefTypeOfImplType(
    /* [in] */UINT index,
    /* [out] */HREFTYPEByReference pRefType) {

        return (HRESULT) this._invokeNativeObject(8,
                new Object[] { this.getPointer(), index, pRefType },
                HRESULT.class);
    }

    /**
     * Gets the impl type flags.
     * 
     * @param index
     *            the index
     * @param pImplTypeFlags
     *            the impl type flags
     * @return the hresult
     */
    public HRESULT GetImplTypeFlags(
    /* [in] */UINT index,
    /* [out] */IntByReference pImplTypeFlags) {

        return (HRESULT) this._invokeNativeObject(9,
                new Object[] { this.getPointer(), index, pImplTypeFlags },
                HRESULT.class);
    }

    /**
     * Gets the i ds of names.
     * 
     * @param rgszNames
     *            the rgsz names
     * @param cNames
     *            the c names
     * @param pMemId
     *            the mem id
     * @return the hresult
     */
    public/* [local] */HRESULT GetIDsOfNames(
    /* [size_is][in] */LPOLESTR[] rgszNames,
    /* [in] */UINT cNames,
    /* [size_is][out] */MEMBERID[] pMemId) {

        return (HRESULT) this._invokeNativeObject(10,
                new Object[] { this.getPointer(), rgszNames, cNames, pMemId },
                HRESULT.class);
    }

    /**
     * Invoke.
     * 
     * @param pvInstance
     *            the pv instance
     * @param memid
     *            the memid
     * @param wFlags
     *            the w flags
     * @param pDispParams
     *            the disp params
     * @param pVarResult
     *            the var result
     * @param pExcepInfo
     *            the excep info
     * @param puArgErr
     *            the pu arg err
     * @return the hresult
     */
    public/* [local] */HRESULT Invoke(
    /* [in] */PVOID pvInstance,
    /* [in] */MEMBERID memid,
    /* [in] */WORD wFlags,
    /* [out][in] */DISPPARAMS.ByReference pDispParams,
    /* [out] */VARIANT.ByReference pVarResult,
    /* [out] */EXCEPINFO.ByReference pExcepInfo,
    /* [out] */UINTByReference puArgErr) {

        return (HRESULT) this._invokeNativeObject(11,
                new Object[] { this.getPointer(), pvInstance, memid, wFlags,
                        pDispParams, pVarResult, pExcepInfo, puArgErr },
                HRESULT.class);
    }

    /**
     * Gets the documentation.
     * 
     * @param memid
     *            the memid
     * @param pBstrName
     *            the bstr name
     * @param pBstrDocString
     *            the bstr doc string
     * @param pdwHelpContext
     *            the pdw help context
     * @param pBstrHelpFile
     *            the bstr help file
     * @return the hresult
     */
    public/* [local] */HRESULT GetDocumentation(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDByReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile) {

        return (HRESULT) this._invokeNativeObject(12,
                new Object[] { this.getPointer(), memid, pBstrName,
                        pBstrDocString, pdwHelpContext, pBstrHelpFile },
                HRESULT.class);
    }

    /**
     * Gets the dll entry.
     * 
     * @param memid
     *            the memid
     * @param invKind
     *            the inv kind
     * @param pBstrDllName
     *            the bstr dll name
     * @param pBstrName
     *            the bstr name
     * @param pwOrdinal
     *            the pw ordinal
     * @return the hresult
     */
    public/* [local] */HRESULT GetDllEntry(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */BSTRByReference pBstrDllName,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */WORDByReference pwOrdinal) {

        return (HRESULT) this._invokeNativeObject(13,
                new Object[] { this.getPointer(), memid, invKind, pBstrDllName,
                        pBstrName, pwOrdinal }, HRESULT.class);
    }

    /**
     * Gets the ref type info.
     * 
     * @param hRefType
     *            the h ref type
     * @param ppTInfo
     *            the pp t info
     * @return the hresult
     */
    public HRESULT GetRefTypeInfo(
    /* [in] */HREFTYPE hRefType,
    /* [out] */PointerByReference ppTInfo) {

        return (HRESULT) this._invokeNativeObject(14,
                new Object[] { this.getPointer(), hRefType, ppTInfo },
                HRESULT.class);
    }

    /**
     * Address of member.
     * 
     * @param memid
     *            the memid
     * @param invKind
     *            the inv kind
     * @param ppv
     *            the ppv
     * @return the hresult
     */
    public/* [local] */HRESULT AddressOfMember(
    /* [in] */MEMBERID memid,
    /* [in] */INVOKEKIND invKind,
    /* [out] */PointerByReference ppv) {

        return (HRESULT) this._invokeNativeObject(15,
                new Object[] { this.getPointer(), memid, invKind, ppv },
                HRESULT.class);
    }

    /**
     * Creates the instance.
     * 
     * @param pUnkOuter
     *            the unk outer
     * @param riid
     *            the riid
     * @param ppvObj
     *            the ppv obj
     * @return the hresult
     */
    public/* [local] */HRESULT CreateInstance(
    /* [in] */IUnknown pUnkOuter,
    /* [in] */REFIID riid,
    /* [iid_is][out] */PointerByReference ppvObj) {

        return (HRESULT) this._invokeNativeObject(16,
                new Object[] { this.getPointer(), pUnkOuter, riid, ppvObj },
                HRESULT.class);
    }

    /**
     * Gets the mops.
     * 
     * @param memid
     *            the memid
     * @param pBstrMops
     *            the bstr mops
     * @return the hresult
     */
    public HRESULT GetMops(
    /* [in] */MEMBERID memid,
    /* [out] */BSTRByReference pBstrMops) {

        return (HRESULT) this._invokeNativeObject(17,
                new Object[] { this.getPointer(), memid, pBstrMops },
                HRESULT.class);
    }

    /**
     * Gets the containing type lib.
     * 
     * @param ppTLib
     *            the pp t lib
     * @param pIndex
     *            the index
     * @return the hresult
     */
    public/* [local] */HRESULT GetContainingTypeLib(
    /* [out] */PointerByReference ppTLib,
    /* [out] */UINTByReference pIndex) {

        return (HRESULT) this._invokeNativeObject(18,
                new Object[] { this.getPointer(), ppTLib, pIndex },
                HRESULT.class);
    }

    /**
     * Release type attr.
     * 
     * @param pTypeAttr
     *            the type attr
     */
    public/* [local] */void ReleaseTypeAttr(
    /* [in] */TYPEATTR pTypeAttr) {
        this._invokeNativeVoid(19, new Object[] { this.getPointer(), pTypeAttr });
    }

    /**
     * Release func desc.
     * 
     * @param pFuncDesc
     *            the func desc
     */
    public/* [local] */void ReleaseFuncDesc(
    /* [in] */FUNCDESC pFuncDesc) {
        this._invokeNativeVoid(20, new Object[] { this.getPointer(), pFuncDesc });
    }

    /**
     * Release var desc.
     * 
     * @param pVarDesc
     *            the var desc
     */
    public/* [local] */void ReleaseVarDesc(
    /* [in] */VARDESC pVarDesc) {
        this._invokeNativeVoid(21, new Object[] { this.getPointer(), pVarDesc });
    }
}
