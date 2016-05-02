
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.Variant.VARIANT;

/**
 * <p>uuid({0002094D-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{0002094D-0000-0000-C000-000000000046}")
public interface Tables {
    /**
     * <p>id(0x2)</p>
     */
    @ComProperty(name = "Count", dispId = 0x2)
    Integer getCount();

    /**
     * <p>id(0x0)</p>
     */
    @ComMethod(name = "Item", dispId = 0x0)
    Table Item(Integer Index);
 
    /**
     * <p>id(0xc8)</p>
     */
    @ComMethod(name = "Add", dispId = 0xc8)
    Table Add(Range Range,
            Integer NumRows,
            Integer NumColumns,
            Object DefaultTableBehavior,
            Object AutoFitBehavior);

}