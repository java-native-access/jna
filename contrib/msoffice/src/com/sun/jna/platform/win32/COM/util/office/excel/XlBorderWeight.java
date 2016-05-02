
package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.office.word.*;
import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlBorderWeight implements IComEnum {
    
    xlHairline(1),
    xlMedium(-4138),
    xlThick(4),
    xlThin(2);

    private XlBorderWeight(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}