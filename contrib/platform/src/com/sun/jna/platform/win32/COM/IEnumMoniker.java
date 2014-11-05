/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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

import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;

/**
 * Enumerates the components of a moniker or the monikers in a table of monikers.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms692852%28v=vs.85%29.aspx">MSDN</a>
 *
 */
public interface IEnumMoniker extends IUnknown {

	public final static IID IID = new IID("{00000102-0000-0000-C000-000000000046}");
	
	/**
	 * Creates a new enumerator that contains the same enumeration state as the
	 * current one.
	 * 
	 * This method makes it possible to record a particular point in the
	 * enumeration sequence and then return to that point at a later time. The
	 * caller must release this new enumerator separately from the first
	 * enumerator.
	 * 
	 * {@code
	 *   HRESULT Clone(
	 *     [out]  IEnumMoniker **ppenum
	 *   );
	 * }
	 * 
	 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd542676%28v=vs.85%29.aspx">MSDN</a>
	 */
	HRESULT Clone(PointerByReference ppenum);

	/**
	 * Retrieves the specified number of items in the enumeration sequence.
	 * 
	 * Note: The caller is responsible for calling Release through each pointer
	 * enumerated.
	 * 
	 * {@code
	 *   HRESULT Next(
	 *     [in] ULONG celt,
	 *     [out] IMoniker **rgelt,
	 *     [in, out] ULONG *pceltFetched
	 *   );
	 * }
	 * 
	 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd542677%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT Next(ULONG celt, PointerByReference rgelt, ULONGByReference pceltFetched);

	/**
	 * Resets the enumeration sequence to the beginning.
	 * 
	 * {@code
	 *   HRESULT Reset();
	 * }
	 * 
	 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd542678%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT Reset();

	/**
	 * Skips over the specified number of items in the enumeration sequence.
	 * 
	 * {@code
	 *   HRESULT Skip(
	 *     [in]  ULONG celt
	 *   );
	 * }
	 * 
	 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/dd542679%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT Skip(ULONG celt);
}
