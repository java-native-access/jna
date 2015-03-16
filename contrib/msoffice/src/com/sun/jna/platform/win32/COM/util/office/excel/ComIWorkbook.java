package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

@ComInterface(iid="{0002096B-0000-0000-C000-000000000046}")
public interface ComIWorkbook {

	@ComMethod
	void Close(boolean saveChanges);


}
