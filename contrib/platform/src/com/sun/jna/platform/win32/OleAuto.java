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
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAYBOUND;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VariantArg;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WTypes.VARTYPEByReference;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Oleaut32.dll Interface.
 *
 * @author scott.palmer
 */
public interface OleAuto extends StdCallLibrary {
	/** The instance. */
	OleAuto INSTANCE = Native.loadLibrary("OleAut32", OleAuto.class, W32APIOptions.DEFAULT_OPTIONS);

	/* Flags for IDispatch::Invoke */
	/** The Constant DISPATCH_METHOD. */
	int DISPATCH_METHOD = 0x1;

	/** The Constant DISPATCH_PROPERTYGET. */
	int DISPATCH_PROPERTYGET = 0x2;

	/** The Constant DISPATCH_PROPERTYPUT. */
	int DISPATCH_PROPERTYPUT = 0x4;

	/** The Constant DISPATCH_PROPERTYPUTREF. */
	int DISPATCH_PROPERTYPUTREF = 0x8;

	/** An array that is allocated on the stac. */
	int FADF_AUTO = 0x0001;

	/** An array that is statically allocated. */
	int FADF_STATIC = 0x0002;

	/** An array that is embedded in a structure. */
	int FADF_EMBEDDED = 0x0004;

	/** An array that is embedded in a structure. */
	int FADF_FIXEDSIZE = 0x0010;

	/** An array that is embedded in a structure. */
	int FADF_RECORD = 0x0020;

	/** An array that is embedded in a structure. */
	int FADF_HAVEIID = 0x0040;

	/**
	 * An array that has a variant type. The variant type can be retrieved with
	 * SafeArrayGetVartype.
	 */
	int FADF_HAVEVARTYPE = 0x0080;

	/** An array of BSTRs. */
	int FADF_BSTR = 0x0100;

	/** An array of IUnknown*. */
	int FADF_UNKNOWN = 0x0200;

	/** An array of IDispatch*. */
	int FADF_DISPATCH = 0x0400;

	/** An array of VARIANTs. */
	int FADF_VARIANT = 0x0800;

	/** Bits reserved for future use. */
	int FADF_RESERVED = 0xF008;

	/**
	 * This function allocates a new string and copies the passed string into
	 * it.
	 *
	 * @param sz
	 *            Null-terminated UNICODE string to copy.
	 * @return Null if there is insufficient memory or if a null pointer is
	 *         passed in.
	 */
	BSTR SysAllocString(String sz);

	/**
	 * This function frees a string allocated previously by SysAllocString,
	 * SysAllocStringByteLen, SysReAllocString, SysAllocStringLen, or
	 * SysReAllocStringLen.
	 *
	 * @param bstr
	 *            Unicode string that was allocated previously, or NULL. Setting
	 *            this parameter to NULL causes the function to simply return.
	 */
	void SysFreeString(BSTR bstr);

	/**
	 * Returns the length (in bytes) of a BSTR.
	 *
	 * @param bstr
	 *            Unicode string that was allocated previously.
	 */
	int SysStringByteLen(BSTR bstr);
        
	/**
	 * Returns the length of a BSTR.
	 *
	 * @param bstr
	 *            Unicode string that was allocated previously.
	 */
	int SysStringLen(BSTR bstr);
        
	/**
	 * The VariantInit function initializes the VARIANTARG by setting the vt
	 * field to VT_EMPTY. Unlike VariantClear, this function does not interpret
	 * the current contents of the VARIANTARG. Use VariantInit to initialize new
	 * local variables of type VARIANTARG (or VARIANT).
	 *
	 * @param pvarg
	 *            The variant to initialize.
	 */
	void VariantInit(VARIANT.ByReference pvarg);

	/**
	 * The VariantInit function initializes the VARIANTARG by setting the vt
	 * field to VT_EMPTY. Unlike VariantClear, this function does not interpret
	 * the current contents of the VARIANTARG. Use VariantInit to initialize new
	 * local variables of type VARIANTARG (or VARIANT).
	 *
	 * @param pvarg
	 *            The variant to initialize.
	 */
	void VariantInit(VARIANT pvarg);

	/**
	 * First, free any memory that is owned by pvargDest, such as VariantClear
	 * (pvargDest must point to a valid initialized variant, and not simply to
	 * an uninitialized memory location). Then pvargDest receives an exact copy
	 * of the contents of pvargSrc.
	 *
	 * If pvargSrc is a VT_BSTR, a copy of the string is made. If pvargSrcis a
	 * VT_ARRAY, the entire array is copied. If pvargSrc is a VT_DISPATCH or
	 * VT_UNKNOWN, AddRef is called to increment the object's reference count.
	 *
	 * If the variant to be copied is a COM object that is passed by reference,
	 * the vtfield of the pvargSrcparameter is VT_DISPATCH | VT_BYREF or
	 * VT_UNKNOWN | VT_BYREF. In this case, VariantCopy does not increment the
	 * reference count on the referenced object. Because the variant being
	 * copied is a pointer to a reference to an object, VariantCopy has no way
	 * to determine if it is necessary to increment the reference count of the
	 * object. It is therefore the responsibility of the caller to call
	 * IUnknown::AddRef on the object or not, as appropriate.
	 *
	 * Note The VariantCopy method is not threadsafe.
	 *
	 * @param pvargDest
	 *            [out] The destination variant.
	 * @param pvargSrc
	 *            [in] The source variant.
	 * @return the hresult
	 */
	HRESULT VariantCopy(Pointer pvargDest, VARIANT pvargSrc);

	/**
	 * Use this function to clear variables of type VARIANTARG (or VARIANT)
	 * before the memory containing the VARIANTARG is freed (as when a local
	 * variable goes out of scope).
	 *
	 * The function clears a VARIANTARG by setting the vt field to VT_EMPTY. The
	 * current contents of the VARIANTARG are released first. If the vtfield is
	 * VT_BSTR, the string is freed. If the vtfield is VT_DISPATCH, the object
	 * is released. If the vt field has the VT_ARRAY bit set, the array is
	 * freed.
	 *
	 * If the variant to be cleared is a COM object that is passed by reference,
	 * the vtfield of the pvargparameter is VT_DISPATCH | VT_BYREF or VT_UNKNOWN
	 * | VT_BYREF. In this case, VariantClear does not release the object.
	 * Because the variant being cleared is a pointer to a reference to an
	 * object, VariantClear has no way to determine if it is necessary to
	 * release the object. It is therefore the responsibility of the caller to
	 * release the object or not, as appropriate.
	 *
	 * In certain cases, it may be preferable to clear a variant in code without
	 * calling VariantClear. For example, you can change the type of a VT_I4
	 * variant to another type without calling this function. Safearrays of BSTR
	 * will have SysFreeString called on each element not VariantClear. However,
	 * you must call VariantClear if a VT_type is received but cannot be
	 * handled. Safearrays of variant will also have VariantClear called on each
	 * member. Using VariantClear in these cases ensures that code will continue
	 * to work if Automation adds new variant types in the future.
	 *
	 * Do not use VariantClear on unitialized variants; use VariantInit to
	 * initialize a new VARIANTARG or VARIANT.
	 *
	 * Variants containing arrays with outstanding references cannot be cleared.
	 * Attempts to do so will return an HRESULT containing DISP_E_ARRAYISLOCKED.
	 *
	 * @param pvarg
	 *            [in, out] The variant to clear.
	 * @return the hresult
	 */
	HRESULT VariantClear(VARIANT pvarg);
        
	public static final short VARIANT_NOVALUEPROP        = 0x01;
	/** For VT_BOOL to VT_BSTR conversions, convert to "True"/"False" instead of "-1"/"0" */
	public static final short VARIANT_ALPHABOOL          = 0x02;
	/** For conversions to/from VT_BSTR, passes LOCALE_NOUSEROVERRIDE to core coercion routines */
	public static final short VARIANT_NOUSEROVERRIDE     = 0x04;
	public static final short VARIANT_CALENDAR_HIJRI     = 0x08;
	/** For VT_BOOL to VT_BSTR and back, convert to local language rather than English */
	public static final short VARIANT_LOCALBOOL          = 0x10;
	/** SOUTHASIA calendar support */
	public static final short VARIANT_CALENDAR_THAI      = 0x20;
	/** SOUTHASIA calendar support */
	public static final short VARIANT_CALENDAR_GREGORIAN = 0x40;
	/** NLS function call support */
	public static final short VARIANT_USE_NLS            = 0x80;

	/**
	 * Converts a variant from one type to another.
	 * @param pvargDest [out] The destination variant. If this is the same as
	 *                      pvarSrc, the variant will be converted in place.
	 * @param pvarSrc [in] The variant to convert.
	 * @param wFlags Combination of the following flags
	 * <table>
	 *     <thead>
	 *         <tr><th><!--indent under wFlags comment--><div style="visibility: hidden">wFlags</div></th><th>Value</th><th>Meaning</th></tr>
	 *     </thead>
	 *     <tbody valign="top">
	 *         <tr><th></th><td>{@link #VARIANT_NOVALUEPROP}</td><td>Prevents the function from attempting to coerce an object to a fundamental type by getting the Value property. Applications should set this flag only if necessary, because it makes their behavior inconsistent with other applications.</td></tr>
	 *         <tr><th></th><td>{@link #VARIANT_ALPHABOOL}</td><td>Converts a {@link Variant#VT_BOOL VT_BOOL} value to a string containing either "True" or "False".</td></tr>
	 *         <tr><th></th><td>{@link #VARIANT_NOUSEROVERRIDE}</td><td>For conversions to or from {@link Variant#VT_BSTR VT_BSTR}, passes LOCALE_NOUSEROVERRIDE to the core coercion routines.</td></tr>
	 *         <tr><th></th><td>{@link #VARIANT_LOCALBOOL}</td><td>For conversions from {@link Variant#VT_BOOL VT_BOOL} to {@link Variant#VT_BSTR VT_BSTR} and back, uses the language specified by the locale in use on the local computer.</td></tr>
	 *     </tbody>
     * </table>
     * @param vt The type to convert to. If the return code is {@link WinError#S_OK S_OK}, the vt
	 *           field of the vargDest is guaranteed to be equal to this value.
	 * @return This function can return one of these values:
	 * <table>
	 *     <thead>
	 *         <tr><th>Return code</th><th>Description</th></tr>
	 *     </thead>
	 *     <tbody valign="top">
	 *         <tr><td>{@link WinError#S_OK S_OK}</td><td>Success.</td></tr>
	 *         <tr><td>{@link WinError#DISP_E_BADVARTYPE DISP_E_BADVARTYPE}</td><td>The variant type is not a valid type of variant.</td></tr>
	 *         <tr><td>{@link WinError#DISP_E_OVERFLOW DISP_E_OVERFLOW}</td><td>The data pointed to by pvarSrc does not fit in the destination type.</td></tr>
	 *         <tr><td>{@link WinError#DISP_E_TYPEMISMATCH DISP_E_TYPEMISMATCH}</td><td>The argument could not be coerced to the specified type.</td></tr>
	 *         <tr><td>{@link WinError#E_INVALIDARG E_INVALIDARG}</td><td>One of the arguments is not valid.</td></tr>
	 *         <tr><td>{@link WinError#E_OUTOFMEMORY E_OUTOFMEMORY}</td><td>Insufficient memory to complete the operation.</td></tr>
	 *     </tbody>
     * </table>
	 *</p>
	 * <b>Remarks</b>
	 *</p>
	 * The VariantChangeType function handles coercions between the fundamental
	 * types (including numeric-to-string and string-to-numeric coercions). The
	 * pvarSrc argument is changed during the conversion process. For example,
	 * if the source variant is of type {@link Variant#VT_BOOL VT_BOOL} and the
	 * destination is of type {@link Variant#VT_UINT VT_UINT}, the pvarSrc
	 * argument is first converted to {@link Variant#VT_I2 VT_I2} and then the
	 * conversion proceeds. A variant that has {@link Variant#VT_BYREF VT_BYREF}
	 * set is coerced to a value by obtaining the referenced value. An object is
	 * coerced to a value by invoking the object's Value property
	 * ({@link OaIdl#DISPID_VALUE DISPID_VALUE}).
	 *</p>
	 * Typically, the implementor of
	 * {@link com.sun.jna.platform.win32.COM.IDispatch#Invoke IDispatch.Invoke}
	 * determines which member is being accessed, and then calls
	 * VariantChangeType to get the value of one or more arguments. For example,
	 * if the IDispatch call specifies a SetTitle member that takes one string
	 * argument, the implementor would call VariantChangeType to attempt to
	 * coerce the argument to {@link Variant#VT_BSTR VT_BSTR}. If
	 * VariantChangeType does not return an error, the argument could then be
	 * obtained directly from the
	 * {@link Variant.VARIANT._VARIANT.__VARIANT#bstrVal bstrVal} field of the
	 * {@link Variant.VARIANT VARIANT}. If VariantChangeType returns
	 * {@link WinError#DISP_E_TYPEMISMATCH DISP_E_TYPEMISMATCH}, the implementor
	 * would set {@link com.sun.jna.platform.win32.COM.IDispatch#Invoke Invoke}
	 * <code> puArgErr</code> parameter referenced value to 0 (indicating the
	 * argument in error) and return DISP_E_TYPEMISMATCH from Invoke.
	 *</p>
	 * Arrays of one type cannot be converted to arrays of another type with
	 * this function.
	 *</p>
	 * <b>Note</b> The type of a {@link Variant.VARIANT VARIANT} should not be
	 * changed in the {@link DISPPARAMS#rgvarg rgvarg} array in place.
	 */
	HRESULT VariantChangeType(VARIANT pvargDest, VARIANT pvarSrc, short wFlags, VARTYPE vt);

    /**
     * Converts a variant from one type to another.
     * @param pvargDest [out] The destination variant. If this is the same as
     *                      pvarSrc, the variant will be converted in place.
     * @param pvarSrc [in] The variant to convert.
     * @param wFlags Combination of the following flags
     * <table>
     *     <thead>
     *         <tr><th><!--indent under wFlags comment--><div style="visibility: hidden">wFlags</div></th><th>Value</th><th>Meaning</th></tr>
     *     </thead>
     *     <tbody valign="top">
     *         <tr><th></th><td>{@link #VARIANT_NOVALUEPROP}</td><td>Prevents the function from attempting to coerce an object to a fundamental type by getting the Value property. Applications should set this flag only if necessary, because it makes their behavior inconsistent with other applications.</td></tr>
     *         <tr><th></th><td>{@link #VARIANT_ALPHABOOL}</td><td>Converts a {@link Variant#VT_BOOL VT_BOOL} value to a string containing either "True" or "False".</td></tr>
     *         <tr><th></th><td>{@link #VARIANT_NOUSEROVERRIDE}</td><td>For conversions to or from {@link Variant#VT_BSTR VT_BSTR}, passes LOCALE_NOUSEROVERRIDE to the core coercion routines.</td></tr>
     *         <tr><th></th><td>{@link #VARIANT_LOCALBOOL}</td><td>For conversions from {@link Variant#VT_BOOL VT_BOOL} to {@link Variant#VT_BSTR VT_BSTR} and back, uses the language specified by the locale in use on the local computer.</td></tr>
     *     </tbody>
     * </table>
     * @param vt The type to convert to. If the return code is {@link WinError#S_OK S_OK}, the vt
     *           field of the vargDest is guaranteed to be equal to this value.
     * @return This function can return one of these values:
     * <table>
     *     <thead>
     *         <tr><th>Return code</th><th>Description</th></tr>
     *     </thead>
     *     <tbody valign="top">
     *         <tr><td>{@link WinError#S_OK S_OK}</td><td>Success.</td></tr>
     *         <tr><td>{@link WinError#DISP_E_BADVARTYPE DISP_E_BADVARTYPE}</td><td>The variant type is not a valid type of variant.</td></tr>
     *         <tr><td>{@link WinError#DISP_E_OVERFLOW DISP_E_OVERFLOW}</td><td>The data pointed to by pvarSrc does not fit in the destination type.</td></tr>
     *         <tr><td>{@link WinError#DISP_E_TYPEMISMATCH DISP_E_TYPEMISMATCH}</td><td>The argument could not be coerced to the specified type.</td></tr>
     *         <tr><td>{@link WinError#E_INVALIDARG E_INVALIDARG}</td><td>One of the arguments is not valid.</td></tr>
     *         <tr><td>{@link WinError#E_OUTOFMEMORY E_OUTOFMEMORY}</td><td>Insufficient memory to complete the operation.</td></tr>
     *     </tbody>
     * </table>
     *</p>
     * <b>Remarks</b>
     *</p>
     * The VariantChangeType function handles coercions between the fundamental
     * types (including numeric-to-string and string-to-numeric coercions). The
     * pvarSrc argument is changed during the conversion process. For example,
     * if the source variant is of type {@link Variant#VT_BOOL VT_BOOL} and the
     * destination is of type {@link Variant#VT_UINT VT_UINT}, the pvarSrc
     * argument is first converted to {@link Variant#VT_I2 VT_I2} and then the
     * conversion proceeds. A variant that has {@link Variant#VT_BYREF VT_BYREF}
     * set is coerced to a value by obtaining the referenced value. An object is
     * coerced to a value by invoking the object's Value property
     * ({@link OaIdl#DISPID_VALUE DISPID_VALUE}).
     *</p>
     * Typically, the implementor of
     * {@link com.sun.jna.platform.win32.COM.IDispatch#Invoke IDispatch.Invoke}
     * determines which member is being accessed, and then calls
     * VariantChangeType to get the value of one or more arguments. For example,
     * if the IDispatch call specifies a SetTitle member that takes one string
     * argument, the implementor would call VariantChangeType to attempt to
     * coerce the argument to {@link Variant#VT_BSTR VT_BSTR}. If
     * VariantChangeType does not return an error, the argument could then be
     * obtained directly from the
     * {@link Variant.VARIANT._VARIANT.__VARIANT#bstrVal bstrVal} field of the
     * {@link Variant.VARIANT VARIANT}. If VariantChangeType returns
     * {@link WinError#DISP_E_TYPEMISMATCH DISP_E_TYPEMISMATCH}, the implementor
     * would set {@link com.sun.jna.platform.win32.COM.IDispatch#Invoke Invoke}
     * <code> puArgErr</code> parameter referenced value to 0 (indicating the
     * argument in error) and return DISP_E_TYPEMISMATCH from Invoke.
     *</p>
     * Arrays of one type cannot be converted to arrays of another type with
     * this function.
     *</p>
     * <b>Note</b> The type of a {@link Variant.VARIANT VARIANT} should not be
     * changed in the {@link DISPPARAMS#rgvarg rgvarg} array in place.
     */
    HRESULT VariantChangeType(VARIANT.ByReference pvargDest, VARIANT.ByReference pvarSrc, short wFlags, VARTYPE vt);


	/**
	 * Creates a new array descriptor, allocates and initializes the data for
	 * the array, and returns a pointer to the new array descriptor.
	 *
	 * @param vt
	 *            [in] The base type of the array (the VARTYPE of each element
	 *            of the array). The VARTYPE is restricted to a subset of the
	 *            variant types. Neither the VT_ARRAY nor the VT_BYREF flag can
	 *            be set. VT_EMPTY and VT_NULL are not valid base types for the
	 *            array. All other types are legal. cDims
	 *
	 * @param cDims
	 *            the number of dims
	 * @param rgsabound
	 *            the rgsabound
	 *
	 * @return Return value
	 *
	 *         A safe array descriptor, or null if the array could not be
	 *         created.
	 */
	SAFEARRAY.ByReference SafeArrayCreate(VARTYPE vt, UINT cDims,
			SAFEARRAYBOUND[] rgsabound);

	/**
	 * Stores the data element at the specified location in the array.
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param idx
	 *            the idx
	 * @param pv
	 *            [in] The data to assign to the array. The variant types
	 *            VT_DISPATCH, VT_UNKNOWN, and VT_BSTR are pointers, and do not
	 *            require another level of indirection.
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
         *         <dl>
	 *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>DISP_E_BADINDEX</dt><dd>The specified index is not valid.</dd>
         *             <dt>E_INVALIDARG</dt><dd>One of the arguments is not valid.</dd>
         *             <dt>E_OUTOFMEMORY</dt><dd>Memory could not be allocated for the element.</dd>
         *         </dl>
	 */
	HRESULT SafeArrayPutElement(SAFEARRAY psa, LONG[] idx, Pointer pv);

	/**
	 * Retrieve the upper bound for the specified dimension of the supplied array
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param nDim
	 *            [in] the dimension, one based
	 * @param bound
	 *            [out] upper bound for the supplied dimension
         * 
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>DISP_E_BADINDEX</dt><dd>The specified index is not valid.</dd>
         *             <dt>E_INVALIDARG</dt><dd>One of the arguments is not valid.</dd>
         *         </dl>
	 */
        HRESULT SafeArrayGetUBound(SAFEARRAY psa, UINT nDim, WinDef.LONGByReference bound);
        
	/**
	 * Retrieve the lower bound for the specified dimension of the supplied array
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param nDim
	 *            [in] the dimension, one based
	 * @param bound
	 *            [out] lower bound for the supplied dimension
         * 
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>DISP_E_BADINDEX</dt><dd>The specified index is not valid.</dd>
         *             <dt>E_INVALIDARG</dt><dd>One of the arguments is not valid.</dd>
         *         </dl>
	 */
        HRESULT SafeArrayGetLBound(SAFEARRAY psa, UINT nDim, WinDef.LONGByReference bound);
        
        
	/**
	 * Retrieves a single element of the array.
	 *
         * The array is automaticly locked via SafeArrayLock and SafeArrayUnlock.
         * 
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param rgIndices
	 *            [in] A vector of indexes for each dimension of the array. The
	 *            right-most (least significant) dimension is rgIndices[0]. The
	 *            left-most dimension is stored at rgIndices[psa-&gt;cDims - 1].
	 * @param pv
	 *            [out] The element of the array.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
         *         <dl>
	 *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>DISP_E_BADINDEX</dt><dd>The specified index is not valid.</dd>
         *             <dt>E_INVALIDARG</dt><dd>One of the arguments is not valid.</dd>
         *             <dt>E_OUTOFMEMORY</dt><dd>Memory could not be allocated for the element.</dd>
         *         </dl>
	 */
	HRESULT SafeArrayGetElement(SAFEARRAY psa, LONG[] rgIndices, Pointer pv);

	/**
	 * Retrieves the pointer to a single element of the array.
         * 
         * <p>The caller is responsible for locking.</p>
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param rgIndices
	 *            [in] A vector of indexes for each dimension of the array. The
	 *            right-most (least significant) dimension is rgIndices[0]. The
	 *            left-most dimension is stored at rgIndices[psa-&gt;cDims - 1].
	 * @param ppv
	 *            [out] The element of the array.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>DISP_E_BADINDEX</dt><dd>The specified index is not valid.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>One of the arguments is not valid.</dd>
         *         </dl>
	 */
        HRESULT SafeArrayPtrOfIndex(SAFEARRAY psa, LONG[] rgIndices, PointerByReference ppv);
        
	/**
	 * Increments the lock count of an array, and places a pointer to the array
	 * data in pvData of the array descriptor.
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *             <dt>E_UNEXPECTED</dt><dd>The array could not be locked.</dd>
         *         </dl>
	 */
	HRESULT SafeArrayLock(SAFEARRAY psa);

	/**
	 * Decrements the lock count of an array so it can be freed or resized.
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *             <dt>S_OK</dt><dd>Success.</dd>
         *             <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *             <dt>E_UNEXPECTED</dt><dd>The array could not be locked.</dd>
         *         </dl>
	 */
	HRESULT SafeArrayUnlock(SAFEARRAY psa);

	/**
	 * Destroys an existing array descriptor and all of the data in the array.
         * If objects are stored in the array, Release is called on each object 
         * in the array.
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *              <dt>DISP_E_ARRAYISLOCKED</dt><dd>The array could not be locked.</dd>
         *         </dl>  
	 */
        HRESULT SafeArrayDestroy(SAFEARRAY psa);
        
	/**
	 * Changes the right-most (least significant) bound of the specified safe array.
	 *
	 * @param psa
	 *            [in, out] An array descriptor created by SafeArrayCreate.
	 * @param psaboundNew
	 *            [in] New bounds for the least significant dimension
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *              <dt>DISP_E_ARRAYISLOCKED</dt><dd>The array could not be locked.</dd>
         *         </dl>  
	 */
        HRESULT SafeArrayRedim(SAFEARRAY psa, SAFEARRAYBOUND psaboundNew);
        
	/**
	 * Return VARTYPE of the SAFEARRAY
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param pvt
	 *            [in] Vartype of the SAFEARRAY
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *         </dl>  
	 */
        HRESULT SafeArrayGetVartype(SAFEARRAY psa, VARTYPEByReference pvt);
        
	/**
	 * Return number of dimensions of the SAFEARRAY
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
         * 
	 * @return Return count of dimensions
	 */
        UINT SafeArrayGetDim(SAFEARRAY psa);
        
	/**
	 * Lock array and retrieve pointer to data
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 * @param ppvData
	 *            [in] pointer to the data array
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *              <dt>E_UNEXPECTED</dt><dd>The array could not be locked.</dd>
         *         </dl>  
	 */
        HRESULT SafeArrayAccessData(SAFEARRAY psa, PointerByReference ppvData);
        
	/**
	 * Unlock array and invalidate the pointer retrieved via SafeArrayAccessData
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 *
	 * @return Return value
	 *
	 *         This function can return one of these values.
	 *
	 *         <dl>
         *              <dt>S_OK</dt><dd>Success.</dd>
	 *              <dt>E_INVALIDARG</dt><dd>The argument psa is not valid.</dd>
         *              <dt>E_UNEXPECTED</dt><dd>The array could not be locked.</dd>
         *         </dl>  
	 */
        HRESULT SafeArrayUnaccessData(SAFEARRAY psa);
        
	/**
	 * Get size of one element in bytes
	 *
	 * @param psa
	 *            [in] An array descriptor created by SafeArrayCreate.
	 *
	 * @return size in bytes
	 */
        UINT SafeArrayGetElemsize(SAFEARRAY psa);
        
	/**
	 * Retrieves a pointer to a running object that has been registered with
	 * OLE.
	 *
	 * @param rclsid
	 *            [in] The class identifier (CLSID) of the active object from
	 *            the OLE registration database.
	 * @param pvReserved
	 *            Reserved for future use. Must be null.
	 * @param ppunk
	 *            [out] The requested active object.
	 *
	 * @return Return value
	 *
	 *         If this function succeeds, it returns S_OK. Otherwise, it returns
	 *         an HRESULT error code.
	 */
	HRESULT GetActiveObject(GUID rclsid, PVOID pvReserved, PointerByReference ppunk);

	/**
	 * The Class DISPPARAMS.
	 */
	public class DISPPARAMS extends Structure {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends DISPPARAMS implements
				Structure.ByReference {
		}
		public static final List<String> FIELDS = createFieldsOrder("rgvarg", "rgdispidNamedArgs", "cArgs", "cNamedArgs");

		/** The rgvarg. */
		public VariantArg.ByReference rgvarg;

                /** The rgdispid named args. */
                public Pointer rgdispidNamedArgs = Pointer.NULL;

		/** The c args. - use setArgs to update arguments */
		public UINT cArgs = new UINT(0);

		/** The c named args. - use setRgdispidNamedArgs to update named arguments map */
		public UINT cNamedArgs = new UINT(0);

                public DISPID[] getRgdispidNamedArgs() {
                        DISPID[] namedArgs = null;
                        int count = cNamedArgs.intValue();
                        if(rgdispidNamedArgs != null && count > 0) {
                            int[] rawData = rgdispidNamedArgs.getIntArray(0, count);
                            namedArgs = new DISPID[count];
                            for(int i = 0; i < count; i++) {
                                namedArgs[i] = new DISPID(rawData[i]);
                            }
                        } else {
                            namedArgs = new DISPID[0];
                        }
                        return namedArgs;
                }
                
                public void setRgdispidNamedArgs(DISPID[] namedArgs) {
                        if(namedArgs == null) {
                            namedArgs = new DISPID[0];
                        }
                        cNamedArgs = new UINT(namedArgs.length);
                        rgdispidNamedArgs = new Memory(DISPID.SIZE * namedArgs.length);
                        int[] rawData = new int[namedArgs.length];
                        for(int i = 0; i < rawData.length; i++) {
                            rawData[i] = namedArgs[i].intValue();
                        }
                        rgdispidNamedArgs.write(0, rawData, 0, namedArgs.length);
                }
                
                public VARIANT[] getArgs() {
                        if(this.rgvarg != null) {
                            this.rgvarg.setArraySize(cArgs.intValue());
                            return this.rgvarg.variantArg;
                        } else {
                            return new VARIANT[0];
                        }
                }
                
                public void setArgs(VARIANT[] arguments) {
                        if(arguments == null) {
                            arguments = new VARIANT[0];
                        }
                        
                        rgvarg = new VariantArg.ByReference(arguments);
                        cArgs = new UINT(arguments.length);
                }
                
		/**
		 * Instantiates a new dispparams.
		 */
		public DISPPARAMS() {
			super();
		}

		/**
		 * Instantiates a new dispparams.
		 *
		 * @param memory
		 *            the memory
		 */
		public DISPPARAMS(Pointer memory) {
			super(memory);
			this.read();
		}

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	/**
	 * Uses registry information to load a type library.
	 *
	 * @param rguid
	 *            The GUID of the library.
	 * @param wVerMajor
	 *            The major version of the library.
	 * @param wVerMinor
	 *            The minor version of the library.
	 * @param lcid
	 *            The national language code of the library.
	 * @param pptlib
	 *            The loaded type library.
	 *
	 *            This function can return one of these values: S_OK Success.
	 *
	 *            E_INVALIDARG One or more of the arguments is not valid.
	 *
	 *            E_OUTOFMEMORY Insufficient memory to complete the operation.
	 *
	 *            TYPE_E_IOERROR The function could not write to the file.
	 *
	 *            TYPE_E_INVALIDSTATE The type library could not be opened.
	 *
	 *            TYPE_E_INVDATAREAD The function could not read from the file.
	 *
	 *            TYPE_E_UNSUPFORMAT The type library has an older format.
	 *
	 *            TYPE_E_UNKNOWNLCID The LCID could not be found in the
	 *            OLE-supported DLLs.
	 *
	 *            TYPE_E_CANTLOADLIBRARY The type library or DLL could not be
	 *            loaded.
     * @return status
	 */
	HRESULT LoadRegTypeLib(GUID rguid, int wVerMajor, int wVerMinor, LCID lcid, PointerByReference pptlib);

	/**
	 * Loads and registers a type library.
	 *
	 * @param szFile
	 *            The name of the file from which the method should attempt to
	 *            load a type library.
	 *
	 * @param pptlib
	 *            The loaded type library. Return value
	 *
	 *            This function can return one of these values.
	 *
	 *            S_OK Success.
	 *
	 *            E_INVALIDARG One or more of the arguments is not valid.
	 *
	 *            E_OUTOFMEMORY Insufficient memory to complete the operation.
	 *
	 *            TYPE_E_IOERROR The function could not write to the file.
	 *
	 *            TYPE_E_INVALIDSTATE The type library could not be opened.
	 *
	 *            TYPE_E_INVDATAREAD The function could not read from the file.
	 *
	 *            TYPE_E_UNSUPFORMAT The type library has an older format.
	 *
	 *            TYPE_E_UNKNOWNLCID The LCID could not be found in the
	 *            OLE-supported DLLs.
	 *
	 *            TYPE_E_CANTLOADLIBRARY The type library or DLL could not be
	 *            loaded.
         * @return status
	 */
	HRESULT LoadTypeLib(String szFile, PointerByReference pptlib);

	/**
	 * Converts a system time to a variant representation.
	 *
	 * @param lpSystemTime
	 *            [in] The system time.
	 *
	 * @param pvtime
	 *            [out] The variant time.
	 *
	 * @return The function returns TRUE on success and FALSE otherwise.
	 */
	int SystemTimeToVariantTime(SYSTEMTIME lpSystemTime, DoubleByReference pvtime);
}
