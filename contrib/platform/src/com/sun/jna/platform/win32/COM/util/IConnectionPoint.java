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

@ComInterface(iid="{B196B284-BAB4-101A-B69C-00AA00341D07}")
public interface IConnectionPoint {

	/**
	 * Set up the comEventCallbackListener to receive callback events from the target COM object
	 * 
	 * @param comEventCallbackInterface - the COM interface that the listener will receive events from
	 * @param comEventCallbackListener - and object that will receive the callback events
	 * @return - a cookie that can be used to detach (unadvise) the event callback listener
	 * 
	 * throws COMException if an error occurs trying to set up the listener on the target COM object,
	 * see exception cause for details.
	 * 
	 */
	IComEventCallbackCookie advise(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) throws COMException;
	
	/**
	 * Stop listening for callback events
	 * 
	 * @param comEventCallbackInterface - the interface that is being listened to
	 * @param cookie - the cookie that was returned when advise was called
	 */
	void unadvise(Class<?> comEventCallbackInterface, final IComEventCallbackCookie cookie) throws COMException;
}
