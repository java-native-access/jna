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
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;

import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

/** Provide access to the local keyboard state.  Note that this is meaningless
 * on a headless system and some VNC setups.
 * 
 * @author twall
 */
// TODO: key clicks
// TODO: auto-repeat
// TODO: keyboard bell
// TODO: led state
public class KeyboardUtils {
    static final NativeKeyboardUtils INSTANCE; 
    static {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("KeyboardUtils requires a keyboard");
        }
        if (Platform.isWindows()) {
            INSTANCE = new W32KeyboardUtils();
        }
        else if (Platform.isMac()) {
            INSTANCE = new MacKeyboardUtils();
            throw new UnsupportedOperationException("No support (yet) for " 
                                                    + System.getProperty("os.name"));
        }
        else {
            INSTANCE = new X11KeyboardUtils();
        }
    }
    
    public static boolean isPressed(int keycode, int location) {
        return INSTANCE.isPressed(keycode, location);
    }
    public static boolean isPressed(int keycode) {
        return INSTANCE.isPressed(keycode);
    }
    
    private static abstract class NativeKeyboardUtils {
        public abstract boolean isPressed(int keycode, int location);
        public boolean isPressed(int keycode) {
            return isPressed(keycode, KeyEvent.KEY_LOCATION_UNKNOWN);
        }
    }
    
    private static class W32KeyboardUtils extends NativeKeyboardUtils {
        private int toNative(int code, int loc) {
            if ((code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z)
                || (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9)) {
                return code;
            }
            if (code == KeyEvent.VK_SHIFT) {
                if ((loc & KeyEvent.KEY_LOCATION_RIGHT) != 0) {
                    return WinUser.VK_RSHIFT; 
                }
                if ((loc & KeyEvent.KEY_LOCATION_LEFT) != 0) {
                    return WinUser.VK_LSHIFT; 
                }
                return WinUser.VK_SHIFT;
            }
            if (code == KeyEvent.VK_CONTROL) {
                if ((loc & KeyEvent.KEY_LOCATION_RIGHT) != 0) {
                    return WinUser.VK_RCONTROL; 
                }
                if ((loc & KeyEvent.KEY_LOCATION_LEFT) != 0) {
                    return WinUser.VK_LCONTROL; 
                }
                return WinUser.VK_CONTROL;
            }
            if (code == KeyEvent.VK_ALT) {
                if ((loc & KeyEvent.KEY_LOCATION_RIGHT) != 0) {
                    return WinUser.VK_RMENU; 
                }
                if ((loc & KeyEvent.KEY_LOCATION_LEFT) != 0) {
                    return WinUser.VK_LMENU; 
                }
                return WinUser.VK_MENU;
            }
            return 0;
        }
        public boolean isPressed(int keycode, int location) {
            User32 lib = User32.INSTANCE;
            return (lib.GetAsyncKeyState(toNative(keycode, location)) & 0x8000) != 0;
        }
    }
    private static class MacKeyboardUtils extends NativeKeyboardUtils {
        public boolean isPressed(int keycode, int location) {
            return false;
        }
    }
    private static class X11KeyboardUtils extends NativeKeyboardUtils {
        // TODO: fully map from X11 keycodes to java keycodes
        // this is a minimal implementation
        private int toKeySym(int code, int location) {
            if (code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z)
                return X11.XK_a + (code - KeyEvent.VK_A);
            if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9)
                return X11.XK_0 + (code - KeyEvent.VK_0);
            if (code == KeyEvent.VK_SHIFT) {
                if ((location & KeyEvent.KEY_LOCATION_RIGHT) != 0)
                    return X11.XK_Shift_R;
                return X11.XK_Shift_L;
            }
            if (code == KeyEvent.VK_CONTROL) {
                if ((location & KeyEvent.KEY_LOCATION_RIGHT) != 0)
                    return X11.XK_Control_R;
                return X11.XK_Control_L;
            }
            if (code == KeyEvent.VK_ALT) {
                if ((location & KeyEvent.KEY_LOCATION_RIGHT) != 0)
                    return X11.XK_Alt_R;
                return X11.XK_Alt_L;
            }
            if (code == KeyEvent.VK_META) {
                if ((location & KeyEvent.KEY_LOCATION_RIGHT) != 0)
                    return X11.XK_Meta_R;
                return X11.XK_Meta_L;
            }
            return 0;
        }
        public boolean isPressed(int keycode, int location) {
            X11 lib = X11.INSTANCE;
            Display dpy = lib.XOpenDisplay(null);
            if (dpy == null) {
                throw new Error("Can't open X Display");
            }
            try {
                byte[] keys = new byte[32];
                // Ignore the return value
                lib.XQueryKeymap(dpy, keys);
                int keysym = toKeySym(keycode, location);
                for (int code=5;code < 256;code++) {
                    int idx = code / 8;
                    int shift = code % 8;
                    if ((keys[idx] & (1 << shift)) != 0) {
                        int sym = lib.XKeycodeToKeysym(dpy, (byte)code, 0).intValue();
                        if (sym == keysym)
                            return true;
                    }
                }
            }
            finally {
                lib.XCloseDisplay(dpy);
            }
            return false;
        }
    }
}
