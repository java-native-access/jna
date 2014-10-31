package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

@ComInterface
public interface ComISelection {

	@ComMethod
	void TypeText(String text);
	
}
