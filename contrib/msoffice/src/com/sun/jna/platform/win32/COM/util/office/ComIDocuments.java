package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

public interface ComIDocuments {

	@ComMethod
	ComIDocument Open(String fileName);

	@ComMethod
	ComIDocument Add();

	@ComMethod
	void Save(boolean noPrompt, WdOriginalFormat originalFormat);
	
}
