/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.sun.jna.ptr.IntByReference;

/**
 * Winspool Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class WinspoolUtil {
	
	public static PRINTER_INFO_1[] getPrinterInfo1() {
    	IntByReference pcbNeeded = new IntByReference();
    	IntByReference pcReturned = new IntByReference();
    	Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, 
    			null, 1, null, 0, pcbNeeded, pcReturned);
    	if (pcbNeeded.getValue() <= 0) {
    		return new PRINTER_INFO_1[0];
    	}
    	
    	PRINTER_INFO_1 pPrinterEnum = new PRINTER_INFO_1(pcbNeeded.getValue());
    	if(! Winspool.INSTANCE.EnumPrinters(Winspool.PRINTER_ENUM_LOCAL, 
    			null, 1, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
    		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    	}
    	
    	return (PRINTER_INFO_1[]) pPrinterEnum.toArray(pcReturned.getValue());
	}
}