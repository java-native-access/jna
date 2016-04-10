/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable_Test {
        static {
                ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
        }
    
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
	public void GetRunningObjectTable() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		
		assertNotNull(pprot.getValue());
	}
	
	@Test
	public void Register() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		//Can't yet be tested as IMoniker is not fully implemented
		//rot.Register(grfFlags, punkObject, pmkObjectName, pdwRegister);
		
	}

	@Test
	public void Revoke() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		//Can't yet be tested as IMoniker is not fully implemented,
		// so we can't register an object, and hence can't get a registration key to Revoke one
		//rot.Revoke(dwRegister);
		
	}

	@Test
	public void IsRunning() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		//Can't yet be tested as IMoniker is not fully implemented,
		//rot.IsRunning(pmkObjectName);
		
	}
	
	@Test
	public void GetObject() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		PointerByReference ppenumMoniker = new PointerByReference();
		hr = rot.EnumRunning(ppenumMoniker);
		COMUtils.checkRC(hr);
		IEnumMoniker iterator = new EnumMoniker(ppenumMoniker.getValue());

		iterator.Reset();
		
		PointerByReference rgelt = new PointerByReference();
		ULONGByReference pceltFetched = new ULONGByReference();
		hr = iterator.Next(new ULONG(1), rgelt, pceltFetched);
		
		while (WinNT.S_OK.equals(hr) && pceltFetched.getValue().intValue() > 0) {
			Moniker moniker = new Moniker(rgelt.getValue());
			
			PointerByReference ppbc = new PointerByReference();
			Ole32.INSTANCE.CreateBindCtx(new DWORD(), ppbc);

			String name = moniker.GetDisplayName(ppbc.getValue(), moniker.getPointer());
			
			PointerByReference ppunkObject = new PointerByReference();
			hr = rot.GetObject(moniker.getPointer(), ppunkObject);
			COMUtils.checkRC(hr);
			
			IUnknown unk = new Unknown(ppunkObject.getValue());
			PointerByReference ppvObject = new PointerByReference();
			hr = unk.QueryInterface(new REFIID(IUnknown.IID_IUNKNOWN), ppvObject);
			assertEquals(0, hr.intValue());
			assertNotNull(ppvObject.getValue());
			
			moniker.Release();
			
			hr = iterator.Next(new ULONG(1), rgelt, pceltFetched);
		}

	}
	
	@Test
	public void NoteChangeTime() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		//Can't yet be tested as IMoniker is not fully implemented,
		// so we can't register an object, and hence can't get a registration key
		//rot.NoteChangeTime(dwRegister, pfiletime);
		
	}
	
	@Test
	public void GetTimeOfLastChange() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		//Can't yet be tested as IMoniker is not fully implemented,
		// so we can't register an object, and hence can't get a registration key
		//rot.GetTimeOfLastChange(pmkObjectName, pfiletime);
		
	}
	
	@Test
	public void EnumRunning() {
		PointerByReference pprot = new PointerByReference();
		HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
		COMUtils.checkRC(hr);
		IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());
		
		PointerByReference ppenumMoniker = new PointerByReference();
		hr = rot.EnumRunning(ppenumMoniker);
		COMUtils.checkRC(hr);

		assertNotNull(ppenumMoniker.getValue());
		
	}



}
