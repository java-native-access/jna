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
public class TypeComp extends Unknown {

    public static class ByReference extends TypeComp implements
            Structure.ByReference {
    }

    /**
     * Instantiates a new i type comp.
     */
    public TypeComp() {
    }

    /**
     * Instantiates a new i type comp.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public TypeComp(Pointer pvInstance) {
        super(pvInstance);
    }

    /**
     * Bind.
     * 
     * @param szName
     *            the sz name
     * @param lHashVal
     *            the l hash val
     * @param wFlags
     *            the w flags
     * @param ppTInfo
     *            the pp t info
     * @param pDescKind
     *            the desc kind
     * @param pBindPtr
     *            the bind ptr
     * @return the hresult
     */
    public HRESULT Bind(
    /* [annotation][in] */
    WString szName,
    /* [in] */ULONG lHashVal,
    /* [in] */WORD wFlags,
    /* [out] */PointerByReference ppTInfo,
    /* [out] */DESCKIND.ByReference pDescKind,
    /* [out] */BINDPTR.ByReference pBindPtr) {

        return (HRESULT) this._invokeNativeObject(3,
                new Object[] { this.getPointer(), szName, lHashVal, wFlags,
                        ppTInfo, pDescKind, pBindPtr }, HRESULT.class);
    }

    /**
     * Bind type.
     * 
     * @param szName
     *            the sz name
     * @param lHashVal
     *            the l hash val
     * @param ppTInfo
     *            the pp t info
     * @param ppTComp
     *            the pp t comp
     * @return the hresult
     */
    public HRESULT BindType(
    /* [annotation][in] */
    WString szName,
    /* [in] */ULONG lHashVal,
    /* [out] */PointerByReference ppTInfo,
    /* [out] */PointerByReference ppTComp) {

        return (HRESULT) this._invokeNativeObject(4,
                new Object[] { this.getPointer(), szName, lHashVal, ppTInfo,
                        ppTComp }, HRESULT.class);
    }
}
