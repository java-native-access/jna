/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32.COM.util;

import java.lang.reflect.Proxy;

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
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Factory keeps track of COM objects - all objects created with this factory
 * can be disposed by calling {@link Factory#disposeAll() }.
 */
public class ObjectFactory {	
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
	public <T> T fetchObject(Class<T> comInterface) throws COMException {
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
        
        IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
            return new CallbackProxy(this, comEventCallbackInterface, comEventCallbackListener);
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
        
        /**
         * The Constant LOCALE_USER_DEFAULT.
         */
        private final static LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
    
        private LCID LCID;
        
        /**
         * Retrieve the LCID to be used for COM calls. 
         * 
         * @return If {@code setLCID} is not called retrieves the users default
         *         locale, else the set LCID.
         */
        public LCID getLCID() {
            if(LCID != null) {
                return LCID;
            } else {
                return LOCALE_USER_DEFAULT;
            }
        }
        
        /**
         * Set the LCID to use for COM calls.
         * 
         * @param value override LCID. NULL resets to default.
         */
        public void setLCID(LCID value) {
            LCID = value;
        }
}
