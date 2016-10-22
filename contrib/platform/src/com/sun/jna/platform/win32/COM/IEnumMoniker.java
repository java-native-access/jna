/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
