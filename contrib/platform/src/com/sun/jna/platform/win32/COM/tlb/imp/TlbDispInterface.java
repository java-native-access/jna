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
public class TlbDispInterface extends TlbBase {

    /**
     * Instantiates a new tlb dispatch.
     * 
     * @param index
     *            the index
     * @param typeLibUtil
     *            the type lib util
     */
    public TlbDispInterface(int index, String packagename,
            TypeLibUtil typeLibUtil) {
        super(index, typeLibUtil, null);

        TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();

        if(typeLibDoc.getName().length() > 0)
            this.name = typeLibDoc.getName();
        
        this.logInfo("Type of kind 'DispInterface' found: " + this.name);

        this.createPackageName(packagename);
        this.createClassName(this.name);
        this.setFilename(this.name);

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
                if (funcDesc.invkind.value == INVOKEKIND.INVOKE_FUNC.value) {
                    method = new TlbFunctionStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYGET.value) {
                    method = new TlbPropertyGetStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYPUT.value) {
                    method = new TlbPropertyPutStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
                    method = new TlbPropertyPutStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                }

                this.content += method.getClassBuffer();

                if (i < cFuncs - 1) {
                    this.content += CR;
                }
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
     */
    @Override
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispInterface.template";
    }
}
