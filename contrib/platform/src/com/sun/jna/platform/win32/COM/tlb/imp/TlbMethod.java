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
import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeInfoUtil.TypeInfoDoc;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.IUnknown;

public class TlbMethod extends TlbBase implements Variant {

	public TlbMethod(int index, ITypeLibUtil typeLibUtil, FUNCDESC funcDesc,
			ITypeInfoUtil typeInfoUtil) {
		super(index, typeLibUtil);

		TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
		String methodname = typeInfoDoc.getName();
		String docStr = typeInfoDoc.getDocString();

		String methodparams = "";
		String methodvariables = "";
		short vtableId = funcDesc.oVft;
		short paramCount = funcDesc.cParams;
		ELEMDESC elemDesdRetType = funcDesc.elemdescFunc;
		String returnType = this.getVarType(elemDesdRetType.tdesc.vt);

		for (int i = 0; i < paramCount; i++) {
			String[] names = typeInfoUtil.getNames(funcDesc.memid,
					paramCount + 1);
			ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
			VARTYPE vt = elemdesc.tdesc.vt;
			methodparams += this.getVarType(vt) + " " + names[i + 1];
			methodvariables += names[i + 1];

			if (i < (paramCount - 1)) {
				methodparams += ", ";
				methodvariables += ", ";
			}
		}

		this.replaceVariable("helpstring", docStr);
		this.replaceVariable("returntype", returnType);
		this.replaceVariable("methodname", methodname);
		this.replaceVariable("methodparams", methodparams);
		this.replaceVariable("methodvariables", methodvariables);
		this.replaceVariable("vtableid", String.valueOf(vtableId));
	}

	private String getVarType(VARTYPE vt) {
		switch (vt.intValue()) {
		case VT_I2:
			return "short";
		case VT_I4:
			return "int";
		case VT_I8:
			return "long";
		case VT_R4:
			return "float";
		case VT_R8:
			return "double";
		case VT_BOOL:
			return "boolean";
		case VT_ERROR:
			return SCODE.class.getSimpleName();
		case VT_CY:
			return CURRENCY.class.getSimpleName();
		case VT_DATE:
			return DATE.class.getSimpleName();
		case VT_BSTR:
			return BSTR.class.getSimpleName();
		case VT_UNKNOWN:
			return IUnknown.class.getSimpleName();
		case VT_DISPATCH:
			return IDispatch.class.getSimpleName();
		case VT_SAFEARRAY:
			return "parray";
		case VT_ARRAY:
			return "parray";
		case VT_BYREF | VT_UI1:
			return "pbVal";
		case VT_BYREF | VT_I2:
			return "piVal";
		case VT_BYREF | VT_I4:
			return "plVal";
		case VT_BYREF | VT_I8:
			return "pllVal";
		case VT_BYREF | VT_R4:
			return "pfltVal";
		case VT_BYREF | VT_R8:
			return "pdblVal";
		case VT_BYREF | VT_BOOL:
			return "pboolVal";
		case VT_BYREF | VT_ERROR:
			return "pscode";
		case VT_BYREF | VT_CY:
			return "pcyVal";
		case VT_BYREF | VT_DATE:
			return "pdate";
		case VT_BYREF | VT_BSTR:
			return "pbstrVal";
		case VT_BYREF | VT_UNKNOWN:
			return "ppunkVal";
		case VT_BYREF | VT_DISPATCH:
			return "ppdispVal";
		case VT_BYREF | VT_ARRAY:
			return "pparray";
		case VT_BYREF | VT_VARIANT:
			return "pvarVal";
		case VT_BYREF:
			return "byref";
		case VT_I1:
			return "cVal";
		case VT_UI2:
			return "uiVal";
		case VT_UI4:
			return "ulVal";
		case VT_UI8:
			return "ullVal";
		case VT_INT:
			return "intVal";
		case VT_UINT:
			return "uintVal";
		case VT_VOID:
			return "void";
		case VT_BYREF | VT_DECIMAL:
			return "pdecVal";
		case VT_BYREF | VT_I1:
			return "pcVal";
		case VT_BYREF | VT_UI2:
			return "puiVal";
		case VT_BYREF | VT_UI4:
			return "pulVal";
		case VT_BYREF | VT_UI8:
			return "pullVal";
		case VT_BYREF | VT_INT:
			return "pintVal";
		case VT_BYREF | VT_UINT:
			return "puintVal";
		default:
			return null;
		}
	}

	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbMethod.template";
	}
}
