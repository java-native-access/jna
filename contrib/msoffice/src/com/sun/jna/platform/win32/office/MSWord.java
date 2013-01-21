package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAut32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.AutomationException;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSWord extends COMObject {

	private Documents m_pDocuments;

	private ActiveDocument m_pActiveDocument;

	public MSWord() throws AutomationException {
		super("Word.Application", false);
	}

	public MSWord(boolean visible) throws AutomationException {
		this();
		this.setVisible(Variant.VARIANT_TRUE);
	}

	public void setVisible(VARIANT_BOOL bVisible) throws AutomationException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAut32.DISPATCH_PROPERTYPUT, result, this.iDispatch,
				"Visible", new VARIANT(bVisible));
	}

	public String getVersion() throws AutomationException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Version");

		return result.getValue().toString();
	}

	public HRESULT newDocument() throws AutomationException {
		HRESULT hr;
		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		this.m_pDocuments = new Documents((IDispatch) result.getValue());

		VARIANT.ByReference result2 = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_METHOD, result2,
				m_pDocuments.getIDispatch(), "Add");
		this.m_pActiveDocument = new ActiveDocument((IDispatch) result2.getValue());

		return hr;
	}

	public HRESULT openDocument(String filename, boolean bVisible)
			throws AutomationException {

		HRESULT hr;
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		m_pDocuments = new Documents((IDispatch) result.getValue());

		// OpenDocument
		BSTR bstrFilename = OleAut32.INSTANCE.SysAllocString(filename);
		VARIANT varFilename = new VARIANT(bstrFilename);
		VARIANT.ByReference result2 = new VARIANT.ByReference();

		hr = oleMethod(OleAut32.DISPATCH_METHOD, result2,
				m_pDocuments.getIDispatch(), "Open", varFilename);
		this.m_pActiveDocument = new ActiveDocument(
				(IDispatch) result2.getValue());

		return hr;
	}

	public HRESULT closeActiveDocument(VARIANT_BOOL bSave)
			throws AutomationException {

		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				m_pActiveDocument.getIDispatch(), "Close", new VARIANT(bSave));
		this.m_pActiveDocument = null;

		return hr;
	}

	public HRESULT quit() throws AutomationException {
		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT insertText(String text) throws AutomationException {
		HRESULT hr;

		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				m_pActiveDocument.getIDispatch(), "Application");
		Application pDocApp = new Application((IDispatch) result.getValue());

		VARIANT.ByReference result2 = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result2,
				pDocApp.getIDispatch(), "Selection");
		Selection pSelection = new Selection((IDispatch) result2.getValue());

		BSTR bstrText = OleAut32.INSTANCE.SysAllocString(text);
		VARIANT varText = new VARIANT(bstrText);
		hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				pSelection.getIDispatch(), "TypeText", varText);

		pDocApp.release();
		pSelection.release();

		return hr;
	}

	public class Documents extends COMObject {

		public Documents(IDispatch iDispatch) throws AutomationException {
			super(iDispatch);
		}
	}

	public class ActiveDocument extends COMObject {

		public ActiveDocument(IDispatch iDispatch) throws AutomationException {
			super(iDispatch);
		}
	}

	public class Application extends COMObject {

		public Application(IDispatch iDispatch) throws AutomationException {
			super(iDispatch);
		}
	}

	public class Selection extends COMObject {

		public Selection(IDispatch iDispatch) throws AutomationException {
			super(iDispatch);
		}
	}

}
