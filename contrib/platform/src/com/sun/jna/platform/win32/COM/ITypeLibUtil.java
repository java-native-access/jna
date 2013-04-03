package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLbyReference;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class ITypeLibUtil {

	public final static OleAuto OLEAUTO = OleAuto.INSTANCE;

	private ITypeLib typelib;
	// get user default lcid
	private LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
	private String name;
	private String docString;
	private int helpContext;
	private String helpFile;

	public ITypeLibUtil(String clsidStr, int wVerMajor, int wVerMinor) {
		CLSID.ByReference clsid = new CLSID.ByReference();
		// get CLSID from string
		HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(clsidStr),
				clsid);
		COMUtils.checkTypeLibRC(hr);

		// load typelib
		PointerByReference pTypeLib = new PointerByReference();
		hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, wVerMajor, wVerMinor, lcid, pTypeLib);
		COMUtils.checkTypeLibRC(hr);

		// init type lib class
		typelib = new ITypeLib(pTypeLib.getValue());

		this.initTypeLibInfo();
	}

	private void initTypeLibInfo() {
		TypeLibDoc documentation = this.getDocumentation(-1);
		this.name = documentation.getName();
		this.docString = documentation.getDocString();
		this.helpContext = documentation.getHelpContext();
		this.helpFile = documentation.getHelpFile();
	}

	public int getTypeInfoCount() {
		return this.typelib.GetTypeInfoCount().intValue();
	}

	public TYPEKIND getTypeInfoType(int index) {
		TYPEKIND.ByReference typekind = new TYPEKIND.ByReference();
		HRESULT hr = this.typelib.GetTypeInfoType(new UINT(index), typekind);
		COMUtils.checkTypeLibRC(hr);
		return typekind;
	}

	public ITypeInfo getTypeInfo(int index) {
		ITypeInfo.ByReference typeinfo = new ITypeInfo.ByReference();
		HRESULT hr = this.typelib.GetTypeInfo(new UINT(index), typeinfo);
		COMUtils.checkTypeLibRC(hr);
		return typeinfo;
	}

	public ITypeInfoUtil getTypeInfoUtil(int index) {
		return new ITypeInfoUtil(this.getTypeInfo(index));
	}

	public TLIBATTR getLibAttr() {
		TLIBATTR.ByReference ppTLibAttr = new TLIBATTR.ByReference();
		HRESULT hr = typelib.GetLibAttr(ppTLibAttr);
		COMUtils.checkTypeLibRC(hr);

		return ppTLibAttr;
	}

	public ITypeComp.ByReference GetTypeComp() {
		ITypeComp.ByReference pTComp = new ITypeComp.ByReference();
		HRESULT hr = this.typelib.GetTypeComp(pTComp);
		COMUtils.checkTypeLibRC(hr);

		return pTComp;
	}

	public TypeLibDoc getDocumentation(int index) {
		BSTRByReference pBstrName = new BSTRByReference();
		BSTRByReference pBstrDocString = new BSTRByReference();
		DWORDbyReference pdwHelpContext = new DWORDbyReference();
		BSTRByReference pBstrHelpFile = new BSTRByReference();

		HRESULT hr = typelib.GetDocumentation(index, pBstrName, pBstrDocString,
				pdwHelpContext, pBstrHelpFile);
		COMUtils.checkTypeLibRC(hr);

		TypeLibDoc typeLibDoc = new TypeLibDoc(pBstrName.getString(),
				pBstrDocString.getString(), pdwHelpContext.getValue()
						.intValue(), pBstrHelpFile.getString());

		OLEAUTO.SysFreeString(pBstrName.getValue());
		OLEAUTO.SysFreeString(pBstrDocString.getValue());
		OLEAUTO.SysFreeString(pBstrHelpFile.getValue());

		return typeLibDoc;
	}

	public static class TypeLibDoc {
		private String name;
		private String docString;
		private int helpContext;
		private String helpFile;

		public TypeLibDoc(String name, String docString, int helpContext,
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

	public IsName IsName(String nameBuf, int hashVal) {

		LPOLESTR szNameBuf = new LPOLESTR(nameBuf);
		ULONG lHashVal = new ULONG(hashVal);
		BOOLbyReference pfName = new BOOLbyReference();

		HRESULT hr = this.typelib.IsName(szNameBuf, lHashVal, pfName);
		COMUtils.checkTypeLibRC(hr);

		return new IsName(szNameBuf.getValue(), pfName.getValue()
				.booleanValue());
	}

	public static class IsName {

		private String nameBuf;
		private boolean name;

		public IsName(String nameBuf, boolean name) {
			this.nameBuf = nameBuf;
			this.name = name;
		}

		public String getNameBuf() {
			return nameBuf;
		}

		public boolean isName() {
			return name;
		}
	}

	public FindName FindName(String name, int hashVal, short found) {
		/* [annotation][out][in] */
		BSTRByReference szNameBuf = new BSTRByReference(
				OleAuto.INSTANCE.SysAllocString(name));
		/* [in] */ULONG lHashVal = new ULONG(hashVal);
		/* [out][in] */USHORTbyReference pcFound = new USHORTbyReference(found);

		HRESULT hr = this.typelib.FindName(szNameBuf, lHashVal, null, null,
				pcFound);
		COMUtils.checkTypeLibRC(hr);

		found = pcFound.getValue().shortValue();
		/* [length_is][size_is][out] */ITypeInfo[] ppTInfo = new ITypeInfo[found];
		/* [length_is][size_is][out] */MEMBERID[] rgMemId = new MEMBERID[found];
		hr = this.typelib.FindName(szNameBuf, lHashVal, ppTInfo, rgMemId,
				pcFound);
		COMUtils.checkTypeLibRC(hr);
		
		FindName findName = new FindName(szNameBuf.getString(), ppTInfo, rgMemId, found);
		OLEAUTO.SysFreeString(szNameBuf.getValue());
		
		return findName;
	}

	public static class FindName {
		private String nameBuf;
		private ITypeInfo[] pTInfo;
		private MEMBERID[] rgMemId;
		private short pcFound;

		public FindName(String nameBuf, ITypeInfo[] pTInfo, MEMBERID[] rgMemId,
				short pcFound) {
			this.nameBuf = nameBuf;
			this.pTInfo = pTInfo;
			this.rgMemId = rgMemId;
			this.pcFound = pcFound;
		}

		public String getNameBuf() {
			return nameBuf;
		}

		public ITypeInfo[] getTInfo() {
			return pTInfo;
		}

		public MEMBERID[] getMemId() {
			return rgMemId;
		}

		public short getFound() {
			return pcFound;
		}
	}

	public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
		this.typelib.ReleaseTLibAttr(pTLibAttr);
	}

	public LCID getLcid() {
		return lcid;
	}

	public ITypeLib getTypelib() {
		return typelib;
	}

	public String getName() {
		return name;
	}

	public String getDocString() {
		return docString;
	}

	public long getHelpContext() {
		return helpContext;
	}

	public String getHelpFile() {
		return helpFile;
	}

}
