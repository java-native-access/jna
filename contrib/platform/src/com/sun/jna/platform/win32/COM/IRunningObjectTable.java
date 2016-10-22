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
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

/**
 * Manages access to the running object table (ROT), a globally accessible
 * look-up table on each workstation.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms695276%28v=vs.85%29.aspx">MSDN</a>
 * 
 */
public interface IRunningObjectTable extends IUnknown {

	public final static IID IID = new IID("{00000010-0000-0000-C000-000000000046}");

	/**
	 * Creates and returns a pointer to an enumerator that can list the monikers
	 * of all the objects currently registered in the running object table
	 * (ROT).
	 * 
	 * {@code
	 *    HRESULT EnumRunning(
	 *      [out] IEnumMoniker **ppenumMoniker
	 *    );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms678491%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT EnumRunning(PointerByReference ppenumMoniker);

	/**
	 * 
	 * Determines whether the object identified by the specified moniker is
	 * running, and if it is, retrieves a pointer to that object.
	 * 
	 * {@code
	 *   HRESULT GetObject(
	 *     [in] IMoniker *pmkObjectName,
	 *     [out] IUnknown **ppunkObject
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms683841%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject);

	/**
	 * Retrieves the time that an object was last modified.
	 * 
	 * {@code
	 *   HRESULT GetTimeOfLastChange(
	 *     [in]   IMoniker *pmkObjectName,
	 *     [out]  FILETIME *pfiletime
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms695243%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT GetTimeOfLastChange(Pointer pmkObjectName, FILETIME.ByReference pfiletime);

	/**
	 * Determines whether the object identified by the specified moniker is
	 * currently running.
	 * 
	 * {@code
	 *   HRESULT IsRunning(
	 *     [in]  IMoniker *pmkObjectName
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms682169%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT IsRunning(Pointer pmkObjectName);

	/**
	 * Records the time that a running object was last modified.
	 * 
	 * {@code
	 *   HRESULT NoteChangeTime(
	 *     [in]  DWORD dwRegister,
	 *     [in]  FILETIME *pfiletime
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms687204%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT NoteChangeTime(DWORD dwRegister, FILETIME pfiletime);

	/**
	 * Registers an object and its identifying moniker in the running object
	 * table (ROT).
	 * 
	 * {@code
	 *   HRESULT Register(
	 *     [in]   DWORD grfFlags,
	 *     [in]   IUnknown *punkObject,
	 *     [in]   IMoniker *pmkObjectName,
	 *     [out]  DWORD *pdwRegister
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms680747%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT Register(DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, DWORDByReference pdwRegister);

	/**
	 * Removes an entry from the running object table (ROT) that was previously
	 * registered by a call to IRunningObjectTable.Register.
	 * 
	 * {@code
	 *   HRESULT Revoke(
	 *     [in]  DWORD dwRegister
	 *   );
	 * }
	 * 
	 * @see <a
	 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms693419%28v=vs.85%29.aspx">MSDN</a>
	 * 
	 */
	HRESULT Revoke(DWORD dwRegister);
}
