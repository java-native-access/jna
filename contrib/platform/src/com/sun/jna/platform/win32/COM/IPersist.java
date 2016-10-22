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
