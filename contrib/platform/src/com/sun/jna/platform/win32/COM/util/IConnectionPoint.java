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

import com.sun.jna.platform.win32.COM.COMException;

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
	void unadvise(Class<?> comEventCallbackInterface, final IComEventCallbackCookie cookie);
}
