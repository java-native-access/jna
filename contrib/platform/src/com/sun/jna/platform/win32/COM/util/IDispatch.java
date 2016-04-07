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

import com.sun.jna.platform.win32.OaIdl.DISPID;

/**
 * Java friendly version of {@link com.sun.jna.platform.win32.COM.IDispatch}.
 *
 */
public interface IDispatch extends IUnknown {
	<T> void setProperty(String name, T value);
	<T> T getProperty(Class<T> returnType, String name, Object... args);
	<T> T invokeMethod(Class<T> returnType, String name, Object... args);
	<T> void setProperty(DISPID dispid, T value);
	<T> T getProperty(Class<T> returnType, DISPID dispid, Object... args);
	<T> T invokeMethod(Class<T> returnType, DISPID dispid, Object... args);
}
