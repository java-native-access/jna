/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from LMCons.h.
 * @author dblock[at]dblock.org
 * Windows SDK 6.0A
 */
public interface LMCons extends StdCallLibrary {
	public static final int  NETBIOS_NAME_LEN = 16;            // NetBIOS net name (bytes)

	/**
	 * Value to be used with APIs which have a "preferred maximum length" parameter.
	 * This value indicates that the API should just allocate "as much as it takes."
	 */
	public static final int  MAX_PREFERRED_LENGTH = -1;
}
