
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({2DEF3465-D4C4-369B-B91E-68C9711F3A6C})</p>
 */
public enum WdCollapseDirection implements IComEnum {
    
    /**
     * (1)
     */
    wdCollapseStart(1),
    
    /**
     * (0)
     */
    wdCollapseEnd(0),
    ;

    private WdCollapseDirection(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}