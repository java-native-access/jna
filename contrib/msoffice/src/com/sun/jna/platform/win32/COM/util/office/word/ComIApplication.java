/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
package com.sun.jna.platform.win32.COM.util.office.word;

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
	
        /**
         * <p>
         * id(0x172)</p>
         */
        @ComMethod(name = "InchesToPoints", dispId = 0x172)
        Float InchesToPoints(Float Inches);
}
