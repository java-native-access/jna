/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.examples.win32;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

/**
 * Secur32.dll Interface.
 * @author dblock[at]dblock.org
 */
public interface Secur32 extends W32API {
	Secur32 INSTANCE = (Secur32) Native.loadLibrary(
			"Secur32", Secur32.class, UNICODE_OPTIONS);
	
	/**
	 * Specifies a format for a directory service object name.
	 * http://msdn.microsoft.com/en-us/library/ms724268(VS.85).aspx
	 */
	public abstract class EXTENDED_NAME_FORMAT {
		public static final int NameUnknown = 0;
		public static final int NameFullyQualifiedDN = 1;
		public static final int NameSamCompatible = 2;
		public static final int NameDisplay = 3;
		public static final int NameUniqueId = 6;
		public static final int NameCanonical = 7;
		public static final int NameUserPrincipal = 8;
		public static final int NameCanonicalEx = 9;
		public static final int NameServicePrincipal = 10;
		public static final int NameDnsDomain = 12;
	};
	
	/**
	 * Retrieves the name of the user or other security principal associated with 
	 * the calling thread. You can specify the format of the returned name.
	 * @param nameFormat The format of the name. 
	 * @param lpNameBuffer A pointer to a buffer that receives the name in the specified format. 
	 * @param len On input, the size of the buffer, on output the number of characters copied into the buffer, not including the terminating null character.
	 * @return True if the function succeeds. False otherwise.
	 */
	public boolean GetUserNameEx(int nameFormat, char[] lpNameBuffer, IntByReference len);
}
