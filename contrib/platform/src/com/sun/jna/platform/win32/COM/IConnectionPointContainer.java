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

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public interface IConnectionPointContainer extends IUnknown {
	 public final static IID IID_IConnectionPointContainer = new IID("B196B284-BAB4-101A-B69C-00AA00341D07");
	 
	 /**
	  * {@code
	  *   HRESULT FindConnectionPoint(
	  *     [in]   REFIID riid,
	  *     [out]  IConnectionPoint **ppCP
	  *   );
	  * }
	  * @param riid
	  * @param ppCP
	  * @return hresult
	  */
	 public HRESULT FindConnectionPoint(  REFIID riid,  PointerByReference ppCP );
}
