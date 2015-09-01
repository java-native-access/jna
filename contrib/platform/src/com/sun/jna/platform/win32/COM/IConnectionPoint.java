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
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinNT;

public interface IConnectionPoint extends IUnknown {
	final static IID IID_IConnectionPoint = new IID(
			"B196B286-BAB4-101A-B69C-00AA00341D07");

	/**
	 * 
	 * 
	 * @param iid
	 * @return interface pointer
	 */
	HRESULT GetConnectionInterface(IID iid);
	
	/**
	 * 
	 * When Advise is called, the called COM object will callback 'QueryInterface' asking for a number of
	 * different interfaces, for example:
	 * 	- {00000003-0000-0000-C000-000000000046} - IMarshal
	 *  - {00000003-0000-0000-C000-000000000046}
	 *  - {0000001B-0000-0000-C000-000000000046} - IdentityUnmarshal
	 *  - {00000000-0000-0000-C000-000000000046} - IUnknown
	 *  - {00000018-0000-0000-C000-000000000046} - IStdMarshalInfo
	 *  - {00000019-0000-0000-C000-000000000046} - IExternalConnection
	 *  - {4C1E39E1-E3E3-4296-AA86-EC938D896E92} - (some unknown private interface)
	 *  - interface of this ConnectionPoint
	 *  
	 * 
	 * {@code
	 *   HRESULT Advise(
	 *     [in]   IUnknown *pUnkSink,
	 *     [out]  DWORD *pdwCookie
	 *     );
	 * }
	 * 
	 * @param pUnkSink
	 * @param pdwCookie
	 * @return status
	 */
	WinNT.HRESULT Advise(IUnknownCallback pUnkSink, DWORDByReference pdwCookie);
	
	/**
	 * 
	 * @param dwCookie
	 * @return status
	 */
	HRESULT Unadvise(DWORD dwCookie);
}
