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

import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;

import static com.sun.jna.platform.win32.COM.COMUtils.S_FALSE;
import static com.sun.jna.platform.win32.COM.COMUtils.S_OK;
import static com.sun.jna.platform.win32.WinError.E_INVALIDARG;

/**
 * {@link Accessible} utility API
 *
 * @author Mo Beigi, me@mobeigi.org
 */
public class AccessibleUtil
{
    /** The Accessible **/
    final private IAccessible accessible;

    public AccessibleUtil(IAccessible accessible) {
        this.accessible = accessible;
    }

    /**
     * The IAccessible::get_accName method retrieves the name of the specified object. All objects support this property.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/oleacc/nf-oleacc-iaccessible-get_accname">IAccessible::get_accName method (oleacc.h)</a>
     *
     * @param childId This parameter is either {@link com.sun.jna.platform.win32.WinUser#CHILDID_SELF} (to obtain information about the object) or a child ID
     *                (to obtain information about the object's child element).
     * @return a string that contains the specified object's name.
     * @throws COMException If not successful
     */
    public String get_accName(int childId) throws COMException
    {
        VARIANT varChild = new VARIANT.ByValue();
        varChild.setValue(Variant.VT_I4, new LONG(childId));
        BSTRByReference bstr = new BSTRByReference();

        HRESULT hresult = accessible.get_accName(varChild, bstr);
        String result = "";

        if (hresult.intValue() == S_OK) {
            result = bstr.getValue().getValue();
        }

        OleAuto.INSTANCE.VariantClear(varChild);
        OleAuto.INSTANCE.SysFreeString(bstr.getValue());

        switch (hresult.intValue()) {
            case S_OK:
                return result;
            case S_FALSE:
                throw new COMException("The specified object does not have a name.", hresult);
            case E_INVALIDARG:
                throw new COMException("An argument is not valid.", hresult);
            default:
                throw new COMException("An unknown error occurred.", hresult);
        }
    }
}
