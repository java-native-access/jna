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
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeLib interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TypeLib extends Unknown implements ITypeLib {

    public static class ByReference extends TypeLib implements
            Structure.ByReference {
    }

    /**
     * Instantiates a new i type lib.
     */
    public TypeLib() {
    }

    /**
     * Instantiates a new i type lib.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public TypeLib(Pointer pvInstance) {
        super(pvInstance);
    }

    /**
     * Gets the type info count.
     * 
     * @return the uint
     */
    public UINT GetTypeInfoCount() {
        return (UINT) this._invokeNativeObject(3,
                new Object[] { this.getPointer() }, UINT.class);
    }

    /**
     * Gets the type info.
     * 
     * @param index
     *            the index
     * @param pTInfo
     *            the t info
     * @return the hresult
     */
    public HRESULT GetTypeInfo(
    /* [in] */UINT index,
    /* [out] */PointerByReference pTInfo) {

        return (HRESULT) this._invokeNativeObject(4,
                new Object[] { this.getPointer(), index, pTInfo },
                HRESULT.class);
    }

    /**
     * Gets the type info type.
     * 
     * @param index
     *            the index
     * @param pTKind
     *            the t kind
     * @return the hresult
     */
    public HRESULT GetTypeInfoType(
    /* [in] */UINT index,
    /* [out] */TYPEKIND.ByReference pTKind) {

        return (HRESULT) this._invokeNativeObject(5,
                new Object[] { this.getPointer(), index, pTKind },
                HRESULT.class);
    }

    /**
     * Gets the type info of guid.
     * 
     * @param guid
     *            the guid
     * @param pTinfo
     *            the tinfo
     * @return the hresult
     */
    public HRESULT GetTypeInfoOfGuid(
    /* [in] */GUID guid,
    /* [out] */PointerByReference pTinfo) {

        return (HRESULT) this
                ._invokeNativeObject(6, new Object[] { this.getPointer(), guid,
                        pTinfo }, HRESULT.class);
    }

    /**
     * Gets the lib attr.
     * 
     * @param ppTLibAttr
     *            the pp t lib attr
     * @return the hresult
     */
    public HRESULT GetLibAttr(
    /* [out] */PointerByReference ppTLibAttr) {

        return (HRESULT) this._invokeNativeObject(7,
                new Object[] { this.getPointer(), ppTLibAttr }, HRESULT.class);
    }

    /**
     * Gets the type comp.
     * 
     * @param pTComp
     *            the t comp
     * @return the hresult
     */
    public HRESULT GetTypeComp(
    /* [out] */PointerByReference pTComp) {

        return (HRESULT) this._invokeNativeObject(8,
                new Object[] { this.getPointer(), pTComp }, HRESULT.class);
    }

    /**
     * Gets the documentation.
     * 
     * @param index
     *            the index
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
    public HRESULT GetDocumentation(
    /* [in] */int index,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDByReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile) {

        return (HRESULT) this._invokeNativeObject(9,
                new Object[] { this.getPointer(), index, pBstrName,
                        pBstrDocString, pdwHelpContext, pBstrHelpFile },
                HRESULT.class);
    }

    /**
     * Checks if is name.
     * 
     * @param szNameBuf
     *            the sz name buf
     * @param lHashVal
     *            the l hash val
     * @param pfName
     *            the pf name
     * @return the hresult
     */
    public HRESULT IsName(
    /* [annotation][out][in] */ LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [out] */BOOLByReference pfName) {

        return (HRESULT) this
                ._invokeNativeObject(10, new Object[] { this.getPointer(),
                        szNameBuf, lHashVal, pfName }, HRESULT.class);
    }

    /**
     * Find name.
     * 
     * @param szNameBuf
     *            the sz name buf
     * @param lHashVal
     *            the l hash val
     * @param ppTInfo
     *            the pp t info
     * @param rgMemId
     *            the rg mem id
     * @param pcFound
     *            the pc found
     * @return the hresult
     */
    public HRESULT FindName(
    /* [annotation][out][in] */ LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [length_is][size_is][out] */Pointer[] ppTInfo,
    /* [length_is][size_is][out] */MEMBERID[] rgMemId,
    /* [out][in] */USHORTByReference pcFound) {

        return (HRESULT) this._invokeNativeObject(11,
                new Object[] { this.getPointer(), szNameBuf, lHashVal, ppTInfo,
                        rgMemId, pcFound }, HRESULT.class);
    }

    /**
     * Release t lib attr.
     * 
     * @param pTLibAttr
     *            the t lib attr
     */
    public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
        this._invokeNativeObject(12, new Object[] { this.getPointer(), 
            pTLibAttr.getPointer() },  HRESULT.class);
    }
}
