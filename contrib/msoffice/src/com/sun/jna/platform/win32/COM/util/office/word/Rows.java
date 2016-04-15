
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

/**
 * <p>uuid({0002094C-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{0002094C-0000-0000-C000-000000000046}")
public interface Rows {
    /**
     * <p>id(0x0)</p>
     */
    @ComMethod(name = "Item", dispId = 0x0)
    Row Item(Integer Index);
            
    /**
     * <p>id(0x64)</p>
     */
    @ComMethod(name = "Add", dispId = 0x64)
    Row Add(Object BeforeRow);
         
    /**
     * <p>id(0xc8)</p>
     */
    @ComMethod(name = "Delete", dispId = 0xc8)
    void Delete();

}