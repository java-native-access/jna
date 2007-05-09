/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples.win32;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JFrame;
import javax.swing.JLabel;
import junit.framework.TestCase;
import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author twall@users.sf.net
 */
public class Win32StdCallTest extends TestCase {

    public static interface TestLibrary extends StdCallLibrary {
        TestLibrary INSTANCE = (TestLibrary)
            Native.loadLibrary("testlib", TestLibrary.class);
        int returnInt32ArgumentStdCall(int arg);
        /** Implementing StdCallCallback is optional if the library is 
         * StdCall. 
         */
        interface Int32Callback extends Callback {
            int callback(int arg, int arg2);
        }
        int callInt32StdCallCallback(Int32Callback c, int arg, int arg2);
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(Win32StdCallTest.class);
    }

    User32 lib;
    GDI32 gdi;
    Kernel32 kernel;
    TestLibrary testlib;
    
    protected void setUp() {
        lib = User32.INSTANCE;
        gdi = GDI32.INSTANCE;
        kernel = Kernel32.INSTANCE;
        testlib = TestLibrary.INSTANCE;
    }
    
    protected void tearDown() {
        lib = null;
        gdi = null;
        kernel = null;
        testlib = null;
    }

    public void testStructureOutArgument() {
        Kernel32.SYSTEMTIME time = new Kernel32.SYSTEMTIME();
        kernel.GetSystemTime(time);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        assertEquals("Hour not properly set", 
                     cal.get(Calendar.HOUR_OF_DAY), time.wHour); 
        assertEquals("Day not properly set", 
                     cal.get(Calendar.DAY_OF_WEEK)-1, 
                     time.wDayOfWeek); 
        assertEquals("Year not properly set", 
                     cal.get(Calendar.YEAR), time.wYear); 
    }
    
    public void testStdCallReturnInt32Argument() {
        final int MAGIC = 0x12345678;
        assertEquals("Expect zero return", 0, testlib.returnInt32ArgumentStdCall(0));
        assertEquals("Expect magic return", MAGIC, testlib.returnInt32ArgumentStdCall(MAGIC));
    }
    
    public void testStdCallCallback() {
        final int MAGIC = 0x11111111;
        final boolean[] called = { false };
        TestLibrary.Int32Callback cb = new TestLibrary.Int32Callback() {
            public int callback(int arg, int arg2) {
                called[0] = true;
                return arg + arg2;
            }
        };
        final int EXPECTED = MAGIC*3;
        int value = testlib.callInt32StdCallCallback(cb, MAGIC, MAGIC*2);
        assertTrue("__stdcall callback not called", called[0]);
        assertEquals("Wrong __stdcall callback value", Integer.toHexString(EXPECTED), 
                     Integer.toHexString(value));
        
        value = testlib.callInt32StdCallCallback(cb, -1, -2);
        assertEquals("Wrong __stdcall callback return", -3, value);
    }
    
    public void xtestFlashWindow() throws Exception {
        final JFrame frame = new JFrame(getName());
        try {
            frame.getContentPane().add(new JLabel(getName()));
            frame.pack();
            frame.setSize(new Dimension(300, 300));
            frame.setLocation(100, 100);
            frame.setResizable(false);
            frame.setVisible(true);

            Pointer hWnd = lib.FindWindowA(null, getName());
            assertNotNull("Couldn't find window", hWnd);

            User32.FLASHWINFO info = new User32.FLASHWINFO();
            info.cbSize = info.size();
            info.hWnd = hWnd;
            info.dwFlags = User32.FLASHW_ALL;
            info.uCount = 10;
            info.dwTimeout = 0;
            lib.FlashWindowEx(info);

            Thread.sleep(5000);
        }
        finally {
            frame.dispose();
        }
    }
    
    public void xtestShowShapedWindow() throws Exception {
        final JFrame frame = new JFrame(getName());
        Pointer p = gdi.CreateRoundRectRgn(0, -150, 300, 300, 300, 300);
        try {
            final boolean[] click = { false };
            final boolean[] ready = { false };
            frame.getContentPane().add(new JLabel(getName()));
            frame.pack();
            frame.setSize(new Dimension(300, 300));
            frame.setLocation(100, 100);
            frame.getContentPane().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    click[0] = true;
                }
            });
            frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    ready[0] = true;
                }
            });

            Pointer hWnd = lib.FindWindowA(null, getName());
            assertNotNull("Couldn't find window", hWnd);

            lib.SetWindowRgn(hWnd, p, true);
            frame.setResizable(false);
            frame.setVisible(true);

            // Click in the lower left corner should miss the window
            Point where = new Point(frame.getX() + 10,
                                    frame.getY() + frame.getHeight() - 10);
            Robot robot = new Robot();
            long start = System.currentTimeMillis();
            while (!ready[0]) {
                robot.mouseMove(where.x + (int)(Math.random()*2), 
                                frame.getY() + frame.getHeight()/2);
                if (System.currentTimeMillis() - start > 5000)
                    break;
                Thread.sleep(10);
            }
            robot.mouseMove(where.x, where.y);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            
            robot.waitForIdle();
            assertFalse("Window not reshaped", click[0]);
            
            //Thread.sleep(100000);
        }
        finally {
            frame.dispose();
            gdi.DeleteObject(p);
        }
    }
    
}
