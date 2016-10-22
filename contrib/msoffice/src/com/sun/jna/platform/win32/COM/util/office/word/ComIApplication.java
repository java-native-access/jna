/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
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
