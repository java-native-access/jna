
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.Variant.VARIANT;

/**
 * <p>uuid({00020951-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020951-0000-0000-C000-000000000046}")
public interface Table {
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Range", dispId = 0x0)
    Range getRange();
 
    /**
     * <p>id(0x64)</p>
     */
    @ComProperty(name = "Columns", dispId = 0x64)
    Columns getColumns();
            
    /**
     * <p>id(0x65)</p>
     */
    @ComProperty(name = "Rows", dispId = 0x65)
    Rows getRows();
            
    /**
     * <p>id(0x11)</p>
     */
    @ComMethod(name = "Cell", dispId = 0x11)
    Cell Cell(Integer Row,
            Integer Column);
    
}