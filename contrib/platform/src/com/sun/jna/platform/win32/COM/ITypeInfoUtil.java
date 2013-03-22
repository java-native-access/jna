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

	private ITypeInfo typeInfo;

	public ITypeInfoUtil(ITypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}

	public TYPEATTR getTypeAttr() {
		TYPEATTR.ByReference pTypeAttr = new TYPEATTR.ByReference();
		HRESULT hr = this.typeInfo.GetTypeAttr(pTypeAttr);
		COMUtils.checkAutoRC(hr);

		return pTypeAttr;
	}

	public TYPEATTR getTypeComp() {
		TYPEATTR.ByReference pTypeAttr = new TYPEATTR.ByReference();
		HRESULT hr = this.typeInfo.GetTypeAttr(pTypeAttr);
		COMUtils.checkAutoRC(hr);

		return pTypeAttr;
	}

	public FUNCDESC getFuncDesc(int index) {
		FUNCDESC.ByReference pFuncDesc = new FUNCDESC.ByReference();
		HRESULT hr = this.typeInfo.GetFuncDesc(new UINT(index), pFuncDesc);
		COMUtils.checkAutoRC(hr);

		return pFuncDesc;
	}

	public VARDESC getVarDesc(int index) {
		VARDESC.ByReference pVarDesc = new VARDESC.ByReference();
		HRESULT hr = this.typeInfo.GetVarDesc(new UINT(index), pVarDesc);
		COMUtils.checkAutoRC(hr);

		return pVarDesc;
	}

	public Object[] getNames(MEMBERID memid, int maxNames) {
		BSTR[] rgBstrNames = new BSTR[0];
		UINTbyReference pcNames = new UINTbyReference();
		HRESULT hr = this.typeInfo.GetNames(memid, rgBstrNames, new UINT(
				maxNames), pcNames);
		COMUtils.checkAutoRC(hr);

		return new Object[] { rgBstrNames, pcNames };
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
		MEMBERID[] pMemId = new MEMBERID[0];
		HRESULT hr = this.typeInfo.GetIDsOfNames(rgszNames, new UINT(cNames),
				pMemId);
		COMUtils.checkAutoRC(hr);

		return pMemId;
	}
/*
	public HRESULT Invoke(
	 [in] PVOID pvInstance,
	 [in] MEMBERID memid,
	 [in] WORD wFlags,
	 [out][in] DISPPARAMS.ByReference pDispParams,
	 [out] VARIANT.ByReference pVarResult,
	 [out] EXCEPINFO.ByReference pExcepInfo,
	 [out] UINTbyReference puArgErr) {

		int hr = this.invoke(11, new Object[] { this.getPointer(), pvInstance,
				memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr });

		return new HRESULT(hr);
	}
*/
	public Object[] getDocumentation(MEMBERID memid) {
		Object[] result = new Object[4];
		BSTRByReference pBstrName = new BSTRByReference();
		BSTRByReference pBstrDocString = new BSTRByReference();
		DWORDbyReference pdwHelpContext = new DWORDbyReference();
		BSTRByReference pBstrHelpFile = new BSTRByReference();

		HRESULT hr = this.typeInfo.GetDocumentation(memid, pBstrName,
				pBstrDocString, pdwHelpContext, pBstrHelpFile);
		COMUtils.checkTypeLibRC(hr);

		result[0] = pBstrName.getString();
		result[1] = pBstrDocString.getString();
		result[2] = pdwHelpContext.getValue().longValue();
		result[3] = pBstrHelpFile.getString();
		return result;
	}
/*
	public [local] HRESULT GetDllEntry(
	 [in] MEMBERID memid,
	 [in] INVOKEKIND invKind,
	 [out] BSTR pBstrDllName,
	 [out] BSTR pBstrName,
	 [out] WORDbyReference pwOrdinal) {

		int hr = this.invoke(13, new Object[] { this.getPointer(), memid,
				invKind, pBstrDllName, pBstrName, pwOrdinal });

		return new HRESULT(hr);
	}

	public ITypeInfo getRefTypeInfo(HREFTYPE hreftype) {
		ITypeInfo.ByReference typeinfo = new ITypeInfo.ByReference();
		HRESULT hr = this.typeInfo.GetRefTypeInfo(hreftype, typeinfo);
		COMUtils.checkAutoRC(hr);

		return typeinfo;
	}

	public [local] HRESULT AddressOfMember(
	 [in] MEMBERID memid,
	 [in] INVOKEKIND invKind,
	 [out] PointerByReference ppv) {

		int hr = this.invoke(15, new Object[] { this.getPointer(), memid,
				invKind, ppv });

		return new HRESULT(hr);
	}

	public [local] HRESULT CreateInstance(
	 [in] IUnknown pUnkOuter,
	 [in] REFIID riid,
	 [iid_is][out] PointerByReference ppvObj) {

		int hr = this.invoke(16, new Object[] { this.getPointer(), pUnkOuter,
				riid, ppvObj });

		return new HRESULT(hr);
	}

	public HRESULT GetMops(
	 [in] MEMBERID memid,
	 [out] BSTR pBstrMops) {

		int hr = this.invoke(17, new Object[] { this.getPointer(), memid,
				pBstrMops });

		return new HRESULT(hr);
	}

	public [local] HRESULT GetContainingTypeLib(
	 [out] ITypeLib.ByReference pTLib,
	 [out] UINTbyReference pIndex) {

		PointerByReference ppTLib = new PointerByReference();
		int hr = this.invoke(18, new Object[] { this.getPointer(), ppTLib,
				pIndex });
		pTLib.setPointer(ppTLib.getPointer());

		return new HRESULT(hr);
	}

	public void ReleaseTypeAttr(TYPEATTR pTypeAttr) {
		this.typeInfo.ReleaseTypeAttr(pTypeAttr);
	}

	public void ReleaseFuncDesc(FUNCDESC pFuncDesc) {
		this.typeInfo.ReleaseFuncDesc(pFuncDesc);
	}

	public void ReleaseVarDesc(VARDESC pVarDesc) {
		this.typeInfo.ReleaseVarDesc(pVarDesc);
	}*/

}
