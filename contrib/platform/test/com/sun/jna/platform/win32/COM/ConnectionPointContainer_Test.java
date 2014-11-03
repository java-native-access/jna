/* Copyright (c) 2014 Dr David H. Akehurst, All Rights Reserved
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer_Test {

	@Before
	public void before() {
		HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
		COMUtils.checkRC(hr);
	}
	
	@After
	public void after() {
		Ole32.INSTANCE.CoUninitialize();
	}
	
	@Test
	public void getConnectionPointContainer() {
		//Get active word object
		CLSID clsid = new CLSID("{000209FF-0000-0000-C000-000000000046}");
		PointerByReference ppUnkApp = new PointerByReference();
		HRESULT hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, ppUnkApp);
		COMUtils.checkRC(hr);
		
		//query for ConnectionPointContainer
		Unknown unk = new Unknown(ppUnkApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc =new ConnectionPointContainer(ppCpc.getValue());
		
		//find connection point for Application_Events4
		IID appEvnts4 = new IID("{00020A01-0000-0000-C000-000000000046}");
		REFIID riid = new REFIID(appEvnts4.getPointer());
		PointerByReference ppCp = new PointerByReference();
		hr = cpc.FindConnectionPoint(riid, ppCp);
		COMUtils.checkRC(hr);
		ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());
		IID cp_iid = new IID();
		hr = cp.GetConnectionInterface(cp_iid);
		String cp_guid = cp_iid.toGuidString();
		UnknownCallback listener = new UnknownCallback();

		
		DWORDByReference pdwCookie = new DWORDByReference();
		hr = cp.Advise(listener, pdwCookie);
		COMUtils.checkRC(hr);
		
	}



}
