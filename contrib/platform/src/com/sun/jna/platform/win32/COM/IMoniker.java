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
