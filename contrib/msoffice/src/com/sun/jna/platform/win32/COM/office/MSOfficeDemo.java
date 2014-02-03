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
        this.testMSWord();
        // this.testMSExcel();
    }

    public void testMSWord() {
        MSWord msWord = null;
        LONG wdFormatPDF = new LONG(17); // PDF format.
        LONG wdFormatRTF = new LONG(6); // Rich text format (RTF).
        LONG wdFormatHTML = new LONG(8); // Standard HTML format.

        try {
            msWord = new MSWord();
            System.out.println("MSWord version: " + msWord.getVersion());

            msWord.setVisible(true);
            // msWord.newDocument();
            msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
            msWord.insertText("Hello from JNA!");
            // wait 10sec. before closing
            Thread.currentThread().sleep(10000);
            // save in different formats
            // pdf format is only supported in MSWord 2007 and above
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.pdf", wdFormatPDF);
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.rtf", wdFormatRTF);
            msWord.SaveAs("C:\\TEMP\\jnatestSaveAs.html", wdFormatHTML);
            // close and save the document
            msWord.closeActiveDocument(true);
            msWord.setVisible(true);
            msWord.newDocument();
            // msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
            msWord.insertText("Hello from JNA!");
            // close and save the document
            msWord.closeActiveDocument(true);
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
