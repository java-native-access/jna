package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil.TypeLibDoc;

public class TlbDispatch extends TlbBase {

	public static String[] IUNKNOWN_METHODS = { "QueryInterface", "AddRef",
			"Release" };

	public static String[] IDISPATCH_METHODS = { "GetTypeInfoCount",
			"GetTypeInfo", "GetIDsOfNames", "Invoke" };

	public TlbDispatch(int index, ITypeLibUtil typeLibUtil) {
		super(index, typeLibUtil);

		TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String dispName = typeLibDoc.getName();
		String docString = typeLibDoc.getDocString();

		this.logInfo("Type of kind 'Dispatch' found: " + dispName);
		this.createClassName(dispName);

		// Get the TypeAttributes
		ITypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
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

			if (!isReservedMethod(methodName)) {
				if (funcDesc.invkind.equals(INVOKEKIND.INVOKE_FUNC)) {
					TlbMethod tlbMethod = new TlbMethod(index, typeLibUtil,
							funcDesc, typeInfoUtil);
					this.content += tlbMethod.getClassBuffer();
				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYGET)) {

				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYPUT)) {

				} else if (funcDesc.invkind
						.equals(INVOKEKIND.INVOKE_PROPERTYPUTREF)) {

				}

				if (i < cFuncs - 1)
					this.content += CR;
			}

			// Release our function description stuff
			typeInfoUtil.ReleaseFuncDesc(funcDesc);
		}

		this.createContent(this.content);
	}

	protected void createJavaDocHeader(String guid, String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("helpstring", helpstring);
	}

	protected boolean isReservedMethod(String method) {
		for (int i = 0; i < IUNKNOWN_METHODS.length; i++) {
			if (IUNKNOWN_METHODS[i].equalsIgnoreCase(method))
				return true;
		}

		for (int i = 0; i < IDISPATCH_METHODS.length; i++) {
			if (IDISPATCH_METHODS[i].equalsIgnoreCase(method))
				return true;
		}

		return false;
	}

	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispatch.template";
	}
}
