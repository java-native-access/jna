package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAut32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSWord extends COMObject {

	public MSWord() throws COMException {
		super("Word.Application", false);
	}

	public MSWord(boolean visible) throws COMException {
		this();
		this.setVisible(Variant.VARIANT_TRUE);
	}

	public void setVisible(VARIANT_BOOL bVisible) throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAut32.DISPATCH_PROPERTYPUT, result, this.iDispatch,
				"Visible", new VARIANT(bVisible));
	}

	public String getVersion() throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Version");

		return result.getValue().toString();
	}

	public HRESULT newDocument() throws COMException {
		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				getDocuments().getIDispatch(), "Add");

		return hr;
	}

	public HRESULT openDocument(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		BSTR bstrFilename = OleAut32.INSTANCE.SysAllocString(filename);
		VARIANT varFilename = new VARIANT(bstrFilename);
		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				getDocuments().getIDispatch(), "Open", varFilename);

		return hr;
	}

	public HRESULT closeActiveDocument(VARIANT_BOOL bSave)
			throws COMException {

		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				getActiveDocument().getIDispatch(), "Close", new VARIANT(bSave));

		return hr;
	}

	public HRESULT quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT insertText(String text) throws COMException {
		HRESULT hr;

		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "Selection");
		Selection pSelection = new Selection((IDispatch) result.getValue());

		BSTR bstrText = OleAut32.INSTANCE.SysAllocString(text);
		VARIANT varText = new VARIANT(bstrText);
		hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				pSelection.getIDispatch(), "TypeText", varText);

		return hr;
	}

	public ActiveDocument getActiveDocument() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"ActiveDocument");
		
		COMUtils.SUCCEEDED(hr);
		return new ActiveDocument((IDispatch) result.getValue());
	}
	
	public Documents getDocuments() {
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		
		COMUtils.SUCCEEDED(hr);
		return new Documents((IDispatch) result.getValue());
	}
	
	public Application getApplication() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Application");
		
		COMUtils.SUCCEEDED(hr);
		return new Application((IDispatch) result.getValue());
	}
	
	public class Application extends COMObject {

		public Application(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
	
	public class Documents extends COMObject {

		public Documents(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class ActiveDocument extends COMObject {

		public ActiveDocument(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class Selection extends COMObject {

		public Selection(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
}
