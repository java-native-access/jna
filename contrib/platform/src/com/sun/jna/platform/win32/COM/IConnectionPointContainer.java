package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public interface IConnectionPointContainer extends IUnknown {
	 public final static IID IID_IConnectionPointContainer = new IID("B196B284-BAB4-101A-B69C-00AA00341D07");
	 
	 /**
	  * @code{
	  *   HRESULT FindConnectionPoint(
	  *     [in]   REFIID riid,
	  *     [out]  IConnectionPoint **ppCP
	  *   );
	  * }
	  * @param riid
	  * @param ppCP
	  * @return
	  */
	 public HRESULT FindConnectionPoint(  REFIID riid,  PointerByReference ppCP );
}
