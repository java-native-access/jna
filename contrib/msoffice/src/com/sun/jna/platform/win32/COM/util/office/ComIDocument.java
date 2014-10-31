package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

public interface ComIDocument {

	@ComMethod
	void SaveAs(String string, WdSaveFormat wdFormatDocument);

	@ComMethod
	void Close(boolean saveChanges);

}
