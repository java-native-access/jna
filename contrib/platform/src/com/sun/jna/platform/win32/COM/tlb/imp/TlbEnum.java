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

import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil.TypeLibDoc;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbEnum.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbEnum extends TlbBase {

    /**
     * Instantiates a new tlb enum.
     * 
     * @param index
     *            the index
     * @param typeLibUtil
     *            the type lib util
     */
    public TlbEnum(int index, String packagename, TypeLibUtil typeLibUtil) {
        super(index, typeLibUtil, null);

        TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();

        if (typeLibDoc.getName().length() > 0)
            this.name = typeLibDoc.getName();

        this.logInfo("Type of kind 'Enum' found: " + this.name);

        this.createPackageName(packagename);
        this.createClassName(this.name);
        this.setFilename(this.name);

        // Get the TypeAttributes
        TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
        TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();

        this.createJavaDocHeader(typeAttr.guid.toGuidString(), docString);

        int cVars = typeAttr.cVars.intValue();
        for (int i = 0; i < cVars; i++) {
            // Get the property description
            VARDESC varDesc = typeInfoUtil.getVarDesc(i);
            VARIANT constValue = varDesc._vardesc.lpvarValue;
            Object value = constValue.getValue();

            // Get the member ID
            MEMBERID memberID = varDesc.memid;

            // Get the name of the property
            TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
            this.content += TABTAB + "//" + typeInfoDoc2.getName() + CR;
            this.content += TABTAB + "public static final int "
                    + typeInfoDoc2.getName() + " = " + value.toString() + ";";

            if (i < cVars - 1)
                this.content += CR;

            // release the pointer
            typeInfoUtil.ReleaseVarDesc(varDesc);
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
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.template";
    }
}
