/* Copyright (c) 2010,2011 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.platform.win32.WinNT.HRESULT;

// TODO: Auto-generated Javadoc
/**
 * Utility class for some common error functions.
 */
public abstract class W32Errors implements WinError {
	
	/**
	 * Generic test for success on any status value (non-negative numbers
	 * indicate success).
	 *
	 * @param hr the hr
	 * @return true, if successful
	 */
	public static final boolean SUCCEEDED(int hr) {
		return hr >= 0;
	}

	/**
	 * and the inverse.
	 *
	 * @param hr the hr
	 * @return true, if successful
	 */
	public static final boolean FAILED(int hr) {
		return hr < 0;
	}

	/**
	 * Succeeded.
	 *
	 * @param hr the hr
	 * @return true, if successful
	 */
	public static final boolean SUCCEEDED(HRESULT hr) {
		if (hr != null)
			return SUCCEEDED(hr.intValue());
		else
			return false;
	}

	/**
	 * Failed.
	 *
	 * @param hr the hr
	 * @return true, if successful
	 */
	public static final boolean FAILED(HRESULT hr) {
		if (hr != null)
			return FAILED(hr.intValue());
		else
			return false;
	}

	/**
	 * Extract error code from HRESULT.
	 *
	 * @param hr the hr
	 * @return the int
	 */
	public static final int HRESULT_CODE(int hr) {
		return hr & 0xFFFF;
	}

	/**
	 * Extract error code from SCODE.
	 *
	 * @param sc the sc
	 * @return the int
	 */
	public static final int SCODE_CODE(int sc) {
		return sc & 0xFFFF;
	}

	/**
	 * Return the facility.
	 *
	 * @param hr the hr
	 * @return the int
	 */
	public static final int HRESULT_FACILITY(int hr) {
		return (hr >>= 16) & 0x1fff;
	}

	/**
	 * Scode facility.
	 *
	 * @param sc the sc
	 * @return the int
	 */
	public static final int SCODE_FACILITY(short sc) {
		return (sc >>= 16) & 0x1fff;
	}

	/**
	 * Return the severity.
	 *
	 * @param hr the hr
	 * @return the short
	 */
	public static short HRESULT_SEVERITY(int hr) {
		return (short) ((hr >>= 31) & 0x1);
	}

	/**
	 * Scode severity.
	 *
	 * @param sc the sc
	 * @return the short
	 */
	public static short SCODE_SEVERITY(short sc) {
		return (short) ((sc >>= 31) & 0x1);
	}

	/**
	 * Create an HRESULT value from component pieces.
	 *
	 * @param sev the sev
	 * @param fac the fac
	 * @param code the code
	 * @return the int
	 */
	public static int MAKE_HRESULT(short sev, short fac, short code) {
		return ((sev << 31) | (fac << 16) | code);
	}

	/**
	 * Make scode.
	 *
	 * @param sev the sev
	 * @param fac the fac
	 * @param code the code
	 * @return the int
	 */
	public static final int MAKE_SCODE(short sev, short fac, short code) {
		return ((sev << 31) | (fac << 16) | code);
	}

	/**
	 * Map a WIN32 error value into a HRESULT Note: This assumes that WIN32
	 * errors fall in the range -32k to=32k.
	 * 
	 * @param x
	 *            original w32 error code
	 * @return the converted value
	 */
	public static final HRESULT HRESULT_FROM_WIN32(int x) {
		int f = FACILITY_WIN32;
		return new HRESULT(x <= 0 ? x : ((x) & 0x0000FFFF) | (f <<= 16)
				| 0x80000000);
	}

	/**
	 * FACILITY_USERMODE_FILTER_MANAGER
	 * 
	 * Translation macro for converting: NTSTATUS --> HRESULT.
	 *
	 * @param x the x
	 * @return the int
	 */
	public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int x) {
		int f = FACILITY_USERMODE_FILTER_MANAGER;
		return (((x) & 0x8000FFFF) | (f <<= 16));
	}

}
