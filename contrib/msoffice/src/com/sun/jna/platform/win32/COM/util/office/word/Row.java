
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

/**
 * <p>uuid({00020950-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020950-0000-0000-C000-000000000046}")
public interface Row {
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Range", dispId = 0x0)
    Range getRange();
    
}