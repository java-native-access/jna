package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

public interface ComIWindow {
	
	
	@ComMethod(name="SetFocus",dispId=0x6d)
	void setFocus();
	
	@ComProperty
	ComIApplication getApplication();

}
