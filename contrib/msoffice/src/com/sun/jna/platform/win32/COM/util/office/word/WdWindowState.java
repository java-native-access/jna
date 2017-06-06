package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum WdWindowState implements IComEnum{
	
	wdWindowStateNormal(0),
	wdWindowStateMaximize(1),
	wdWindowStateMinimize (2),

	 ;
	
    private WdWindowState(long value) {
        this.value = value;
    }
    
    private long value;

    public long getValue() {
        return this.value;
    }

}
