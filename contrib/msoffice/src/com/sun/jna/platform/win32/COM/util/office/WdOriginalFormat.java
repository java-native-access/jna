package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum WdOriginalFormat implements IComEnum {
	wdOriginalDocumentFormat(1),  // Original document format.
	wdPromptUser(2),              // Prompt user to select a document format.
	wdWordDocument(0);            // Microsoft Word document format.
	
	 private WdOriginalFormat(long value) {
		 this.value = value;
	 }
	 private long value;
	 public long getValue() {
		 return this.value;
	 }
}
