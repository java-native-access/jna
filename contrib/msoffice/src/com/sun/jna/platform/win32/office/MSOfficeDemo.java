package com.sun.jna.platform.win32.office;

import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.COM.COMException;

public class MSOfficeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MSOfficeDemo();
	}

	public MSOfficeDemo() {
		MSWord msWord = null;
		try {
			msWord = new MSWord();
//			System.out.println("MSWord version: " + msWord.getVersion());
			msWord.setVisible(Variant.VARIANT_TRUE);
			// msWord.newDocument(Variant.VARIANT_TRUE);
			// msWord.OpenDocument("jnatest.doc", true);
		} catch (COMException e) {
			e.printStackTrace();
		} finally {
			if (msWord != null)
				try {
					msWord.quit();
				} catch (COMException e) {
					e.printStackTrace();
				}
		}
	}
}
