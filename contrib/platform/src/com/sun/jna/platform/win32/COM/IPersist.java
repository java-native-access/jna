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

import com.sun.jna.platform.win32.Guid.CLSID;

/**
 * Provides the CLSID of an object that can be stored persistently in the
 * system. Allows the object to specify which object handler to use in the
 * client process, as it is used in the default implementation of marshaling.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms688695%28v=vs.85%29.aspx">MSDN</a>
 * 
 */
public interface IPersist extends IUnknown {

	/**
	 * Retrieves the class identifier (CLSID) of the object.
	 * 
	 * {@code
	 *   HRESULT GetClassID(
	 *     [out]  CLSID *pClassID
	 *   );
	 * }
	 * 
	 * <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms688664%28v=vs.85%29.aspx">MSDN</a>
	 */
	CLSID GetClassID();
}
