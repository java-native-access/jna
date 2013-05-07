/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import static com.sun.jna.platform.win32.User32.INSTANCE;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.WinUser.LASTINPUTINFO;

/**
 * @author dblock[at]dblock[dot]org
 */
public class User32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(User32Test.class);
    }

    public void testGetSystemMetrics() {
        int cursorWidth = INSTANCE.GetSystemMetrics(WinUser.SM_CXCURSOR);
        assertTrue(cursorWidth > 0);
    }

    public void testRegisterHotKey() {
        int vk = KeyEvent.VK_D;
        int id = 1;

        assertTrue("RegisterHotKey failed", INSTANCE.RegisterHotKey(null, id, WinUser.MOD_CONTROL | WinUser.MOD_ALT, vk));

        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(vk);
            Thread.sleep(50);
            robot.keyRelease(vk);
            WinUser.MSG msg = waitForMessage(500);
            assertNotNull(msg);
            assertEquals(msg.wParam.intValue(), id);

            assertTrue(INSTANCE.UnregisterHotKey(null, id));
            robot.keyPress(vk);
            Thread.sleep(10);
            robot.keyRelease(vk);
            msg = waitForMessage(500);
            assertNull(msg);
        } catch (AWTException e) {
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            if (robot != null) {
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_ALT);
            }
        }
    }

    private WinUser.MSG waitForMessage(int timeout) {
        WinUser.MSG msg = new WinUser.MSG();

        try {
            long time = System.currentTimeMillis();
            while (true) {
                while (INSTANCE.PeekMessage(msg, null, 0, 0, 1)) {
                    if (msg.message == WinUser.WM_HOTKEY) {
                        return msg;
                    }
                }
                if (System.currentTimeMillis() - time > timeout)
                    break;

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void testGetLastInputInfo() throws Exception {
        LASTINPUTINFO plii = new LASTINPUTINFO();
        assertEquals(plii.size(), plii.cbSize);

        assertTrue(User32.INSTANCE.GetLastInputInfo(plii));
        assertTrue(Kernel32.INSTANCE.GetTickCount() >= plii.dwTime);
        assertTrue(plii.dwTime > 0);
    }
    
    public final void testRegisterWindowMessage() {
        final int msg = User32.INSTANCE.RegisterWindowMessage("RM_UNITTEST"); 
        assertTrue(msg >= 0xC000 && msg <= 0xFFFF);
    }
}
