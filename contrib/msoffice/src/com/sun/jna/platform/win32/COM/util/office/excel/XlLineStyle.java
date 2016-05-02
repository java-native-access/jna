
package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.office.word.*;
import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlLineStyle implements IComEnum {
    
    xlContinuous(1),
    xlDash(-4115),
    xlDashDot(4),
    xlDashDotDot(5),
    xlDot(-4118),
    xlDouble(-4119),
    xlSlantDashDot(13),
    xlLineStyleNone(-4142);

    private XlLineStyle(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}