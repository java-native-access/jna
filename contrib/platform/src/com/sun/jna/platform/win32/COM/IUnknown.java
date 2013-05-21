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

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

/**
 * Wrapper class for the ITypeInfo interface
 * 
 * Method Name V-Table Offset IUnknown.QueryInterface 0 IUnknown.AddRef 4
 * IUnknown.Release 8
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class IUnknown extends PointerType {

    /**
     * The Class ByReference.
     */
    public static class ByReference extends IUnknown implements
                                                         Structure.ByReference {
    }

    /** The Constant IID_IDispatch. */
    public final static IID IID_IDispatch = new IID(
                                                    "{00000000-0000-0000-C000-000000000046}");

    /**
     * Instantiates a new i unknown.
     */
    public IUnknown() {
    }

    /**
     * Instantiates a new i unknown.
     * 
     * @param pvInstance
     *            the pv instance
     */
    public IUnknown(Pointer pvInstance) {
        super(pvInstance);
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
    public HRESULT QueryInterface(IID riid, PointerByReference ppvObject) {
        Pointer base = this.getPointer();
        Pointer vptr = base.getPointer(0);
        Pointer root = vptr.getPointer(0);
        Function func = Function.getFunction(root);
        int hr = func.invokeInt(new Object[] { base, riid, ppvObject });
        return new HRESULT(hr);
    }

    /**
     * Adds the ref.
     * 
     * @return the ulong
     */
    public int AddRef() {
        Pointer vptr = this.getPointer().getPointer(0);
        Function func = Function.getFunction(vptr.getPointer(4));

        return func.invokeInt(new Object[] { this.getPointer() });
    }

    /**
     * Release.
     * 
     * @return the ulong
     */
    public int Release() {
        Pointer vptr = this.getPointer().getPointer(0);
        Function func = Function.getFunction(vptr.getPointer(8));

        return func.invokeInt(new Object[] { this.getPointer() });
    }
}
