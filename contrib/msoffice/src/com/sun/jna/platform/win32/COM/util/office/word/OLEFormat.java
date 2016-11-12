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
 * <p>uuid({00020933-0000-0000-C000-000000000046})</p>
 */
@ComInterface(iid="{00020933-0000-0000-C000-000000000046}")
public interface OLEFormat {
    /**
     * <p>id(0x3e8)</p>
     */
    @ComProperty(name = "Application", dispId = 0x3e8)
    ComIApplication getApplication();
            
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
     * <p>id(0x2)</p>
     */
    @ComProperty(name = "ClassType", dispId = 0x2)
    String getClassType();
            
    /**
     * <p>id(0x2)</p>
     */
    @ComProperty(name = "ClassType", dispId = 0x2)
    void setClassType(String param0);
            
    /**
     * <p>id(0x3)</p>
     */
    @ComProperty(name = "DisplayAsIcon", dispId = 0x3)
    Boolean getDisplayAsIcon();
            
    /**
     * <p>id(0x3)</p>
     */
    @ComProperty(name = "DisplayAsIcon", dispId = 0x3)
    void setDisplayAsIcon(Boolean param0);
            
    /**
     * <p>id(0x7)</p>
     */
    @ComProperty(name = "IconName", dispId = 0x7)
    String getIconName();
            
    /**
     * <p>id(0x7)</p>
     */
    @ComProperty(name = "IconName", dispId = 0x7)
    void setIconName(String param0);
            
    /**
     * <p>id(0x8)</p>
     */
    @ComProperty(name = "IconPath", dispId = 0x8)
    String getIconPath();
            
    /**
     * <p>id(0x9)</p>
     */
    @ComProperty(name = "IconIndex", dispId = 0x9)
    Integer getIconIndex();
            
    /**
     * <p>id(0x9)</p>
     */
    @ComProperty(name = "IconIndex", dispId = 0x9)
    void setIconIndex(Integer param0);
            
    /**
     * <p>id(0xa)</p>
     */
    @ComProperty(name = "IconLabel", dispId = 0xa)
    String getIconLabel();
            
    /**
     * <p>id(0xa)</p>
     */
    @ComProperty(name = "IconLabel", dispId = 0xa)
    void setIconLabel(String param0);
            
    /**
     * <p>id(0xc)</p>
     */
    @ComProperty(name = "Label", dispId = 0xc)
    String getLabel();
            
    /**
     * <p>id(0xe)</p>
     */
    @ComProperty(name = "Object", dispId = 0xe)
    com.sun.jna.platform.win32.COM.util.IDispatch getObject();
            
    /**
     * <p>id(0x16)</p>
     */
    @ComProperty(name = "ProgID", dispId = 0x16)
    String getProgID();
            
    /**
     * <p>id(0x68)</p>
     */
    @ComMethod(name = "Activate", dispId = 0x68)
    void Activate();
            
    /**
     * <p>id(0x6a)</p>
     */
    @ComMethod(name = "Edit", dispId = 0x6a)
    void Edit();
            
    /**
     * <p>id(0x6b)</p>
     */
    @ComMethod(name = "Open", dispId = 0x6b)
    void Open();
            
    /**
     * <p>id(0x6d)</p>
     */
    @ComMethod(name = "DoVerb", dispId = 0x6d)
    void DoVerb(Object VerbIndex);
            
    /**
     * <p>id(0x6e)</p>
     */
    @ComMethod(name = "ConvertTo", dispId = 0x6e)
    void ConvertTo(Object ClassType,
            Object DisplayAsIcon,
            Object IconFileName,
            Object IconIndex,
            Object IconLabel);
            
    /**
     * <p>id(0x6f)</p>
     */
    @ComMethod(name = "ActivateAs", dispId = 0x6f)
    void ActivateAs(String ClassType);
            
    /**
     * <p>id(0x70)</p>
     */
    @ComProperty(name = "PreserveFormattingOnUpdate", dispId = 0x70)
    Boolean getPreserveFormattingOnUpdate();
            
    /**
     * <p>id(0x70)</p>
     */
    @ComProperty(name = "PreserveFormattingOnUpdate", dispId = 0x70)
    void setPreserveFormattingOnUpdate(Boolean param0);
            
    
}