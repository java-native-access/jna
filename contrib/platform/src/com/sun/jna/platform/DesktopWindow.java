/*
 * Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
 *
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
package com.sun.jna.platform;

import java.awt.Rectangle;

import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * Holds some general information about a window.
 * 
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
public class DesktopWindow {
    private HWND hwnd;
    private String title;
    private String filePath;
    private Rectangle locAndSize;

    /**
     * @param hwnd
     *            The associated window handle for this window.
     * @param title
     *            The title text of the window.
     * @param filePath
     *            The full file path to the main process that created the
     *            window.
     * @param locAndSize
     *            The window's location on screen and its dimensions.
     */
    public DesktopWindow(final HWND hwnd, final String title,
                         final String filePath, final Rectangle locAndSize) {
        this.hwnd = hwnd;
        this.title = title;
        this.filePath = filePath;
        this.locAndSize = locAndSize;
    }

    /**
     * @return The associated window handle for this window.
     */
    public HWND getHWND() {
        return hwnd;
    }

    /**
     * @return The title text of the window.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The full file path to the main process that created the window.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return The window's location on screen and its dimensions.
     */
    public Rectangle getLocAndSize() {
        return locAndSize;
    }
}
