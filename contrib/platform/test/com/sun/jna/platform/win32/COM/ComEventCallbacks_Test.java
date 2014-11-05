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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.NtDll;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ComEventCallbacks_Test {

	final String WORD_APPLICATION_INTERFACE = "{00020970-0000-0000-C000-000000000046}";
	final String APPLICATION_EVENTS_4 = "{00020A01-0000-0000-C000-000000000046}";
	
	@Before
	public void before() {
		HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
		COMUtils.checkRC(hr);
	}

	@After
	public void after() {
		Ole32.INSTANCE.CoUninitialize();
	}

	class Application_Events4 implements IDispatchCallback {
		public DispatchListener listener = new DispatchListener(this);

		@Override
		public Pointer getPointer() {
			return this.listener.getPointer();
		}

		//------------------------ IDispatch ------------------------------
		@Override
		public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
			return new HRESULT(WinError.E_NOTIMPL);
		}

		@Override
		public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
			return new HRESULT(WinError.E_NOTIMPL);
		}

		@Override
		public HRESULT GetIDsOfNames(REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid, DISPIDByReference rgDispId) {
			return new HRESULT(WinError.E_NOTIMPL);
		}

		public boolean Invoke_called = false;
		@Override
		public HRESULT Invoke(DISPID dispIdMember, REFIID.ByValue riid, LCID lcid,
	            WORD wFlags, DISPPARAMS.ByReference pDispParams,
	            VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
	            IntByReference puArgErr) {
			this.Invoke_called = true;
			
			return new HRESULT(WinError.E_NOTIMPL);
		}
		
		
		//------------------------ IUnknown ------------------------------
		public boolean QueryInterface_called = false;
		@Override
		public HRESULT QueryInterface(REFIID.ByValue refid, PointerByReference ppvObject) {
			this.QueryInterface_called = true;
			if (null==ppvObject) {
				return new HRESULT(WinError.E_POINTER);
			}

			String s = refid.toGuidString();
			IID appEvnts4 = new IID(APPLICATION_EVENTS_4);
			REFIID.ByValue riid = new REFIID.ByValue(appEvnts4.getPointer());

			if (refid.equals(riid)) {
				ppvObject.setValue(this.getPointer());
				return WinError.S_OK;
			}
			
			if (new Guid.IID(refid.getPointer()).equals(Unknown.IID_IUNKNOWN)) {
				ppvObject.setValue(this.getPointer());
				return WinError.S_OK;
			}
			
			if (new Guid.IID(refid.getPointer()).equals(Dispatch.IID_IDISPATCH)) {
				ppvObject.setValue(this.getPointer());
				return WinError.S_OK;
			}
			
			return new HRESULT(WinError.E_NOINTERFACE);
		}

		public int AddRef() {
			return 0;
		}

		public int Release() {
			return 0;
		}

	}

	@Test
	public void cause_Quit_Event() {
		// Create word object
		CLSID clsid = new CLSID("{000209FF-0000-0000-C000-000000000046}");
		PointerByReference ppWordApp = new PointerByReference();
//		HRESULT hr = Ole32.INSTANCE
//				.CoCreateInstance(clsid, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ppWordApp);
		HRESULT hr =OleAuto.INSTANCE.GetActiveObject(clsid, null, ppWordApp);
		COMUtils.checkRC(hr);
		
		// query for ConnectionPointContainer
		Unknown unk = new Unknown(ppWordApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

		// find connection point for Application_Events4
		IID appEvnts4 = new IID(APPLICATION_EVENTS_4);
		REFIID riid = new REFIID(appEvnts4.getPointer());
		PointerByReference ppCp = new PointerByReference();
		hr = cpc.FindConnectionPoint(riid, ppCp);
		COMUtils.checkRC(hr);
		final ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());
		IID cp_iid = new IID();
		hr = cp.GetConnectionInterface(cp_iid);
		COMUtils.checkRC(hr);

		final Application_Events4 listener = new Application_Events4();
		final DWORDByReference pdwCookie = new DWORDByReference();
		HRESULT hr1 = cp.Advise(listener, pdwCookie);
		COMUtils.checkRC(hr1);

//		Assert.assertTrue(listener.QueryInterface_called);
//		
//		// Call Quit
		Dispatch d = new Dispatch(ppWordApp.getValue());
		DISPID dispIdMember = new DISPID(1105); // Quit
		REFIID.ByValue niid = new REFIID.ByValue(Guid.IID_NULL);
		LCID lcid = Kernel32.INSTANCE.GetSystemDefaultLCID();
		WinDef.WORD wFlags = new WinDef.WORD(1);
		DISPPARAMS.ByReference pDispParams = new DISPPARAMS.ByReference();
		VARIANT.ByReference pVarResult = new VARIANT.ByReference();
		IntByReference puArgErr = new IntByReference();
		EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
		hr = d.Invoke(dispIdMember, niid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
		COMUtils.checkRC(hr);
		
		//Wait for event to happen
		try {
			WinUser.MSG msg = new WinUser.MSG();
			while (((User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0)) {
			    User32.INSTANCE.TranslateMessage(msg);
			    User32.INSTANCE.DispatchMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(listener.Invoke_called);
	}

}
