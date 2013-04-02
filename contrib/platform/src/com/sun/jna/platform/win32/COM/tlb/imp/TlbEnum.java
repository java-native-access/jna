package com.sun.jna.platform.win32.COM.tlb.imp;

import java.io.PrintStream;

import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil.TypeLibDoc;

public class TlbEnum extends TlbBase {

	public TlbEnum(int index, ITypeLibUtil typeLibUtil) {
		super(index, typeLibUtil);

		TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String enumName = typeLibDoc.getName();
		String docString = typeLibDoc.getDocString();
				
		this.logInfo("Type of kind 'enum' found: " + enumName);
		this.createClassName(enumName);

		// Get the TypeAttributes
		ITypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
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
		}

		this.createContent(this.content);
	}

	protected void createJavaDocHeader(String guid, String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("helpstring", helpstring);
	}

	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.template";
	}
}
