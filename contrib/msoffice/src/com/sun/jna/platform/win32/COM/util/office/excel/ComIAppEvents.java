package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;

@ComInterface(iid="{00024413-0000-0000-C000-000000000046}")
public interface ComIAppEvents {
	
	  @ComEventCallback(dispid=1558)
	  public void SheetSelectionChange(ComIWorksheet sheet, ComIRange target);

}
