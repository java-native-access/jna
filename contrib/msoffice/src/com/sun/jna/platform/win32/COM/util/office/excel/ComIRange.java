package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid = "{00020846-0000-0000-C000-000000000046}")
public interface ComIRange {

	@ComProperty
	ComIApplication getApplication();

	@ComProperty
	String getText();

	@ComMethod
	void Select();

	@ComProperty
	void setValue(String value);

	@ComMethod
	void Activate();

}
