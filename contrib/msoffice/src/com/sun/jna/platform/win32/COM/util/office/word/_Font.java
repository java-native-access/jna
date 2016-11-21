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
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.Variant.VARIANT;

/**
 * <p>uuid({00020952-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020952-0000-0000-C000-000000000046}")
public interface _Font {
    /**
     * <p>id(0x82)</p>
     */
    @ComProperty(name = "Bold", dispId = 0x82)
    Integer getBold();
            
    /**
     * <p>id(0x82)</p>
     */
    @ComProperty(name = "Bold", dispId = 0x82)
    void setBold(Integer param0);
            
    /**
     * <p>id(0x83)</p>
     */
    @ComProperty(name = "Italic", dispId = 0x83)
    Integer getItalic();
            
    /**
     * <p>id(0x83)</p>
     */
    @ComProperty(name = "Italic", dispId = 0x83)
    void setItalic(Integer param0);

}