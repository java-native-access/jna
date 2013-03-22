package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class ITypeLibUtil {

	private ITypeLib typelib;
	// get user default lcid
	private LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
	private String name;
	private String docString;
	private long helpContext;
	private String helpFile;

	public ITypeLibUtil(String clsidStr) {
		CLSID.ByReference clsid = new CLSID.ByReference();
		// get CLSID from string
		HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(clsidStr),
				clsid);
		COMUtils.checkTypeLibRC(hr);

		// load typelib
		PointerByReference pTypeLib = new PointerByReference();
		hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 1, 0, lcid, pTypeLib);
		COMUtils.checkTypeLibRC(hr);

		// init type lib class
		typelib = new ITypeLib(pTypeLib.getValue());

		this.initTypeLibInfo();
	}

	private void initTypeLibInfo() {
		Object[] documentation = this.getDocumentation(-1);
		this.name = (String) documentation[0];
		this.docString = (String) documentation[1];
		this.helpContext = (Long) documentation[2];
		this.helpFile = (String) documentation[3];
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
	
	public Object[] getDocumentation(int index) {
		Object[] result = new Object[4];
		BSTRByReference pBstrName = new BSTRByReference();
		BSTRByReference pBstrDocString = new BSTRByReference();
		DWORDbyReference pdwHelpContext = new DWORDbyReference();
		BSTRByReference pBstrHelpFile = new BSTRByReference();

		HRESULT hr = typelib.GetDocumentation(index, pBstrName, pBstrDocString,
				pdwHelpContext, pBstrHelpFile);
		COMUtils.checkTypeLibRC(hr);

		result[0] = pBstrName.getString();
		result[1] = pBstrDocString.getString();
		result[2] = pdwHelpContext.getValue().longValue();
		result[3] = pBstrHelpFile.getString();
		return result;
	}

	public TLIBATTR getLibAttr() {
		TLIBATTR.ByReference ppTLibAttr = new TLIBATTR.ByReference();
		HRESULT hr = typelib.GetLibAttr(ppTLibAttr);
		COMUtils.checkTypeLibRC(hr);

		return ppTLibAttr;
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
