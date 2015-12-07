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

    /**
     * Retrieves pointers to the supported interfaces on an object.
     * This method calls IUnknown::AddRef on the pointer it returns.
     *
     * @param riid      The identifier of the interface being requested.
     * @param ppvObject The address of a pointer variable that receives the interface pointer requested in the riid parameter. Upon successful
     *                  return, *ppvObject contains the requested interface pointer to the object. If the object does not support the
     *                  interface, *ppvObject is set to NULL.
     * @return This method returns S_OK if the interface is supported, and E_NOINTERFACE otherwise. If ppvObject is NULL, this method returns E_POINTER.
     * For any one object, a specific query for the IUnknown interface on any of the object's interfaces must always return the same pointer value.
     * This enables a client to determine whether two pointers point to the same component by calling QueryInterfacewith IID_IUnknown
     * and comparing the results. It is specifically not the case that queries for interfaces other than IUnknown (even the same interface
     * through the same pointer) must return the same pointer value.
     * <p/>
     * There are four requirements for implementations of QueryInterface (In these cases, "must succeed" means "must succeed barring
     * catastrophic failure."):
     * The set of interfaces accessible on an object through QueryInterface must be static, not dynamic. This means that if a call
     * toQueryInterface for a pointer to a specified interface succeeds the first time, it must succeed again, and if it fails
     * the first time, it must fail on all subsequent queries.
     * <p/>
     * It must be reflexive: if a client holds a pointer to an interface on an object, and queries for that interface, the call must succeed.
     * <p/>
     * It must be symmetric: if a client holding a pointer to one interface queries successfully for another, a query through
     * the obtained pointer for the first interface must succeed.
     * <p/>
     * It must be transitive: if a client holding a pointer to one interface queries successfully for a second, and through that
     * pointer queries successfully for a third interface, a query for the first interface through the pointer for the
     * third interface must succeed.
     * Notes to Implementers
     * Implementations of QueryInterface must never check ACLs. The main reason for this rule is that COM requires that an object supporting a
     * particular interface always return success when queried for that interface. Another reason is that checking ACLs on QueryInterface
     * does not provide any real security because any client who has access to a particular interface can hand it directly to another
     * client without any calls back to the server. Also, because COM caches interface pointers, it does not callQueryInterface on
     * the server every time a client does a query.
     */
    public HRESULT QueryInterface(REFIID.ByValue riid, PointerByReference ppvObject);

    /**
     * Increments the reference count for an interface on an object. This method should be called for every new copy of a pointer to an interface on an object.
     *
     * @return The method returns the new reference count. This value is intended to be used only for test purposes.
     * <p/>
     * Objects use a reference counting mechanism to ensure that the lifetime of the object includes the lifetime of references to it. You use AddRef
     * to stabilize a copy of an interface pointer. It can also be called when the life of a cloned pointer must extend beyond the
     * lifetime of the original pointer. The cloned pointer must be released by calling IUnknown::Release.
     * <p/>
     * The internal reference counter that AddRef maintains should be a 32-bit unsigned integer.
     * Notes to Callers
     * Call this method for every new copy of an interface pointer that you make. For example, if you are passing a copy of a pointer
     * back from a method, you must call AddRef on that pointer. You must also call AddRef on a pointer before passing it as an in-out
     * parameter to a method; the method will call IUnknown::Release before copying the out-value on top of it.
     */
    public int AddRef();

    /**
     * Decrements the reference count for an interface on an object.
     *
     * @return The method returns the new reference count. This value is intended to be used only for test purposes.
     * <p/>
     * When the reference count on an object reaches zero, Release must cause the interface pointer to free itself. When the released
     * pointer is the only existing reference to an object (whether the object supports single or multiple interfaces), the
     * implementation must free the object.
     * <p/>
     * Note that aggregation of objects restricts the ability to recover interface pointers.
     * Notes to Callers
     * Call this method when you no longer need to use an interface pointer. If you are writing a method that takes an in-out
     * parameter, call Release on the pointer you are passing in before copying the out-value on top of it.
     */
    public int Release();
}