
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({D67854FC-9A45-33F6-A4D3-DC0002A53CE9})</p>
 */
public enum WdExportItem implements IComEnum {
    
    /**
     * (0)
     */
    wdExportDocumentContent(0),
    
    /**
     * (7)
     */
    wdExportDocumentWithMarkup(7),
    ;

    private WdExportItem(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}