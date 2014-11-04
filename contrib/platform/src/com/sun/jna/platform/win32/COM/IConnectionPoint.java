package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT;

public interface IConnectionPoint extends IUnknown {
	final static IID IID_IConnectionPoint = new IID(
			"B196B286-BAB4-101A-B69C-00AA00341D07");

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
	 * @return
	 */
	WinNT.HRESULT Advise(IUnknownCallback pUnkSink, DWORDByReference pdwCookie);
}
