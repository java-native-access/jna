
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.Variant.VARIANT;

/**
 * <p>uuid({00020957-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020957-0000-0000-C000-000000000046}")
public interface Paragraph {
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Range", dispId = 0x0)
    Range getRange();

    /**
     * <p>id(0x44e)</p>
     */
    @ComProperty(name = "Format", dispId = 0x44e)
    ParagraphFormat getFormat();
            
    /**
     * <p>id(0x44e)</p>
     */
    @ComProperty(name = "Format", dispId = 0x44e)
    void setFormat(ParagraphFormat param0);
}