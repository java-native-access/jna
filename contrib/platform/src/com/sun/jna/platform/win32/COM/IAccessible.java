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

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.LongByReference;

/**
 * IAccessible Interface
 *
 * Exposes methods and properties that make a user interface element and its children accessible to client applications.
 * The IAccessible interface inherits from the IDispatch interface.
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nn-oleacc-iaccessible">IAccessible interface (oleacc.h)</a>
 * @author Mo Beigi, me@mobeigi.com
 */
public interface IAccessible extends IDispatch
{
    /**
     * The GUID associated with the IAccessible interface
     */
    IID IID_IACCESSIBLE = new IID("618736E0-3C3D-11CF-810C-00AA00389B71");

    /**
     * The IAccessible::get_accName method retrieves the name of the specified object. All objects support this property.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accname">IAccessible::get_accName method (oleacc.h)</a>
     *
     * @param varChild [in] Specifies whether the retrieved name belongs to the object or one of the object's child elements.
     *                 This parameter is either CHILDID_SELF (to obtain information about the object) or a child ID
     *                 (to obtain information about the object's child element).
     * @param pszName [out] Address of a BSTR that receives a string that contains the specified object's name.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT get_accName(VARIANT varChild, BSTRByReference pszName);

    /**
     * The IAccessible::get_accValue method retrieves the value of the specified object. Not all objects have a value.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accvalue">IAccessible::get_accValue method (oleacc.h)</a>
     *
     * @param varChild [in] Specifies whether the retrieved name belongs to the object or one of the object's child elements.
     *                 This parameter is either CHILDID_SELF (to obtain information about the object) or a child ID
     *                 (to obtain information about the object's child element).
     * @param pszValue [out] Address of the BSTR that receives a localized string that contains the object's current value.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT get_accValue(VARIANT varChild, BSTRByReference pszValue);

    /**
     * The IAccessible::get_accRole method retrieves information that describes the role of the specified object. All objects support this property.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accrole">IAccessible::get_accRole method (oleacc.h)</a>
     *
     * @param varChild [in] Specifies whether the retrieved name belongs to the object or one of the object's child elements.
     *                 This parameter is either CHILDID_SELF (to obtain information about the object) or a child ID
     *                 (to obtain information about the object's child element).
     * @param pvarRole [out] Address of a VARIANT that receives an object role constant.
     *                 The vt member must be VT_I4. The lVal member receives an object role constant.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT get_accRole(VARIANT varChild, VARIANT.ByReference pvarRole);

    /**
     * The IAccessible::get_accChildCount method retrieves the number of children that belong to this object. All objects must support this property.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accchildcount">IAccessible::get_accChildCount method (oleacc.h)</a>
     *
     * @param pcountChildren [out] Address of a variable that receives the number of children that belong to this object.
     *                       The children are accessible objects or child elements. If the object has no children,
     *                       this value is zero.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT get_accChildCount(LongByReference pcountChildren);

    /**
     * The IAccessible::get_accDefaultAction method retrieves a string that indicates the object's default action. Not all objects have a default action.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accdefaultaction">IAccessible::get_accDefaultAction method (oleacc.h)</a>
     *
     * @param varChild [in] Specifies whether the retrieved name belongs to the object or one of the object's child elements.
     *                 This parameter is either CHILDID_SELF (to obtain information about the object) or a child ID
     *                 (to obtain information about the object's child element).
     * @param pszDefaultAction [out] Address of a BSTR that receives a localized string that describes the default
     *                         action for the specified object; if this object has no default action, the value is NULL.
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT get_accDefaultAction(VARIANT varChild, BSTRByReference pszDefaultAction);

    /**
     * The IAccessible::accDoDefaultAction method performs the specified object's default action. Not all objects have a default action.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-accdodefaultaction">IAccessible::accDoDefaultAction method (oleacc.h)</a>
     *
     * @param varChild [in] Specifies whether the retrieved name belongs to the object or one of the object's child elements.
     *                 This parameter is either CHILDID_SELF (to obtain information about the object) or a child ID
     *                 (to obtain information about the object's child element).
     * @return the HRESULT. If successful, returns S_OK. If not successful, returns one of the error values, or another standard COM error code.
     */
    HRESULT accDoDefaultAction(VARIANT varChild);
}
