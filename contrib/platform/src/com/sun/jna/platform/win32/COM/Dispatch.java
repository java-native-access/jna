/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDbyReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the IDispatch interface
 * 
 * IDispatch.GetTypeInfoCount 12 IDispatch.GetTypeInfo 16
 * IDispatch.GetIDsOfNames 20 IDispatch.Invoke 24
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class Dispatch extends Unknown implements IDispatch {

	public static class ByReference extends Dispatch implements
			Structure.ByReference {
	}

	private IDispatch iDispatch;

	public Dispatch() {
		this.iDispatch = (IDispatch) this.createCOMClass(IDispatch.class);
	}

	public Dispatch(Pointer pvInstance) {
		super(pvInstance);
		this.iDispatch = (IDispatch) this.createCOMClass(IDispatch.class);
	}

	/**
	 * Gets the type info count.
	 * 
	 * @param pctinfo
	 *            the pctinfo
	 * @return the hresult
	 */
	public HRESULT GetTypeInfoCount(UINTbyReference pctinfo) {
		return this.iDispatch.GetTypeInfoCount(pctinfo);
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
		return this.iDispatch.GetTypeInfo(iTInfo, lcid, ppTInfo);
	}

	/**
	 * Gets the ids of names.
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
		return this.iDispatch.GetIDsOfNames(riid, rgszNames, cNames, lcid,
				rgDispId);
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
		return this.iDispatch.Invoke(dispIdMember, riid, lcid, wFlags,
				pDispParams, pVarResult, pExcepInfo, puArgErr);
	}
}
