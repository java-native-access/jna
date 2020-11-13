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
package com.sun.jna.contrib.demo;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

/** Sample implementation of a low-level window hooks on W32. */
public class WindowHooks {
    private static volatile boolean quit;

    private static WinUser.LowLevelMouseProc mouseProc;
    private static WinUser.HHOOK mouseHook;

    private static WinUser.LowLevelKeyboardProc keyboardProc;
    private static WinUser.HHOOK keyboardHook;

    public static void main(String[] args) {
        final User32 lib = User32.INSTANCE;

        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);

        keyboardProc = new LowLevelKeyboardProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                    switch(wParam.intValue()) {
                        case WinUser.WM_KEYUP:
                        case WinUser.WM_KEYDOWN:
                        case WinUser.WM_SYSKEYUP:
                        case WinUser.WM_SYSKEYDOWN:
                            System.err.println("in callback, key=" + info.vkCode);
                            if (info.vkCode == 81) {
                                quit = true;
                            }
                    }
                }

                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(keyboardHook, nCode, wParam, new LPARAM(peer));
            }
        };
        keyboardHook = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardProc, hMod, 0);

        System.out.println("Keyboard hook installed, type anywhere, 'q' to quit");

        mouseProc = new WinUser.LowLevelMouseProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, WinUser.MSLLHOOKSTRUCT lParam) {
                if (nCode >= 0) {
                    switch (wParam.intValue()) {
                        case 512: {
                            System.out.println("Mouse moved!");

                            break;
                        }
                        case 513: {
                            System.out.println("Left button down!");

                            break;
                        }
                        case 514: {
                            System.out.println("Left button up!");

                            break;
                        }
                        case 516: {
                            System.out.println("Right button down!");

                            break;
                        }
                        case 517: {
                            System.out.println("Right button up!");

                            break;
                        }
                        case 519: {
                            System.out.println("Middle button down!");

                            break;
                        }
                        case 520: {
                            System.out.println("Middle button up!");

                            break;
                        }
                    }
                }
                return lib.CallNextHookEx(mouseHook, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
            }
        };
        mouseHook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseProc, hMod, 0);

        new Thread() {
            @Override
            public void run() {
                while (!quit) {
                    try { Thread.sleep(10); } catch(Exception e) { }
                }
                System.err.println("unhook and exit");
                lib.UnhookWindowsHookEx(keyboardHook);
                lib.UnhookWindowsHookEx(mouseHook);
                System.exit(0);
            }
        }.start();

        // This bit never returns from GetMessage
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                System.err.println("error in get message");
                break;
            }
            else {
                System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(keyboardHook);
        lib.UnhookWindowsHookEx(mouseHook);
    }
}