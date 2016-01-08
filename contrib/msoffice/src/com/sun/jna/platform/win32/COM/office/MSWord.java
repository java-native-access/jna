package com.sun.jna.platform.win32.COM.office;

import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.WinDef.LONG;

public class MSWord extends COMLateBindingObject {

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
                "Selection", this.getIDispatch()));
        this.invokeNoReply("TypeText", pSelection, new VARIANT(text));
    }

    public void Save(boolean bNoPrompt, LONG originalFormat) throws COMException {
        VARIANT vtNoPrompt = new VARIANT(bNoPrompt);
        VARIANT vtOriginalFormat = new VARIANT(originalFormat);

        this.invokeNoReply("Save", this.getDocuments(),
                vtNoPrompt, vtOriginalFormat);
    }

    public void SaveAs(String FileName, LONG FileFormat) throws COMException {
        VARIANT vtFileName = new VARIANT(FileName);
        VARIANT vtFileFormat = new VARIANT(FileFormat);

        this.invokeNoReply("SaveAs", this.getActiveDocument(),
                vtFileName, vtFileFormat);
    }

    public ActiveDocument getActiveDocument() {
        return new ActiveDocument(this.getAutomationProperty("ActiveDocument"));
    }

    public Documents getDocuments() {
        // GetDocuments
        Documents pDocuments = new Documents(this.getAutomationProperty(
                "Documents", this.getApplication().getIDispatch()));

        return pDocuments;
    }

    public Application getApplication() {
        return new Application(this.getAutomationProperty("Application"));
    }

    public class Application extends COMLateBindingObject {
        public Application(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class Documents extends COMLateBindingObject {
        public Documents(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class ActiveDocument extends COMLateBindingObject {
        public ActiveDocument(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class Selection extends COMLateBindingObject {
        public Selection(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }
}
