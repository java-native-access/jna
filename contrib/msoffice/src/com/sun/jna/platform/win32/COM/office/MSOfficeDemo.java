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

import com.sun.jna.Pointer;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.Helper;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinDef.LONG;
import java.io.File;
import java.io.IOException;

public class MSOfficeDemo {
    public static void main(String[] args) throws IOException {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        try {
            MSOfficeDemo demo = new MSOfficeDemo();
            demo.testMSExcel();
            demo.testMSWord();
        } finally {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    public void testMSWord() throws IOException {
        File demoDocument = null;
        MSWord msWord = null;
        
        // http://msdn.microsoft.com/en-us/library/office/ff839952(v=office.15).aspx
        LONG wdFormatPDF = new LONG(17); // PDF format.
        LONG wdFormatRTF = new LONG(6); // Rich text format (RTF).
        LONG wdFormatHTML = new LONG(8); // Standard HTML format.
        LONG wdFormatDocument = new LONG(0); // Microsoft Office Word 97 - 2003 binary file format.
        LONG wdFormatDocumentDefault = new LONG(16); // Word default document file format. For Word 2010, this is the DOCX format.
        
        // http://msdn.microsoft.com/en-us/library/office/ff838709(v=office.15).aspx
        LONG wdOriginalDocumentFormat = new LONG(1); // Original document format.
        LONG wdPromptUser = new LONG(2); // Prompt user to select a document format.
        LONG wdWordDocument = new LONG(0); // Microsoft Word document format.        
        
        try {
            msWord = new MSWord();
            System.out.println("MSWord version: " + msWord.getVersion());

            msWord.setVisible(true);
            
            Helper.sleep(5);
            
            demoDocument = Helper.createNotExistingFile("jnatest", ".doc");
            Helper.extractClasspathFileToReal("/com/sun/jna/platform/win32/COM/util/office/resources/jnatest.doc", demoDocument);
            
            msWord.openDocument(demoDocument.getAbsolutePath());
            msWord.insertText("Hello from JNA! \n\n");
            // wait 10sec. before closing
            Helper.sleep(10);
            // save in different formats
            // pdf format is only supported in MSWord 2007 and above
            System.out.println("Wrinting files to: " + Helper.tempDir);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestSaveAs.doc").getAbsolutePath(), wdFormatDocument);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestSaveAs.pdf").getAbsolutePath(), wdFormatPDF);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestSaveAs.rtf").getAbsolutePath(), wdFormatRTF);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestSaveAs.html").getAbsolutePath(), wdFormatHTML);
            // close and save the document
            msWord.closeActiveDocument(false);
            msWord.newDocument();
            // msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
            msWord.insertText("Hello from JNA! \n Please notice that JNA can control MS Word via the new COM interface! \nHere we are creating a new word document and we save it to the 'TEMP' directory!");
            // save with no user prompt
            msWord.SaveAs(new File(Helper.tempDir, "jnatestNewDoc1.docx").getAbsolutePath(), wdFormatDocumentDefault);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestNewDoc2.docx").getAbsolutePath(), wdFormatDocumentDefault);
            msWord.SaveAs(new File(Helper.tempDir, "jnatestNewDoc3.docx").getAbsolutePath(), wdFormatDocumentDefault);
            // close and save the document
            msWord.closeActiveDocument(false);
            // open 3 documents
            msWord.openDocument(new File(Helper.tempDir, "jnatestNewDoc1.docx").getAbsolutePath());
            msWord.insertText("Hello some changes from JNA!\n");            
            msWord.openDocument(new File(Helper.tempDir, "jnatestNewDoc2.docx").getAbsolutePath());
            msWord.insertText("Hello some changes from JNA!\n");            
            msWord.openDocument(new File(Helper.tempDir, "jnatestNewDoc3.docx").getAbsolutePath());
            msWord.insertText("Hello some changes from JNA!\n");            
            // save the document and prompt the user
            msWord.Save(false, wdPromptUser);
        } catch (COMException e) {
            if (e.getExcepInfo() != null) {
                System.out.println("bstrSource: " + e.getExcepInfo().bstrSource);
                System.out.println("bstrDescription: " + e.getExcepInfo().bstrDescription);
            }
        } finally {
            if (msWord != null) {
                msWord.quit();
            }
            
            if(demoDocument != null && demoDocument.exists()) {
                demoDocument.delete();
            }
        }
    }

    public void testMSExcel() throws IOException {
        File demoDocument = null;
        MSExcel msExcel = null;

        try {
            msExcel = new MSExcel();
            System.out.println("MSExcel version: " + msExcel.getVersion());
            msExcel.setVisible(true);

            Helper.sleep(5);
            
            demoDocument = Helper.createNotExistingFile("jnatest", ".xls");
            Helper.extractClasspathFileToReal("/com/sun/jna/platform/win32/COM/util/office/resources/jnatest.xls", demoDocument);
            
            msExcel.openExcelBook(demoDocument.getAbsolutePath());
            msExcel.insertValue("A1", "Hello from JNA!");
            // wait 10sec. before closing
            Helper.sleep(10);
            // close and save the active sheet
            msExcel.closeActiveWorkbook(true);

            msExcel.newExcelBook();
            msExcel.insertValue("A1", "Hello from JNA!");
            // close and save the active sheet
            msExcel.closeActiveWorkbook(true);
        } finally {
            if (msExcel != null) {
                msExcel.quit();
            }
            
            if (demoDocument != null && demoDocument.exists()) {
                demoDocument.delete();
            }
        }
    }
}
