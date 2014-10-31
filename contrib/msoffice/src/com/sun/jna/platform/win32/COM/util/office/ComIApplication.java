package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid="{00020970-0000-0000-C000-000000000046}")
public interface ComIApplication {

	@ComProperty
	String getVersion();

	@ComProperty
	boolean getVisible();
	
	@ComProperty
	void setVisible(boolean value);

	@ComProperty
	ComIDocuments getDocuments();

	@ComProperty
	ComISelection getSelection();
	
	@ComProperty
	ComIDocument getActiveDocument();

	@ComMethod
	void Quit();
	
}
