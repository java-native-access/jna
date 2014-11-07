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
import java.util.Date;

import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.Variant.VARIANT;

public class Convert {
	
	public static VARIANT toVariant(Object value) {
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
			return new VARIANT(pobj.getRawDispatch());
		}
		if (value instanceof IComEnum) {
			IComEnum enm = (IComEnum) value;
			return new VARIANT(new WinDef.LONG(enm.getValue()));
		} else {
			return null;
		}
	}
	
	public static Object toJavaObject(VARIANT value) {
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
}
