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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from Wdm.h.
 * Microsoft Windows DDK.
 * @author dblock[at]dblock.org
 */
public interface Wdm extends StdCallLibrary {
	
	/**
	 * The KEY_BASIC_INFORMATION structure defines a subset of 
	 * the full information that is available for a registry key.
	 */
	public static class KEY_BASIC_INFORMATION extends Structure {		
		public KEY_BASIC_INFORMATION() {
			super();
		}

		public KEY_BASIC_INFORMATION(int size) {
			NameLength = size - 16; // write time, title index and name length
			Name = new char[NameLength];
			allocateMemory();
		}
		
		public KEY_BASIC_INFORMATION(Pointer memory) {
			useMemory(memory);
			read();
		}
				
		/**
		 * The last time the key or any of its values changed. 
		 */
		public long LastWriteTime;
		/**
		 * Device and intermediate drivers should ignore this member.
		 */
		public int TitleIndex;
		/**
		 * Specifies the size in bytes of the following name. 
		 */
		public int NameLength;
		/**
		 * A string of Unicode characters naming the key. 
		 * The string is not null-terminated.
		 */
		public char[] Name;
		/**
		 * Name of the key.
		 * @return String.
		 */
		public String getName() {
			return Native.toString(Name);
		}
		
		public void read() {
			super.read();
			Name = new char[NameLength / 2];
			readField("Name");			
		}
	}
	
	/**
	 * The KEY_INFORMATION_CLASS enumeration type represents 
	 * the type of information to supply about a registry key.
	 */
	public abstract class KEY_INFORMATION_CLASS { 
		public static final int KeyBasicInformation = 0;
		public static final int KeyNodeInformation = 1;
		public static final int KeyFullInformation = 2;
		public static final int KeyNameInformation = 3;
		public static final int KeyCachedInformation = 4;
		public static final int KeyVirtualizationInformation = 5;
	}
}
