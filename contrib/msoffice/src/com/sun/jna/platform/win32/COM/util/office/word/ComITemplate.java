package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

public interface ComITemplate {

	@ComProperty(name = "Name", dispId = 0x0)
	String getName();
	
	@ComProperty(name = "Application",dispId=0x3e8)
	ComIApplication getApplication();
	
	@ComProperty
	void setSaved(boolean bSaved);

	@ComProperty
	boolean getSaved();
}
