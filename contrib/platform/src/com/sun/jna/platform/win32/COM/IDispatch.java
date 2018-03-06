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
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the IDispatch interface
 *
 * IDispatch.GetTypeInfoCount 12 IDispatch.GetTypeInfo 16
 * IDispatch.GetIDsOfNames 20 IDispatch.Invoke 24
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface IDispatch extends IUnknown {

    public final static IID IID_IDISPATCH = new IID(
            "00020400-0000-0000-C000-000000000046");

    /**
     * Retrieves the number of type information interfaces that an object provides (either 0 or 1).
     *
     * @param pctinfo The number of type information interfaces provided by the object. If the object provides type information, this number is 1; otherwise the number is 0.
     * @return This method can return one of these values.
     * S_OK
     * Success.
     * E_NOTIMPL
     * Failure.
     */
    public HRESULT GetTypeInfoCount(UINTByReference pctinfo);

    /**
     * Retrieves the type information for an object, which can then be used to get the type information for an interface.
     *
     * @param iTInfo  The type information to return. Pass 0 to retrieve type information for the IDispatch implementation.
     * @param lcid    The locale identifier for the type information.
     *                An object may be able to return different type information for different languages. This is important
     *                for classes that support localized member names. For classes that do not support localized member names,
     *                this parameter can be ignored.
     * @param ppTInfo The requested type information object.
     * @return S_OK
     * Success.
     * DISP_E_BADINDEX
     * The iTInfo parameter was not 0.
     */
    public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid,
            PointerByReference ppTInfo);

    /**
     * Maps a single member and an optional set of argument names to a corresponding set of integer DISPIDs, which can be used
     * on subsequent calls to Invoke. The dispatch function DispGetIDsOfNames provides a standard implementation of GetIDsOfNames.
     *
     * @param riid      Reserved for future use. Must be IID_NULL.
     * @param rgszNames The array of names to be mapped.
     * @param cNames    The count of the names to be mapped.
     * @param lcid      The locale context in which to interpret the names.
     * @param rgDispId  Caller-allocated array, each element of which contains an identifier (ID) corresponding to one of the names passed in
     *                  the rgszNames array. The first element represents the member name. The subsequent elements represent each of the member's parameters.
     * @return status of the operation
     */
    public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames,
            LCID lcid, DISPIDByReference rgDispId);

    /**
     * Provides access to properties and methods exposed by an object. The dispatch function DispInvoke provides a standard implementation of Invoke.
     *
     * @param dispIdMember Identifies the member. Use GetIDsOfNames or the object's documentation to obtain the dispatch identifier.
     * @param riid         Reserved for future use. Must be IID_NULL.
     * @param lcid         The locale context in which to interpret arguments. The lcid is used by the GetIDsOfNames function, and is also
     *                     passed to Invoke to allow the object to interpret its arguments specific to a locale.
     *                     <p/>
     *                     Applications that do not support multiple national languages can ignore this parameter. For more information,
     *                     refer to Supporting Multiple National Languages and Exposing ActiveX Objects.
     * @param wFlags       Flags describing the context of the Invoke call.
     *                     DISPATCH_METHOD
     *                     The member is invoked as a method. If a property has the same name, both this and the DISPATCH_PROPERTYGET flag can be set.
     *                     DISPATCH_PROPERTYGET
     *                     The member is retrieved as a property or data member.
     *                     DISPATCH_PROPERTYPUT
     *                     The member is changed as a property or data member.
     *                     DISPATCH_PROPERTYPUTREF
     *                     The member is changed by a reference assignment, rather than a value assignment. This flag is valid only when the property accepts a reference to an object.
     * @param pDispParams  Pointer to a DISPPARAMS structure containing an array of arguments, an array of argument DISPIDs for named arguments, and counts for the number of elements in the arrays.
     * @param pVarResult   Pointer to the location where the result is to be stored, or NULL if the caller expects no result. This argument is ignored if DISPATCH_PROPERTYPUT or DISPATCH_PROPERTYPUTREF is specified.
     * @param pExcepInfo   Pointer to a structure that contains exception information. This structure should be filled in if DISP_E_EXCEPTION is returned. Can be NULL.
     * @param puArgErr     The index within rgvarg of the first argument that has an error. Arguments are stored in pDispParams-&gt;rgvarg in reverse order,
     *                     so the first argument is the one with the highest index in the array. This parameter is returned only when the resulting return
     *                     value is DISP_E_TYPEMISMATCH or DISP_E_PARAMNOTFOUND. This argument can be set to null. For details, see Returning Errors.
     * @return This method can return one of these values.
     * S_OK
     * Success.
     * DISP_E_BADPARAMCOUNT
     * The number of elements provided to DISPPARAMS is different from the number of arguments accepted by the method or property.
     * DISP_E_BADVARTYPE
     * One of the arguments in DISPPARAMS is not a valid variant type.
     * DISP_E_EXCEPTION
     * The application needs to raise an exception. In this case, the structure passed in pexcepinfo should be filled in.
     * DISP_E_MEMBERNOTFOUND
     * The requested member does not exist.
     * DISP_E_NONAMEDARGS
     * This implementation of IDispatch does not support named arguments.
     * DISP_E_OVERFLOW
     * One of the arguments in DISPPARAMS could not be coerced to the specified type.
     * DISP_E_PARAMNOTFOUND
     * One of the parameter IDs does not correspond to a parameter on the method. In this case, puArgErr is set to the first argument that contains the error.
     * DISP_E_TYPEMISMATCH
     * One or more of the arguments could not be coerced. The index of the first parameter with the incorrect type within rgvarg is returned in puArgErr.
     * DISP_E_UNKNOWNINTERFACE
     * The interface identifier passed in riid is not IID_NULL.
     * DISP_E_UNKNOWNLCID
     * The member being invoked interprets string arguments according to the LCID, and the LCID is not recognized. If the LCID is not needed to interpret arguments, this error should not be returned
     * DISP_E_PARAMNOTOPTIONAL
     * A required parameter was omitted.
     */
    public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid,
            WORD wFlags, DISPPARAMS.ByReference pDispParams,
            VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
            IntByReference puArgErr);
}
