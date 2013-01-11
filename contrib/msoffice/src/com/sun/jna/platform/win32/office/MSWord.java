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

	private Documents m_pDocuments;

	private ActiveDocument m_pActiveDocument;

	public MSWord() throws COMException {
		super("Word.Application");
	}

	public MSWord(boolean visible) throws COMException {
		this();
		this.setVisible(Variant.VARIANT_TRUE);
	}

	public void setVisible(VARIANT_BOOL bVisible) throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result);

		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_PROPERTYPUT, result,
				this.iDispatch, "Visible", new VARIANT(bVisible));

		COMUtils.SUCCEEDED(hr);

	}

	public String getVersion() throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result);

		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "Version");

		COMUtils.SUCCEEDED(hr);
		return result.getValue().toString();
	}

	public HRESULT newDocument(VARIANT_BOOL visible) throws COMException {
		HRESULT hr;
		// Mozda problem?
		VARIANT.ByReference result = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result);
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		m_pDocuments = new Documents((IDispatch)result.getValue());

		VARIANT.ByReference result2 = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result);
		hr = oleMethod(OleAut32.DISPATCH_METHOD, result2,
				m_pDocuments.getIDispatch(), "Add");
		m_pActiveDocument = new ActiveDocument((IDispatch)result2.getValue());

		return hr;
	}

	public HRESULT openDocument(String szFilename, boolean bVisible)
			throws COMException {
		HRESULT hr;
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result);
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		COMUtils.SUCCEEDED(hr);
		m_pDocuments = new Documents((IDispatch)result.getValue());

		// OpenDocument
		VARIANT[] variantArgs = new VARIANT[1];
		variantArgs[0] = new VARIANT(new BSTR(szFilename));
		VARIANT.ByReference result2 = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result2);
//		hr = oleMethod(OleAut32.DISPATCH_METHOD, result2,
//				m_pDocuments.getIDispatch(), "Open", 1, variantArgs);
		COMUtils.SUCCEEDED(hr);
		m_pDocuments = new Documents((IDispatch)result.getValue());

		VARIANT.ByReference result3 = new VARIANT.ByReference();
		OleAut32.INSTANCE.VariantInit(result3);
		oleMethod(OleAut32.DISPATCH_PROPERTYGET, result3,
				m_pActiveDocument.getIDispatch(), "Application");
		m_pActiveDocument = new ActiveDocument((IDispatch)result2.getValue());

		return hr;
	}

	public HRESULT closeActiveDocument(VARIANT_BOOL bSave) throws COMException {
		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				m_pActiveDocument.getIDispatch(), "Close", new VARIANT(bSave));

		this.m_pActiveDocument = null;
		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
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

}
