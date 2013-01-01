package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAut32;
import com.sun.jna.platform.win32.OleAut32.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class COMObject {

	public final static LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE
			.GetUserDefaultLCID();
	public final static LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE
			.GetSystemDefaultLCID();

	protected IDispatch iDispatch;

	private PointerByReference pDispatch = new PointerByReference();

	public COMObject(String progId) throws COMException {
		// enable JNA protected mode
		Native.setProtected(true);

		// Initialize COM for this thread...
		HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

		if (W32Errors.FAILED(hr)) {
			this.release();
			throw new COMException("CoInitialize() failed!");
		}

		// Get CLSID for Word.Application...
		CLSID.ByReference clsid = new CLSID.ByReference();
		hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);

		if (W32Errors.FAILED(hr)) {
			Ole32.INSTANCE.CoUninitialize();
			throw new COMException("CLSIDFromProgID() failed!");
		}

		hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
				WTypes.CLSCTX_LOCAL_SERVER, IDispatch.IID_IDispatch,
				this.pDispatch);

		if (W32Errors.FAILED(hr)) {
			throw new COMException("COM object '" + progId
					+ "' not registered properly!");
		}

		this.iDispatch = new IDispatch(this.pDispatch.getValue());
	}

	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
			IDispatch pDisp, String name, int cArgs, VARIANT[] pArgs) throws COMException {

		if (pDisp == null)
			return new HRESULT(COMUtils.E_FAIL);

		// va_list marker;
		// va_start(marker, cArgs);

		WString[] ptName = new WString[] { new WString(name) };
		DISPPARAMS.ByReference dp = new DISPPARAMS.ByReference();
		DISPID dispidNamed = new DISPID(OleAut32.DISPATCH_PROPERTYPUT);
		DISPID.ByReference pdispID = new DISPID.ByReference();

		// Get DISPID for name passed...
		HRESULT hr = pDisp.GetIDsOfNames(Guid.IID_NULL, ptName, 1,
				LOCALE_USER_DEFAULT, pdispID);

		COMUtils.SUCCEEDED(hr);

		// Build DISPPARAMS
		dp.cArgs = cArgs;
		dp.rgvarg = pArgs;

		// Handle special-case for property-puts!
		if (nType == OleAut32.DISPATCH_PROPERTYPUT) {
			dp.cNamedArgs = 1;
			dp.rgdispidNamedArgs[0] = dispidNamed;
		}

		// Make the call!
		hr = pDisp.Invoke(pdispID.getDISPID(), Guid.IID_NULL,
				LOCALE_SYSTEM_DEFAULT, new DISPID(nType), dp, pvResult, null,
				null);

		COMUtils.SUCCEEDED(hr);
		return hr;

	}

	public void release() {
		if (this.iDispatch != null)
			this.iDispatch.Release();

		Ole32.INSTANCE.CoUninitialize();
	}
}
