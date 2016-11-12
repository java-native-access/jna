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

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({58B14C6F-0FE6-3BCA-880E-E3A9C039E588})</p>
 */
public enum WdBreakType implements IComEnum {
    
    /**
     * (2)
     */
    wdSectionBreakNextPage(2),
    
    /**
     * (3)
     */
    wdSectionBreakContinuous(3),
    
    /**
     * (4)
     */
    wdSectionBreakEvenPage(4),
    
    /**
     * (5)
     */
    wdSectionBreakOddPage(5),
    
    /**
     * (6)
     */
    wdLineBreak(6),
    
    /**
     * (7)
     */
    wdPageBreak(7),
    
    /**
     * (8)
     */
    wdColumnBreak(8),
    
    /**
     * (9)
     */
    wdLineBreakClearLeft(9),
    
    /**
     * (10)
     */
    wdLineBreakClearRight(10),
    
    /**
     * (11)
     */
    wdTextWrappingBreak(11),
    ;

    private WdBreakType(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}