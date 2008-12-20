/* Copyright (c) 2008 Stefan Endrullis, All Rights Reserved
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
package jnacontrib.x11.api;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.examples.unix.X11;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 * Object oriented X window system.
 * <p/>
 * Some code segments in this class are based on the code of the program
 * wmctrl (licensed under GPLv2) written by Tomas Styblo &lt;tripie@cpan.org&gt;.
 * Thanks a lot, Tomas!
 *
 * @author Stefan Endrullis
 */
public class X {
    /** Remove/unset property. */
    public static final int _NET_WM_STATE_REMOVE = 0;
    /** Add/set property. */
    public static final int _NET_WM_STATE_ADD = 1;
    /** Toggle property. */
    public static final int _NET_WM_STATE_TOGGLE = 2;
    /** Maximal property value length. */
    public static final int MAX_PROPERTY_VALUE_LEN = 4096;

    private static final X11 x11 = X11.INSTANCE;

    private static int bytesToInt(byte[] prop) {
        return ((prop[3] & 0xff) << 24)
            | ((prop[2] & 0xff) << 16)
            | ((prop[1] & 0xff) << 8)
            | ((prop[0] & 0xff));
    }

    private static int bytesToInt(byte[] prop, int offset) {
        return ((prop[3 + offset] & 0xff) << 24)
            | ((prop[2 + offset] & 0xff) << 16)
            | ((prop[1 + offset] & 0xff) << 8)
            | ((prop[offset] & 0xff));
    }


    /**
     * X Display.
     */
    public static class Display {
        /**
         * Open display.
         */
        private X11.Display x11Display;
        /**
         * HashMap<String,X11.Atom>.
         */
        private HashMap atomsHash = new HashMap();

        /**
         * Creates the OOWindowUtils using the default display.
         */
        public Display() {
            x11Display = x11.XOpenDisplay(null);

            if (x11Display == null) {
                throw new Error("Can't open X Display");
            }
        }

        /**
         * Creates the OOWindowUtils using a given display.
         *
         * @param x11Display open display
         */
        public Display(X11.Display x11Display) {
            this.x11Display = x11Display;

            if (x11Display == null) {
                throw new Error("X Display is null");
            }
        }

        /**
         * Closes the display.
         */
        public void close() {
            x11.XCloseDisplay(x11Display);
        }

        /**
         * Flushes the output buffer / event queue.
         */
        public void flush() {
            x11.XFlush(x11Display);
        }

        /**
         * Returns the X11 display.
         *
         * @return X11 display
         */
        public X11.Display getX11Display() {
            return x11Display;
        }

        /**
         * Get internal atoms by name.
         *
         * @param name name of the atom
         * @return atom
         */
        public X11.Atom getAtom(String name) {
            X11.Atom atom = (X11.Atom) atomsHash.get(name);
            if (atom == null) {
                atom = x11.XInternAtom(x11Display, name, false);
                atomsHash.put(name, atom);
            }
            return atom;
        }

        /**
         * Returns the window manager information as an window.
         *
         * @return window manager information as an window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Window getWindowManagerInfo() throws X11Exception {
            Window rootWindow = getRootWindow();

            try {
                return rootWindow.getWindowProperty(X11.XA_WINDOW, "_NET_SUPPORTING_WM_CHECK");
            } catch (X11Exception e) {
                try {
                    return rootWindow.getWindowProperty(X11.XA_CARDINAL, "_WIN_SUPPORTING_WM_CHECK");
                } catch (X11Exception e1) {
                    throw new X11Exception("Cannot get window manager info properties. (_NET_SUPPORTING_WM_CHECK or _WIN_SUPPORTING_WM_CHECK)");
                }
            }
        }

        /**
         * Returns the root window.
         *
         * @return root window
         */
        public Window getRootWindow() {
            return new Window(this, x11.XDefaultRootWindow(x11Display));
        }

        /**
         * Returns the current active window.
         *
         * @return current active window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Window getActiveWindow() throws X11Exception {
            return getRootWindow().getWindowProperty(X11.XA_WINDOW, "_NET_ACTIVE_WINDOW");
        }

        /**
         * Returns all windows managed by the window manager.
         *
         * @return all windows managed by the window manager
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Window[] getWindows() throws X11Exception {
            byte[] bytes;
            Window rootWindow = getRootWindow();

            try {
                bytes = rootWindow.getProperty(X11.XA_WINDOW, "_NET_CLIENT_LIST");
            } catch (X11Exception e) {
                try {
                    bytes = rootWindow.getProperty(X11.XA_CARDINAL, "_WIN_CLIENT_LIST");
                } catch (X11Exception e1) {
                    throw new X11Exception("Cannot get client list properties (_NET_CLIENT_LIST or _WIN_CLIENT_LIST)");
                }
            }

            Window[] windowList = new Window[bytes.length / X11.Window.SIZE];

            for (int i = 0; i < windowList.length; i++) {
                windowList[i] = new Window(this, new X11.Window(bytesToInt(bytes, X11.XID.SIZE * i)));
            }

            return windowList;
        }

        /**
         * Returns the number of desktops.
         *
         * @return number of desktops
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getDesktopCount() throws X11Exception {
            Window root = getRootWindow();

            try {
                return root.getIntProperty(X11.XA_CARDINAL, "_NET_NUMBER_OF_DESKTOPS");
            } catch (X11Exception e) {
                try {
                    return root.getIntProperty(X11.XA_CARDINAL, "_WIN_WORKSPACE_COUNT");
                } catch (X11Exception e1) {
                    throw new X11Exception("Cannot get number of desktops properties (_NET_NUMBER_OF_DESKTOPS or _WIN_WORKSPACE_COUNT)");
                }
            }
        }

        /**
         * Returns the number of the active desktop.
         *
         * @return number of the active desktop
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getActiveDesktopNumber() throws X11Exception {
            Window root = getRootWindow();
            int cur_desktop;

            try {
                cur_desktop = root.getIntProperty(X11.XA_CARDINAL, "_NET_CURRENT_DESKTOP");
            } catch (X11Exception e) {
                try {
                    cur_desktop = root.getIntProperty(X11.XA_CARDINAL, "_WIN_WORKSPACE");
                } catch (X11Exception e1) {
                    throw new X11Exception("Cannot get current desktop properties (_NET_CURRENT_DESKTOP or _WIN_WORKSPACE property)");
                }
            }

            return cur_desktop;
        }

        /**
         * Returns the available desktops.
         *
         * @return available desktops
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Desktop[] getDesktops() throws X11Exception {
            Window root = getRootWindow();
            String[] desktopNames;
            try {
                desktopNames = root.getUtf8ListProperty(getAtom("UTF8_STRING"), "_NET_DESKTOP_NAMES");
            } catch (X11Exception e) {
                try {
                    desktopNames = root.getStringListProperty(X11.XA_STRING, "_WIN_WORKSPACE_NAMES");
                } catch (X11Exception e1) {
                    throw new X11Exception("Cannot get desktop names properties (_NET_DESKTOP_NAMES or _WIN_WORKSPACE_NAMES)");
                }
            }

            Desktop[] desktops = new Desktop[getDesktopCount()];
            for (int i = 0; i < desktops.length; i++) {
                desktops[i] = new Desktop(this, i, desktopNames[i]);
            }

            return desktops;
        }

        /**
         * Switches to the given desktop.
         *
         * @param nr desktop number
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public void switchDesktop(int nr) throws X11Exception {
            getRootWindow().clientMsg("_NET_CURRENT_DESKTOP", nr, 0, 0, 0, 0);
        }

        /**
         * Sets the "showing the desktop" state.
         *
         * @param state true if the desktop should be shown
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public void showingDesktop(boolean state) throws X11Exception {
            getRootWindow().clientMsg("_NET_SHOWING_DESKTOP", state ? 1 : 0, 0, 0, 0, 0);
        }

        /**
         * Enables / disables the auto-repeat of pressed keys.
         *
         * @param on true if auto-repeat shall be enabled
         */
        public void setKeyAutoRepeat(boolean on) {
           if (on) {
               x11.XAutoRepeatOn(x11Display);
           } else {
               x11.XAutoRepeatOff(x11Display);
           }
        }

        /**
         * Returns the key symbol corresponding to the the key name.
         *
         * @param keyName name of the key
         * @return key symbol
         */
        public X11.KeySym getKeySym(String keyName) {
            return x11.XStringToKeysym(keyName);
        }

        /**
         * Returns the key symbol corresponding to the keycode.
         *
         * @param keyCode keycode
         * @param index element of the keycode vector
         * @return key symbol
         */
        public X11.KeySym getKeySym(byte keyCode, int index) {
            return x11.XKeycodeToKeysym(x11Display, keyCode, index);
        }

        /**
         * Returns the keycode corresponding to the key symbol.
         *
         * @param keySym key symbol
         * @return keycode
         */
        public byte getKeyCode(X11.KeySym keySym) {
            return x11.XKeysymToKeycode(x11Display,  keySym);
        }

        /**
         * Returns the keycode corresponding to the key name.
         *
         * @param keyName name of the key
         * @return keycode
         */
        public byte getKeyCode(String keyName) {
            return x11.XKeysymToKeycode(x11Display,  getKeySym(keyName));
        }

        /**
         * Returns the key name corresponding to the key symbol.
         *
         * @param keySym key symbol
         * @return name of the key
         */
        public String getKeyName(X11.KeySym keySym) {
            return x11.XKeysymToString(keySym);
        }

        /**
         * Returns the key name corresponding to the keycode and the index in the keycode vector.
         *
         * @param keyCode keycode
         * @param index index in the keycode vector
         * @return name of the key
         */
        public String getKeyName(byte keyCode, int index) {
            return getKeyName(getKeySym(keyCode, index));
        }

        /**
         * Returns the modifier keymap.
         *
         * @return modifier keymap
         */
        public ModifierKeymap getModifierKeymap() {
            X11.XModifierKeymapRef xModifierKeymapRef = x11.XGetModifierMapping(x11Display);
            ModifierKeymap modifierKeymap = new ModifierKeymap(xModifierKeymapRef);
            x11.XFreeModifiermap(xModifierKeymapRef);
            return modifierKeymap;
        }

        /**
         * Sets the modifier keymap.
         *
         * @param modifierKeymap modifier keymap
         */
        public void setModifierKeymap(ModifierKeymap modifierKeymap) {
            X11.XModifierKeymapRef xModifierKeymapRef = modifierKeymap.toXModifierKeyamp();
            x11.XSetModifierMapping(x11Display,xModifierKeymapRef);
        }
    }


    /**
     * Modifier keymap. The lists shift, lock, control, mod1, mod1, mod1, mod1, mod1
     * contain the keycodes as Byte objects. You can directly access these lists to
     * read, replace, remove or insert new keycodes to these modifiers.
     * To apply a new modifier keymap call
     * {@link X.Display#setModifierKeymap(ModifierKeymap)}.
     */
    public static class ModifierKeymap {
        /** Shift modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList shift = new ArrayList(4);
        /** Lock modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList lock = new ArrayList(4);
        /** Control modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList control = new ArrayList(4);
        /** Mod1 modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList mod1 = new ArrayList(4);
        /** Mod2 modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList mod2 = new ArrayList(4);
        /** Mod3 modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList mod3 = new ArrayList(4);
        /** Mod4 modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList mod4 = new ArrayList(4);
        /** Mod5 modifier as an ArrayList&lt;Byte&gt;. */
        public ArrayList mod5 = new ArrayList(4);

        /**
         * Creates an empty modifier keymap.
         */
        public ModifierKeymap() {
        }

        /**
         * Creates a modifier keymap and reads the modifiers from the XModifierKeymap.
         *
         * @param xModifierKeymapRef XModifierKeymap
         */
        public ModifierKeymap(X11.XModifierKeymapRef xModifierKeymapRef) {
            fromXModifierKeymap(xModifierKeymapRef);
        }

        /**
         * Reads all modifiers from the XModifierKeymap.
         *
         * @param xModifierKeymapRef XModifierKeymap
         */
        public void fromXModifierKeymap(X11.XModifierKeymapRef xModifierKeymapRef) {
            int count = xModifierKeymapRef.max_keypermod;
            byte[] keys = xModifierKeymapRef.modifiermap.getByteArray(0, 8*count);

            ArrayList[] allModifiers = getAllModifiers();

            for (int modNr = 0; modNr < 8; modNr++) {
                ArrayList modifier = allModifiers[modNr];
                modifier.clear();

                for (int keyNr = 0; keyNr < count; keyNr++) {
                    byte key = keys[modNr*count + keyNr];
                    if (key != 0) {
                        modifier.add(new Byte(key));
                    }
                }
            }
        }

        /**
         * Returns an XModifierKeymap corresponding to this object.
         *
         * @return XModifierKeymap
         */
        public X11.XModifierKeymapRef toXModifierKeyamp() {
            ArrayList[] allModifiers = getAllModifiers();

            // determine max list size
            int count = 0;
            for (int i = 0; i < allModifiers.length; i++) {
                count = Math.max(count, allModifiers[i].size());
            }

            byte[] keys = new byte[8*count];
            for (int modNr = 0; modNr < 8; modNr++) {
                ArrayList modifier = allModifiers[modNr];

                for (int keyNr = 0; keyNr < modifier.size(); keyNr++) {
                    keys[modNr*count + keyNr] = ((Byte) modifier.get(keyNr)).byteValue();
                }
            }

            X11.XModifierKeymapRef xModifierKeymapRef = new X11.XModifierKeymapRef();
            xModifierKeymapRef.max_keypermod = count;
            xModifierKeymapRef.modifiermap = new Memory(keys.length);
            xModifierKeymapRef.modifiermap.write(0, keys, 0, keys.length);

            return xModifierKeymapRef;
        }

        /**
         * Returns all modifiers as an array.
         *
         * @return array of modifier lists
         */
        public ArrayList[] getAllModifiers() {
            return new ArrayList[] {
                    shift, lock, control, mod1, mod2, mod3, mod4, mod5
            };
        }
    }

    /**
     * X Desktop.
     */
    public static class Desktop {
        public X.Display display;
        public int number;
        public String name;

        public Desktop(Display display, int number, String name) {
            this.display = display;
            this.number = number;
            this.name = name;
        }
    }


    /**
     * X Window.
     */
    public static class Window {
        private X.Display display;
        private X11.Window x11Window;

        /**
         * Returns the X11 window object.
         *
         * @return X11 window
         */
        public X11.Window getX11Window() {
            return x11Window;
        }

        /**
         * Returns the ID of the window.
         *
         * @return window ID
         */
        public int getID() {
            return x11Window.intValue();
        }

        /**
         * Creates the window.
         *
         * @param display   display where the window is allocated
         * @param x11Window X11 window
         */
        public Window(X.Display display, X11.Window x11Window) {
            this.display = display;
            this.x11Window = x11Window;
        }

        /**
         * Returns the title of the window.
         *
         * @return title of the window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getTitle() throws X11Exception {
            try {
                return getUtf8Property(display.getAtom("UTF8_STRING"), "_NET_WM_NAME");
            } catch (X11Exception e) {
                return getUtf8Property(X11.XA_STRING, X11.XA_WM_NAME);
            }

        }

        /**
         * Returns the window class.
         *
         * @return window class
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getWindowClass() throws X11Exception {
            return getUtf8Property(X11.XA_STRING, X11.XA_WM_CLASS);
        }

        /**
         * Returns the PID of the window.
         *
         * @return PID of the window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getPID() throws X11Exception {
            return getIntProperty(X11.XA_CARDINAL, "_NET_WM_PID");
        }

        /**
         * Returns the desktop ID of the window.
         *
         * @return desktop ID of the window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getDesktop() throws X11Exception {
            try {
                return getIntProperty(X11.XA_CARDINAL, "_NET_WM_DESKTOP");
            } catch (X11Exception e) {
                return getIntProperty(X11.XA_CARDINAL, "_WIN_WORKSPACE");
            }
        }

        /**
         * Returns the client machine name of the window.
         *
         * @return client machine name of the window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getMachine() throws X11Exception {
            return getStringProperty(X11.XA_STRING, "WM_CLIENT_MACHINE");
        }

        /**
         * Returns the XWindowAttributes of the window.
         *
         * @return XWindowAttributes of the window
         */
        public X11.XWindowAttributes getXWindowAttributes() {
            X11.XWindowAttributes xwa = new X11.XWindowAttributes();
            x11.XGetWindowAttributes(display.x11Display, x11Window, xwa);

            return xwa;
        }

        /**
         * Returns the geometry of the window.
         *
         * @return geometry of the window
         */
        public Geometry getGeometry() {
            X11.WindowByReference junkRoot = new X11.WindowByReference();
            IntByReference junkX = new IntByReference();
            IntByReference junkY = new IntByReference();
            IntByReference x = new IntByReference();
            IntByReference y = new IntByReference();
            IntByReference width = new IntByReference();
            IntByReference height = new IntByReference();
            IntByReference borderWidth = new IntByReference();
            IntByReference depth = new IntByReference();

            x11.XGetGeometry(display.x11Display, x11Window, junkRoot, junkX, junkY, width, height, borderWidth, depth);

            x11.XTranslateCoordinates(display.x11Display, x11Window, junkRoot.getValue(), junkX.getValue(),
                junkY.getValue(), x, y, junkRoot);

            return new Geometry(x.getValue(), y.getValue(), width.getValue(), height.getValue(),
                borderWidth.getValue(), depth.getValue());
        }

        /**
         * Returns the bounding box of the window.
         *
         * @return bounding box of the window
         */
        public Rectangle getBounds() {
            X11.WindowByReference junkRoot = new X11.WindowByReference();
            IntByReference junkX = new IntByReference();
            IntByReference junkY = new IntByReference();
            IntByReference x = new IntByReference();
            IntByReference y = new IntByReference();
            IntByReference width = new IntByReference();
            IntByReference height = new IntByReference();
            IntByReference border_width = new IntByReference();
            IntByReference depth = new IntByReference();

            x11.XGetGeometry(display.x11Display, x11Window, junkRoot, junkX, junkY, width, height, border_width, depth);

            x11.XTranslateCoordinates(display.x11Display, x11Window, junkRoot.getValue(), junkX.getValue(),
                junkY.getValue(), x, y, junkRoot);

            int xVal = x.getValue();
            int yVal = y.getValue();

            return new Rectangle(xVal, yVal, xVal + width.getValue(), yVal + height.getValue());
        }

        /**
         * Activates the window.
         *
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public void activate() throws X11Exception {
            clientMsg("_NET_ACTIVE_WINDOW", 0, 0, 0, 0, 0);
            x11.XMapRaised(display.x11Display, x11Window);
        }

        /**
         * Moves the window to the specified desktop.
         *
         * @param desktopNr desktop
         * @return X11.SUCCESS if closing was successful
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int moveToDesktop(int desktopNr) throws X11Exception {
            return clientMsg("_NET_WM_DESKTOP", desktopNr, 0, 0, 0, 0);
        }

        /**
         * Selects the input events to listen for.
         *
         * @param eventMask event mask representing the events to listen for
         */
        public void selectInput(int eventMask) {
            x11.XSelectInput(display.x11Display, x11Window, new NativeLong(eventMask));
        }

        public int nextEvent(X11.XEvent event) {
            return x11.XNextEvent(display.x11Display, event);
        }

        public void sendEvent(int eventMask, X11.XEvent event) {
            x11.XSendEvent(display.x11Display, x11Window, 1, new NativeLong(eventMask), event);
        }

        /**
         * Closes the window gracefully.
         *
         * @return X11.SUCCESS if closing was successful
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int close() throws X11Exception {
            return clientMsg("_NET_CLOSE_WINDOW", 0, 0, 0, 0, 0);
        }

        /**
         * Returns the property value as integer.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as integer
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getIntProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            return bytesToInt(getProperty(xa_prop_type, xa_prop_name));
        }

        /**
         * Returns the property value as integer.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as integer
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public int getIntProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getIntProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as window.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Window getWindowProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            int windowId = getIntProperty(xa_prop_type, xa_prop_name);
            X11.Window x11Window = new X11.Window(windowId);
            return new Window(display, x11Window);
        }

        /**
         * Returns the property value as window.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as window
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public Window getWindowProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getWindowProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as a null terminated byte array.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as a null terminated byte array
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getNullTerminatedProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            byte[] bytesOrig = getProperty(xa_prop_type, xa_prop_name);
            byte[] bytesDest;

            // search for '\0'
            int i;
            for (i = 0; i < bytesOrig.length; i++) {
                if (bytesOrig[i] == '\0') break;
            }

            if (i < bytesOrig.length - 1) {
                bytesDest = new byte[i + 1];
                System.arraycopy(bytesOrig, 0, bytesDest, 0, i + 1);
            } else {
                bytesDest = bytesOrig;
            }

            return bytesDest;
        }

        /**
         * Returns the property value as a null terminated byte array.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as a null terminated byte array
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getNullTerminatedProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getNullTerminatedProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as byte array where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as byte array where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getNullReplacedStringProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            byte[] bytes = getProperty(xa_prop_type, xa_prop_name);

            // search for '\0'
            int i;
            for (i = 0; i < bytes.length; i++) {
                if (bytes[i] == '\0') {
                    bytes[i] = '.';
                }
            }

            return bytes;
        }

        /**
         * Returns the property value as byte array where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as byte array where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getNullReplacedStringProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getNullReplacedStringProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as string where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as string where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getStringProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            return new String(getNullReplacedStringProperty(xa_prop_type, xa_prop_name));
        }

        /**
         * Returns the property value as string where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as string where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getStringProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return new String(getNullReplacedStringProperty(xa_prop_type, xa_prop_name));
        }

        /**
         * Returns the property value as string list.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as string list
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String[] getStringListProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            return new String(getProperty(xa_prop_type, xa_prop_name)).split("\0");
        }

        /**
         * Returns the property value as string list.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as string list
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String[] getStringListProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return new String(getProperty(xa_prop_type, xa_prop_name)).split("\0");
        }

        /**
         * Returns the property value as UTF8 string where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as UTF8 string where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getUtf8Property(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            try {
                return new String(getNullReplacedStringProperty(xa_prop_type, xa_prop_name), "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new X11Exception(e);
            }
        }

        /**
         * Returns the property value as UTF8 string where every '\0' character is replaced by '.'.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as UTF8 string where every '\0' character is replaced by '.'
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String getUtf8Property(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getUtf8Property(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as UTF8 string list
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as UTF8 string list
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String[] getUtf8ListProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            try {
                return new String(getProperty(xa_prop_type, xa_prop_name), "UTF8").split("\0");
            } catch (UnsupportedEncodingException e) {
                throw new X11Exception(e);
            }
        }

        /**
         * Returns the property value as UTF8 string list
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as UTF8 string list
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public String[] getUtf8ListProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getUtf8ListProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        /**
         * Returns the property value as a byte array.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as a byte array
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getProperty(X11.Atom xa_prop_type, X11.Atom xa_prop_name) throws X11Exception {
            X11.AtomByReference xa_ret_type_ref = new X11.AtomByReference();
            IntByReference ret_format_ref = new IntByReference();
            NativeLongByReference ret_nitems_ref = new NativeLongByReference();
            NativeLongByReference ret_bytes_after_ref = new NativeLongByReference();
            PointerByReference ret_prop_ref = new PointerByReference();

            NativeLong long_offset = new NativeLong(0);
            NativeLong long_length = new NativeLong(MAX_PROPERTY_VALUE_LEN / 4);

            /* MAX_PROPERTY_VALUE_LEN / 4 explanation (XGetWindowProperty manpage):
                                    *
                                    * long_length = Specifies the length in 32-bit multiples of the
                                    *               data to be retrieved.
                                    */
            if (x11.XGetWindowProperty(display.x11Display, x11Window, xa_prop_name, long_offset, long_length, false,
                xa_prop_type, xa_ret_type_ref, ret_format_ref,
                ret_nitems_ref, ret_bytes_after_ref, ret_prop_ref) != X11.Success) {
                String prop_name = x11.XGetAtomName(display.x11Display, xa_prop_name);
                throw new X11Exception("Cannot get " + prop_name + " property.");
            }

            X11.Atom xa_ret_type = xa_ret_type_ref.getValue();
            Pointer ret_prop = ret_prop_ref.getValue();

            if (xa_ret_type == null || xa_prop_type == null ||
                !xa_ret_type.toNative().equals(xa_prop_type.toNative())) {
                x11.XFree(ret_prop);
                String prop_name = x11.XGetAtomName(display.x11Display, xa_prop_name);
                throw new X11Exception("Invalid type of " + prop_name + " property");
            }

            int ret_format = ret_format_ref.getValue();
            long ret_nitems = ret_nitems_ref.getValue().longValue();

            // null terminate the result to make string handling easier
            int nbytes;
            if (ret_format == 32)
                nbytes = Native.LONG_SIZE;
            else if (ret_format == 16)
                nbytes = Native.LONG_SIZE / 2;
            else if (ret_format == 8)
                nbytes = 1;
            else if (ret_format == 0)
                nbytes = 0;
            else
                throw new X11Exception("Invalid return format");
            int length = Math.min((int) ret_nitems * nbytes, MAX_PROPERTY_VALUE_LEN);

            byte[] ret = ret_prop.getByteArray(0, length);

            x11.XFree(ret_prop);
            return ret;
        }

        /**
         * Returns the property value as a byte array.
         *
         * @param xa_prop_type property type
         * @param xa_prop_name property name
         * @return property value as a byte array
         * @throws X11Exception thrown if X11 window errors occurred
         */
        public byte[] getProperty(X11.Atom xa_prop_type, String xa_prop_name) throws X11Exception {
            return getProperty(xa_prop_type, display.getAtom(xa_prop_name));
        }

        public int clientMsg(String msg, int data0, int data1, int data2, int data3, int data4) throws X11Exception {
            return clientMsg(
                msg,
                new NativeLong(data0),
                new NativeLong(data1),
                new NativeLong(data2),
                new NativeLong(data3),
                new NativeLong(data4)
            );
        }

        public int clientMsg(String msg, NativeLong data0, NativeLong data1, NativeLong data2, NativeLong data3, NativeLong data4) throws X11Exception {
            X11.XClientMessageEvent event;
            NativeLong mask = new NativeLong(X11.SubstructureRedirectMask | X11.SubstructureNotifyMask);

            event = new X11.XClientMessageEvent();
            event.type = X11.ClientMessage;
            event.serial = new NativeLong(0);
            event.send_event = 1;
            event.message_type = display.getAtom(msg);
            event.window = x11Window;
            event.format = 32;
            event.data.setType(NativeLong[].class);
            event.data.l[0] = data0;
            event.data.l[1] = data1;
            event.data.l[2] = data2;
            event.data.l[3] = data3;
            event.data.l[4] = data4;

            X11.XEvent e = new X11.XEvent();
            e.setTypedValue(event);

            if (x11.XSendEvent(display.x11Display, display.getRootWindow().x11Window, 0, mask, e) != 0) {
                return X11.Success;
            } else {
                throw new X11Exception("Cannot send " + msg + " event.");
            }
        }

        public String toString() {
            return x11Window.toString();
        }

        public static class Geometry {
            public int x, y, width, height, borderWidth, depth;

            public Geometry(int x, int y, int width, int height, int border_width, int depth) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.borderWidth = border_width;
                this.depth = depth;
            }
        }
    }

    /**
     * General exception which is thrown when an X11 window error occurred.
     */
    public static class X11Exception extends Exception {
        public X11Exception() {
        }

        public X11Exception(String message) {
            super(message);
        }

        public X11Exception(String message, Throwable cause) {
            super(message, cause);
        }

        public X11Exception(Throwable cause) {
            super(cause);
        }
    }
}
