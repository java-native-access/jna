package com.sun.jna.platform.win32.office;

import java.io.File;

import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.COM.AutomationException;

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
		MSWord msWord = null;

		try {
			msWord = new MSWord();
			// System.out.println("MSWord version: " + msWord.getVersion());
			msWord.setVisible(Variant.VARIANT_TRUE);
			msWord.newDocument();
			Thread.currentThread().sleep(2000);
			//msWord.insertText("Hello from JNA!");
			// msWord.openDocument(currentWorkingDir + "jnatest.doc", true);
			Thread.currentThread().sleep(2000);
			// close and save the document
			msWord.closeActiveDocument(Variant.VARIANT_FALSE);
			Thread.currentThread().sleep(2000);
			// wait then close word
			msWord.quit();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (AutomationException e) {
			if (e.getExcepInfo() != null) {
				System.out
						.println("bstrSource: " + e.getExcepInfo().bstrSource);
				System.out.println("bstrDescription: "
						+ e.getExcepInfo().bstrDescription);
			} else
				e.printStackTrace();
			
			if(msWord != null)
				msWord.quit();
		}
	}
}
