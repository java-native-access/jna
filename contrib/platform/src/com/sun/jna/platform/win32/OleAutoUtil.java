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
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;

/**
 * The Class OleAut32Util.
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public abstract class OleAutoUtil {

	/**
	 * Creates a new the variant array.
	 *
	 * @param size the size
	 * @return the sAFEARRA y. by reference
	 */
	public static SAFEARRAY createVarArray(int size) {
		SAFEARRAY psa;
		SAFEARRAYBOUND[] rgsabound = new SAFEARRAYBOUND[1];
		rgsabound[0] = new SAFEARRAYBOUND(size, 0);

		psa = OleAuto.INSTANCE.SafeArrayCreate(
				new VARTYPE(Variant.VT_VARIANT), 1, rgsabound);

		return psa;
	}

	/**
	 * Safe array put element.
	 *
	 * @param array the array
	 * @param index the index
	 * @param arg the arg
	 */
	public static void SafeArrayPutElement(SAFEARRAY array, long index,
			VARIANT arg) {
		long[] idx = new long[1];
		idx[0] = index;
		HRESULT hr = OleAuto.INSTANCE.SafeArrayPutElement(array, idx, arg);
		COMUtils.SUCCEEDED(hr);
	}

	/**
	 * Safe array get element.
	 *
	 * @param array the array
	 * @param index the index
	 * @return the variant
	 */
	public static VARIANT SafeArrayGetElement(SAFEARRAY array, long index) {
		long[] idx = new long[1];
		idx[0] = index;
		VARIANT result = new VARIANT();
		HRESULT hr = OleAuto.INSTANCE.SafeArrayGetElement(array, idx,
				result.getPointer());
		COMUtils.SUCCEEDED(hr);
		return result;
	}
}
