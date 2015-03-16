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

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.ptr.PointerByReference;

public class Factory {

	/**
	 * Creates a utility COM Factory and a ComThread on which all COM calls are executed.
	 * NOTE: Remember to call factory.getComThread().terminate() at some appropriate point.
	 * 
	 */
	public Factory() {
		this(new ComThread("Default Factory COM Thread", 5000, new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				//ignore
			}
		}));
	}

	public Factory(ComThread comThread) {
		this.comThread = comThread;
		this.registeredObjects = new WeakHashMap<ProxyObject, Integer>();
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			this.disposeAll();
		} finally {
			super.finalize();
		}
	}
	
	ComThread comThread;
	public ComThread getComThread() {
		return this.comThread;
	}

	/**
	 * CoInitialize must be called be fore this method. Either explicitly or
	 * implicitly via other methods.
	 * 
	 * @return
	 */
	public IRunningObjectTable getRunningObjectTable() {
		try {

			final PointerByReference rotPtr = new PointerByReference();

			WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0), rotPtr);
				}
			});
			COMUtils.checkRC(hr);
			com.sun.jna.platform.win32.COM.RunningObjectTable raw = new com.sun.jna.platform.win32.COM.RunningObjectTable(
					rotPtr.getValue());
			IRunningObjectTable rot = new RunningObjectTable(raw, this);
			return rot;

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a ProxyObject for the given interface and IDispatch pointer.
	 * 
	 */
	public <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
		ProxyObject jop = new ProxyObject(comInterface, dispatch, this);
		Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class<?>[] { comInterface }, jop);
		T result = comInterface.cast(proxy);
		return result;
	}

	/** only for use when creating ProxyObjects from Callbacks
	 * 
	 * @param comInterface
	 * @param unk
	 * @param dispatch
	 * @return
	 */
	<T> T createProxy(Class<T> comInterface, long unknownId, IDispatch dispatch) {
		ProxyObject jop = new ProxyObject(comInterface, unknownId, dispatch, this);
		Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class<?>[] { comInterface }, jop);
		T result = comInterface.cast(proxy);
		return result;
	}
	
	/**
	 * Creates a new COM object (CoCreateInstance) for the given progId and
	 * returns a ProxyObject for the given interface.
	 */
	public <T> T createObject(Class<T> comInterface) {
		try {

			ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
			if (null == comObectAnnotation) {
				throw new COMException(
						"createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
			}
			final GUID guid = this.discoverClsId(comObectAnnotation);

			final PointerByReference ptrDisp = new PointerByReference();
			WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return Ole32.INSTANCE.CoCreateInstance(guid, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH,
							ptrDisp);
				}
			});
			COMUtils.checkRC(hr);
			Dispatch d = new Dispatch(ptrDisp.getValue());
			T t = this.createProxy(comInterface,d);
			//CoCreateInstance returns a pointer to COM object with a +1 reference count, so we must drop one
			//Note: the createProxy adds one
			int n = d.Release();
			return t;

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets and existing COM object (GetActiveObject) for the given progId and
	 * returns a ProxyObject for the given interface.
	 */
	public <T> T fetchObject(Class<T> comInterface) {
		try {
			ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
			if (null == comObectAnnotation) {
				throw new COMException(
						"createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
			}
			final GUID guid = this.discoverClsId(comObectAnnotation);

			final PointerByReference ptrDisp = new PointerByReference();
			WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return OleAuto.INSTANCE.GetActiveObject(guid, null, ptrDisp);
				}
			});
			COMUtils.checkRC(hr);
			Dispatch d = new Dispatch(ptrDisp.getValue());
			T t = this.createProxy(comInterface, d);
			//GetActiveObject returns a pointer to COM object with a +1 reference count, so we must drop one
			//Note: the createProxy adds one
			d.Release();
			
			return t;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	GUID discoverClsId(ComObject annotation) {
		try {
			String clsIdStr = annotation.clsId();
			final String progIdStr = annotation.progId();
			if (null != clsIdStr && !clsIdStr.isEmpty()) {
				return new CLSID(clsIdStr);
			} else if (null != progIdStr && !progIdStr.isEmpty()) {
				final CLSID.ByReference rclsid = new CLSID.ByReference();

				WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
					@Override
					public WinNT.HRESULT call() throws Exception {
						return Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);
					}
				});

				COMUtils.checkRC(hr);
				return rclsid;
			} else {
				throw new COMException("ComObject must define a value for either clsId or progId");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	//factory needs to keep a register of all handles to COM objects so that it can clean them up properly
	// (if java had an out of scope clean up destructor like C++, this wouldn't be needed)
	WeakHashMap<ProxyObject, Integer> registeredObjects;
	public void register(ProxyObject proxyObject) {
		synchronized (this.registeredObjects) {
			//ProxyObject identity resolves to the underlying native pointer value
			// different java ProxyObjects will resolve to the same pointer
			// thus we need to count the number of references.
			if (this.registeredObjects.containsKey(proxyObject)) {
				int r = this.registeredObjects.get(proxyObject);
				this.registeredObjects.put(proxyObject, r+1);
			} else {
				this.registeredObjects.put(proxyObject, 1);
			}
		}
	}
	
	public void unregister(ProxyObject proxyObject, int d) {
		synchronized (this.registeredObjects) {
			if (this.registeredObjects.containsKey(proxyObject)) {
				int r = this.registeredObjects.get(proxyObject);
				if (r > 1) {
					this.registeredObjects.put(proxyObject, r-d);
				} else {
					this.registeredObjects.remove(proxyObject);
				}
			} else {
				throw new RuntimeException("Tried to dispose a ProxyObject that is not registered");
			}
			
		}
	}
	
	public void disposeAll() {
		synchronized (this.registeredObjects) {
			Set<ProxyObject> s = new HashSet<ProxyObject>(this.registeredObjects.keySet());
			for(ProxyObject proxyObject : s) {
				int r = this.registeredObjects.get(proxyObject);
				proxyObject.dispose(r);
			}
			this.registeredObjects.clear();
		}
	}
}
