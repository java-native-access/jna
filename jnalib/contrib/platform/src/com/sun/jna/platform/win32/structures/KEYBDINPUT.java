package com.sun.jna.platform.win32.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;

/**
 * Contains information about a simulated keyboard event.
 */
public class KEYBDINPUT extends Structure {

    /**
     * If specified, the scan code was preceded by a prefix byte that has the value 0xE0 (224).
     */
    public static final int KEYEVENTF_EXTENDEDKEY = 0x0001;

    /**
     * If specified, the key is being released. If not specified, the key is being pressed.
     */
    public static final int KEYEVENTF_KEYUP = 0x0002;

    /**
     * If specified, the system synthesizes a VK_PACKET keystroke. The wVk parameter must be zero. This flag can only be
     * combined with the KEYEVENTF_KEYUP flag. For more information, see the Remarks section.
     */
    public static final int KEYEVENTF_UNICODE = 0x0004;

    /**
     * If specified, wScan identifies the key and wVk is ignored.
     */
    public static final int KEYEVENTF_SCANCODE = 0x0008;
    
    public static class ByReference extends KEYBDINPUT implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(Pointer memory) {
            super(memory);
        }
    }

    public KEYBDINPUT() {
        setAlignType(Structure.ALIGN_MSVC);
    }

    public KEYBDINPUT(Pointer memory) {
        setAlignType(Structure.ALIGN_MSVC);
        useMemory(memory);
        read();
    }

    /**
     * A virtual-key code. The code must be a value in the range 1 to 254. If the dwFlags member specifies
     * KEYEVENTF_UNICODE, wVk must be 0.
     */
    public WinDef.WORD wVk;

    /**
     * A hardware scan code for the key. If dwFlags specifies KEYEVENTF_UNICODE, wScan specifies a Unicode character
     * which is to be sent to the foreground application.
     */
    public WinDef.WORD wScan;

    /**
     * Specifies various aspects of a keystroke. This member can be certain combinations of the following values.
     */
    public WinDef.DWORD dwFlags;

    /**
     * The time stamp for the event, in milliseconds. If this parameter is zero, the system will provide its own time
     * stamp.
     */
    public WinDef.DWORD time;

    /**
     * An additional value associated with the keystroke. Use the GetMessageExtraInfo function to obtain this
     * information.
     */
    public BaseTSD.ULONG_PTR dwExtraInfo;
}
