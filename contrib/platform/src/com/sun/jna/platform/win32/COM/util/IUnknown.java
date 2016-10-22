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

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;

/**
 * Java friendly version of the IUnknown interface.
 * 
 *
 */
@ComInterface(iid="{00000000-0000-0000-C000-000000000046}")
public interface IUnknown {
	/**
	 * Returns a proxy object for the given interface. Assuming that the
	 * interface is annotated with a ComInterface annotation that provides a
	 * valid iid.
	 * 
	 * Will throw COMException if an error occurs trying to retrieve the requested interface,
	 * see exception cause for details.  
	 * 
	 */
	<T> T queryInterface(Class<T> comInterface) throws COMException;
}
