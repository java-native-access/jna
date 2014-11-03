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
package com.sun.jna.platform.win32.COM.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.Variant.VariantArg;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Ole32.INSTANCE.CoInitialize must be called on the current thread before using
 * this object
 */
public class ProxyObject implements InvocationHandler, com.sun.jna.platform.win32.COM.util.IDispatch {

	public ProxyObject(Class<?> theInterface, IDispatch rawDispatch, ComThread comThread) {
		this.rawDispatch = rawDispatch;
		this.comThread = comThread;
		this.theInterface = theInterface;
	}

	Class<?> theInterface;
	ComThread comThread;
	com.sun.jna.platform.win32.COM.IDispatch rawDispatch;

	com.sun.jna.platform.win32.COM.IDispatch getIDispatch() {
		return this.rawDispatch;
	}

	// --------------------- InvocationHandler -----------------------------
	@Override
	public Object invoke(final Object proxy, final java.lang.reflect.Method method, final Object[] args)
			throws Throwable {
		Class<?> returnType = method.getReturnType();
		boolean isVoid = Void.TYPE.equals(returnType);

		ComProperty prop = method.getAnnotation(ComProperty.class);
		if (null != prop) {
			if (isVoid) {
				String propName = this.getMutatorName(method, prop);
				this.setProperty(propName, args[0]);
				return null;
			} else {
				String propName = this.getAccessorName(method, prop);
				return this.getProperty(returnType, propName);
			}
		}

		ComMethod meth = method.getAnnotation(ComMethod.class);
		if (null != meth) {
			String methName = this.getMethodName(method, meth);
			Object res = this.invokeMethod(returnType, methName, args);
			return res;
		}

		if (method.equals(Object.class.getMethod("toString"))) {
			return this.theInterface.getName();
		} else if (method.equals(IUnknown.class.getMethod("queryInterface", Class.class))) {
			return this.queryInterface((Class<?>) args[0]);
		}

		return null;
	}

	// --------------------- IDispatch ------------------------------
	@Override
	public <T> void setProperty(String name, T value) {
		VARIANT v = this.toVariant(value);
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(), name, v);
		COMUtils.checkRC(hr);
	}

	@Override
	public <T> T getProperty(Class<T> returnType, String name) {
		Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.getIDispatch(), name);
		COMUtils.checkRC(hr);
		Object jobj = this.getJavaObject(result);
		if (jobj instanceof com.sun.jna.platform.win32.COM.IDispatch) {
			return Factory.INSTANCE.createProxy(returnType, (com.sun.jna.platform.win32.COM.IDispatch) jobj,
					this.comThread);
		}
		return (T) jobj;
	}

	@Override
	public <T> T invokeMethod(Class<T> returnType, String name, Object... args) {
		VARIANT[] vargs;
		if (null == args) {
			vargs = new VARIANT[0];
		} else {
			vargs = new VARIANT[args.length];
		}
		for (int i = 0; i < vargs.length; ++i) {
			vargs[i] = this.toVariant(args[i]);
		}
		Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(), name, vargs);
		COMUtils.checkRC(hr);

		Object jobj = this.getJavaObject(result);
		if (jobj instanceof IDispatch) {
			return Factory.INSTANCE.createProxy(returnType, (IDispatch) jobj, this.comThread);
		}
		return (T) jobj;
	}

	@Override
	public <T> T queryInterface(Class<T> comInterface) {
		try {
			ComInterface comInterfaceAnnotation = comInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"createObject: Interface must define a value for either iid or progId via the ComInterface annotation");
			}
			final IID iid = this.getIID(comInterfaceAnnotation);
			final PointerByReference ppvObject = new PointerByReference();

			HRESULT hr = this.comThread.execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					return ProxyObject.this.getIDispatch().QueryInterface(new REFIID.ByValue(iid), ppvObject);
				}
			});

			if (WinNT.S_OK.equals(hr)) {
				Dispatch dispatch = new Dispatch(ppvObject.getValue());
				return Factory.INSTANCE.createProxy(comInterface, dispatch, this.comThread);
			} else {
				String formatMessageFromHR = Kernel32Util.formatMessage(hr);
				throw new COMException("queryInterface: " + formatMessageFromHR);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	IID getIID(ComInterface annotation) {
		String iidStr = annotation.iid();
		if (null != iidStr && !iidStr.isEmpty()) {
			return new IID(iidStr);
		} else {
			throw new COMException("ComInterface must define a value for either iid");
		}
	}

	// --------------------- ProxyObject ---------------------

	private String getAccessorName(java.lang.reflect.Method method, ComProperty prop) {
		if (prop.name().isEmpty()) {
			String methName = method.getName();
			if (methName.startsWith("get")) {
				return methName.replaceFirst("get", "");
			} else {
				throw new RuntimeException(
						"Property Accessor name must start with 'get', or set the anotation 'name' value");
			}
		} else {
			return prop.name();
		}
	}

	private String getMutatorName(java.lang.reflect.Method method, ComProperty prop) {
		if (prop.name().isEmpty()) {
			String methName = method.getName();
			if (methName.startsWith("set")) {
				return methName.replaceFirst("set", "");
			} else {
				throw new RuntimeException(
						"Property Mutator name must start with 'set', or set the anotation 'name' value");
			}
		} else {
			return prop.name();
		}
	}

	private String getMethodName(java.lang.reflect.Method method, ComMethod meth) {
		if (meth.name().isEmpty()) {
			String methName = method.getName();
			return methName;
		} else {
			return meth.name();
		}
	}

	protected Object getJavaObject(VARIANT value) {
		Object vobj = value.getValue();
		if (vobj instanceof WinDef.BOOL) {
			return ((WinDef.BOOL) vobj).booleanValue();
		} else if (vobj instanceof WinDef.LONG) {
			return ((WinDef.LONG) vobj).longValue();
		} else if (vobj instanceof WinDef.SHORT) {
			return ((WinDef.SHORT) vobj).shortValue();
		} else if (vobj instanceof WinDef.UINT) {
			return ((WinDef.UINT) vobj).intValue();
		} else if (vobj instanceof WinDef.WORD) {
			return ((WinDef.WORD) vobj).intValue();
		} else if (vobj instanceof WTypes.BSTR) {
			return ((WTypes.BSTR) vobj).getValue();
		}
		return vobj;
	}

	protected VARIANT toVariant(Object value) {
		if (value instanceof Boolean) {
			return new VARIANT((Boolean) value);
		} else if (value instanceof Long) {
			return new VARIANT(new WinDef.LONG((long) value));
		} else if (value instanceof Integer) {
			return new VARIANT((Integer) value);
		} else if (value instanceof Short) {
			return new VARIANT(new WinDef.SHORT((short) value));
		} else if (value instanceof Float) {
			return new VARIANT((Float) value);
		} else if (value instanceof Double) {
			return new VARIANT((Double) value);
		} else if (value instanceof String) {
			return new VARIANT((String) value);
		} else if (value instanceof Date) {
			return new VARIANT((Date) value);
		} else if (value instanceof Proxy) {
			InvocationHandler ih = Proxy.getInvocationHandler(value);
			ProxyObject pobj = (ProxyObject) ih;
			return new VARIANT(pobj.getIDispatch());
		} if (value instanceof IComEnum) {
			IComEnum enm = (IComEnum)value;
			return new VARIANT(new WinDef.LONG(enm.getValue()));
		} else {
			return null;
		}
	}

	/** The Constant LOCALE_USER_DEFAULT. */
	public final static LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();

	/** The Constant LOCALE_SYSTEM_DEFAULT. */
	public final static LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE.GetSystemDefaultLCID();

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, String name, VARIANT pArg)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, name, new VARIANT[] { pArg });
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, DISPID dispId, VARIANT pArg)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, dispId, new VARIANT[] { pArg });
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, String name)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, name, (VARIANT[]) null);
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, DISPID dispId)
			throws COMException {

		return this.oleMethod(nType, pvResult, pDisp, dispId, (VARIANT[]) null);
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, final IDispatch pDisp, String name,
			VARIANT[] pArgs) throws COMException {
		try {
			if (pDisp == null)
				throw new COMException("pDisp (IDispatch) parameter is null!");

			// variable declaration
			final WString[] ptName = new WString[] { new WString(name) };
			final DISPIDByReference pdispID = new DISPIDByReference();

			// Get DISPID for name passed...
			HRESULT hr = this.comThread.execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					HRESULT hr = pDisp.GetIDsOfNames(Guid.IID_NULL, ptName, 1, LOCALE_USER_DEFAULT, pdispID);
					return hr;
				}
			});
			COMUtils.checkRC(hr);

			return this.oleMethod(nType, pvResult, pDisp, pdispID.getValue(), pArgs);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(final int nType, final VARIANT.ByReference pvResult, final IDispatch pDisp,
			final DISPID dispId, VARIANT[] pArgs) throws COMException {

		if (pDisp == null)
			throw new COMException("pDisp (IDispatch) parameter is null!");

		// variable declaration
		int _argsLen = 0;
		VARIANT[] _args = null;
		final DISPPARAMS dp = new DISPPARAMS();
		final EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
		final IntByReference puArgErr = new IntByReference();

		// make parameter reverse ordering as expected by COM runtime
		if ((pArgs != null) && (pArgs.length > 0)) {
			_argsLen = pArgs.length;
			_args = new VARIANT[_argsLen];

			int revCount = _argsLen;
			for (int i = 0; i < _argsLen; i++) {
				_args[i] = pArgs[--revCount];
			}
		}

		// Handle special-case for property-puts!
		if (nType == OleAuto.DISPATCH_PROPERTYPUT) {
			dp.cNamedArgs = new UINT(_argsLen);
			dp.rgdispidNamedArgs = new DISPIDByReference(OaIdl.DISPID_PROPERTYPUT);
		}

		// Build DISPPARAMS
		if (_argsLen > 0) {
			dp.cArgs = new UINT(_args.length);
			// make pointer of variant array
			dp.rgvarg = new VariantArg.ByReference(_args);

			// write 'DISPPARAMS' structure to memory
			dp.write();
		}

		// Make the call!
		try {

			HRESULT hr = this.comThread.execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					return pDisp.Invoke(dispId, Guid.IID_NULL, LOCALE_SYSTEM_DEFAULT, new DISPID(nType), dp, pvResult,
							pExcepInfo, puArgErr);
				}
			});

			COMUtils.checkRC(hr, pExcepInfo, puArgErr);
			return hr;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
