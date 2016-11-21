/*
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

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

    public void openDocument(String filename) throws COMException {
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
