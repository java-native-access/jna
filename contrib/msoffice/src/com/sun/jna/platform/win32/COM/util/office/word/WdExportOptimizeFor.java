
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({147553BC-4DC5-3681-A445-D1C4BEA414AD})</p>
 */
public enum WdExportOptimizeFor implements IComEnum {
    
    /**
     * (0)
     */
    wdExportOptimizeForPrint(0),
    
    /**
     * (1)
     */
    wdExportOptimizeForOnScreen(1),
    ;

    private WdExportOptimizeFor(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}