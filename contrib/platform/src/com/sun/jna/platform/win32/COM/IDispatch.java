package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDbyReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAut32.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * * IDispatch.GetTypeInfoCount 12 IDispatch.GetTypeInfo 16
 * IDispatch.GetIDsOfNames 20 IDispatch.Invoke 24
 */
public class IDispatch extends IUnknown {

	public static class ByReference extends IDispatch implements
			Structure.ByReference {
	}

	/** The Constant IID_IDispatch. */
	public final static IID IID_IDispatch = new IID(
			"00020400-0000-0000-C000-000000000046");

	public IDispatch() {
	}

	/**
	 * Instantiates a new i dispatch.
	 * 
	 * @param pvInstance
	 *            the pv instance
	 */
	public IDispatch(Pointer pvInstance) {
		super(pvInstance);
	}

	/**
	 * Gets the type info count.
	 * 
	 * @param pctinfo
	 *            the pctinfo
	 * @return the hresult
	 */
	public HRESULT GetTypeInfoCount(IntByReference pctinfo) {
		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(12));
		int hr = func.invokeInt(new Object[] { this.getPointer(), pctinfo });

		return new HRESULT(hr);
	}

	/**
	 * Gets the type info.
	 * 
	 * @param iTInfo
	 *            the i t info
	 * @param lcid
	 *            the lcid
	 * @param ppTInfo
	 *            the pp t info
	 * @return the hresult
	 */
	public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid,
			PointerByReference ppTInfo) {
		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(16));
		int hr = func.invokeInt(new Object[] { this.getPointer(), iTInfo, lcid,
				ppTInfo });

		return new HRESULT(hr);
	}

	/**
	 * Gets the i ds of names.
	 * 
	 * @param riid
	 *            the riid
	 * @param rgszNames
	 *            the rgsz names
	 * @param cNames
	 *            the c names
	 * @param lcid
	 *            the lcid
	 * @param rgDispId
	 *            the rg disp id
	 * @return the hresult
	 */
	public HRESULT GetIDsOfNames(IID riid, WString[] rgszNames, int cNames,
			LCID lcid, DISPIDbyReference rgDispId) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(20));
		int hr = func.invokeInt(new Object[] { this.getPointer(), riid,
				rgszNames, cNames, lcid, rgDispId });

		return new HRESULT(hr);
	}

	/**
	 * Invoke.
	 * 
	 * @param dispIdMember
	 *            the disp id member
	 * @param riid
	 *            the riid
	 * @param lcid
	 *            the lcid
	 * @param wFlags
	 *            the w flags
	 * @param pDispParams
	 *            the disp params
	 * @param pVarResult
	 *            the var result
	 * @param pExcepInfo
	 *            the excep info
	 * @param puArgErr
	 *            the pu arg err
	 * @return the hresult
	 */
	public HRESULT Invoke(DISPID dispIdMember, IID riid, LCID lcid,
			DISPID wFlags, DISPPARAMS pDispParams,
			VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
			IntByReference puArgErr) {

		Pointer vptr = this.getPointer().getPointer(0);
		Function func = Function.getFunction(vptr.getPointer(24));
		int hr = func.invokeInt(new Object[] { this.getPointer(), dispIdMember,
				riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo,
				puArgErr });

		return new HRESULT(hr);
	}
}
