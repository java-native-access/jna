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
public class TlbPropertyPut extends TlbAbstractMethod implements Variant {

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
    public TlbPropertyPut(int count, int index, TypeLibUtil typeLibUtil,
            FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);

        this.methodName = "set" + getMethodName();
        String methodparams = "";
        String methodvariables = ", ";
        short vtableId = funcDesc.oVft;
        short paramCount = funcDesc.cParams;
        String varType;
        String[] names = typeInfoUtil.getNames(funcDesc.memid, paramCount + 1);

        for (int i = 0; i < paramCount; i++) {
            ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            varType = this.getType(elemdesc);
            methodparams += varType + " " + names[i].toLowerCase();
            methodvariables += names[i].toLowerCase();

            // if there is more than 1 param
            if (i < (paramCount - 1)) {
                methodparams += ", ";
                methodvariables += ", ";
            }
        }

        this.replaceVariable("helpstring", docStr);
        this.replaceVariable("methodname", methodName);
        this.replaceVariable("methodparams", methodparams);
        this.replaceVariable("methodvariables", methodvariables);
        this.replaceVariable("vtableid", String.valueOf(vtableId));
        this.replaceVariable("functionCount", String.valueOf(count));        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
     */
    @Override
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPut.template";
    }
}
