
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({42A64EC8-BC68-3DBC-8BF0-58A8CBA4AB3E})</p>
 */
public enum WdExportCreateBookmarks implements IComEnum {
    
    /**
     * (0)
     */
    wdExportCreateNoBookmarks(0),
    
    /**
     * (1)
     */
    wdExportCreateHeadingBookmarks(1),
    
    /**
     * (2)
     */
    wdExportCreateWordBookmarks(2),
    ;

    private WdExportCreateBookmarks(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}