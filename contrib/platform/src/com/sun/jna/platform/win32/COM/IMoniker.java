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

import com.sun.jna.Pointer;


/**
 * Enables you to use a moniker object, which contains information that uniquely
 * identifies a COM object.
 * 
 * (Unimplemented, placeholder only at present)
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms679705%28v=vs.85%29.aspx">MSDN</a>
 * 
 */
public interface IMoniker extends IPersistStream {

	/**
	 * Binds to the specified object. The binding process involves finding the
	 * object, putting it into the running state if necessary, and providing the
	 * caller with a pointer to a specified interface on the identified object.
	 * 
	 * {@code
	 *   HRESULT BindToObject(
	 *     [in]   IBindCtx *pbc,
	 *     [in]   IMoniker *pmkToLeft,
	 *     [in]   REFIID riidResult,
	 *     [out]  void **ppvResult
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms691433%28v=vs.85%29.aspx">MSDN</a>
	 */
	void BindToObject();

	void BindToStorage();

	void Reduce();

	void ComposeWith();

	void Enum();

	void IsEqual();

	void Hash();

	void IsRunning();

	void GetTimeOfLastChange();

	void Inverse();

	void CommonPrefixWith();

	/**
	 * Retrieves the display name for the moniker.
	 * 
	 * {@code
	 *   HRESULT GetDisplayName(
	 *     [in]   IBindCtx *pbc,
	 *     [in]   IMoniker *pmkToLeft,
	 *     [out]  LPOLESTR *ppszDisplayName
	 *   );
	 * }
         * 
	 * @see <a href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms680754%28v=vs.85%29.aspx">MSDN</a>
	 */
	String GetDisplayName(Pointer bindContext, Pointer pmkToLeft);

	void ParseDisplayName();

	void IsSystemMoniker();
	
	void RelativePathTo();
}
