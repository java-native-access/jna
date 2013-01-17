package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAut32;
import com.sun.jna.platform.win32.OleAut32.DISPPARAMS;
import com.sun.jna.platform.win32.OleAut32Util;
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

	protected IDispatch iDispatch = new IDispatch();

	private PointerByReference pDispatch = new PointerByReference();

	public COMObject(IDispatch iDispatch) {
		this.iDispatch = iDispatch;
	}

	public COMObject(String progId) throws AutomationException {
		// enable JNA protected mode
		Native.setProtected(true);

		// Initialize COM for this thread...
		HRESULT hr = Ole32.INSTANCE.CoInitialize(null);

		if (W32Errors.FAILED(hr)) {
			this.release();
			throw new AutomationException("CoInitialize() failed!");
		}

		// Get CLSID for Word.Application...
		CLSID.ByReference clsid = new CLSID.ByReference();
		hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);

		if (W32Errors.FAILED(hr)) {
			Ole32.INSTANCE.CoUninitialize();
			throw new AutomationException("CLSIDFromProgID() failed!");
		}

		hr = Ole32.INSTANCE.CoCreateInstance(clsid, null,
				WTypes.CLSCTX_LOCAL_SERVER, IDispatch.IID_IDispatch,
				this.pDispatch);

		if (W32Errors.FAILED(hr)) {
			throw new AutomationException("COM object '" + progId
					+ "' not registered properly!");
		}

		this.iDispatch = new IDispatch(this.pDispatch.getValue());
	}

	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
			IDispatch pDisp, String name, VARIANT[] pArgs) throws AutomationException {

		if (pDisp == null)
			throw new AutomationException("pDisp parameter is null!");

		WString[] ptName = new WString[] { new WString(name) };
		DISPPARAMS dp = new DISPPARAMS();
		DISPID.ByReference pdispID = new DISPID.ByReference();

		// Get DISPID for name passed...
		HRESULT hr = pDisp.GetIDsOfNames(Guid.IID_NULL, ptName, 1,
				LOCALE_USER_DEFAULT, pdispID);

		COMUtils.SUCCEEDED(hr);

		// Build DISPPARAMS
		if ((pArgs != null) && (pArgs.length > 0)) {
			SAFEARRAY.ByReference safeArg = OleAut32Util
					.createVarArray(pArgs.length);

			for (int i = 0; i < pArgs.length; i++) {
				OleAut32Util.SafeArrayPutElement(safeArg, i, pArgs[i]);
			}

			dp.cArgs = pArgs.length;
			dp.rgvarg = safeArg;
		}

		// Handle special-case for property-puts!
		if (nType == OleAut32.DISPATCH_PROPERTYPUT) {
			dp.cNamedArgs = 1;
			dp.rgdispidNamedArgs = new DISPID.ByReference(OleAut32.DISPATCH_PROPERTYPUT);
		}

		System.out.println(dp.toString(true));

		// Make the call!
		hr = pDisp.Invoke(pdispID.getDISPID(), Guid.IID_NULL,
				LOCALE_SYSTEM_DEFAULT, new DISPID(nType), dp, pvResult, null,
				null);

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
			IDispatch pDisp, String name, VARIANT pArg) throws AutomationException {

		return this.oleMethod(nType, pvResult, pDisp, name,
				new VARIANT[] { pArg });
	}

	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult,
			IDispatch pDisp, String name) throws AutomationException {

		return this.oleMethod(nType, pvResult, pDisp, name, (VARIANT[]) null);
	}

	public IDispatch getIDispatch() {
		return iDispatch;
	}

	public void setIDispatch(IDispatch iDispatch) {
		this.iDispatch = iDispatch;
	}

	public PointerByReference getIDispatchPointer() {
		return pDispatch;
	}

	public void setIDispatchPointer(PointerByReference pDispatch) {
		this.pDispatch = pDispatch;
	}

	public void release() {
		if (this.iDispatch != null)
			this.iDispatch.Release();

		Ole32.INSTANCE.CoUninitialize();
	}
}
