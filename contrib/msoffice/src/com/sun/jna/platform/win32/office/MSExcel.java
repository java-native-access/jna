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

public class MSExcel extends COMObject {

	public MSExcel() throws COMException {
		super("Excel.Application", false);
	}

	public MSExcel(boolean visible) throws COMException {
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

	public HRESULT newExcelBook() throws COMException {
		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null, getWorkbooks()
				.getIDispatch(), "Add");

		return hr;
	}

	public HRESULT openExcelBook(String filename, boolean bVisible)
			throws COMException {
		// OpenDocument
		BSTR bstrFilename = OleAut32.INSTANCE.SysAllocString(filename);
		VARIANT varFilename = new VARIANT(bstrFilename);
		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null, getWorkbooks()
				.getIDispatch(), "Open", varFilename);

		return hr;
	}

	public HRESULT closeActiveWorkbook(VARIANT_BOOL bSave) throws COMException {

		HRESULT hr = oleMethod(OleAut32.DISPATCH_METHOD, null,
				getActiveWorkbook().getIDispatch(), "Close", new VARIANT(bSave));

		return hr;
	}

	public HRESULT quit() throws COMException {
		HRESULT hr = this.oleMethod(OleAut32.DISPATCH_METHOD, null,
				this.iDispatch, "Quit");

		COMUtils.SUCCEEDED(hr);
		return hr;
	}

	public HRESULT insertValue(String range, String value) throws COMException {
		HRESULT hr;

		BSTR bstrRange = OleAut32.INSTANCE.SysAllocString(range);
		VARIANT varRange = new VARIANT(bstrRange);
		VARIANT.ByReference result = new VARIANT.ByReference();
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result, this
				.getActiveSheet().getIDispatch(), "Range", varRange);
		Range pRange = new Range((IDispatch) result.getValue());

		BSTR bstrValue = OleAut32.INSTANCE.SysAllocString(value);
		VARIANT varText = new VARIANT(bstrValue);
		hr = oleMethod(OleAut32.DISPATCH_PROPERTYPUT, null,
				pRange.getIDispatch(), "Value", varText);

		return hr;
	}

	public Application getApplication() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "Application");

		COMUtils.SUCCEEDED(hr);
		return new Application((IDispatch) result.getValue());
	}

	public ActiveWorkbook getActiveWorkbook() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "ActiveWorkbook");

		COMUtils.SUCCEEDED(hr);
		return new ActiveWorkbook((IDispatch) result.getValue());
	}

	public Workbooks getWorkbooks() {
		// GetDocuments
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "WorkBooks");

		COMUtils.SUCCEEDED(hr);
		return new Workbooks((IDispatch) result.getValue());
	}

	public ActiveSheet getActiveSheet() {
		VARIANT.ByReference result = new VARIANT.ByReference();
		HRESULT hr = oleMethod(OleAut32.DISPATCH_PROPERTYGET, result,
				this.iDispatch, "ActiveSheet");

		COMUtils.SUCCEEDED(hr);
		return new ActiveSheet((IDispatch) result.getValue());
	}

	public class Application extends COMObject {

		public Application(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class Workbooks extends COMObject {

		public Workbooks(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class ActiveWorkbook extends COMObject {

		public ActiveWorkbook(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class ActiveSheet extends COMObject {

		public ActiveSheet(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}

	public class Range extends COMObject {

		public Range(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}
	}
}
