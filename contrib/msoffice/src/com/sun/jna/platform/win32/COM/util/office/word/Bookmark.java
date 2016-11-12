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
 * <p>uuid({00020968-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020968-0000-0000-C000-000000000046}")
public interface Bookmark {
    /**
     * <p>id(0x0)</p>
     */
    @ComProperty(name = "Name", dispId = 0x0)
    String getName();
            
    /**
     * <p>id(0x1)</p>
     */
    @ComProperty(name = "Range", dispId = 0x1)
    Range getRange();
            
    /**
     * <p>id(0x2)</p>
     */
    @ComProperty(name = "Empty", dispId = 0x2)
    Boolean getEmpty();
            
    /**
     * <p>id(0x3)</p>
     */
    @ComProperty(name = "Start", dispId = 0x3)
    Integer getStart();
            
    /**
     * <p>id(0x3)</p>
     */
    @ComProperty(name = "Start", dispId = 0x3)
    void setStart(Integer param0);
            
    /**
     * <p>id(0x4)</p>
     */
    @ComProperty(name = "End", dispId = 0x4)
    Integer getEnd();
            
    /**
     * <p>id(0x4)</p>
     */
    @ComProperty(name = "End", dispId = 0x4)
    void setEnd(Integer param0);
            
    /**
     * <p>id(0x5)</p>
     */
    @ComProperty(name = "Column", dispId = 0x5)
    Boolean getColumn();

    /**
     * <p>id(0x3e9)</p>
     */
    @ComProperty(name = "Creator", dispId = 0x3e9)
    Integer getCreator();
            
    /**
     * <p>id(0x3ea)</p>
     */
    @ComProperty(name = "Parent", dispId = 0x3ea)
    com.sun.jna.platform.win32.COM.util.IDispatch getParent();
            
    /**
     * <p>id(0xffff)</p>
     */
    @ComMethod(name = "Select", dispId = 0xffff)
    void Select();
            
    /**
     * <p>id(0xb)</p>
     */
    @ComMethod(name = "Delete", dispId = 0xb)
    void Delete();
            
    /**
     * <p>id(0xc)</p>
     */
    @ComMethod(name = "Copy", dispId = 0xc)
    Bookmark Copy(String Name);
            
    
}