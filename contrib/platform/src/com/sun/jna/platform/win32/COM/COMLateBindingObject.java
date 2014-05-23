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

import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.SHORT;

// TODO: Auto-generated Javadoc
/**
 * Helper class to provide basic COM support.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMLateBindingObject extends COMBindingBaseObject {

    /**
     * Instantiates a new cOM object.
     * 
     * @param iDispatch
     *            the i dispatch
     */
    public COMLateBindingObject(IDispatch iDispatch) {
        super(iDispatch);
    }

    /**
     * Instantiates a new cOM object.
     * 
     * @param clsid
     *            the clsid
     * @param useActiveInstance
     *            the use active instance
     */
    public COMLateBindingObject(CLSID clsid, boolean useActiveInstance) {
        super(clsid, useActiveInstance);
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
    public COMLateBindingObject(String progId, boolean useActiveInstance)
            throws COMException {
        super(progId, useActiveInstance);
    }

    /**
     * Gets the automation property.
     * 
     * @param propertyName
     *            the property name
     * @return the automation property
     */
    protected IDispatch getAutomationProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return ((IDispatch) result.getValue());
    }

    /**
     * Gets the automation property.
     * 
     * @param propertyName
     *            the property name
     * @param comObject
     *            the com object
     * @return the automation property
     */
    protected IDispatch getAutomationProperty(String propertyName,
            COMLateBindingObject comObject) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                comObject.getIDispatch(), propertyName);

        return ((IDispatch) result.getValue());
    }

    /**
     * Gets the automation property.
     * 
     * @param propertyName
     *            the property name
     * @param comObject
     *            the com object
     * @param value
     *            the value
     * @return the automation property
     */
    protected IDispatch getAutomationProperty(String propertyName,
            COMLateBindingObject comObject, VARIANT value) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                comObject.getIDispatch(), propertyName, value);

        return ((IDispatch) result.getValue());
    }

    /**
     * Gets the automation property.
     * 
     * @param propertyName
     *            the property name
     * @param iDispatch
     *            the i dispatch
     * @return the automation property
     */
    protected IDispatch getAutomationProperty(String propertyName,
            IDispatch iDispatch) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return ((IDispatch) result.getValue());
    }

    /**
     * Gets the boolean property.
     * 
     * @param propertyName
     *            the property name
     * @return the boolean property
     */
    protected boolean getBooleanProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return (((VARIANT_BOOL) result.getValue()).intValue() != 0);
    }

    /**
     * Gets the date property.
     * 
     * @param propertyName
     *            the property name
     * @return the date property
     */
    protected Date getDateProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return result.dateValue();
    }

    /**
     * Gets the int property.
     * 
     * @param propertyName
     *            the property name
     * @return the int property
     */
    protected int getIntProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return ((LONG) result.getValue()).intValue();
    }

    /**
     * Gets the short property.
     * 
     * @param propertyName
     *            the property name
     * @return the short property
     */
    protected short getShortProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return ((SHORT) result.getValue()).shortValue();
    }

    /**
     * Gets the string property.
     * 
     * @param propertyName
     *            the property name
     * @return the string property
     */
    protected String getStringProperty(String propertyName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
                this.getIDispatch(), propertyName);

        return result.getValue().toString();
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @return the variant
     */
    protected VARIANT invoke(String methodName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName);

        return result;
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @param arg
     *            the arg
     * @return the variant
     */
    protected VARIANT invoke(String methodName, VARIANT arg) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName, arg);

        return result;
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @param args
     *            the args
     * @return the variant
     */
    protected VARIANT invoke(String methodName, VARIANT[] args) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName, args);

        return result;
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     * @return the variant
     */
    protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2) {
        return invoke(methodName, new VARIANT[] { arg1, arg2 });
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     * @param arg3
     *            the arg3
     * @return the variant
     */
    protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2,
            VARIANT arg3) {
        return invoke(methodName, new VARIANT[] { arg1, arg2, arg3 });
    }

    /**
     * Invoke.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     * @param arg3
     *            the arg3
     * @param arg4
     *            the arg4
     * @return the variant
     */
    protected VARIANT invoke(String methodName, VARIANT arg1, VARIANT arg2,
            VARIANT arg3, VARIANT arg4) {
        return invoke(methodName, new VARIANT[] { arg1, arg2, arg3, arg4 });
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param dispatch
     *            the dispatch
     */
    protected void invokeNoReply(String methodName, IDispatch dispatch) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param comObject
     *            the com object
     */
    protected void invokeNoReply(String methodName,
            COMLateBindingObject comObject) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, comObject.getIDispatch(),
                methodName);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param dispatch
     *            the dispatch
     * @param arg
     *            the arg
     */
    protected void invokeNoReply(String methodName, IDispatch dispatch,
            VARIANT arg) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName, arg);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param dispatch
     *            the dispatch
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     */
    protected void invokeNoReply(String methodName, IDispatch dispatch,
            VARIANT arg1, VARIANT arg2) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName,
                new VARIANT[] { arg1, arg2 });
    }

    protected void invokeNoReply(String methodName, COMLateBindingObject comObject,
            VARIANT arg1, VARIANT arg2) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, comObject.getIDispatch(), methodName,
                new VARIANT[] { arg1, arg2 });
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param comObject
     *            the com object
     * @param arg
     *            the arg
     */
    protected void invokeNoReply(String methodName,
            COMLateBindingObject comObject, VARIANT arg) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, comObject.getIDispatch(),
                methodName, arg);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param dispatch
     *            the dispatch
     * @param args
     *            the args
     */
    protected void invokeNoReply(String methodName, IDispatch dispatch,
            VARIANT[] args) {
        this.oleMethod(OleAuto.DISPATCH_METHOD, null, dispatch, methodName,
                args);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     */
    protected void invokeNoReply(String methodName) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param arg
     *            the arg
     */
    protected void invokeNoReply(String methodName, VARIANT arg) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName, arg);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param args
     *            the args
     */
    protected void invokeNoReply(String methodName, VARIANT[] args) {
        VARIANT.ByReference result = new VARIANT.ByReference();
        this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getIDispatch(),
                methodName, args);
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     */
    protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2) {
        invokeNoReply(methodName, new VARIANT[] { arg1, arg2 });
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     * @param arg3
     *            the arg3
     */
    protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2,
            VARIANT arg3) {
        invokeNoReply(methodName, new VARIANT[] { arg1, arg2, arg3 });
    }

    /**
     * Invoke no reply.
     * 
     * @param methodName
     *            the method name
     * @param arg1
     *            the arg1
     * @param arg2
     *            the arg2
     * @param arg3
     *            the arg3
     * @param arg4
     *            the arg4
     */
    protected void invokeNoReply(String methodName, VARIANT arg1, VARIANT arg2,
            VARIANT arg3, VARIANT arg4) {
        invokeNoReply(methodName, new VARIANT[] { arg1, arg2, arg3, arg4 });
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, boolean value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, Date value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, IDispatch value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, int value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, short value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, String value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getIDispatch(),
                propertyName, new VARIANT(value));
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param iDispatch
     *            the i dispatch
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName, IDispatch iDispatch,
            VARIANT value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, iDispatch,
                propertyName, value);
    }

    /**
     * Sets the property.
     * 
     * @param propertyName
     *            the property name
     * @param comObject
     *            the com object
     * @param value
     *            the value
     */
    protected void setProperty(String propertyName,
            COMLateBindingObject comObject, VARIANT value) {
        this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null,
                comObject.getIDispatch(), propertyName, value);
    }

    /**
     * To variant.
     * 
     * @return the variant
     */
    public VARIANT toVariant() {
        return new VARIANT(this.getIDispatch());
    }
}
