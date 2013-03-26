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

	public static void main(String[] args) {
		junit.textui.TestRunner.run(COMTest.class);
	}

	public COMTest() {
		Native.setProtected(true);
	}

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testTYPEATTR() {
		TYPEATTR typeAttr = new TYPEATTR();
		typeAttr.guid = new GUID.ByReference(
				GUID.fromString("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}"));
		typeAttr.lcid = Kernel32.INSTANCE.GetSystemDefaultLCID();
		typeAttr.dwReserved = new DWORD(1);
		typeAttr.memidConstructor = new MEMBERID(1);
		typeAttr.memidDestructor = new MEMBERID(1);
		//typeAttr.lpstrSchema = new LPOLESTR("Hello World!");
		typeAttr.cbSizeInstance = new ULONG(1);
		typeAttr.typekind = new TYPEKIND.ByReference(10);
		typeAttr.cFuncs = new WORD(100);
		typeAttr.cVars = new WORD(100);
		typeAttr.cImplTypes = new WORD(12345);
		typeAttr.cbSizeVft = new WORD(1234);
		typeAttr.cbAlignment = new WORD(123);
		typeAttr.wMajorVerNum = new WORD(111);
		typeAttr.wMinorVerNum = new WORD(101);
		typeAttr.tdescAlias = new TYPEDESC.ByReference();
		typeAttr.idldescType = new IDLDESC.ByReference();

		System.out.println(typeAttr.toString());
	}

	public void testDirectMemory() {
	}
}