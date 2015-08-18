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
import com.sun.jna.platform.win32.OaIdl.BINDPTR;
import com.sun.jna.platform.win32.OaIdl.DESCKIND;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeComp interface.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface ITypeComp extends IUnknown {

    public HRESULT Bind(
    /* [annotation][in] */
    WString szName,
    /* [in] */ULONG lHashVal,
    /* [in] */WORD wFlags,
    /* [out] */PointerByReference ppTInfo,
    /* [out] */DESCKIND.ByReference pDescKind,
    /* [out] */BINDPTR.ByReference pBindPtr);

    public HRESULT BindType(
    /* [annotation][in] */
    WString szName,
    /* [in] */ULONG lHashVal,
    /* [out] */PointerByReference ppTInfo,
    /* [out] */PointerByReference ppTComp);
}
