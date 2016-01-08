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

import java.util.List;

/**
 * Java friendly version of
 * {@link com.sun.jna.platform.win32.COM.IRunningObjectTable}
 * 
 */
public interface IRunningObjectTable {

	/**
	 * Creates and returns an enumerator of all the objects currently registered
	 * in the running object table (ROT).
	 * 
	 */
	Iterable<IDispatch> enumRunning();

	/**
	 * Gets all the active (running) objects that support the give interface.
	 * 
	 * Enumerates the running objects (via enumRunning), and returns a list of
	 * those for which queryInterface(iid) gives a valid result.
	 * 
	 * @param comInterface
	 * @return active objects
	 */
	<T> List<T> getActiveObjectsByInterface(Class<T> comInterface);
}
