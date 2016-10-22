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
