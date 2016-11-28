/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */

package com.sun.jna.platform.win32.COM.util.office.word;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.COM.Helper;
import com.sun.jna.platform.win32.COM.util.Factory;

/**
 * @author joseluisll[at]gmail[dot]com
 * 
 * Testcase to test additions to the MS Word COM classes. 
 */
public class TestWordClasses{
    private static final String currentWorkingDir = new File("").getAbsolutePath() + File.separator;

    File demoDocument = null;
	ComIApplication msWord = null;
	Factory factory = null;
	ComWord_Application msWordObject = null;

	private ComWord_Application msWordObject2;

	private ComIApplication msWord2;

	@Before
	public void setUP() throws Exception {
		Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
		
		factory=new Factory();
		msWordObject=factory.createObject(ComWord_Application.class);
		msWord = msWordObject.queryInterface(ComIApplication.class);
        demoDocument = Helper.createNotExistingFile("jnatest", ".doc");
        Helper.extractClasspathFileToReal("/com/sun/jna/platform/win32/COM/util/office/resources/jnatest.doc", demoDocument);
       
        msWordObject2=factory.createObject(ComWord_Application.class);
		msWord2 = msWordObject2.queryInterface(ComIApplication.class);

	}
	
	@After
	public void cleanUP() throws Exception {
		
		if (msWord != null) {
			msWord.Quit();
			msWord2.Quit();
		}
                    
        // Release all objects acquired by the factory
        factory.disposeAll();
        
        if (demoDocument != null && demoDocument.exists()) {
            demoDocument.delete();
        }
		
		Ole32.INSTANCE.CoUninitialize();
	}
	
	@Test
	public void testDocumentHandling() throws Throwable{
		//TEST STEP 1: Create documents, save as PDF, RTF, HTML, DOC. Open 3 documents
		testWordDocuments();
		
		//TEST STEP 2: Test read the Count Property of the Word.Documents object. It should be 3
		testLongProperties();
		
		//TEST STEP 3: Test the Normal Template property of the Word.Application
		testNormalTemplate();
		
	}
	@Test
	public void testActivate() throws Exception {
		msWord2.setCaption("MSWORD2");
		Helper.sleep(5);
		msWord2.setVisible(true);
		msWord.setCaption("MSWORD1");
		msWord.setVisible(true);
		msWord.Activate();
		Helper.sleep(5);
		msWord2.Activate();
		Helper.sleep(5);
		msWord.setVisible(false);
		Helper.sleep(5);
		msWord2.setVisible(false);
	}
	
	@Test
	public void testDifferentApplications() throws Throwable {
		//The two word applications must be different Processes
		Assert.assertTrue(!msWord.equals(msWord2));
		System.out.println("There are two different word application instances running.");
	}
	
	
	private void testNormalTemplate() throws Exception {
		ComITemplate normal = msWord.getNormalTemplate();
		System.out.println("Word Aplication Name of Normal Template:"+normal.getName());
	}
	
	@Test
	public void testCaptionProperty() throws Exception{
		String caption="JNA - MSWORD - CAPTIONTEST";
		msWord.setVisible(true);
		msWord.setCaption(caption);
		Helper.sleep(5);
		Assert.assertTrue(caption.equals(msWord.getCaption()));
		System.out.println("Tested the caption:" +caption);
	}
	
	@Test
	public void testWindows() throws Exception {
		msWord.setVisible(true);
		ComIDocument doc1 = msWord.getDocuments().Add();
		ComIDocument doc2 = msWord.getDocuments().Add();
		ComIWindows windows = msWord.getWindows();
		long l=windows.getCount();
		Assert.assertTrue(l==2L);
		System.out.println("Number of opened document windows (should be 2):"+l);
		doc1.Close(false);
		doc2.Close(false);
		msWord.setVisible(false);
	}
	
	@Test
	public void testNoWindows() throws Exception {
		msWord.setVisible(true);
		ComIWindows windows = msWord.getWindows();
		long l=windows.getCount();
		System.out.println("Number of opened document windows (should be 0):"+l);
		msWord.setVisible(false);
	}
	
	@Test 
	public void testActiveWindow() throws Exception {
		msWord.setVisible(true);
		ComIDocument doc1=msWord.getDocuments().Add();
		ComIWindow window = msWord.getActiveWindow();
		window.setFocus();
		doc1.Close(false);
		msWord.setVisible(false);
	}

	
	private void testWordDocuments() throws Exception {
		

		System.out.println("Files in temp dir: " + Helper.tempDir.getAbsolutePath());

		System.out.println("MSWord version: " + msWord.getVersion());

		msWord.setVisible(true);            
               
		msWord.getDocuments().Open(demoDocument.getAbsolutePath());
                    
        Helper.sleep(1);
                    
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
		
	}
	
    
	
	private void testLongProperties() throws Exception {
		long l=msWord.getDocuments().Count();
		Assert.assertTrue(l==3L);
		System.out.println("Number of documents:"+l);
		
	}
   
}