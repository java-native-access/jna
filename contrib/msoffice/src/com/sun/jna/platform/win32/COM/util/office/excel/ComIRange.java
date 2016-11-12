/*
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

package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface(iid = "{00020846-0000-0000-C000-000000000046}")
public interface ComIRange {

	@ComProperty
	ComIApplication getApplication();

	@ComProperty
	String getText();

	@ComMethod
	void Select();

	@ComProperty
	void setValue(String value);

	@ComMethod
	void Activate();
        
	@ComProperty
        ComIRange getItem(Object rowIndex, Object columnIndex);
        
        @ComProperty
        void setValue(Object data);
        
        @ComProperty
        Object getValue();
        
        @ComProperty
        void setFormula(String data);
        
        @ComProperty
        String getFormula();
        
        @ComProperty
        void setNumberFormat(String data);
        
        @ComProperty
        String getNumberFormat();
        
        @ComProperty
        ComIRange getEntireColumn();
        
        @ComMethod
        void AutoFit();
        
        @ComProperty
        public ComIRange getResize(Object rowSize, Object columnSize);
        
        @ComProperty
        void setOrientation(int degree);
        
        @ComProperty
        int getOrientation();
        
        @ComProperty
        void setWrapText(boolean wrap);
        
        @ComProperty
        boolean getWrapText();
        
        @ComProperty
        Interior getInterior();
        
        @ComProperty
        Borders getBorders();
        
        @ComProperty
        int getTop();
        
        @ComProperty
        void setTop(int value);
        
        @ComProperty
        int getLeft();
        
        @ComProperty
        void setLeft(int value);
        
        @ComProperty
        String getName();
        
        @ComProperty
        void setName(String name);
        
        @ComProperty
        void setAddress(String name);
}
