/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil.FindName;
import com.sun.jna.platform.win32.COM.TypeLibUtil.IsName;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author dblock[at]dblock[dot]org
 */
public class TypeLibUtilTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TypeLibUtilTest.class);
    }

    public TypeLibUtilTest() {
    }

    private TypeLibUtil loadShellTypeLib() {
        return new TypeLibUtil("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}", 1, 0);
    }

    public void testGetTypeInfoCount() {
        TypeLibUtil shellTypeLib = loadShellTypeLib();
        int typeInfoCount = shellTypeLib.getTypeInfoCount();
        assertTrue("MS Shell should contain at least 36 types.", typeInfoCount >= 36);
    }

    public void testGetTypeInfo() {
        TypeLibUtil shellTypeLib = loadShellTypeLib();
        int typeInfoCount = shellTypeLib.getTypeInfoCount();

        for (int i = 0; i < typeInfoCount; i++)
        {
            ITypeInfo typeInfo = shellTypeLib.getTypeInfo(i);
            TypeInfoUtil typeInfoUtil = new TypeInfoUtil(typeInfo);

            TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
            int cFuncs = typeAttr.cFuncs.intValue();

            for (int y = 0; y < cFuncs; y++) {
                // Get the function description
                FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(y);
                // Get the member ID
                MEMBERID memberID = funcDesc.memid;
                // Get the name of the method
                TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
                String methodName = typeInfoDoc2.getName();

                assertNotNull(methodName);

                typeInfoUtil.ReleaseFuncDesc(funcDesc);
            }

            typeInfoUtil.ReleaseTypeAttr(typeAttr);
        }
    }

    public void testFindName() {
        // Test is modelled after ITypeLibTest#testFindName
        TypeLibUtil shellTypeLib = loadShellTypeLib();

        String memberValue = "count";
        String memberValueOk = "Count";

        FindName result = shellTypeLib.FindName(memberValue, 0, (short) 100);

        // 2 matches come from manual tests
        assertEquals(2, result.getFound());
        // Check that function return corrected member name (Count) - see uppercase C
        assertEquals(memberValueOk, result.getNameBuf());

        // There have to be as many pointers as reported by pcFound
        ITypeInfo[] typelib = result.getTInfo();
        assertEquals(2, typelib.length);
        assertNotNull(typelib[0]);
        assertNotNull(typelib[1]);

        PointerByReference pbr = new PointerByReference();
        HRESULT hr = typelib[1].GetTypeAttr(pbr);
        assertTrue(COMUtils.SUCCEEDED(hr));
        OaIdl.TYPEATTR pTypeAttr = new OaIdl.TYPEATTR(pbr.getValue());

        // Either interface FolderItemVerbs ({1F8352C0-50B0-11CF-960C-0080C7F4EE85})
        // or FolderItems ({744129E0-CBE5-11CE-8350-444553540000})
        String typeGUID = pTypeAttr.guid.toGuidString();

        assertTrue(typeGUID.equals("{1F8352C0-50B0-11CF-960C-0080C7F4EE85}") ||
                typeGUID.equals("{744129E0-CBE5-11CE-8350-444553540000}"));

        typelib[1].ReleaseTypeAttr(pTypeAttr);
    }

    public void testIsName() {
        // Test is modelled after ITypeLibTest#testFindName
        TypeLibUtil shellTypeLib = loadShellTypeLib();

        String memberValue = "count";
        String memberValueOk = "Count";

        IsName isNameResult = shellTypeLib.IsName(memberValue, 0);

        assertEquals(memberValueOk, isNameResult.getNameBuf());
        assertTrue(isNameResult.isName());
    }
}
