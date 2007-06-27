/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples;

import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import com.sun.jna.Platform;
import junit.framework.TestCase;

public class KeyboardUtilsTest extends TestCase {
    
    public void testIsPressed() throws Exception {
        // Can't run this test headless; not yet implemented on mac
        if (GraphicsEnvironment.isHeadless() || Platform.isMac())
            return;
        
        Robot robot = new Robot();
        int[] keys = {
            // order these to avoid any visible characters
            KeyEvent.VK_CONTROL,
            KeyEvent.VK_A, KeyEvent.VK_Z,
            KeyEvent.VK_0, KeyEvent.VK_9, 
            KeyEvent.VK_SHIFT,
            KeyEvent.VK_U,
        };
        String[] keystr = {
            "VK_CONTROL", "VK_A", "VK_Z", "VK_0", "VK_9", "VK_SHIFT", "VK_U", 
        };
        int[] nonkeys = {
            KeyEvent.VK_B, KeyEvent.VK_1,
            KeyEvent.VK_ALT,
        };
        String[] nonkeystr = {
            "VK_B", "VK_1", "VK_ALT",
        };
        for (int i=0;i < keys.length;i++) {
            robot.keyPress(keys[i]);
        }
        robot.delay(1000);
        try {
            for (int i=0;i < keys.length;i++) {
                assertTrue("Key should be pressed: " + keystr[i], KeyboardUtils.isPressed(keys[i]));
            }
            for (int i=0;i < nonkeys.length;i++) {
                assertFalse("Key should not be pressed: " + nonkeystr[i], KeyboardUtils.isPressed(nonkeys[i]));
            }
        }
        finally {
            for (int i=0;i < keys.length;i++) {
                try { robot.keyRelease(keys[i]); }
                catch(Exception e) { }
            }
        }
    }
}
