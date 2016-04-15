
package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.office.word.*;
import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlRowCol implements IComEnum {
    
    xlColumns(2),
    xlRows(1);

    private XlRowCol(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}