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

import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil.TypeLibDoc;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbDispatch.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbDispatchInterface extends TlbBase {

	/** The iunknown methods. */
	public static String[] IUNKNOWN_METHODS = { "QueryInterface", "AddRef",
			"Release" };

	/** The idispatch methods. */
	public static String[] IDISPATCH_METHODS = { "GetTypeInfoCount",
			"GetTypeInfo", "GetIDsOfNames", "Invoke" };

	/**
	 * Instantiates a new tlb dispatch.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 */
	public TlbDispatchInterface(int index, TypeLibUtil typeLibUtil) {
		super(index, typeLibUtil);

		TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String dispName = typeLibDoc.getName();
		String docString = typeLibDoc.getDocString();

		this.logInfo("Type of kind 'Dispatch' found: " + dispName);
		this.createClassName(dispName);
		this.setFilename(dispName);
		
		// Get the TypeAttributes
		TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
		TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();

		this.createJavaDocHeader(typeAttr.guid.toGuidString(), docString);

		int cFuncs = typeAttr.cFuncs.intValue();
		for (int i = 0; i < cFuncs; i++) {
			// Get the function description
			FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);

			// Get the member ID
			MEMBERID memberID = funcDesc.memid;

			// Get the name of the method
			TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
			String methodName = typeInfoDoc2.getName();
			TlbAbstractMethod method = null;

			if (!isReservedMethod(methodName)) {
				if (funcDesc.invkind.equals(INVOKEKIND.INVOKE_FUNC)) {
					method = new TlbFunction(index, typeLibUtil, funcDesc,
							typeInfoUtil);
				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYGET)) {
					method = new TlbPropertyGet(index, typeLibUtil, funcDesc,
							typeInfoUtil);
				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYPUT)) {
					method = new TlbPropertyPut(index, typeLibUtil, funcDesc,
							typeInfoUtil);
				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYPUTREF)) {

				}

				this.content += method.getClassBuffer();

				if (i < cFuncs - 1)
					this.content += CR;
			}

			// Release our function description stuff
			typeInfoUtil.ReleaseFuncDesc(funcDesc);
		}

		this.createContent(this.content);
	}

	/**
	 * Creates the java doc header.
	 * 
	 * @param guid
	 *            the guid
	 * @param helpstring
	 *            the helpstring
	 */
	protected void createJavaDocHeader(String guid, String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("helpstring", helpstring);
	}

	/**
	 * Checks if is reserved method.
	 * 
	 * @param method
	 *            the method
	 * @return true, if is reserved method
	 */
	protected boolean isReservedMethod(String method) {
		for (int i = 0; i < IUNKNOWN_METHODS.length; i++) {
			if (IUNKNOWN_METHODS[i].equalsIgnoreCase(method))
				return true;
		}

		for (int i = 0; i < IDISPATCH_METHODS.length; i++) {
			if (IDISPATCH_METHODS[i].equalsIgnoreCase(method))
				return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
	 */
	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispatchInterface.template";
	}
}
