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

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author dblock[at]dblock[dot]org
 */
@SuppressWarnings("serial")
public interface BaseTSD extends StdCallLibrary {
	/**
	 * Signed long type for pointer precision. 
	 * Use when casting a pointer to a long to perform pointer arithmetic. 
	 */
	public static class LONG_PTR extends IntegerType {
		public LONG_PTR() {
			this(0);
		}

		public LONG_PTR(long value) {
			super(Pointer.SIZE, value);
		}
	}
	
	/**
	 * Signed SIZE_T. 
	 */
	public static class SSIZE_T extends LONG_PTR {
		public SSIZE_T() {
			this(0);
		}

		public SSIZE_T(long value) {
			super(value);
		}
	}

	/**
	 * Unsigned LONG_PTR. 
	 */
	public static class ULONG_PTR extends IntegerType {
		public ULONG_PTR() {
			this(0);
		}

		public ULONG_PTR(long value) {
			super(Pointer.SIZE, value);
		}
	}

	/**
	 * Unsigned DWORD_PTR. 
	 */
	public static class DWORD_PTR extends IntegerType {
		public DWORD_PTR() {
			this(0);
		}

		public DWORD_PTR(long value) {
			super(Pointer.SIZE, value);
		}
	}

	/**
	 * The maximum number of bytes to which a pointer can point. 
	 * Use for a count that must span the full range of a pointer. 
	 */
	public static class SIZE_T extends ULONG_PTR {
		public SIZE_T() {
			this(0);
		}

		public SIZE_T(long value) {
			super(value);
		}
	}
}
