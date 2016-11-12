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
import com.sun.jna.platform.win32.COM.TypeLibUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbFunction.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbFunctionDispId extends TlbAbstractMethod {

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
    public TlbFunctionDispId(int count, int index, TypeLibUtil typeLibUtil,
            FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);

        String[] names = typeInfoUtil.getNames(funcDesc.memid, paramCount + 1);

        for (int i = 0; i < paramCount; i++) {
            ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String methodName = names[i + 1].toLowerCase();
            String type = this.getType(elemdesc.tdesc);
            String _methodName = this.replaceJavaKeyword(methodName);
            methodparams += type + " " + _methodName;
            
            //wrap all in a VARIANT
            if(type.equals("VARIANT"))
                methodvariables += _methodName;
            else
                methodvariables += "new VARIANT(" + _methodName + ")";

            // if there is more than 1 param
            if (i < (paramCount - 1)) {
                methodparams += ", ";
                methodvariables += ", ";
            }
        }
        
        String returnValue;
        if(this.returnType.equalsIgnoreCase("VARIANT"))
            returnValue = "pResult";
        else
            returnValue = "((" + returnType + ") pResult.getValue())";

        this.replaceVariable("helpstring", docStr);
        this.replaceVariable("returntype", returnType);
        this.replaceVariable("returnvalue", returnValue);
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
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.template";
    }
}
