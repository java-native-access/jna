/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;

/**
 * @author dblock[at]dblock[dot]org
 */
public class TypeLibUtilTest extends TestCase {

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
        assertTrue("MS Shell should contain at 36 types.", typeInfoCount == 36);
    }

    public void testGetTypeInfo() {
        TypeLibUtil shellTypeLib = loadShellTypeLib();
        ITypeInfo typeInfo = shellTypeLib.getTypeInfo(0);
        TypeInfoUtil typeInfoUtil = new TypeInfoUtil(typeInfo);

        TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        int cFuncs = typeAttr.cFuncs.intValue();

        for (int i = 0; i < cFuncs; i++) {
            FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
        }
    }
}
