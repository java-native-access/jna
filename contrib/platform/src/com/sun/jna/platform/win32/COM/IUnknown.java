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

import com.sun.jna.platform.win32.Guid.IID;
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
public interface IUnknown {

    /** The Constant IID_IDispatch. */
    public final static IID IID_IUNKNOWN = new IID(
            "{00000000-0000-0000-C000-000000000046}");

    public HRESULT QueryInterface(REFIID riid, PointerByReference ppvObject);

    public int AddRef();

    public int Release();
}
