/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl.ELEMDESC;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbPropertyPut.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbPropertyPutStub extends TlbAbstractMethod {

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
        String[] names = typeInfoUtil.getNames(funcDesc.memid, paramCount + 1);

        for (int i = 0; i < paramCount; i++) {
            ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String varType = this.getType(elemdesc);
            methodparams += varType + " "
                    + this.replaceJavaKeyword(names[i].toLowerCase());

            // if there is more than 1 param
            if (i < (paramCount - 1)) {
                methodparams += ", ";
            }
        }

        this.replaceVariable("helpstring", docStr);
        this.replaceVariable("methodname", methodname);
        this.replaceVariable("methodparams", methodparams);
        this.replaceVariable("vtableid", String.valueOf(vtableId));
        this.replaceVariable("memberid", String.valueOf(memberid));
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
