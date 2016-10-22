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
public interface ITypeLib extends IUnknown {

    public UINT GetTypeInfoCount();

    public HRESULT GetTypeInfo(
    /* [in] */UINT index,
    /* [out] */PointerByReference pTInfo);

    public HRESULT GetTypeInfoType(
    /* [in] */UINT index,
    /* [out] */TYPEKIND.ByReference pTKind);

    public HRESULT GetTypeInfoOfGuid(
    /* [in] */GUID guid,
    /* [out] */PointerByReference pTinfo);

    public HRESULT GetLibAttr(
    /* [out] */PointerByReference ppTLibAttr);

    public HRESULT GetTypeComp(
    /* [out] */PointerByReference ppTComp);

    public HRESULT GetDocumentation(
    /* [in] */int index,
    /* [out] */BSTRByReference pBstrName,
    /* [out] */BSTRByReference pBstrDocString,
    /* [out] */DWORDByReference pdwHelpContext,
    /* [out] */BSTRByReference pBstrHelpFile);

    public HRESULT IsName(
    /* [annotation][out][in] */
    LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [out] */BOOLByReference pfName);

    public HRESULT FindName(
    /* [annotation][out][in] */
    LPOLESTR szNameBuf,
    /* [in] */ULONG lHashVal,
    /* [length_is][size_is][out] */Pointer[] ppTInfo,
    /* [length_is][size_is][out] */MEMBERID[] rgMemId,
    /* [out][in] */USHORTByReference pcFound);

    public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr);
}
