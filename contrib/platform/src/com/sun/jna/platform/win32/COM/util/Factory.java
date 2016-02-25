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
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Factory {

	/**
	 * Creates a utility COM Factory and a ComThread on which all COM calls are executed.
	 * NOTE: Remember to call factory.getComThread().terminate() at some appropriate point.
	 * 
	 */
	public Factory() {
            assert COMUtils.comIsInitialized() : "COM not initialized";
	}

	
	@Override
	protected void finalize() throws Throwable {
		try {
			this.disposeAll();
		} finally {
			super.finalize();
		}
	}

	/**
	 * CoInitialize must be called be fore this method. Either explicitly or
	 * implicitly via other methods.
	 * 
	 * @return running object table
	 */
	public IRunningObjectTable getRunningObjectTable() {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                final PointerByReference rotPtr = new PointerByReference();

                HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0), rotPtr);

                COMUtils.checkRC(hr);
                com.sun.jna.platform.win32.COM.RunningObjectTable raw = new com.sun.jna.platform.win32.COM.RunningObjectTable(
                        rotPtr.getValue());
                IRunningObjectTable rot = new RunningObjectTable(raw, this);
                return rot;
        }

	/**
	 * Creates a ProxyObject for the given interface and IDispatch pointer.
	 * 
	 */
	public <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		ProxyObject jop = new ProxyObject(comInterface, dispatch, this);
		Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class<?>[] { comInterface }, jop);
		T result = comInterface.cast(proxy);
		return result;
	}

	/** only for use when creating ProxyObjects from Callbacks
	 * 
	 * @param comInterface
	 * @param unknownId
	 * @param dispatch
	 * @return proxy object
	 */
	<T> T createProxy(Class<T> comInterface, long unknownId, IDispatch dispatch) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
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
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
                if (null == comObectAnnotation) {
                        throw new COMException(
                                        "createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
                }
                final GUID guid = this.discoverClsId(comObectAnnotation);

                final PointerByReference ptrDisp = new PointerByReference();
                WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateInstance(guid, null,
                        WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ptrDisp);

                COMUtils.checkRC(hr);
                Dispatch d = new Dispatch(ptrDisp.getValue());
                T t = this.createProxy(comInterface,d);
                //CoCreateInstance returns a pointer to COM object with a +1 reference count, so we must drop one
                //Note: the createProxy adds one
                int n = d.Release();
                return t;
	}

	/**
	 * Gets and existing COM object (GetActiveObject) for the given progId and
	 * returns a ProxyObject for the given interface.
	 */
	public <T> T fetchObject(Class<T> comInterface) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
                if (null == comObectAnnotation) {
                        throw new COMException(
                                        "createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
                }
                final GUID guid = this.discoverClsId(comObectAnnotation);

                final PointerByReference ptrDisp = new PointerByReference();
                WinNT.HRESULT hr = OleAuto.INSTANCE.GetActiveObject(guid, null, ptrDisp);

                COMUtils.checkRC(hr);
                Dispatch d = new Dispatch(ptrDisp.getValue());
                T t = this.createProxy(comInterface, d);
                //GetActiveObject returns a pointer to COM object with a +1 reference count, so we must drop one
                //Note: the createProxy adds one
                d.Release();

                return t;
	}

	GUID discoverClsId(ComObject annotation) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                String clsIdStr = annotation.clsId();
                final String progIdStr = annotation.progId();
                if (null != clsIdStr && !clsIdStr.isEmpty()) {
                        return new CLSID(clsIdStr);
                } else if (null != progIdStr && !progIdStr.isEmpty()) {
                        final CLSID.ByReference rclsid = new CLSID.ByReference();

                        WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);

                        COMUtils.checkRC(hr);
                        return rclsid;
                } else {
                        throw new COMException("ComObject must define a value for either clsId or progId");
                }
	}

	// Proxy object release their COM interface reference latest in the
        // finalize method, which is run when garbadge collection removes the
        // object.
        // When the factory is finished, the referenced objects loose their
        // environment and can't be used anymore. registeredObjects is used
        // to dispose interfaces even if garbadge collection has not yet collected
        // the proxy objects.
	private final List<WeakReference<ProxyObject>> registeredObjects = new LinkedList<WeakReference<ProxyObject>>();
	public void register(ProxyObject proxyObject) {
            synchronized (this.registeredObjects) {
                this.registeredObjects.add(new WeakReference<ProxyObject>(proxyObject));
            }
	}
	
	public void unregister(ProxyObject proxyObject) {
            synchronized (this.registeredObjects) {
                Iterator<WeakReference<ProxyObject>> iterator = this.registeredObjects.iterator();
                while(iterator.hasNext()) {
                    WeakReference<ProxyObject> weakRef = iterator.next();
                    ProxyObject po = weakRef.get();
                    if(po == null || po == proxyObject) {
                        iterator.remove();
                    }
                }
            }
        }
	
	public void disposeAll() {
            synchronized (this.registeredObjects) {
                List<WeakReference<ProxyObject>> s = new ArrayList<WeakReference<ProxyObject>>(this.registeredObjects);
                for(WeakReference<ProxyObject> weakRef : s) {
                        ProxyObject po = weakRef.get();
                        if(po != null) {
                            po.dispose();
                        }
                }
                this.registeredObjects.clear();
            }
	}
}
