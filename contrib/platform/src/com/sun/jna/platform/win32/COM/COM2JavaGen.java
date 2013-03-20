package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEbyReference;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class COM2JavaGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Microsoft Shell Controls And Automation
			CLSID.ByReference clsid = new CLSID.ByReference();
			// get CLSID from string
			HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(
					"{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}"), clsid);
			COMUtils.checkTypeLibRC(hr);
			
			// get user default lcid
			LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
			
			// load typelib
			PointerByReference pTypeLib = new PointerByReference();
			hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, 1, 0, lcid,
					pTypeLib);
			COMUtils.checkTypeLibRC(hr);

			ITypeLib typelib = new ITypeLib(pTypeLib.getValue());
			
			BSTRByReference pBstrName = new BSTRByReference();
			BSTRByReference pBstrDocString = new BSTRByReference();
			DWORDbyReference pdwHelpContext = new DWORDbyReference();
			BSTRByReference pBstrHelpFile = new BSTRByReference();
			HRESULT hr2 = typelib.GetDocumentation(-1, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile);

			String name = pBstrName.getValue();
			String docString = pBstrDocString.getValue();
			DWORD value = pdwHelpContext.getValue();
			String helpFile = pBstrHelpFile.getValue();

			ITypeInfo.ByReference typeinfo = new ITypeInfo.ByReference();
			UINT typeInfoCount = typelib.GetTypeInfoCount();

			for (int i = 0; i < typeInfoCount.intValue(); ++i) {
				TYPEKIND.ByReference typekind = new TYPEKIND.ByReference();
				typelib.GetTypeInfoType(new UINT(i), typekind);
				if (typekind.value == TYPEKIND.TKIND_COCLASS) {
					BSTR className = new BSTR();
					TYPEATTR.ByReference typeattr = new TYPEATTR.ByReference();
					typelib.GetTypeInfo(new UINT(i), typeinfo);
					typeinfo.GetDocumentation(OaIdl.MEMBERID_NIL, className,
							null, null, null);
					typeinfo.GetTypeAttr(typeattr);

					for (int j = 0; j < typeattr.cImplTypes.intValue(); ++j) {
						// interface!
						BSTR interfaceName = new BSTR();
						HREFTYPEbyReference hreftype = new HREFTYPEbyReference();
						ITypeInfo.ByReference classtypeinfo = new ITypeInfo.ByReference();
						typeinfo.GetRefTypeOfImplType(new UINT(j), hreftype);
						typeinfo.GetRefTypeInfo(hreftype.getValue(),
								classtypeinfo);
						classtypeinfo.GetDocumentation(OaIdl.MEMBERID_NIL,
								interfaceName, null, null, null);
						// associate interfaceName with classGUID here
					}
				}
			}

			System.out.println("end.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
