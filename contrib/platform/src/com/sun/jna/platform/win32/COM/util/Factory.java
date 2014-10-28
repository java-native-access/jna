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
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;

public class Factory {
	
	public static void initializeThreadForComAccess() {
		WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
		COMUtils.checkRC(hr);
	}
	
	public static void releaseThreadFromComAccess() {
		Ole32.INSTANCE.CoUninitialize();
	}
	
	/**
	 * CoInitialize must be called be fore this method. Either explicitly or
	 * implicitly via other methods.
	 * 
	 * @return
	 */
	static public IRunningObjectTable getRunningObjectTable() {
		PointerByReference rotPtr = new PointerByReference();
		WinNT.HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(
				new WinDef.DWORD(0), rotPtr);
		COMUtils.checkRC(hr);
		com.sun.jna.platform.win32.COM.RunningObjectTable raw = new com.sun.jna.platform.win32.COM.RunningObjectTable(rotPtr.getValue());
		IRunningObjectTable rot = new RunningObjectTable(raw);
		return rot;
	}
	
	/**
	 * Creates a ProxyObject for the given interface and IDispatch pointer.
	 * 
	 */
	public static <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
		ProxyObject jop = new ProxyObject(dispatch);
		Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class<?>[] { comInterface }, jop);
		T result = comInterface.cast(proxy);
		return result;
	}

	/**
	 * Creates a new COM object (CoCreateInstance) for the given progId and returns a ProxyObject
	 * for the given interface. 
	 */
	public static <T> T createObject(Class<T> comInterface) {
		ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
		if (null==comObectAnnotation) {
			throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
		}
		GUID guid = Factory.discoverClsId(comObectAnnotation);
	    
	    PointerByReference ptrDisp = new PointerByReference();
		WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateInstance(guid, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ptrDisp);
		COMUtils.checkRC(hr);
		
		T t = Factory.createProxy(comInterface, new Dispatch(ptrDisp.getValue()));
		return t;
	}
	
	/**
	 * Gets and existing COM object (GetActiveObject) for the given progId and returns a ProxyObject
	 * for the given interface. 
	 */
	public static <T> T fetchObject(Class<T> comInterface) {
		ComObject comObectAnnotation = comInterface.getAnnotation(ComObject.class);
		if (null==comObectAnnotation) {
			throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
		}
		GUID guid = Factory.discoverClsId(comObectAnnotation);
	    
	    PointerByReference ptrDisp = new PointerByReference();
	    WinNT.HRESULT hr = OleAuto.INSTANCE.GetActiveObject(guid, null, ptrDisp);
		COMUtils.checkRC(hr);

		T t = Factory.createProxy(comInterface, new Dispatch(ptrDisp.getValue()));
		return t;
	}
	
	static GUID discoverClsId(ComObject annotation) {
		String clsIdStr = annotation.clsId();
		String progIdStr = annotation.progId();
		if (null!=clsIdStr && !clsIdStr.isEmpty()) {
			return new CLSID(clsIdStr);
		} else if (null!=progIdStr && !progIdStr.isEmpty()) {
			CLSID.ByReference rclsid = new CLSID.ByReference();
		    WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);
		    COMUtils.checkRC(hr);
		    return rclsid;
		} else {
			throw new COMException("ComObject must define a value for either clsId or progId");
		}
	}
}
