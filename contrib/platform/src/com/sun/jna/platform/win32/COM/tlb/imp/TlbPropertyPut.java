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

import com.sun.jna.platform.win32.OaIdl.ELEMDESC;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbPropertyPut.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbPropertyPut extends TlbAbstractMethod {

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
        String[] names = typeInfoUtil.getNames(funcDesc.memid, paramCount + 1);

        if (paramCount > 0)
            methodvariables += ", ";

        for (int i = 0; i < paramCount; i++) {
            ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String varType = this.getType(elemdesc);
            methodparams += varType + " "
                    + this.replaceJavaKeyword(names[i].toLowerCase());
            methodvariables += this.replaceJavaKeyword(names[i].toLowerCase());

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
        this.replaceVariable("memberid", String.valueOf(memberid));
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
