
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

/**
 * <p>uuid({00020953-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020953-0000-0000-C000-000000000046}")
public interface _ParagraphFormat {
    /**
     * <p>id(0x70)</p>
     */
    @ComProperty(name = "SpaceAfter", dispId = 0x70)
    void setSpaceAfter(Float param0);
}