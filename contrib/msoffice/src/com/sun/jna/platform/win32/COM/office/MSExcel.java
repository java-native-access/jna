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

public class MSExcel extends COMLateBindingObject {

    public MSExcel() throws COMException {
        super("Excel.Application", false);
    }

    public MSExcel(boolean visible) throws COMException {
        this();
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

    public void openExcelBook(String filename) throws COMException {
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

        public Application(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class Workbooks extends COMLateBindingObject {
        public Workbooks(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class ActiveWorkbook extends COMLateBindingObject {
        public ActiveWorkbook(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class ActiveSheet extends COMLateBindingObject {
        public ActiveSheet(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public class Range extends COMLateBindingObject {
        public Range(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }
}
