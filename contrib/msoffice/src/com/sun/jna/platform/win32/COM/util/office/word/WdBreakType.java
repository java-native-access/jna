
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