package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid = "{000208DB-0000-0000-C000-000000000046}")
public interface ComIWorkbooks {

	@ComProperty
	long getCount();

	@ComMethod
	ComIWorkbook Item(long index);

	@ComMethod
	ComIWorkbook Open(Object FileName);

        @ComMethod
        ComIWorkbook Add();
}
