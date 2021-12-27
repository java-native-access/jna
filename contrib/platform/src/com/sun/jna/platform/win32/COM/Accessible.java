/* Copyright (c) 2021 Mo Beigi, All Rights Reserved
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
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * Implementation for {@link IAccessible } interface
 *
 * @author Mo Beigi, me@mobeigi.com
 */
public class Accessible extends Dispatch implements IAccessible
{
    public static class ByReference extends Accessible implements
            Structure.ByReference {
    }

    public Accessible() {
    }

    public Accessible(Pointer pvInstance) { super(pvInstance); }

    public HRESULT get_accName(VARIANT varChild, BSTRByReference pszName)
    {
        return (HRESULT) this._invokeNativeObject(10,
                new Object[] { this.getPointer(), varChild, pszName }, HRESULT.class);
    }

    public HRESULT get_accValue(VARIANT varChild, BSTRByReference pszName)
    {
        return (HRESULT) this._invokeNativeObject(11,
                new Object[] { this.getPointer(), varChild, pszName }, HRESULT.class);
    }

    public HRESULT get_accRole(VARIANT varChild, VARIANT.ByReference pvarRole)
    {
        return (HRESULT) this._invokeNativeObject(13,
                new Object[] { this.getPointer(), varChild, pvarRole }, HRESULT.class);
    }

    public HRESULT get_accChildCount(IntByReference pcountChildren)
    {
        return (HRESULT) this._invokeNativeObject(8,
                new Object[] { this.getPointer(), pcountChildren }, HRESULT.class);
    }

    public HRESULT get_accDefaultAction(VARIANT varChild, BSTRByReference pszDefaultAction)
    {
        return (HRESULT) this._invokeNativeObject(20,
                new Object[] { this.getPointer(), varChild, pszDefaultAction }, HRESULT.class);
    }

    public HRESULT accDoDefaultAction(VARIANT varChild)
    {
        return (HRESULT) this._invokeNativeObject(25,
                new Object[] { this.getPointer(), varChild }, HRESULT.class);
    }
}
