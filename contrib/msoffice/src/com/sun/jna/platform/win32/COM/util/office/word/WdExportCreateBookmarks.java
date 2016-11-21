/*
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

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