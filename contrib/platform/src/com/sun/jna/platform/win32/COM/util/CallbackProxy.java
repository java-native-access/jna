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

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CallbackProxy implements IDispatchCallback {

	public CallbackProxy(Factory factory, Class<?> comEventCallbackInterface,
			IComEventCallbackListener comEventCallbackListener) {
		this.factory = factory;
		this.comEventCallbackInterface = comEventCallbackInterface;
		this.comEventCallbackListener = comEventCallbackListener;
		this.listenedToRiid = this.createRIID(comEventCallbackInterface);
		this.dsipIdMap = this.createDispIdMap(comEventCallbackInterface);
		this.dispatchListener = new DispatchListener(this);
		this.executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "COM Event Callback executor");
				thread.setDaemon(true);
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						CallbackProxy.this.factory.comThread.uncaughtExceptionHandler.uncaughtException(t, e);
					}
				});
				return thread;
			}
		});
	}

	Factory factory;
	Class<?> comEventCallbackInterface;
	IComEventCallbackListener comEventCallbackListener;
	REFIID listenedToRiid;
	public DispatchListener dispatchListener;
	Map<DISPID, Method> dsipIdMap;
	ExecutorService executorService;

	REFIID createRIID(Class<?> comEventCallbackInterface) {
		ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
		if (null == comInterfaceAnnotation) {
			throw new COMException(
					"advise: Interface must define a value for either iid via the ComInterface annotation");
		}
		String iidStr = comInterfaceAnnotation.iid();
		if (null == iidStr || iidStr.isEmpty()) {
			throw new COMException("ComInterface must define a value for iid");
		}
		return new REFIID(new IID(iidStr).getPointer());
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

	void invokeOnThread(final DISPID dispIdMember, final REFIID riid, LCID lcid, WORD wFlags,
			final DISPPARAMS.ByReference pDispParams) {
		// decode arguments
		// must decode them on this thread, and create a proxy for any COM objects (IDispatch)
		// this will AddRef on the COM object so that it is not cleaned up before we can use it
		// on the thread that does the java callback.
		List<Object> rjargs = new ArrayList<Object>();
		if (pDispParams.cArgs.intValue() > 0) {
			VariantArg vargs = pDispParams.rgvarg;
			vargs.setArraySize(pDispParams.cArgs.intValue());
			for (Variant.VARIANT varg : vargs.variantArg) {
				Object jarg = Convert.toJavaObject(varg);
				if (jarg instanceof IDispatch) {
					IDispatch dispatch = (IDispatch) jarg;
					//get raw IUnknown interface
					PointerByReference ppvObject = new PointerByReference();
					IID iid = com.sun.jna.platform.win32.COM.IUnknown.IID_IUNKNOWN;
					dispatch.QueryInterface(new REFIID(iid), ppvObject);
					Unknown rawUnk = new Unknown(ppvObject.getValue());
					long unknownId = Pointer.nativeValue( rawUnk.getPointer() );
					int n = rawUnk.Release();
					//Note: unlike in other places, there is currently no COM ref already added for this pointer 
					IUnknown unk = CallbackProxy.this.factory.createProxy(IUnknown.class, unknownId, dispatch);
					rjargs.add(unk);
				} else {
					rjargs.add(jarg);
				}
			}
		}
		final List<Object> jargs = new ArrayList<Object>(rjargs);
		Runnable invokation = new Runnable() {
			@Override
			public void run() {
				try {
					if (CallbackProxy.this.dsipIdMap.containsKey(dispIdMember)) {
						Method eventMethod = CallbackProxy.this.dsipIdMap.get(dispIdMember);
						if (eventMethod.getParameterTypes().length != jargs.size()) {
							CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
									"Trying to invoke method " + eventMethod + " with " + jargs.size() + " arguments",
									null);
						} else {
							try {
								// need to convert arguments maybe
								List<Object> margs = new ArrayList<Object>();
								Class<?>[] params = eventMethod.getParameterTypes();
								for (int i = 0; i < eventMethod.getParameterTypes().length; ++i) {
									Class<?> paramType = params[i];
									Object jobj = jargs.get(i);
									if (jobj != null && paramType.getAnnotation(ComInterface.class) != null) {
										if (jobj instanceof IUnknown) {
											IUnknown unk = (IUnknown) jobj;
											Object mobj = unk.queryInterface(paramType);
											margs.add(mobj);
										} else {
											throw new RuntimeException("Cannot convert argument " + jobj.getClass()
													+ " to ComInterface " + paramType);
										}
									} else {
										margs.add(jobj);
									}
								}
								eventMethod.invoke(comEventCallbackListener, margs.toArray());
							} catch (Exception e) {
								CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
										"Exception invoking method " + eventMethod, e);
							}
						}
					} else {
						CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
								"No method found with dispId = " + dispIdMember, null);
					}
				} catch (Exception e) {
					CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
							"Exception receiving callback event ", e);
				}
			}
		};
		this.executorService.execute(invokation);
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
	public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames, LCID lcid,
			DISPIDByReference rgDispId) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid, WORD wFlags,
			DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
			IntByReference puArgErr) {

		this.invokeOnThread(dispIdMember, riid, lcid, wFlags, pDispParams);

		return WinError.S_OK;
	}

	// ------------------------ IUnknown ------------------------------
	@Override
	public HRESULT QueryInterface(REFIID refid, PointerByReference ppvObject) {
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
