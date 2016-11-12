/*
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

/**
 * <p>uuid({000209A8-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{000209A8-0000-0000-C000-000000000046}")
public interface InlineShape {
    /**
     * <p>id(0x2)</p>
     */
    @ComProperty(name = "Range", dispId = 0x2)
    Range getRange();

    /**
     * <p>id(0x5)</p>
     */
    @ComProperty(name = "OLEFormat", dispId = 0x5)
    OLEFormat getOLEFormat();

    /**
     * <p>id(0x8)</p>
     */
    @ComProperty(name = "Height", dispId = 0x8)
    Float getHeight();
            
    /**
     * <p>id(0x8)</p>
     */
    @ComProperty(name = "Height", dispId = 0x8)
    void setHeight(Float param0);
            
    /**
     * <p>id(0x9)</p>
     */
    @ComProperty(name = "Width", dispId = 0x9)
    Float getWidth();
            
    /**
     * <p>id(0x9)</p>
     */
    @ComProperty(name = "Width", dispId = 0x9)
    void setWidth(Float param0);
}