package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface
public interface Series {
    @ComProperty
    void setXValues(Object values);
    
    @ComProperty
    String getName();
    
    @ComProperty
    void setName(String name);
}
