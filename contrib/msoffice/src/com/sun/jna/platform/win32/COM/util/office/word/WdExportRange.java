
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({20D65698-7CDC-3A68-A83D-D52A76FEA1A4})</p>
 */
public enum WdExportRange implements IComEnum {
    
    /**
     * (0)
     */
    wdExportAllDocument(0),
    
    /**
     * (1)
     */
    wdExportSelection(1),
    
    /**
     * (2)
     */
    wdExportCurrentPage(2),
    
    /**
     * (3)
     */
    wdExportFromTo(3),
    ;

    private WdExportRange(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}