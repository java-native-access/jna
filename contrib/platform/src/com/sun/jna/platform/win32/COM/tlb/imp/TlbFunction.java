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
 * The Class TlbFunction.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbFunction extends TlbAbstractMethod implements Variant {

    /**
     * Instantiates a new tlb function.
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
    public TlbFunction(int count, int index, TypeLibUtil typeLibUtil, FUNCDESC funcDesc,
            TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);

        String methodparams = "";
        String methodvariables = "";
        short vtableId = funcDesc.oVft;
        short paramCount = funcDesc.cParams;
        ELEMDESC elemDesdRetType = funcDesc.elemdescFunc;
        String returnType = this.getType(elemDesdRetType.tdesc);
        String[] names = typeInfoUtil.getNames(funcDesc.memid, paramCount + 1);

        // if there is at least one param we need a comma
        if (paramCount > 0)
            methodvariables = ", ";

        for (int i = 0; i < paramCount; i++) {
            ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String methodName = names[i + 1].toLowerCase();
            methodparams += this.getType(elemdesc.tdesc) + " " + this.validateMethodName(methodName);
            methodvariables += methodName;

            // if there is more than 1 param
            if (i < (paramCount - 1)) {
                methodparams += ", ";
                methodvariables += ", ";
            }
        }

        this.replaceVariable("helpstring", docStr);
        this.replaceVariable("returntype", returnType);
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
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunction.template";
    }
}
