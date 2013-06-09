/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl.CURRENCY;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.ELEMDESC;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.IUnknown;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbPropertyPut.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbPropertyPutStub extends TlbAbstractMethod implements Variant {

	/**
	 * Instantiates a new tlb property set.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 * @param funcDesc
	 *            the func desc
	 * @param typeInfoUtil
	 *            the type info util
	 */
	public TlbPropertyPutStub(int index, TypeLibUtil typeLibUtil,
			FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
		super(index, typeLibUtil, funcDesc, typeInfoUtil);

		TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
		String docStr = typeInfoDoc.getDocString();
		String methodname = "set" + typeInfoDoc.getName();
		String methodparams = "";
		short paramCount = funcDesc.cParams;
		String varType;
		
		for (int i = 0; i < paramCount; i++) {
			ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
			VARTYPE vt = elemdesc.tdesc.vt;
			varType = this.getVarType(vt);
			methodparams += varType + " " + varType.toLowerCase();
		}

		this.replaceVariable("helpstring", docStr);
		this.replaceVariable("methodname", methodname);
		this.replaceVariable("methodparams", methodparams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
	 */
	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
	}
}
