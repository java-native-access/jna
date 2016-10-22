/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
 *
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
package com.sun.jna.platform.win32.COM.util.office;

import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.COM.Helper;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.office.word.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.word.ComWord_Application;
import com.sun.jna.platform.win32.COM.util.office.word.WdOriginalFormat;
import com.sun.jna.platform.win32.COM.util.office.word.WdSaveFormat;
import java.io.IOException;

public class MSOfficeWordDemo {
        private static final String currentWorkingDir = new File("").getAbsolutePath() + File.separator;

	public static void main(String[] args) throws IOException {
            Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
            try {
                testMSWord();
            } finally {
                Ole32.INSTANCE.CoUninitialize();
            }
	}

	public static void testMSWord() throws IOException {
                File demoDocument = null;
		ComIApplication msWord = null;
		Factory factory = new Factory();

		try {
			System.out.println("Files in temp dir: " + Helper.tempDir.getAbsolutePath());

			ComWord_Application msWordObject = factory.createObject(ComWord_Application.class);
			msWord = msWordObject.queryInterface(ComIApplication.class);

			System.out.println("MSWord version: " + msWord.getVersion());

			msWord.setVisible(true);
                        
                        demoDocument = Helper.createNotExistingFile("jnatest", ".doc");
                        Helper.extractClasspathFileToReal("/com/sun/jna/platform/win32/COM/util/office/resources/jnatest.doc", demoDocument);
                        
			msWord.getDocuments().Open(demoDocument.getAbsolutePath());
                        
                        Helper.sleep(5);
                        
			msWord.getSelection().TypeText("Hello from JNA! \n\n");
			// wait 10sec. before closing
			Helper.sleep(10);
			// save in different formats
			// pdf format is only supported in MSWord 2007 and above
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestSaveAs.doc").getAbsolutePath(), WdSaveFormat.wdFormatDocument);
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestSaveAs.pdf").getAbsolutePath(), WdSaveFormat.wdFormatPDF);
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestSaveAs.rtf").getAbsolutePath(), WdSaveFormat.wdFormatRTF);
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestSaveAs.html").getAbsolutePath(), WdSaveFormat.wdFormatHTML);
			// close and don't save the changes
			msWord.getActiveDocument().Close(false);
                        
                        // Create a new document
			msWord.getDocuments().Add();
			// msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
                        msWord.getSelection().TypeText(
                            "Hello from JNA! \n Please notice that JNA can control "
                            + "MS Word via the new COM interface! \nHere we are "
                            + "creating a new word document and we save it "
                            + "to the 'TEMP' directory!");
                        // save with no user prompt
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestNewDoc1.docx").getAbsolutePath(), WdSaveFormat.wdFormatDocumentDefault);
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestNewDoc2.docx").getAbsolutePath(), WdSaveFormat.wdFormatDocumentDefault);
			msWord.getActiveDocument().SaveAs(new File(Helper.tempDir, "jnatestNewDoc3.docx").getAbsolutePath(), WdSaveFormat.wdFormatDocumentDefault);
			// close and don't save the changes
			msWord.getActiveDocument().Close(false);
                        
			// open 3 documents
			msWord.getDocuments().Open(new File(Helper.tempDir, "jnatestNewDoc1.docx").getAbsolutePath());
			msWord.getSelection().TypeText("Hello some changes from JNA!\n");
			msWord.getDocuments().Open(new File(Helper.tempDir, "jnatestNewDoc2.docx").getAbsolutePath());
			msWord.getSelection().TypeText("Hello some changes from JNA!\n");
			msWord.getDocuments().Open(new File(Helper.tempDir, "jnatestNewDoc3.docx").getAbsolutePath());
			msWord.getSelection().TypeText("Hello some changes from JNA!\n");
			// save the document and prompt the user
			msWord.getDocuments().Save(false, WdOriginalFormat.wdPromptUser);
		} finally {
                        // Make sure the word instance is shut down
			if (msWord != null) {
				msWord.Quit();
			}
                        
                        // Release all objects acquired by the factory
                        factory.disposeAll();
                        
                        if (demoDocument != null && demoDocument.exists()) {
                            demoDocument.delete();
                        }
		}
	}
}
