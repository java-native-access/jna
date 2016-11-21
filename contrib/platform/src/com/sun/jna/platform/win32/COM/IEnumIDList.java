/*
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

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IEnumIDList {

    /**
     * The interface IID for QueryInterface et al
     */
    public final static IID IID_IEnumIDList = new IID(
            "{000214F2-0000-0000-C000-000000000046}");

    /**
     *
     * Retrieves pointers to the supported interfaces on an object.
     * This method calls IUnknown::AddRef on the pointer it returns.
     *
     * @param riid
     *            The identifier of the interface being requested.
     *
     * @param ppvObject
     *            The address of a pointer variable that receives the interface pointer requested in the riid parameter. Upon successful
     *            return, *ppvObject contains the requested interface pointer to the object. If the object does not support the
     *            interface, *ppvObject is set to NULL.
     *
     * @return
     *            This method returns S_OK if the interface is supported, and E_NOINTERFACE otherwise. If ppvObject is NULL, this method returns E_POINTER.
     *            For any one object, a specific query for the IUnknown interface on any of the object's interfaces must always return the same pointer value.
     *            This enables a client to determine whether two pointers point to the same component by calling QueryInterfacewith IID_IUnknown
     *            and comparing the results. It is specifically not the case that queries for interfaces other than IUnknown (even the same interface
     *            through the same pointer) must return the same pointer value.
     *
     *            There are four requirements for implementations of QueryInterface (In these cases, "must succeed" means "must succeed barring
     *            catastrophic failure."):
     *            The set of interfaces accessible on an object through QueryInterface must be static, not dynamic. This means that if a call
     *            toQueryInterface for a pointer to a specified interface succeeds the first time, it must succeed again, and if it fails
     *            the first time, it must fail on all subsequent queries. 
     *
     *            It must be reflexive: if a client holds a pointer to an interface on an object, and queries for that interface, the call must succeed. 
     *
     *            It must be symmetric: if a client holding a pointer to one interface queries successfully for another, a query through
     *            the obtained pointer for the first interface must succeed. 
     *
     *            It must be transitive: if a client holding a pointer to one interface queries successfully for a second, and through that
     *            pointer queries successfully for a third interface, a query for the first interface through the pointer for the
     *            third interface must succeed. 
     *            Notes to Implementers
     *            Implementations of QueryInterface must never check ACLs. The main reason for this rule is that COM requires that an object supporting a
     *            particular interface always return success when queried for that interface. Another reason is that checking ACLs on QueryInterface
     *            does not provide any real security because any client who has access to a particular interface can hand it directly to another
     *            client without any calls back to the server. Also, because COM caches interface pointers, it does not callQueryInterface on
     *            the server every time a client does a query.
     */
    HRESULT QueryInterface(
            REFIID riid,
            PointerByReference ppvObject);

    /**
     *
     * Increments the reference count for an interface on an object. This method should be called for every new copy of a pointer to an interface on an object.
     * @return
     *            The method returns the new reference count. This value is intended to be used only for test purposes.
     *
     *            Objects use a reference counting mechanism to ensure that the lifetime of the object includes the lifetime of references to it. You use AddRef
     *            to stabilize a copy of an interface pointer. It can also be called when the life of a cloned pointer must extend beyond the
     *            lifetime of the original pointer. The cloned pointer must be released by calling IUnknown::Release.
     *
     *            The internal reference counter that AddRef maintains should be a 32-bit unsigned integer.
     *            Notes to Callers
     *            Call this method for every new copy of an interface pointer that you make. For example, if you are passing a copy of a pointer
     *            back from a method, you must call AddRef on that pointer. You must also call AddRef on a pointer before passing it as an in-out
     *            parameter to a method; the method will call IUnknown::Release before copying the out-value on top of it.
     */
    int AddRef();

    /**
     * Decrements the reference count for an interface on an object.
     *
     * @return
     *            The method returns the new reference count. This value is intended to be used only for test purposes.
     *
     *            When the reference count on an object reaches zero, Release must cause the interface pointer to free itself. When the released
     *            pointer is the only existing reference to an object (whether the object supports single or multiple interfaces), the
     *            implementation must free the object.
     *
     *            Note that aggregation of objects restricts the ability to recover interface pointers.
     *            Notes to Callers
     *            Call this method when you no longer need to use an interface pointer. If you are writing a method that takes an in-out
     *            parameter, call Release on the pointer you are passing in before copying the out-value on top of it.
     */
    int Release();

    /*
    Retrieves the specified number of item identifiers in the enumeration sequence and advances the current position by the number of items retrieved.
     * @param celt
     *            The number of elements in the array referenced by the rgelt parameter.
     * @param rgelt
     *            The address of a pointer to an array of ITEMIDLIST pointers that receive the item identifiers.
     *            The implementation must allocate these item identifiers using CoTaskMemAlloc.
     *            The calling application is responsible for freeing the item identifiers using CoTaskMemFree.
     *            The ITEMIDLIST structures returned in the array are relative to the IShellFolder being enumerated.
     * @param pceltFetched
     *            A pointer to a value that receives a count of the item identifiers actually returned in rgelt.
     *            The count can be smaller than the value specified in the celt parameter. This parameter can be NULL on entry only if celt = 1,
     *            because in that case the method can only retrieve one (S_OK) or zero (S_FALSE) items.
     *
     * @return HRESULT
     *            Returns S_OK if the method successfully retrieved the requested celt elements.
     *            This method only returns S_OK if the full count of requested items are successfully retrieved.
     *            S_FALSE indicates that more items were requested than remained in the enumeration.
     *            The value pointed to by the pceltFetched parameter specifies the actual number of items retrieved.
     *            Note that the value will be 0 if there are no more items to retrieve.
     *            Returns a COM-defined error value otherwise.
     *
     *            If this method returns a Component Object Model (COM) error code (as determined by the COMUtils.FAILED macro),
     *            then no entries in the rgelt array are valid on exit. If this method returns a success code (such as S_OK or S_FALSE),
     *            then the ULONG pointed to by the pceltFetched parameter determines how many entries in the rgelt array are valid on exit.
     *
     *            The distinction is important in the case where celt > 1. For example, if you pass celt=10 and there are only 3 elements left,
     *            *pceltFetched will be 3 and the method will return S_FALSE meaning that you reached the end of the file.
     *            The three fetched elements will be stored into rgelt and are valid.
     */
    HRESULT Next(
            int celt,
            PointerByReference rgelt,
            IntByReference pceltFetched);

    /**
     * Skips the specified number of elements in the enumeration sequence.
     * @param celt
     *            The number of item identifiers to skip.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */
    HRESULT Skip(
            int celt);

    /**
     * Returns to the beginning of the enumeration sequence.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */

    HRESULT Reset();

    /**
     * Creates a new item enumeration object with the same contents and state as the current one.
     * @param ppenum
     *                The address of a pointer to the new enumeration object. The calling application must eventually free the new object by calling its Release member function.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */
    HRESULT Clone(
            PointerByReference ppenum);


    /*
    Use this like:

    PointerByReference pbr=new PointerByReference();
    HRESULT result=SomeCOMObject.QueryInterface(IID_IEnumIDList, pbr);
    if(COMUtils.SUCCEEDED(result)) IENumIDList eil=IEnumIDList.Converter.PointerToIEnumIDList(pbr);

     */
    public static class Converter {
        public static IEnumIDList PointerToIEnumIDList(final PointerByReference ptr) {
            final Pointer interfacePointer = ptr.getValue();
            final Pointer vTablePointer = interfacePointer.getPointer(0);
            final Pointer[] vTable = new Pointer[7];
            vTablePointer.read(0, vTable, 0, 7);
            return new IEnumIDList() {

                @Override
                public WinNT.HRESULT QueryInterface(REFIID byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction(vTable[0], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override
                public int AddRef() {
                    Function f = Function.getFunction(vTable[1], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                public int Release() {
                    Function f = Function.getFunction(vTable[2], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override
                public HRESULT Next(int celt, PointerByReference rgelt, IntByReference pceltFetched) {
                    Function f = Function.getFunction(vTable[3], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, celt, rgelt, pceltFetched}));
                }

                @Override
                public HRESULT Skip(int celt) {
                    Function f = Function.getFunction(vTable[4], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, celt}));
                }

                @Override
                public HRESULT Reset() {
                    Function f = Function.getFunction(vTable[5], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer}));
                }

                @Override
                public HRESULT Clone(PointerByReference ppenum) {
                    Function f = Function.getFunction(vTable[6], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, ppenum}));
                }
            };
        }
    }
}
