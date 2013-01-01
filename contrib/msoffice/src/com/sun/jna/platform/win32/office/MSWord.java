package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.OleAut32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.COMUtils;

public class MSWord extends COMObject {

	public MSWord() throws COMException {
		super("Word.Application");
	}

	public void setVisible(boolean visible) throws COMException {
		VARIANT[] variantArr = new VARIANT[1];
		VARIANT variant = new VARIANT(Variant.VT_I1);
		variant._variant.__variant.iVal = 1;
		variantArr[0] = variant;

		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_PROPERTYPUT, null,
				this.iDispatch, "Visible", 1, variantArr);

		COMUtils.SUCCEEDED(hr);

	}

	public void quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_METHOD, null,
				this.iDispatch, "Quit", 1, new VARIANT[1]);

		COMUtils.SUCCEEDED(hr);
	}
}
