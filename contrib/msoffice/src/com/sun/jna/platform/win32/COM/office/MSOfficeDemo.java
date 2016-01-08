package com.sun.jna.platform.win32.COM.office;

import java.io.File;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.WinDef.LONG;

public class MSOfficeDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new MSOfficeDemo();
    }

    private String currentWorkingDir = new File("").getAbsolutePath()
            + File.separator;

    public MSOfficeDemo() {
        //this.testMSWord();
         this.testMSExcel();
    }

    public void testMSWord() {
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
            // msWord.newDocument();
            msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
            msWord.insertText("Hello from JNA! \n\n");
            // wait 10sec. before closing
            Thread.currentThread().sleep(1000);
            // save in different formats
            // pdf format is only supported in MSWord 2007 and above
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.doc", wdFormatDocument);
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.pdf", wdFormatPDF);
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.rtf", wdFormatRTF);
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.html", wdFormatHTML);
            // close and save the document
            msWord.closeActiveDocument(false);
            msWord.newDocument();
            // msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
            msWord.insertText("Hello from JNA! \n Please notice that JNA can control MS Word via the new COM interface! \nHere we are creating a new word document and we save it to the 'TEMP' directory!");
            // save with no user prompt
            msWord.SaveAs("C:\\TEMP\\jnatestNewDoc1.docx", wdFormatDocumentDefault);
            msWord.SaveAs("C:\\TEMP\\jnatestNewDoc2.docx", wdFormatDocumentDefault);
            msWord.SaveAs("C:\\TEMP\\jnatestNewDoc3.docx", wdFormatDocumentDefault);
            // close and save the document
            msWord.closeActiveDocument(false);
            // open 3 documents
            msWord.openDocument("C:\\TEMP\\jnatestNewDoc1.docx", true);
            msWord.insertText("Hello some changes from JNA!\n");            
            msWord.openDocument("C:\\TEMP\\jnatestNewDoc2.docx", true);
            msWord.insertText("Hello some changes from JNA!\n");            
            msWord.openDocument("C:\\TEMP\\jnatestNewDoc3.docx", true);
            msWord.insertText("Hello some changes from JNA!\n");            
            // save the document and prompt the user
            msWord.Save(false, wdPromptUser);
            // wait then close word
            msWord.quit();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        } catch (COMException e) {
            if (e.getExcepInfo() != null) {
                System.out
                        .println("bstrSource: " + e.getExcepInfo().bstrSource);
                System.out.println("bstrDescription: "
                        + e.getExcepInfo().bstrDescription);
            }

            // print stack trace
            e.printStackTrace();

            if (msWord != null)
                msWord.quit();
        }
    }

    public void testMSExcel() {
        MSExcel msExcel = null;

        try {
            msExcel = new MSExcel();
            System.out.println("MSExcel version: " + msExcel.getVersion());
            msExcel.setVisible(true);
            // msExcel.newExcelBook();
            msExcel.openExcelBook(currentWorkingDir + "jnatest.xls", true);
            msExcel.insertValue("A1", "Hello from JNA!");
            // wait 10sec. before closing
            Thread.currentThread().sleep(10000);
            // close and save the active sheet
            msExcel.closeActiveWorkbook(true);
            msExcel.setVisible(true);
            // msExcel.newExcelBook();
            msExcel.openExcelBook(currentWorkingDir + "jnatest.xls", true);
            msExcel.insertValue("A1", "Hello from JNA!");
            // close and save the active sheet
            msExcel.closeActiveWorkbook(true);
        } catch (Exception e) {
            e.printStackTrace();

            if (msExcel != null)
                msExcel.quit();
        }
    }
}
