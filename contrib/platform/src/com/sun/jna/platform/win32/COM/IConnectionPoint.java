package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT;

public interface IConnectionPoint extends IUnknown {
	final static IID IID_IConnectionPoint = new IID(
			"B196B286-BAB4-101A-B69C-00AA00341D07");

	/**
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
