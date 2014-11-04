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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer_Test {

	PointerByReference ppWordApp;

	@Before
	public void before() {
		HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
		COMUtils.checkRC(hr);

		// Create word object
		CLSID clsid = new CLSID("{000209FF-0000-0000-C000-000000000046}");
		this.ppWordApp = new PointerByReference();
		hr = Ole32.INSTANCE
				.CoCreateInstance(clsid, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, this.ppWordApp);
		COMUtils.checkRC(hr);
	}

	@After
	public void after() {
		// Close Word
		Dispatch d = new Dispatch(this.ppWordApp.getValue());
		DISPID dispIdMember = new DISPID(1105); // Quit
		REFIID.ByValue riid = new REFIID.ByValue(Guid.IID_NULL);
		LCID lcid = Kernel32.INSTANCE.GetSystemDefaultLCID();
		WinDef.WORD wFlags = new WinDef.WORD(1);
		DISPPARAMS.ByReference pDispParams = new DISPPARAMS.ByReference();
		VARIANT.ByReference pVarResult = new VARIANT.ByReference();
		IntByReference puArgErr = new IntByReference();
		EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
		d.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
		
		Ole32.INSTANCE.CoUninitialize();
	}

	@Test
	public void queryInterface_ConnectionPointContainer() {
		Unknown unk = new Unknown(this.ppWordApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		HRESULT hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());
	}

	@Test
	public void FindConnectionPoint() {
		// query for ConnectionPointContainer
		Unknown unk = new Unknown(this.ppWordApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		HRESULT hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

		// find connection point for Application_Events4
		IID appEvnts4 = new IID("{00020A01-0000-0000-C000-000000000046}");
		REFIID riid = new REFIID(appEvnts4.getPointer());
		PointerByReference ppCp = new PointerByReference();
		hr = cpc.FindConnectionPoint(riid, ppCp);
		COMUtils.checkRC(hr);
		ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());
	}

	@Test
	public void GetConnectionInterface() {
		// query for ConnectionPointContainer
		Unknown unk = new Unknown(this.ppWordApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		HRESULT hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

		// find connection point for Application_Events4
		IID appEvnts4 = new IID("{00020A01-0000-0000-C000-000000000046}");
		REFIID riid = new REFIID(appEvnts4.getPointer());
		PointerByReference ppCp = new PointerByReference();
		hr = cpc.FindConnectionPoint(riid, ppCp);
		COMUtils.checkRC(hr);
		ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());

		IID cp_iid = new IID();
		hr = cp.GetConnectionInterface(cp_iid);
		COMUtils.checkRC(hr);

		Assert.assertEquals(appEvnts4, cp_iid);
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
		public HRESULT Invoke(DISPID dispIdMember, REFIID.ByValue riid, LCID lcid, WORD wFlags,
				DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
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
			IID appEvnts4 = new IID("{00020A01-0000-0000-C000-000000000046}");
			REFIID.ByValue riid = new REFIID.ByValue(appEvnts4.getPointer());

			if (refid.equals(riid)) {
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
	public void Advise() {

		// query for ConnectionPointContainer
		Unknown unk = new Unknown(this.ppWordApp.getValue());
		PointerByReference ppCpc = new PointerByReference();
		IID cpcIID = new IID("{B196B284-BAB4-101A-B69C-00AA00341D07}");
		HRESULT hr = unk.QueryInterface(new REFIID.ByValue(cpcIID), ppCpc);
		COMUtils.checkRC(hr);
		ConnectionPointContainer cpc = new ConnectionPointContainer(ppCpc.getValue());

		// find connection point for Application_Events4
		IID appEvnts4 = new IID("{00020A01-0000-0000-C000-000000000046}");
		REFIID riid = new REFIID(appEvnts4.getPointer());
		PointerByReference ppCp = new PointerByReference();
		hr = cpc.FindConnectionPoint(riid, ppCp);
		COMUtils.checkRC(hr);
		ConnectionPoint cp = new ConnectionPoint(ppCp.getValue());
		IID cp_iid = new IID();
		hr = cp.GetConnectionInterface(cp_iid);
		COMUtils.checkRC(hr);

		Application_Events4 listener = new Application_Events4();

		DWORDByReference pdwCookie = new DWORDByReference();
		hr = cp.Advise(listener, pdwCookie);
		COMUtils.checkRC(hr);

		Assert.assertTrue(listener.QueryInterface_called);
		
	}

}
