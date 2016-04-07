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

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.IDLDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEDESC;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.WORD;

/**
 * @author dblock[at]dblock[dot]org
 */
public class COMTest extends TestCase {
    
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(COMTest.class);
    }

    public COMTest() {
    }

    @Override
	protected void setUp() throws Exception {
    }

    @Override
	protected void tearDown() throws Exception {
    }

    public void testTYPEATTR() {
        int pSize = Native.POINTER_SIZE;

        TYPEATTR typeAttr = new TYPEATTR();
        typeAttr.guid = GUID
            .fromString("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}");
        typeAttr.lcid = Kernel32.INSTANCE.GetSystemDefaultLCID();
        typeAttr.dwReserved = new DWORD(1);
        typeAttr.memidConstructor = new MEMBERID(2);
        typeAttr.memidDestructor = new MEMBERID(3);
        typeAttr.lpstrSchema = new LPOLESTR("Hello World");
        typeAttr.cbSizeInstance = new ULONG(4);
        typeAttr.typekind = new TYPEKIND(5);
        typeAttr.cFuncs = new WORD(6);
        typeAttr.cVars = new WORD(7);
        typeAttr.cImplTypes = new WORD(8);
        typeAttr.cbSizeVft = new WORD(9);
        typeAttr.cbAlignment = new WORD(10);
        typeAttr.wMajorVerNum = new WORD(11);
        typeAttr.wMinorVerNum = new WORD(12);
        typeAttr.tdescAlias = new TYPEDESC();
        typeAttr.idldescType = new IDLDESC();

        typeAttr.write();
        typeAttr.read();
        //System.out.println(typeAttr.toString());
        //System.out.println("TYPEATTR size: " + typeAttr.size());
    }

    public void testDirectMemory() {
    }
}