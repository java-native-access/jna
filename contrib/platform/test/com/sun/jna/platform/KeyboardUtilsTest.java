/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import junit.framework.TestCase;

import com.sun.jna.Platform;

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
