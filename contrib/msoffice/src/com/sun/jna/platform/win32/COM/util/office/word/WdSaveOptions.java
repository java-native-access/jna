
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({E1B4A968-3072-3060-B6B7-1A1356D45CA2})</p>
 */
public enum WdSaveOptions implements IComEnum {
    
    /**
     * (0)
     */
    wdDoNotSaveChanges(0),
    
    /**
     * (-1)
     */
    wdSaveChanges(-1),
    
    /**
     * (-2)
     */
    wdPromptToSaveChanges(-2),
    ;

    private WdSaveOptions(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}