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
package com.sun.jna.platform.win32.COM.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VariantArg;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.DispatchListener;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CallbackProxy implements IDispatchCallback {

	public CallbackProxy(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
		this.comEventCallbackInterface = comEventCallbackInterface;
		this.comEventCallbackListener = comEventCallbackListener;
		this.listenedToRiid = this.createRIID(comEventCallbackInterface);
		this.dsipIdMap = this.createDispIdMap(comEventCallbackInterface);
		this.dispatchListener = new DispatchListener(this);
	}

	Class<?> comEventCallbackInterface;
	IComEventCallbackListener comEventCallbackListener;
	REFIID.ByValue listenedToRiid;
	public DispatchListener dispatchListener;
	Map<DISPID, Method> dsipIdMap;

	REFIID.ByValue createRIID(Class<?> comEventCallbackInterface) {
		ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
		if (null == comInterfaceAnnotation) {
			throw new COMException(
					"advise: Interface must define a value for either iid via the ComInterface annotation");
		}
		String iidStr = comInterfaceAnnotation.iid();
		if (null == iidStr || iidStr.isEmpty()) {
			throw new COMException("ComInterface must define a value for iid");
		}
		return new REFIID.ByValue(new IID(iidStr).getPointer());
	}

	Map<DISPID, Method> createDispIdMap(Class<?> comEventCallbackInterface) {
		Map<DISPID, Method> map = new HashMap<DISPID, Method>();

		for (Method meth : comEventCallbackInterface.getMethods()) {
			ComEventCallback annotation = meth.getAnnotation(ComEventCallback.class);
			if (null != annotation) {
				int dispId = annotation.dispid();
				if (-1 == dispId) {
					dispId = this.fetchDispIdFromName(annotation);
				}
				map.put(new DISPID(dispId), meth);
			}
		}

		return map;
	}

	int fetchDispIdFromName(ComEventCallback annotation) {
		// TODO
		return -1;
	}

	@Override
	public Pointer getPointer() {
		return this.dispatchListener.getPointer();
	}

	// ------------------------ IDispatch ------------------------------
	@Override
	public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetIDsOfNames(REFIID.ByValue riid, WString[] rgszNames, int cNames, LCID lcid,
			DISPIDByReference rgDispId) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT Invoke(DISPID dispIdMember, REFIID.ByValue riid, LCID lcid, WORD wFlags,
			DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
			IntByReference puArgErr) {

		// decode argumentt
		final List<Object> jargs = new ArrayList<Object>();
		if (pDispParams.cArgs.intValue() > 0) {
			VariantArg vargs = new VariantArg(pDispParams.getPointer());
			for (Variant.VARIANT varg : vargs.variantArg) {
				Object jarg = Convert.toJavaObject(varg);
				jargs.add(jarg);
			}
		}

		if (this.dsipIdMap.containsKey(dispIdMember)) {
			final Method eventMethod = this.dsipIdMap.get(dispIdMember);
			String eventMethodName = eventMethod.getName();
			Runnable invokation = new Runnable() {
				@Override
				public void run() {
					try {
						eventMethod.invoke(comEventCallbackListener, jargs.toArray());
					} catch (Exception e) {
						CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent("Exception invoking method "+eventMethod, e);
					}
				}
			};
			Thread t = new Thread(invokation, "COM Event Callback: " + eventMethodName);
			t.run();
		} else {
			this.comEventCallbackListener.errorReceivingCallbackEvent("No method found with dispId = "+dispIdMember, null);
		}			
		return WinError.S_OK;
	}

	// ------------------------ IUnknown ------------------------------
	@Override
	public HRESULT QueryInterface(REFIID.ByValue refid, PointerByReference ppvObject) {
		if (null == ppvObject) {
			return new HRESULT(WinError.E_POINTER);
		}

		if (refid.equals(this.listenedToRiid)) {
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
