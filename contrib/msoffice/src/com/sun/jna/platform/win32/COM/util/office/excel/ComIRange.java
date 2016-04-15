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
