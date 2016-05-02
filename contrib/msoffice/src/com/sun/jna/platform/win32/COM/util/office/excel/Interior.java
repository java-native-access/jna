
package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

@ComInterface
public interface Interior {
    @ComProperty
    int getColorIndex();
    
    @ComProperty
    void setColorIndex(int value);
}
