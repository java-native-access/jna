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
            // Avoid terminal control letters (like ^Z)
            KeyEvent.VK_CONTROL,
            KeyEvent.VK_SHIFT,
        };
        String[] keystr = {
            "VK_CONTROL", "VK_SHIFT", 
        };
        int[] nonkeys = {
            KeyEvent.VK_B, KeyEvent.VK_1,
            KeyEvent.VK_ALT,
        };
        String[] nonkeystr = {
            "VK_B", "VK_1", "VK_ALT",
        };
        for (int i=0;i < keys.length;i++) {
            try {
                robot.keyPress(keys[i]);
                long start = System.currentTimeMillis();
                while (!KeyboardUtils.isPressed(keys[i])) {
                    if (System.currentTimeMillis() - start > 5000) {
                        fail("Timed out waiting for keypress: " + keystr[i]);
                    }
                    Thread.sleep(10);
                }
            }
            finally {
                robot.keyRelease(keys[i]);
            }
        }
        for (int i=0;i < nonkeys.length;i++) {
            assertFalse("Key should not be pressed: " + nonkeystr[i], KeyboardUtils.isPressed(nonkeys[i]));
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(KeyboardUtilsTest.class);
    }
}
