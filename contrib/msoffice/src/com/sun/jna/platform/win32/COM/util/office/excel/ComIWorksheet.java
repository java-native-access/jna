package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid="{000208D8-0000-0000-C000-000000000046}")
public interface ComIWorksheet {
        @ComProperty
        ComIWorkbook getParent();
    
	@ComProperty
	String getName();
	
	@ComProperty
	ComIRange getRange(String cell1);
        
        @ComProperty
        ComIRange getRange(String cell1, String cell2);

	@ComProperty
	ComIApplication getApplication();

        @ComProperty
        ComIRange getCells();
        
        @ComProperty
        Shapes getShapes();
        
	@ComProperty
	ComIRange getRows(Object identifier);
        
	@ComProperty
	ComIRange getColumns(Object identifier);
}
