
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;

/**
 * <p>uuid({000209A9-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{000209A9-0000-0000-C000-000000000046}")
public interface InlineShapes {
    /**
     * <p>id(0x18)</p>
     */
    @ComMethod(name = "AddOLEObject", dispId = 0x18)
    InlineShape AddOLEObject(Object ClassType,
            Object FileName,
            Object LinkToFile,
            Object DisplayAsIcon,
            Object IconFileName,
            Object IconIndex,
            Object IconLabel,
            Object Range);
            
}