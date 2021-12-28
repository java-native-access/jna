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

public class AccessibleUtil
{
    /** The Accessible **/
    final private IAccessible accessible;

    public AccessibleUtil(IAccessible accessible) {
        this.accessible = accessible;
    }

    public String get_accName(int childId)
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
