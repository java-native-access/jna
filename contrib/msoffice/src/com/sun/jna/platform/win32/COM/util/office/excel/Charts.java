package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface
public interface Charts {

	@ComMethod
	Chart Add(Object before, Object after, Object count, Object type);

        @ComProperty
        int getCount();
        
        @ComProperty
        int getItem(Object item);
}
