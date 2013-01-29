/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Wrapper class for the ITypeInfo interface
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class ITypeInfo extends IUnknown {
	
	public static class ByReference extends ITypeInfo implements
			Structure.ByReference {
	}
	
	public ITypeInfo() {
	}
	
	public ITypeInfo(Pointer pvInstance) {
		super(pvInstance);
	}
	
	
}
