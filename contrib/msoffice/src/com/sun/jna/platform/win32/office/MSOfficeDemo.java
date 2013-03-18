package com.sun.jna.platform.win32.office;

import java.io.File;

import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.COM.COMException;

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

		try {
			msWord = new MSWord();
			System.out.println("MSWord version: " + msWord.getVersion());
			msWord.setVisible(Variant.VARIANT_TRUE);
			msWord.newDocument();
			//msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
			msWord.insertText("Hello from JNA!");
			// wait 10sec. before closing
			Thread.currentThread().sleep(10000);
			// close and save the document
			msWord.closeActiveDocument(Variant.VARIANT_TRUE);
			// wait then close word
			msWord.quit();
		} catch (COMException e) {
			if (e.getExcepInfo() != null) {
				System.out
						.println("bstrSource: " + e.getExcepInfo().bstrSource);
				System.out.println("bstrDescription: "
						+ e.getExcepInfo().bstrDescription);
			} else
				e.printStackTrace();
			
			if(msWord != null)
				msWord.quit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testMSExcel() {
		MSExcel msExcel = null;

		try {
			msExcel = new MSExcel();
			System.out.println("MSExcel version: " + msExcel.getVersion());
			msExcel.setVisible(Variant.VARIANT_TRUE);
			//msExcel.newExcelBook();
			msExcel.openExcelBook(currentWorkingDir + "jnatest.xls", true);
			msExcel.insertValue("A1", "Hello from JNA!");
			// wait 10sec. before closing
			Thread.currentThread().sleep(10000);
			// close and save the active sheet
			msExcel.closeActiveWorkbook(Variant.VARIANT_TRUE);
			// wait then close excel
			msExcel.quit();
		} catch (COMException e) {
			if (e.getExcepInfo() != null) {
				System.out
						.println("bstrSource: " + e.getExcepInfo().bstrSource);
				System.out.println("bstrDescription: "
						+ e.getExcepInfo().bstrDescription);
			} else
				e.printStackTrace();
			
			if(msExcel != null)
				msExcel.quit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
