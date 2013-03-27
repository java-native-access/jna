package com.sun.jna.platform.win32.COM.tlb.imp;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;

public class TlbEnumImp extends TlbBaseImp {

	private String variables = "";
	
	public TlbEnumImp(int index, PrintStream out, ITypeLibUtil typeLibUtil) {
		super(index, out, typeLibUtil);		
		
		Object[] typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String enumName = (String) typeLibDoc[0];
		String helpString = (String) typeLibDoc[1];
		
		 this.logInfo("Type of kind 'enum' found: " + enumName);
		 this.createClassName(enumName);

		// Get the TypeAttributes
		ITypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
		TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
		
		this.createJavaDocHeader(typeAttr.guid.toGuidString(), helpString);

		int cVars = typeAttr.cVars.intValue();
		for (int i = 0; i < cVars; i++) {
			// Get the property description
			VARDESC varDesc = typeInfoUtil.getVarDesc(i);
			VARIANT constValue = varDesc.union.lpvarValue;
			int varType = constValue.getVarType().intValue();
			Object value = constValue.getValue();
			
			// Get the member ID
			MEMBERID memberID = varDesc.memid;

			// Get the name of the property
			Object[] typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
			variables += TABTAB +"//" + typeInfoDoc2[1] + CR;
			variables += TABTAB + "public static final int " + typeInfoDoc2[0] + " = " + ";";
			
			if(i < cVars-1)
				variables += CR;
		}
		
		this.replaceVariable("variables", variables);		
		this.out.print(this.classBuffer);
	}
	
	protected void createJavaDocHeader(String guid, String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("helpstring", helpstring);
	}

	protected void createClassName(String name) {
		this.replaceVariable("classname", name);
	}

	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.template";
	}

}
