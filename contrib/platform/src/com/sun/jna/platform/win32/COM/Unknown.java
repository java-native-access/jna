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
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the ITypeInfo interface
 * 
 * Method Name V-Table Offset IUnknown.QueryInterface 0 IUnknown.AddRef 4
 * IUnknown.Release 8
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class Unknown extends COMInvoker implements IUnknown {

    public static class ByReference extends Unknown implements
            Structure.ByReference {
    }

    public Unknown() {
    }

    /**
     * Instantiates a new i unknown.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public Unknown(Pointer pvInstance) {
        this.setPointer(pvInstance);
    }

    /**
     * Query interface.
     * 
     * @param riid
     *            the riid
     * @param ppvObject
     *            the ppv object
     * @return the hresult
     */
    public HRESULT QueryInterface(REFIID riid, PointerByReference ppvObject) {
        return (HRESULT) this._invokeNativeObject(0,
                new Object[] { this.getPointer(), riid, ppvObject },
                HRESULT.class);
    }

    public int AddRef() {
        return this._invokeNativeInt(1, new Object[] { this.getPointer() });
    }

    public int Release() {
        return this._invokeNativeInt(2, new Object[] { this.getPointer() });
    }
}
