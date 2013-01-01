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
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAYBOUND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.ULONG;

/**
 * @author Tobias Wolf, wolf.tobias@gmx.net
 * 
 */
public abstract class OleAut32Util {

	public static SAFEARRAY createVarArray(long size) {
		SAFEARRAY psa;
		SAFEARRAYBOUND[] rgsabound = new SAFEARRAYBOUND[1];
		SAFEARRAYBOUND element = new SAFEARRAYBOUND();

		element.lLbound = new LONG(0);
		element.cElements = new ULONG(size);

		rgsabound[0] = element;

		psa = OleAut32.INSTANCE
				.SafeArrayCreate(Variant.VT_DECIMAL, 1, rgsabound);

		return psa;
	}
}
