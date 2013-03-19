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

import java.util.Date;

import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.SHORT;

/**
 * Helper class to provide basic COM support.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMObject extends COMBaseObject {

	public COMObject(IDispatch iDispatch) {
		super(iDispatch);
	}

	/**
	 * Instantiates a new cOM object.
	 * 
	 * @param progId
	 *            the prog id
	 * @param useActiveInstance
	 *            the use active instance
	 * @throws COMException
	 *             the automation exception
	 */
	public COMObject(String progId, boolean useActiveInstance)
			throws COMException {
		super(progId, useActiveInstance);
	}

	protected IDispatch getAutomationProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return ((IDispatch) result.getValue());
	}

	protected IDispatch getAutomationProperty(String propertyName,
			COMObject comObject) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				comObject.getIDispatch(), propertyName);

		return ((IDispatch) result.getValue());
	}

	protected IDispatch getAutomationProperty(String propertyName,
			COMObject comObject, VARIANT value) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				comObject.getIDispatch(), propertyName, value);

		return ((IDispatch) result.getValue());
	}

	protected IDispatch getAutomationProperty(String propertyName,
			IDispatch iDispatch) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, iDispatch,
				propertyName);

		return ((IDispatch) result.getValue());
	}

	protected boolean getBooleanProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return (((VARIANT_BOOL) result.getValue()).intValue() != 0);
	}

	protected Date getDateProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return result.dateValue();
	}

	protected int getIntProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return ((LONG) result.getValue()).intValue();
	}

	protected short getShortProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return ((SHORT) result.getValue()).shortValue();
	}

	protected String getStringProperty(String propertyName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				propertyName);

		return result.getValue().toString();
	}

	protected VARIANT invoke(String methodName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName);

		return result;
	}

	protected VARIANT invoke(String methodName, VARIANT arg) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName, arg);

		return result;
	}

	protected VARIANT invoke(String methodName, VARIANT[] args) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName, args);

		return result;
	}

	protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2) {

		return invoke(methodName, new VARIANT[] { arg1, arg2 });
	}

	protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2,
			VARIANT arg3) {

		return invoke(methodName, new VARIANT[] { arg1, arg2, arg3 });
	}

	protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2,
			VARIANT arg3, VARIANT arg4) {

		return invoke(methodName, new VARIANT[] { arg1, arg2, arg3, arg4 });
	}

	protected void invokeNoReply(String methodName, IDispatch dispatch) {

		this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName);
	}

	protected void invokeNoReply(String methodName, COMObject comObject) {

		this.oleMethod(OleAuto.DISPATCH_METHOD, null, comObject.getIDispatch(),
				methodName);
	}

	protected void invokeNoReply(String methodName, IDispatch dispatch,
			VARIANT arg) {

		this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName, arg);
	}

	protected void invokeNoReply(String methodName, COMObject comObject,
			VARIANT arg) {

		this.oleMethod(OleAuto.DISPATCH_METHOD, null, comObject.getIDispatch(),
				methodName, arg);
	}

	protected void invokeNoReply(String methodName, IDispatch dispatch,
			VARIANT[] args) {

		this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName,
				args);
	}

	protected void invokeNoReply(String methodName) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName);
	}

	protected void invokeNoReply(String methodName, VARIANT arg) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName, arg);
	}

	protected void invokeNoReply(String methodName, VARIANT[] args) {

		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.iDispatch,
				methodName, args);
	}

	protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2) {

		invokeNoReply(methodName, new VARIANT[] { arg1, arg2 });
	}

	protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2,
			VARIANT arg3) {

		invokeNoReply(methodName, new VARIANT[] { arg1, arg2, arg3 });
	}

	protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2,
			VARIANT arg3, VARIANT arg4) {

		invokeNoReply(methodName, new VARIANT[] { arg1, arg2, arg3, arg4 });
	}

	protected void setProperty(String propertyName, boolean value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, Date value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, IDispatch value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, int value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, short value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, String value) {

		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.iDispatch,
				propertyName, new VARIANT(value));
	}

	protected void setProperty(String propertyName, IDispatch iDispatch,
			VARIANT value) {
		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, iDispatch,
				propertyName, value);
	}

	protected void setProperty(String propertyName, COMObject comObject,
			VARIANT value) {
		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null,
				comObject.getIDispatch(), propertyName, value);
	}

	public VARIANT toVariant() {
		return new VARIANT(this.iDispatch);
	}
}
