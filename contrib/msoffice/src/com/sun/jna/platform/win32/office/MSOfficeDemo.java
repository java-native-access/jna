package com.sun.jna.platform.win32.office;

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
			msWord.setVisible(true);
		} catch (COMException e) {
			e.printStackTrace();
		}finally {
			if(msWord != null)
				try {
					msWord.quit();
				} catch (COMException e) {
					e.printStackTrace();
				}
		}
	}
}
