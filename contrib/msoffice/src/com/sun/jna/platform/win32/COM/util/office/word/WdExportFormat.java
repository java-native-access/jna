
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({5D7E6F43-3E57-353C-95E1-52E9783BE2BE})</p>
 */
public enum WdExportFormat implements IComEnum {
    
    /**
     * (17)
     */
    wdExportFormatPDF(17),
    
    /**
     * (18)
     */
    wdExportFormatXPS(18),
    ;

    private WdExportFormat(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}