package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEbyReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTbyReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ITypeInfoUtil {
	
	public final static OleAuto OLEAUTO = OleAuto.INSTANCE; 

	private ITypeInfo typeInfo;

	public ITypeInfoUtil(ITypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}

	public TYPEATTR getTypeAttr() {
		PointerByReference ppTypeAttr = new PointerByReference();
		HRESULT hr = this.typeInfo.GetTypeAttr(ppTypeAttr);
		COMUtils.checkAutoRC(hr);

		return new TYPEATTR(ppTypeAttr.getValue());
	}

	public ITypeComp getTypeComp() {
		PointerByReference ppTypeAttr = new PointerByReference();
		HRESULT hr = this.typeInfo.GetTypeComp(ppTypeAttr);
		COMUtils.checkAutoRC(hr);

		return new ITypeComp(ppTypeAttr.getValue());
	}

	public FUNCDESC getFuncDesc(int index) {
		PointerByReference ppFuncDesc = new PointerByReference();
		HRESULT hr = this.typeInfo.GetFuncDesc(new UINT(index), ppFuncDesc);
		COMUtils.checkAutoRC(hr);

		return new FUNCDESC(ppFuncDesc.getValue());
	}

	public VARDESC getVarDesc(int index) {
		PointerByReference ppVarDesc = new PointerByReference();
		HRESULT hr = this.typeInfo.GetVarDesc(new UINT(index), ppVarDesc);
		COMUtils.checkAutoRC(hr);
		
		return new VARDESC(ppVarDesc.getValue());
	}

	public String[] getNames(MEMBERID memid, int maxNames) {
		String[] result = new String[maxNames];
		BSTR[] rgBstrNames = new BSTR[maxNames];
		UINTbyReference pcNames = new UINTbyReference();
		HRESULT hr = this.typeInfo.GetNames(memid, rgBstrNames, new UINT(maxNames), pcNames);
		COMUtils.checkAutoRC(hr);
		
		for (int i = 0; i < rgBstrNames.length; i++) {
			if(rgBstrNames[i] != null)
			{
				result[i] = rgBstrNames[i].getValue();
				OLEAUTO.SysFreeString(rgBstrNames[i]);
			}
		}
		
		return result;
	}

	public HREFTYPE getRefTypeOfImplType(int index) {
		HREFTYPEbyReference ppTInfo = new HREFTYPEbyReference();
		HRESULT hr = this.typeInfo.GetRefTypeOfImplType(new UINT(index),
				ppTInfo);
		COMUtils.checkAutoRC(hr);

		return ppTInfo.getValue();
	}

	public int getImplTypeFlags(int index) {
		IntByReference pImplTypeFlags = new IntByReference();
		HRESULT hr = this.typeInfo.GetImplTypeFlags(new UINT(index),
				pImplTypeFlags);
		COMUtils.checkAutoRC(hr);

		return pImplTypeFlags.getValue();
	}

	public MEMBERID[] getIDsOfNames(LPOLESTR[] rgszNames, int cNames) {
		MEMBERID[] pMemId = new MEMBERID[cNames];
		HRESULT hr = this.typeInfo.GetIDsOfNames(rgszNames, new UINT(cNames), pMemId);
		COMUtils.checkAutoRC(hr);

		return pMemId;
	}

	public Invoke Invoke(PVOID pvInstance, MEMBERID memid, WORD wFlags,
			DISPPARAMS.ByReference pDispParams) {

		VARIANT.ByReference pVarResult = new VARIANT.ByReference();
		EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
		UINTbyReference puArgErr = new UINTbyReference();

		HRESULT hr = this.typeInfo.Invoke(pvInstance, memid, wFlags,
				pDispParams, pVarResult, pExcepInfo, puArgErr);
		COMUtils.checkAutoRC(hr);

		return new Invoke(pVarResult, pExcepInfo, puArgErr.getValue()
				.intValue());
	}

	public static class Invoke {
		private VARIANT.ByReference pVarResult;
		private EXCEPINFO.ByReference pExcepInfo;
		private int puArgErr;

		public Invoke(VARIANT.ByReference pVarResult,
				EXCEPINFO.ByReference pExcepInfo, int puArgErr) {
			this.pVarResult = pVarResult;
			this.pExcepInfo = pExcepInfo;
			this.puArgErr = puArgErr;
		}

		public VARIANT.ByReference getpVarResult() {
			return pVarResult;
		}

		public EXCEPINFO.ByReference getpExcepInfo() {
			return pExcepInfo;
		}

		public int getPuArgErr() {
			return puArgErr;
		}
	}

	public TypeInfoDoc getDocumentation(MEMBERID memid) {
		BSTRByReference pBstrName = new BSTRByReference();
		BSTRByReference pBstrDocString = new BSTRByReference();
		DWORDbyReference pdwHelpContext = new DWORDbyReference();
		BSTRByReference pBstrHelpFile = new BSTRByReference();

		HRESULT hr = this.typeInfo.GetDocumentation(memid, pBstrName,
				pBstrDocString, pdwHelpContext, pBstrHelpFile);
		COMUtils.checkTypeLibRC(hr);

		TypeInfoDoc TypeInfoDoc = new TypeInfoDoc(pBstrName.getString(),
				pBstrDocString.getString(), pdwHelpContext.getValue()
						.intValue(), pBstrHelpFile.getString());
		
		OLEAUTO.SysFreeString(pBstrName.getValue());
		OLEAUTO.SysFreeString(pBstrDocString.getValue());
		OLEAUTO.SysFreeString(pBstrHelpFile.getValue());
		
		return TypeInfoDoc;
	}

	public static class TypeInfoDoc {
		private String name;
		private String docString;
		private int helpContext;
		private String helpFile;

		public TypeInfoDoc(String name, String docString, int helpContext,
				String helpFile) {
			this.name = name;
			this.docString = docString;
			this.helpContext = helpContext;
			this.helpFile = helpFile;
		}

		public String getName() {
			return name;
		}

		public String getDocString() {
			return docString;
		}

		public int getHelpContext() {
			return helpContext;
		}

		public String getHelpFile() {
			return helpFile;
		}
	}

	public DllEntry GetDllEntry(MEMBERID memid, INVOKEKIND invKind) {
		BSTRByReference pBstrDllName = new BSTRByReference();
		BSTRByReference pBstrName = new BSTRByReference();
		WORDbyReference pwOrdinal = new WORDbyReference();

		HRESULT hr = this.typeInfo.GetDllEntry(memid, invKind, pBstrDllName,
				pBstrName, pwOrdinal);
		COMUtils.checkTypeLibRC(hr);

		OLEAUTO.SysFreeString(pBstrDllName.getValue());
		OLEAUTO.SysFreeString(pBstrName.getValue());
		
		return new DllEntry(pBstrDllName.getString(), pBstrName.getString(),
				pwOrdinal.getValue().intValue());
	}

	public static class DllEntry {
		private String dllName;
		private String name;
		private int ordinal;

		public DllEntry(String dllName, String name, int ordinal) {
			this.dllName = dllName;
			this.name = name;
			this.ordinal = ordinal;
		}

		public String getDllName() {
			return dllName;
		}

		public void setDllName(String dllName) {
			this.dllName = dllName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}
	}

	public ITypeInfo getRefTypeInfo(HREFTYPE hreftype) {
		PointerByReference ppTInfo = new PointerByReference();
		HRESULT hr = this.typeInfo.GetRefTypeInfo(hreftype, ppTInfo);
		COMUtils.checkAutoRC(hr);

		return new ITypeInfo(ppTInfo.getValue());
	}

	public PointerByReference AddressOfMember(MEMBERID memid, INVOKEKIND invKind) {
		PointerByReference ppv = new PointerByReference();
		HRESULT hr = this.typeInfo.AddressOfMember(memid, invKind, ppv);
		COMUtils.checkAutoRC(hr);

		return ppv;
	}

	public PointerByReference CreateInstance(IUnknown pUnkOuter, REFIID riid) {
		PointerByReference ppvObj = new PointerByReference();
		HRESULT hr = this.typeInfo.CreateInstance(pUnkOuter, riid, ppvObj);
		COMUtils.checkAutoRC(hr);

		return ppvObj;
	}

	public String GetMops(MEMBERID memid) {

		BSTRByReference pBstrMops = new BSTRByReference();
		HRESULT hr = this.typeInfo.GetMops(memid, pBstrMops);
		COMUtils.checkAutoRC(hr);

		return pBstrMops.getString();
	}

	public ContainingTypeLib GetContainingTypeLib() {

		PointerByReference ppTLib = new PointerByReference();
		UINTbyReference pIndex = new UINTbyReference();

		HRESULT hr = this.typeInfo.GetContainingTypeLib(ppTLib, pIndex);
		COMUtils.checkAutoRC(hr);

		return new ContainingTypeLib(new ITypeLib(ppTLib.getValue()), pIndex
				.getValue().intValue());
	}

	public static class ContainingTypeLib {
		private ITypeLib typeLib;
		private int index;

		public ContainingTypeLib(ITypeLib typeLib, int index) {
			this.typeLib = typeLib;
			this.index = index;
		}

		public ITypeLib getTypeLib() {
			return typeLib;
		}

		public void setTypeLib(ITypeLib typeLib) {
			this.typeLib = typeLib;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	public void ReleaseTypeAttr(TYPEATTR pTypeAttr) {
		this.typeInfo.ReleaseTypeAttr(pTypeAttr);
	}

	public void ReleaseFuncDesc(FUNCDESC pFuncDesc) {
		this.typeInfo.ReleaseFuncDesc(pFuncDesc);
	}

	public void ReleaseVarDesc(VARDESC pVarDesc) {
		this.typeInfo.ReleaseVarDesc(pVarDesc);
	}
}
