/* Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
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
package com.sun.jna.platform;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;

/**
 * Utility methods to be used in tests.
 * 
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public class Utils {
	private Utils() {
		// prevent instantiations of utility class
	}

	/**
	 * @return Obtains the human-readable error message text from the last error
	 *         that occurred by invocating {@code Kernel32.GetLastError()}.
	 */
	public static String getLastErrorMessage() {
		return Kernel32Util.formatMessageFromLastErrorCode(Kernel32.INSTANCE
				.GetLastError());
	}
}
