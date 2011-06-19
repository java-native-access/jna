package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef;

/**
 * Used by SendInput to store information for synthesizing input events such as keystrokes, mouse movement, and mouse
 * clicks.
 */
public class INPUT extends Structure {

    public static final int INPUT_MOUSE = 0;
    public static final int INPUT_KEYBOARD = 1;
    public static final int INPUT_HARDWARE = 2;

    public static class ByReference extends INPUT implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public INPUT() {
        setAlignType(Structure.ALIGN_MSVC);
    }

    public INPUT(Pointer memory) {
        setAlignType(Structure.ALIGN_MSVC);
        useMemory(memory);
        read();
    }

    public WinDef.DWORD type;
    public INPUT_UNION input = new INPUT_UNION();

    public static class INPUT_UNION extends Union {

        public INPUT_UNION() {
        }

        public INPUT_UNION(Pointer memory) {
            useMemory(memory);
            read();
        }

        public MOUSEINPUT mi;
        public KEYBDINPUT ki;
        public HARDWAREINPUT hi;
    }
}
