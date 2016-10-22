/* Copyright (c) 2010,2011 Daniel Doubrovkine, All Rights Reserved
 * 
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.WinNT.HRESULT;

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
            return hr == null || SUCCEEDED(hr.intValue());
	}

	/**
	 * Failed.
	 *
	 * @param hr the hr
	 * @return true, if successful
	 */
	public static final boolean FAILED(HRESULT hr) {
            return hr != null && FAILED(hr.intValue());
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
	 * Translation macro for converting: NTSTATUS --&gt; HRESULT.
	 *
	 * @param x the x
	 * @return the int
	 */
	public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int x) {
		int f = FACILITY_USERMODE_FILTER_MANAGER;
		return (((x) & 0x8000FFFF) | (f <<= 16));
	}

}
