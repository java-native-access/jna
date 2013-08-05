package com.sun.jna.platform.win32.office;

<<<<<<< HEAD
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSWord extends COMLateBindingObject {
=======
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSWord extends COMObject {
>>>>>>> master

	public MSWord() throws COMException {
		super("Word.Application", false);
	}

	public MSWord(boolean visible) throws COMException {
		this();
<<<<<<< HEAD
		this.setVisible(visible);
	}

	public void setVisible(boolean bVisible) throws COMException {
		this.setProperty("Visible", bVisible);
	}

	public String getVersion() throws COMException {
		return this.getStringProperty("Version");
	}

	public void newDocument() throws COMException {
		this.invokeNoReply("Add", getDocuments());
	}

	public void openDocument(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		this.invokeNoReply("Open", getDocuments(), new VARIANT(filename));
	}

	public void closeActiveDocument(boolean bSave) throws COMException {
		this.invokeNoReply("Close", getActiveDocument(), new VARIANT(bSave));
	}

	public void quit() throws COMException {
		this.invokeNoReply("Quit");
	}

	public void insertText(String text) throws COMException {
		Selection pSelection = new Selection(this.getAutomationProperty(
				"Selection", this.iDispatch));
		this.invokeNoReply("TypeText", pSelection, new VARIANT(text));
	}

	public void SaveAs(String FileName, LONG FileFormat) throws COMException {
		VARIANT vtFileName = new VARIANT(FileName);
		VARIANT vtFileFormat = new VARIANT(FileFormat);
		
		this.invokeNoReply("SaveAs", this.getActiveDocument().getIDispatch(),
				vtFileName, vtFileFormat);
	}

	public ActiveDocument getActiveDocument() {
		return new ActiveDocument(this.getAutomationProperty("ActiveDocument"));
	}

	public Documents getDocuments() {
		// GetDocuments
		return new Documents(this.getAutomationProperty("Documents"));
	}

	public Application getApplication() {
		return new Application(this.getAutomationProperty("Application"));
	}

	public class Application extends COMLateBindingObject {
=======
		this.setVisible(Variant.VARIANT_TRUE);
	}

	public void setVisible(VARIANT_BOOL bVisible) throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, result, this.iDispatch,
				"Visible", new VARIANT(bVisible));
	}

	public String getVersion() throws COMException {
		VARIANT.ByReference result = new VARIANT.ByReference();
		this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Version");

		return result.getValue().toString();
	}

	public HRESULT newDocument() throws COMException {
		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null,
				getDocuments().getIDispatch(), "Add");

		return hr;
	}

	public HRESULT openDocument(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		BSTR bstrFilename = OleAuto.INSTANCE.SysAllocString(filename);
		VARIANT varFilename = new VARIANT(bstrFilename);
		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null,
				getDocuments().getIDispatch(), "Open", varFilename);

		return hr;
	}

	public HRESULT closeActiveDocument(VARIANT_BOOL bSave)
			throws COMException {

		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null,
				getActiveDocument().getIDispatch(), "Close", new VARIANT(bSave));

		return hr;
	}

	public HRESULT quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAuto.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT insertText(String text) throws COMException {
		HRESULT hr;

		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "Selection");
		Selection pSelection = new Selection((IDispatch) result.getValue());

		BSTR bstrText = OleAuto.INSTANCE.SysAllocString(text);
		VARIANT varText = new VARIANT(bstrText);
		hr = oleMethod(OleAuto.DISPATCH_METHOD, null,
				pSelection.getIDispatch(), "TypeText", varText);

		return hr;
	}

	public ActiveDocument getActiveDocument() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"ActiveDocument");
		
		COMUtils.SUCCEEDED(hr);
		return new ActiveDocument((IDispatch) result.getValue());
	}
	
	public Documents getDocuments() {
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Documents");
		
		COMUtils.SUCCEEDED(hr);
		return new Documents((IDispatch) result.getValue());
	}
	
	public Application getApplication() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.iDispatch,
				"Application");
		
		COMUtils.SUCCEEDED(hr);
		return new Application((IDispatch) result.getValue());
	}
	
	public class Application extends COMObject {
>>>>>>> master

		public Application(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
<<<<<<< HEAD

	public class Documents extends COMLateBindingObject {
=======
	
	public class Documents extends COMObject {
>>>>>>> master

		public Documents(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class ActiveDocument extends COMLateBindingObject {
=======
	public class ActiveDocument extends COMObject {
>>>>>>> master

		public ActiveDocument(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class Selection extends COMLateBindingObject {
=======
	public class Selection extends COMObject {
>>>>>>> master

		public Selection(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
}
