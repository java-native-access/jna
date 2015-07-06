/*
 * Copyright (c) 2015 Andreas "PAX" L\u00FCck, All Rights Reserved
 *
 * This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later
 * version. <p/> This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
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
