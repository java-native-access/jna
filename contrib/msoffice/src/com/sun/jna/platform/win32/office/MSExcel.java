package com.sun.jna.platform.win32.office;

<<<<<<< HEAD
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSExcel extends COMLateBindingObject {

	public MSExcel() throws COMException {
		super("Excel.Application", true);
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

public class MSExcel extends COMObject {

	public MSExcel() throws COMException {
		super("Excel.Application", false);
>>>>>>> master
	}

	public MSExcel(boolean visible) throws COMException {
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

	public void newExcelBook() throws COMException {
		this.invokeNoReply("Add", getWorkbooks());
	}

	public void openExcelBook(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		this.invokeNoReply("Open", getWorkbooks(), new VARIANT(filename));
	}

	public void closeActiveWorkbook(boolean bSave) throws COMException {
		this.invokeNoReply("Close", getActiveWorkbook(), new VARIANT(bSave));
	}

	public void quit() throws COMException {
		this.invokeNoReply("Quit");
	}

	public void insertValue(String range, String value) throws COMException {
		Range pRange = new Range(this.getAutomationProperty("Range",
				this.getActiveSheet(), new VARIANT(range)));
		this.setProperty("Value", pRange, new VARIANT(value));
	}

	public Application getApplication() {
		return new Application(this.getAutomationProperty("Application"));
	}

	public ActiveWorkbook getActiveWorkbook() {
		return new ActiveWorkbook(this.getAutomationProperty("ActiveWorkbook"));
	}

	public Workbooks getWorkbooks() {
		return new Workbooks(this.getAutomationProperty("WorkBooks"));
	}

	public ActiveSheet getActiveSheet() {
		return new ActiveSheet(this.getAutomationProperty("ActiveSheet"));
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

	public HRESULT newExcelBook() throws COMException {
		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null, getWorkbooks()
				.getIDispatch(), "Add");

		return hr;
	}

	public HRESULT openExcelBook(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		BSTR bstrFilename = OleAuto.INSTANCE.SysAllocString(filename);
		VARIANT varFilename = new VARIANT(bstrFilename);
		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null, getWorkbooks()
				.getIDispatch(), "Open", varFilename);

		return hr;
	}

	public HRESULT closeActiveWorkbook(VARIANT_BOOL bSave) throws COMException {

		HRESULT hr = oleMethod(OleAuto.DISPATCH_METHOD, null,
				getActiveWorkbook().getIDispatch(), "Close", new VARIANT(bSave));

		return hr;
	}

	public HRESULT quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAuto.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT insertValue(String range, String value) throws COMException {
		HRESULT hr;

		BSTR bstrRange = OleAuto.INSTANCE.SysAllocString(range);
		VARIANT varRange = new VARIANT(bstrRange);
		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this
				.getActiveSheet().getIDispatch(), "Range", varRange);
		Range pRange = new Range((IDispatch) result.getValue());

		BSTR bstrValue = OleAuto.INSTANCE.SysAllocString(value);
		VARIANT varText = new VARIANT(bstrValue);
		hr = oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null,
				pRange.getIDispatch(), "Value", varText);

		return hr;
	}

	public Application getApplication() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "Application");

		COMUtils.SUCCEEDED(hr);
		return new Application((IDispatch) result.getValue());
	}

	public ActiveWorkbook getActiveWorkbook() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "ActiveWorkbook");

		COMUtils.SUCCEEDED(hr);
		return new ActiveWorkbook((IDispatch) result.getValue());
	}

	public Workbooks getWorkbooks() {
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "WorkBooks");

		COMUtils.SUCCEEDED(hr);
		return new Workbooks((IDispatch) result.getValue());
	}

	public ActiveSheet getActiveSheet() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAuto.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "ActiveSheet");

		COMUtils.SUCCEEDED(hr);
		return new ActiveSheet((IDispatch) result.getValue());
	}

	public class Application extends COMObject {
>>>>>>> master

		public Application(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class Workbooks extends COMLateBindingObject {
=======
	public class Workbooks extends COMObject {
>>>>>>> master

		public Workbooks(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class ActiveWorkbook extends COMLateBindingObject {
=======
	public class ActiveWorkbook extends COMObject {
>>>>>>> master

		public ActiveWorkbook(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class ActiveSheet extends COMLateBindingObject {
=======
	public class ActiveSheet extends COMObject {
>>>>>>> master

		public ActiveSheet(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

<<<<<<< HEAD
	public class Range extends COMLateBindingObject {
=======
	public class Range extends COMObject {
>>>>>>> master

		public Range(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
}
