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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.sun.jna.Pointer;
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
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ConnectionPoint;
import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * This object acts as the invocation handler for interfaces annotated with
 * ComInterface. It wraps all (necessary) low level COM calls and executes them
 * on a 'ComThread' held by the Factory object.
 */
public class ProxyObject implements InvocationHandler, com.sun.jna.platform.win32.COM.util.IDispatch,
		IRawDispatchHandle {

	public ProxyObject(Class<?> theInterface, IDispatch rawDispatch, Factory factory) {
		this.unknownId = -1;
		this.rawDispatch = rawDispatch;
		this.comThread = factory.getComThread();
		this.theInterface = theInterface;
		this.factory = factory;
		// make sure dispatch object knows we have a reference to it
		// (for debug it is usefult to be able to see how many refs are present
		int n = this.rawDispatch.AddRef();
		this.getUnknownId(); // pre cache/calculate it
		factory.register(this);
	}

	/** when proxy is created for arguments on a call back, they are already on the
	 * com thread, and hence calling 'getUnknownId' will not work as it uses the ComThread
	 * however, the unknown pointer value is passed in;
	 *
	 * @param theInterface
	 * @param unknownId
	 * @param rawDispatch
	 * @param factory
	 */
	ProxyObject(Class<?> theInterface, long unknownId, IDispatch rawDispatch, Factory factory) {
		this.unknownId = unknownId;
		this.rawDispatch = rawDispatch;
		this.comThread = factory.getComThread();
		this.theInterface = theInterface;
		this.factory = factory;
		// make sure dispatch object knows we have a reference to it
		// (for debug it is usefult to be able to see how many refs are present
		int n = this.rawDispatch.AddRef();
		factory.register(this);
	}

	// cached value of the IUnknown interface pointer
	// Rules of COM state that querying for the IUnknown interface must return
	// an identical pointer value
	long unknownId;

	long getUnknownId() {
		if (-1 == this.unknownId) {
			try {

				final PointerByReference ppvObject = new PointerByReference();

				Thread current = Thread.currentThread();
				String tn = current.getName();

				HRESULT hr = this.comThread.execute(new Callable<HRESULT>() {
					@Override
					public HRESULT call() throws Exception {
						IID iid = com.sun.jna.platform.win32.COM.IUnknown.IID_IUNKNOWN;
						return ProxyObject.this.getRawDispatch().QueryInterface(new REFIID(iid), ppvObject);
					}
				});

				if (WinNT.S_OK.equals(hr)) {
					Dispatch dispatch = new Dispatch(ppvObject.getValue());
					this.unknownId = Pointer.nativeValue(dispatch.getPointer());
					// QueryInterface returns a COM object pointer with a +1
					// reference, we must drop one,
					// Note: createProxy adds one;
					int n = dispatch.Release();
				} else {
					String formatMessageFromHR = Kernel32Util.formatMessage(hr);
					throw new COMException("getUnknownId: " + formatMessageFromHR);
				}
			} catch (Exception e) {
				throw new COMException("Error occured when trying get Unknown Id ", e);
			}
		}
		return this.unknownId;
	}

	@Override
	protected void finalize() throws Throwable {
	    try {
	        this.dispose(1);
        } finally {
            super.finalize();
        }
	}

	public void dispose(int r) {
		if (((Dispatch) this.rawDispatch).getPointer().equals(Pointer.NULL)) {
			// do nothing, already disposed
		} else {
			for (int i = 0; i < r; ++i) {
				// catch result to help with debug
				int n = this.rawDispatch.Release();
				int n2 = n;
			}
			this.factory.unregister(this, r);
			((Dispatch) this.rawDispatch).setPointer(Pointer.NULL);
		}
	}

	Class<?> theInterface;
	Factory factory;
	ComThread comThread;
	com.sun.jna.platform.win32.COM.IDispatch rawDispatch;

	@Override
    public com.sun.jna.platform.win32.COM.IDispatch getRawDispatch() {
		return this.rawDispatch;
	}

	// -------------------- Object -------------------------

	/*
	 * The QueryInterface rule state that 'a call to QueryInterface with
	 * IID_IUnknown must always return the same physical pointer value.'
	 *
	 * [http://msdn.microsoft.com/en-us/library/ms686590%28VS.85%29.aspx]
	 *
	 * therefore we can compare the pointers
	 */
	@Override
    public boolean equals(Object arg) {
		if (null == arg) {
			return false;
		} else if (arg instanceof ProxyObject) {
			ProxyObject other = (ProxyObject) arg;
			return this.getUnknownId() == other.getUnknownId();
		} else if (Proxy.isProxyClass(arg.getClass())) {
			InvocationHandler handler = Proxy.getInvocationHandler(arg);
			if (handler instanceof ProxyObject) {
				try {
					ProxyObject other = (ProxyObject) handler;
					return this.getUnknownId() == other.getUnknownId();
				} catch (Exception e) {
					// if can't do this comparison, return false
					// (queryInterface may throw if COM objects become invalid)
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	};

	@Override
	public int hashCode() {
	    long id = this.getUnknownId();
		return (int) ((id >>> 32) & 0xFFFFFFFF) + (int) (id & 0xFFFFFFFF);
	}

	@Override
	public String toString() {
		return this.theInterface.getName() + "{unk=" + this.hashCode() + "}";
	}

	// --------------------- InvocationHandler -----------------------------
	@Override
	public Object invoke(final Object proxy, final java.lang.reflect.Method method, final Object[] args)
			throws Throwable {
		return this.invokeSynchronised(proxy, method, args);
	}

	/*
	 * may not necessary for this method to be synchronised as all calls to COM
	 * are on their own , single, thread. However, might be best not to overlap
	 * calls to COM object with advise, unadvise, queryInterface, etc.
	 */
	synchronized Object invokeSynchronised(final Object proxy, final java.lang.reflect.Method method,
			final Object[] args) throws Throwable {
		if (method.equals(Object.class.getMethod("toString"))) {
			return this.toString();
		} else if (method.equals(Object.class.getMethod("equals", Object.class))) {
			return this.equals(args[0]);
		} else if (method.equals(Object.class.getMethod("hashCode"))) {
			return this.hashCode();
		} else if (method.equals(IRawDispatchHandle.class.getMethod("getRawDispatch"))) {
			return this.getRawDispatch();
		} else if (method.equals(IUnknown.class.getMethod("queryInterface", Class.class))) {
			return this.queryInterface((Class<?>) args[0]);
		} else if (method.equals(IConnectionPoint.class.getMethod("advise", Class.class,
				IComEventCallbackListener.class))) {
			return this.advise((Class<?>) args[0], (IComEventCallbackListener) args[1]);
		} else if (method.equals(IConnectionPoint.class.getMethod("unadvise", Class.class,
				IComEventCallbackCookie.class))) {
			this.unadvise((Class<?>) args[0], (IComEventCallbackCookie) args[1]);
			return null;
		}

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
				return this.getProperty(returnType, propName, args);
			}
		}

		ComMethod meth = method.getAnnotation(ComMethod.class);
		if (null != meth) {
			String methName = this.getMethodName(method, meth);
			Object res = this.invokeMethod(returnType, methName, args);
			return res;
		}

		return null;
	}

	// ---------------------- IConnectionPoint ----------------------
	ConnectionPoint fetchRawConnectionPoint(IID iid) throws InterruptedException, ExecutionException, TimeoutException {
		// query for ConnectionPointContainer
		IConnectionPointContainer cpc = this.queryInterface(IConnectionPointContainer.class);
		Dispatch rawCpcDispatch = (Dispatch) cpc.getRawDispatch();
		final ConnectionPointContainer rawCpc = new ConnectionPointContainer(rawCpcDispatch.getPointer());

		// find connection point for comEventCallback interface
		final REFIID adviseRiid = new REFIID(iid.getPointer());
		final PointerByReference ppCp = new PointerByReference();
		HRESULT hr = factory.getComThread().execute(new Callable<HRESULT>() {
			@Override
			public HRESULT call() throws Exception {
				return rawCpc.FindConnectionPoint(adviseRiid, ppCp);
			}
		});
		COMUtils.checkRC(hr);
		final ConnectionPoint rawCp = new ConnectionPoint(ppCp.getValue());
		return rawCp;
	}

	public IComEventCallbackCookie advise(Class<?> comEventCallbackInterface,
			final IComEventCallbackListener comEventCallbackListener) {
		try {
			ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"advise: Interface must define a value for either iid via the ComInterface annotation");
			}
			final IID iid = this.getIID(comInterfaceAnnotation);

			final ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);

			// create the dispatch listener
			final IDispatchCallback rawListener = new CallbackProxy(this.factory, comEventCallbackInterface,
					comEventCallbackListener);
			// store it the comEventCallback argument, so it is not garbage
			// collected.
			comEventCallbackListener.setDispatchCallbackListener(rawListener);
			// set the dispatch listener to listen to events from the connection
			// point
			final DWORDByReference pdwCookie = new DWORDByReference();
			HRESULT hr = factory.getComThread().execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					return rawCp.Advise(rawListener, pdwCookie);
				}
			});
			int n = rawCp.Release(); // release before check in case check
										// throws exception
			COMUtils.checkRC(hr);

			// return the cookie so that a call to stop listening can be made
			return new ComEventCallbackCookie(pdwCookie.getValue());

		} catch (Exception e) {
			throw new COMException("Error occured in advise when trying to connect the listener "
					+ comEventCallbackListener, e);
		}
	}

	public void unadvise(Class<?> comEventCallbackInterface, final IComEventCallbackCookie cookie) {
		try {
			ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"unadvise: Interface must define a value for iid via the ComInterface annotation");
			}
			IID iid = this.getIID(comInterfaceAnnotation);

			final ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);

			HRESULT hr = factory.getComThread().execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					return rawCp.Unadvise(((ComEventCallbackCookie) cookie).getValue());
				}
			});

			rawCp.Release();
			COMUtils.checkRC(hr);

		} catch (Exception e) {
			throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, e);
		}
	}

	// --------------------- IDispatch ------------------------------
	@Override
	public <T> void setProperty(String name, T value) {
		VARIANT v = Convert.toVariant(value);
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getRawDispatch(), name, v);
		COMUtils.checkRC(hr);
	}

	@Override
	public <T> T getProperty(Class<T> returnType, String name, Object... args) {
		VARIANT[] vargs;
		if (null == args) {
			vargs = new VARIANT[0];
		} else {
			vargs = new VARIANT[args.length];
		}
		for (int i = 0; i < vargs.length; ++i) {
			vargs[i] = Convert.toVariant(args[i]);
		}
		Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.getRawDispatch(), name, vargs);
		COMUtils.checkRC(hr);
		Object jobj = Convert.toJavaObject(result);
		if (IComEnum.class.isAssignableFrom(returnType)) {
			return returnType.cast(Convert.toComEnum((Class<? extends IComEnum>) returnType, jobj));
		}
		if (jobj instanceof IDispatch) {
			IDispatch d = (IDispatch) jobj;
			T t = this.factory.createProxy(returnType, d);
			// must release a COM reference, createProxy adds one, as does the
			// call
			int n = d.Release();
			return t;
		}
		return returnType.cast(jobj);
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
			vargs[i] = Convert.toVariant(args[i]);
		}
		Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getRawDispatch(), name, vargs);
		COMUtils.checkRC(hr);

		Object jobj = Convert.toJavaObject(result);
		if (IComEnum.class.isAssignableFrom(returnType)) {
			return returnType.cast(Convert.toComEnum((Class<? extends IComEnum>) returnType, jobj));
		}
		if (jobj instanceof IDispatch) {
			IDispatch d = (IDispatch) jobj;
			T t = this.factory.createProxy(returnType, d);
			// must release a COM reference, createProxy adds one, as does the
			// call
			int n = d.Release();
			return t;
		}
		return returnType.cast(jobj);
	}

	@Override
	public <T> T queryInterface(Class<T> comInterface) throws COMException {
		try {
			ComInterface comInterfaceAnnotation = comInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"queryInterface: Interface must define a value for iid via the ComInterface annotation");
			}
			final IID iid = this.getIID(comInterfaceAnnotation);
			final PointerByReference ppvObject = new PointerByReference();

			HRESULT hr = this.comThread.execute(new Callable<HRESULT>() {
				@Override
				public HRESULT call() throws Exception {
					return ProxyObject.this.getRawDispatch().QueryInterface(new REFIID(iid), ppvObject);
				}
			});

			if (WinNT.S_OK.equals(hr)) {
				Dispatch dispatch = new Dispatch(ppvObject.getValue());
				T t = this.factory.createProxy(comInterface, dispatch);
				// QueryInterface returns a COM object pointer with a +1
				// reference, we must drop one,
				// Note: createProxy adds one;
				int n = dispatch.Release();
				return t;
			} else {
				String formatMessageFromHR = Kernel32Util.formatMessage(hr);
				throw new COMException("queryInterface: " + formatMessageFromHR);
			}
		} catch (Exception e) {
			throw new COMException("Error occured when trying to query for interface " + comInterface.getName(), e);
		}
	}

	IID getIID(ComInterface annotation) {
		String iidStr = annotation.iid();
		if (null != iidStr && !iidStr.isEmpty()) {
			return new IID(iidStr);
		} else {
			throw new COMException("ComInterface must define a value for iid");
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
					HRESULT hr = pDisp.GetIDsOfNames(new REFIID(Guid.IID_NULL), ptName, 1, LOCALE_USER_DEFAULT,
							pdispID);
					return hr;
				}
			});
			COMUtils.checkRC(hr);

			return this.oleMethod(nType, pvResult, pDisp, pdispID.getValue(), pArgs);
		} catch (InterruptedException e) {
			throw new COMException(e);
		} catch (ExecutionException e) {
			throw new COMException(e);
		} catch (TimeoutException e) {
			throw new COMException(e);
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
		final DISPPARAMS.ByReference dp = new DISPPARAMS.ByReference();
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
					return pDisp.Invoke(dispId, new REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT,
							new WinDef.WORD(nType), dp, pvResult, pExcepInfo, puArgErr);
				}
			});

			COMUtils.checkRC(hr, pExcepInfo, puArgErr);
			return hr;
		} catch (InterruptedException e) {
			throw new COMException(e);
		} catch (ExecutionException e) {
			throw new COMException(e);
		} catch (TimeoutException e) {
			throw new COMException(e);
		}
	}
}
