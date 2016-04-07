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
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil.TypeLibDoc;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbClass.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbCoClass extends TlbBase {

    /**
     * Instantiates a new tlb class.
     * 
     * @param index
     *            the index
     * @param typeLibUtil
     *            the type lib util
     */
    public TlbCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) {
        super(index, typeLibUtil, null);
        
        TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);

        TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();

        if(typeLibDoc.getName().length() > 0)
            this.name = typeLibDoc.getName();
        
        this.logInfo("Type of kind 'CoClass' found: " + this.name);

        this.createPackageName(packagename);
        this.createClassName(this.name);
        this.setFilename(this.name);

        String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
        int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
        int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();
        String version = majorVerNum + "." + minorVerNum;
        String clsid = typeInfoUtil.getTypeAttr().guid.toGuidString();
        
        this.createJavaDocHeader(guidStr, version, docString);
        this.createCLSID(clsid);
        this.createCLSIDName(this.name);
        
     // Get the TypeAttributes
        TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        int cImplTypes = typeAttr.cImplTypes.intValue();
        String interfaces = "";

        for (int i = 0; i < cImplTypes; i++) {
            HREFTYPE refTypeOfImplType = typeInfoUtil.getRefTypeOfImplType(i);
            ITypeInfo refTypeInfo = typeInfoUtil
                    .getRefTypeInfo(refTypeOfImplType);
            TypeInfoUtil refTypeInfoUtil = new TypeInfoUtil(refTypeInfo);
            this.createFunctions(refTypeInfoUtil, bindingMode);
            TypeInfoDoc documentation = refTypeInfoUtil
                    .getDocumentation(new MEMBERID(-1));
            interfaces += documentation.getName();

            if (i < cImplTypes - 1)
                interfaces += ", ";
        }

        this.createInterfaces(interfaces);
        this.createContent(this.content);
    }

    protected void createFunctions(TypeInfoUtil typeInfoUtil, String bindingMode) {
        TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        int cFuncs = typeAttr.cFuncs.intValue();
        for (int i = 0; i < cFuncs; i++) {
            // Get the function description
            FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
            
            TlbAbstractMethod method = null;
            if (funcDesc.invkind.value == INVOKEKIND.INVOKE_FUNC.value) {
                if(this.isVTableMode()) {
                    method = new TlbFunctionVTable(i, index, typeLibUtil, funcDesc, typeInfoUtil);
                } else {
                    method = new TlbFunctionDispId(i, index, typeLibUtil, funcDesc, typeInfoUtil);
            }
            } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYGET.value) {
                method = new TlbPropertyGet(i, index, typeLibUtil, funcDesc, typeInfoUtil);
            } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYPUT.value) {
                method = new TlbPropertyPut(i, index, typeLibUtil, funcDesc, typeInfoUtil);
            } else if (funcDesc.invkind.value == INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
                method = new TlbPropertyPut(i, index, typeLibUtil, funcDesc, typeInfoUtil);
            }
                
            if(!isReservedMethod(method.getMethodName()))
            {
                this.content += method.getClassBuffer();
                
                if (i < cFuncs - 1)
                    this.content += CR;
            }
            
            // Release our function description stuff
            typeInfoUtil.ReleaseFuncDesc(funcDesc);
        }
    }

    protected void createJavaDocHeader(String guid, String version,
            String helpstring) {
        this.replaceVariable("uuid", guid);
        this.replaceVariable("version", version);
        this.replaceVariable("helpstring", helpstring);
    }

    protected void createCLSIDName(String clsidName) {
        this.replaceVariable("clsidname", clsidName.toUpperCase());
    }

    protected void createCLSID(String clsid) {
        this.replaceVariable("clsid", clsid);
    }

    protected void createInterfaces(String interfaces) {
        this.replaceVariable("interfaces", interfaces);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
     */
    @Override
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
    }
}
