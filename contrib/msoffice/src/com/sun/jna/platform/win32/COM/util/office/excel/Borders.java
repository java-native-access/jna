
package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface
public interface Borders {
    @ComProperty
    XlBorderWeight getWeight();
    
    @ComProperty
    void setWeight(XlBorderWeight weight);
    
    @ComProperty
    XlLineStyle getLineStyle();
    
    @ComProperty
    void setLineStyle(XlLineStyle weight);
}
