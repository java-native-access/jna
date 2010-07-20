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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from Guid.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface Guid extends StdCallLibrary {
	
	public static class GUID extends Structure {
		
		public static class ByReference extends GUID implements Structure.ByReference {
			public ByReference() {
				
			}
			
			public ByReference(Pointer memory) {
				super(memory);
			}
		}
		
		public GUID() {
			
		}
		    
		public GUID(Pointer memory) {
			useMemory(memory);
		    read();
		}
		    
		public int Data1;
		public short Data2;
		public short Data3;
		public byte[] Data4 = new byte[8];
	}	
}
