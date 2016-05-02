package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid="{000208D5-0000-0000-C000-000000000046}")
public interface ComIApplication extends IUnknown, IConnectionPoint {

	@ComProperty
	String getVersion();
	
	@ComProperty
	boolean getVisible();

	@ComProperty
	void setVisible(boolean value);
	
	@ComProperty
	ComIWorkbooks getWorkbooks();
	
	@ComProperty
	ComIWorksheet getActiveSheet();

	@ComProperty
	ComIWorkbook getActiveWorkbook();

	@ComMethod
	void Quit();
        
        @ComProperty
        boolean getUserControl();
        
        @ComProperty
        void setUserControl(boolean value);
}
