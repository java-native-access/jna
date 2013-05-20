package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMObject;
import com.sun.jna.platform.win32.COM.IDispatch;

public class MSWord extends COMObject {

	public MSWord() throws COMException {
		super("Word.Application", false);
	}

	public MSWord(boolean visible) throws COMException {
		this();
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
		VARIANT[] args = new VARIANT[2];
		args[0] = new VARIANT(FileFormat);
		args[1] = new VARIANT(FileName);
		
		this.invokeNoReply("SaveAs", this.getActiveDocument().getIDispatch(),
				args);
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
