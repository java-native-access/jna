/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class OleAutoTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(OleAutoTest.class);
    }

    public OleAutoTest() {
    }

    public void testSysAllocString() {
        assertEquals(null, OleAuto.INSTANCE.SysAllocString(null));
        BSTR p = OleAuto.INSTANCE.SysAllocString("hello world");
        assertEquals("hello world", p.getValue());
        OleAuto.INSTANCE.SysFreeString(p);
    }

    public void testSysFreeString() {
        OleAuto.INSTANCE.SysFreeString(null);
    }

    public void testLoadRegTypeLib() {
        CLSID.ByReference clsid = new CLSID.ByReference();
        // get CLSID from string, Microsoft Scripting Engine
        HRESULT hr = Ole32.INSTANCE.CLSIDFromString(
                "{420B2830-E718-11CF-893D-00A0C9054228}", clsid);
        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());

        // get user default lcid
        LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
        PointerByReference pWordTypeLib = new PointerByReference();
        // get typelib version 1.0
        hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 1, 0, lcid, pWordTypeLib);
        COMUtils.checkRC(hr);
        assertEquals(0, hr.intValue());
    }

    public void testVariantConvertBoolean() {
        VARIANT variant = new Variant.VARIANT(true);
        OleAuto.INSTANCE.VariantChangeType(variant, variant, OleAuto.VARIANT_ALPHABOOL, new VARTYPE(Variant.VT_BSTR));
        assertEquals("Variant-Type", Variant.VT_BSTR, variant.getVarType().intValue());
        assertEquals("Variant-Value", "True", variant.stringValue());
        OleAuto.INSTANCE.VariantClear(variant);

        VARIANT.ByReference variant2 = new Variant.VARIANT.ByReference();
        variant2.setValue(Variant.VT_BOOL, new OaIdl.VARIANT_BOOL(true));
        OleAuto.INSTANCE.VariantChangeType(variant2, variant2, OleAuto.VARIANT_ALPHABOOL, new VARTYPE(Variant.VT_BSTR));
        assertEquals("Variant-Type", Variant.VT_BSTR, variant2.getVarType().intValue());
        assertEquals("Variant-Value", "True", variant2.stringValue());
        OleAuto.INSTANCE.VariantClear(variant2);
    }

    public void testVariantConvertBSTR() {
        VARIANT variant = new Variant.VARIANT("42");
        OleAuto.INSTANCE.VariantChangeType(variant, variant, (short) 0, new VARTYPE(Variant.VT_INT));
        assertEquals("Variant-Type", Variant.VT_INT, variant.getVarType().intValue());
        assertEquals("Variant-Value", 42, variant.intValue());
        OleAuto.INSTANCE.VariantClear(variant);

        VARIANT.ByReference variant2 = new Variant.VARIANT.ByReference();
        variant2.setValue(Variant.VT_BSTR, OleAuto.INSTANCE.SysAllocString("42"));
        OleAuto.INSTANCE.VariantChangeType(variant2, variant2, (short) 0, new VARTYPE(Variant.VT_INT));
        assertEquals("Variant-Type", Variant.VT_INT, variant2.getVarType().intValue());
        assertEquals("Variant-Value", 42, variant2.intValue());
        OleAuto.INSTANCE.VariantClear(variant2);
    }
}
