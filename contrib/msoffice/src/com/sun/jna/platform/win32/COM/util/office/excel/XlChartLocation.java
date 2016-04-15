package com.sun.jna.platform.win32.COM.util.office.excel;

import com.sun.jna.platform.win32.COM.util.office.word.*;
import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum XlChartLocation implements IComEnum {

    xlLocationAsNewSheet(1),
    xlLocationAsObject(2),
    xlLocationAutomatic(3);

    private XlChartLocation(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}
