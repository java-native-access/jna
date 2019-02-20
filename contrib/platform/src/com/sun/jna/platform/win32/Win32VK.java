package com.sun.jna.platform.win32;

/**
 * Windows API Virtual Key codes.
 * 
 * @author Keve M&uuml;ller
 */
public enum Win32VK {
    VK_UNDEFINED(0x00),

    /*
     * Virtual Keys, Standard Set
     */
    VK_LBUTTON(0x01), 
    VK_RBUTTON(0x02), 
    VK_CANCEL(0x03), 
    VK_MBUTTON(0x04),     /* NOT contiguous with L & RBUTTON */

    VK_XBUTTON1(0x05, 0x0500), /* NOT contiguous with L & RBUTTON */
    VK_XBUTTON2(0x06, 0x0500), /* NOT contiguous with L & RBUTTON */

    /*
     * 0x07 : reserved
     */
    VK_RESERVED_07(0x07),

    VK_BACK(0x08),
    VK_TAB(0x09),

    /*
     * 0x0A - 0x0B : reserved
     */
    VK_RESERVED_0A(0x0A),
    VK_RESERVED_0B(0x0B),

    VK_CLEAR(0x0C),
    VK_RETURN(0x0D),

    /*
     * 0x0E - 0x0F : unassigned
     */
    VK_UNASSIGNED_0E(0x0E),
    VK_UNASSIGNED_0F(0x0F),

    VK_SHIFT(0x10), 
    VK_CONTROL(0x11), 
    VK_MENU(0x12), 
    VK_PAUSE(0x13),
    VK_CAPITAL(0x14),

    VK_KANA(0x15), 
    VK_HANGEUL(0x15), /* old name - should be here for compatibility */
    VK_HANGUL(0x15),

    /*
     * 0x16 : unassigned
     */
    VK_UNASSIGNED_16(0x16),

    VK_JUNJA(0x17), 
    VK_FINAL(0x18), 
    VK_HANJA(0x19), 
    VK_KANJI(0x19),

    /*
     * 0x1A : unassigned
     */
    VK_UNASSIGNED_1A(0x1A),

    VK_ESCAPE(0x1B),

    VK_CONVERT(0x1C),
    VK_NONCONVERT(0x1D), 
    VK_ACCEPT(0x1E), 
    VK_MODECHANGE(0x1F),

    VK_SPACE(0x20), 
    VK_PRIOR(0x21), 
    VK_NEXT(0x22), 
    VK_END(0x23), 
    VK_HOME(0x24),
    VK_LEFT(0x25), 
    VK_UP(0x26), 
    VK_RIGHT(0x27), 
    VK_DOWN(0x28), 
    VK_SELECT(0x29),
    VK_PRINT(0x2A), 
    VK_EXECUTE(0x2B), 
    VK_SNAPSHOT(0x2C), 
    VK_INSERT(0x2D),
    VK_DELETE(0x2E), 
    VK_HELP(0x2F),

    /*
     * VK_0 - VK_9 are the same as ASCII '0' - '9' (0x30 - 0x39)
     */
    VK_0(0x30), 
    VK_1(0x31), 
    VK_2(0x32), 
    VK_3(0x33), 
    VK_4(0x34), 
    VK_5(0x35),
    VK_6(0x36), 
    VK_7(0x37), 
    VK_8(0x38), 
    VK_9(0x39),

    /*
     * 0x3A - 0x40 : unassigned
     */
    VK_UNASSIGNED_3A(0x3A), 
    VK_UNASSIGNED_3B(0x3B), 
    VK_UNASSIGNED_3C(0x3C),
    VK_UNASSIGNED_3D(0x3D), 
    VK_UNASSIGNED_3E(0x3E), 
    VK_UNASSIGNED_3F(0x3F),
    VK_UNASSIGNED_40(0x40),

    /*
     * VK_A - VK_Z are the same as ASCII 'A' - 'Z' (0x41 - 0x5A)
     */
    VK_A(0x41), 
    VK_B(0x42), 
    VK_C(0x43), 
    VK_D(0x44), 
    VK_E(0x45), 
    VK_F(0x46),
    VK_G(0x47), 
    VK_H(0x48), 
    VK_I(0x49), 
    VK_J(0x4A), 
    VK_K(0x4B), 
    VK_L(0x4C),
    VK_M(0x4D), 
    VK_N(0x4E), 
    VK_O(0x4F), 
    VK_P(0x50), 
    VK_Q(0x51), 
    VK_R(0x52),
    VK_S(0x53), 
    VK_T(0x54), 
    VK_U(0x55), 
    VK_V(0x56), 
    VK_W(0x57), 
    VK_X(0x58),
    VK_Y(0x59), 
    VK_Z(0x5A),

    /** Left Windows */
    VK_LWIN(0x5B),
    /** Right Windows */
    VK_RWIN(0x5C),
    /** Application */
    VK_APPS(0x5D),

    /*
     * 0x5E : reserved
     */
    VK_RESERVED_5E(0x5E), 
    VK_SLEEP(0x5F),

    VK_NUMPAD0(0x60), 
    VK_NUMPAD1(0x61), 
    VK_NUMPAD2(0x62), 
    VK_NUMPAD3(0x63),
    VK_NUMPAD4(0x64), 
    VK_NUMPAD5(0x65), 
    VK_NUMPAD6(0x66), 
    VK_NUMPAD7(0x67),
    VK_NUMPAD8(0x68), 
    VK_NUMPAD9(0x69), 
    VK_MULTIPLY(0x6A), 
    VK_ADD(0x6B),
    VK_SEPARATOR(0x6C), 
    VK_SUBTRACT(0x6D), 
    VK_DECIMAL(0x6E), 
    VK_DIVIDE(0x6F),
    VK_F1(0x70), 
    VK_F2(0x71), 
    VK_F3(0x72), 
    VK_F4(0x73), 
    VK_F5(0x74),
    VK_F6(0x75), 
    VK_F7(0x76), 
    VK_F8(0x77), 
    VK_F9(0x78), 
    VK_F10(0x79),
    VK_F11(0x7A), 
    VK_F12(0x7B), 
    VK_F13(0x7C), 
    VK_F14(0x7D), 
    VK_F15(0x7E),
    VK_F16(0x7F), 
    VK_F17(0x80), 
    VK_F18(0x81), 
    VK_F19(0x82), 
    VK_F20(0x83),
    VK_F21(0x84), 
    VK_F22(0x85), 
    VK_F23(0x86), 
    VK_F24(0x87),

    /*
     * 0x88 - 0x8F : UI navigation
     */

    VK_NAVIGATION_VIEW(0x88, 0x0604), // reserved
    VK_NAVIGATION_MENU(0x89, 0x0604), // reserved
    VK_NAVIGATION_UP(0x8A, 0x0604), // reserved
    VK_NAVIGATION_DOWN(0x8B, 0x0604), // reserved
    VK_NAVIGATION_LEFT(0x8C, 0x0604), // reserved
    VK_NAVIGATION_RIGHT(0x8D, 0x0604), // reserved
    VK_NAVIGATION_ACCEPT(0x8E, 0x0604), // reserved
    VK_NAVIGATION_CANCEL(0x8F, 0x0604), // reserved

    VK_NUMLOCK(0x90), 
    VK_SCROLL(0x91),

    /*
     * NEC PC-9800 kbd definitions
     */
    VK_OEM_NEC_EQUAL(0x92), // '=' key on numpad

    /*
     * Fujitsu/OASYS kbd definitions
     */
    VK_OEM_FJ_JISHO(0x92), // 'Dictionary' key
    VK_OEM_FJ_MASSHOU(0x93), // 'Unregister word' key
    VK_OEM_FJ_TOUROKU(0x94), // 'Register word' key
    VK_OEM_FJ_LOYA(0x95), // 'Left OYAYUBI' key
    VK_OEM_FJ_ROYA(0x96), // 'Right OYAYUBI' key

    /*
     * 0x97 - 0x9F : unassigned
     */
    VK_UNASSIGNED_97(0x97), 
    VK_UNASSIGNED_98(0x98), 
    VK_UNASSIGNED_99(0x99),
    VK_UNASSIGNED_9A(0x9A), 
    VK_UNASSIGNED_9B(0x9B), 
    VK_UNASSIGNED_9C(0x9C),
    VK_UNASSIGNED_9D(0x9D), 
    VK_UNASSIGNED_9E(0x9E), 
    VK_UNASSIGNED_9F(0x9F),

    /*
     * VK_L* & VK_R* - left and right Alt, Ctrl and Shift virtual keys. Used only as
     * parameters to GetAsyncKeyState() and GetKeyState(). No other API or message
     * will distinguish left and right keys in this way.
     */
    VK_LSHIFT(0xA0), 
    VK_RSHIFT(0xA1), 
    VK_LCONTROL(0xA2), 
    VK_RCONTROL(0xA3),
    VK_LMENU(0xA4), 
    VK_RMENU(0xA5),

    VK_BROWSER_BACK(0xA6, 0x0500), 
    VK_BROWSER_FORWARD(0xA7, 0x0500),
    VK_BROWSER_REFRESH(0xA8, 0x0500), 
    VK_BROWSER_STOP(0xA9, 0x0500),
    VK_BROWSER_SEARCH(0xAA, 0x0500), 
    VK_BROWSER_FAVORITES(0xAB, 0x0500),
    VK_BROWSER_HOME(0xAC, 0x0500),

    VK_VOLUME_MUTE(0xAD, 0x0500), 
    VK_VOLUME_DOWN(0xAE, 0x0500), 
    VK_VOLUME_UP(0xAF, 0x0500),
    VK_MEDIA_NEXT_TRACK(0xB0, 0x0500), 
    VK_MEDIA_PREV_TRACK(0xB1, 0x0500),
    VK_MEDIA_STOP(0xB2, 0x0500), 
    VK_MEDIA_PLAY_PAUSE(0xB3, 0x0500), 
    VK_LAUNCH_MAIL(0xB4, 0x0500),
    VK_LAUNCH_MEDIA_SELECT(0xB5, 0x0500), 
    VK_LAUNCH_APP1(0xB6, 0x0500),
    VK_LAUNCH_APP2(0xB7, 0x0500),

    /*
     * 0xB8 - 0xB9 : reserved
     */
    VK_RESERVED_B8(0xB8), 
    VK_RESERVED_B9(0xB9),

    VK_OEM_1(0xBA), // ';:' for US
    VK_OEM_PLUS(0xBB), // '+' any country
    VK_OEM_COMMA(0xBC), // ',' any country
    VK_OEM_MINUS(0xBD), // '-' any country
    VK_OEM_PERIOD(0xBE), // '.' any country
    VK_OEM_2(0xBF), // '/?' for US
    VK_OEM_3(0xC0), // '`~' for US

    /*
     * 0xC1 - 0xC2 : reserved
     */
    VK_RESERVED_C1(0xC1), 
    VK_RESERVED_C2(0xC2),

    /*
     * 0xC3 - 0xDA : Gamepad input
     */

    VK_GAMEPAD_A(0xC3, 0x0604), // reserved
    VK_GAMEPAD_B(0xC4, 0x0604), // reserved
    VK_GAMEPAD_X(0xC5, 0x0604), // reserved
    VK_GAMEPAD_Y(0xC6, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_SHOULDER(0xC7, 0x0604), // reserved
    VK_GAMEPAD_LEFT_SHOULDER(0xC8, 0x0604), // reserved
    VK_GAMEPAD_LEFT_TRIGGER(0xC9, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_TRIGGER(0xCA, 0x0604), // reserved
    VK_GAMEPAD_DPAD_UP(0xCB, 0x0604), // reserved
    VK_GAMEPAD_DPAD_DOWN(0xCC, 0x0604), // reserved
    VK_GAMEPAD_DPAD_LEFT(0xCD, 0x0604), // reserved
    VK_GAMEPAD_DPAD_RIGHT(0xCE, 0x0604), // reserved
    VK_GAMEPAD_MENU(0xCF, 0x0604), // reserved
    VK_GAMEPAD_VIEW(0xD0, 0x0604), // reserved
    VK_GAMEPAD_LEFT_THUMBSTICK_BUTTON(0xD1, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_THUMBSTICK_BUTTON(0xD2, 0x0604), // reserved
    VK_GAMEPAD_LEFT_THUMBSTICK_UP(0xD3, 0x0604), // reserved
    VK_GAMEPAD_LEFT_THUMBSTICK_DOWN(0xD4, 0x0604), // reserved
    VK_GAMEPAD_LEFT_THUMBSTICK_RIGHT(0xD5, 0x0604), // reserved
    VK_GAMEPAD_LEFT_THUMBSTICK_LEFT(0xD6, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_THUMBSTICK_UP(0xD7, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_THUMBSTICK_DOWN(0xD8, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_THUMBSTICK_RIGHT(0xD9, 0x0604), // reserved
    VK_GAMEPAD_RIGHT_THUMBSTICK_LEFT(0xDA, 0x0604), // reserved

    VK_OEM_4(0xDB), // '[{' for US
    VK_OEM_5(0xDC), // '\|' for US
    VK_OEM_6(0xDD), // ']}' for US
    VK_OEM_7(0xDE), // ''"' for US
    VK_OEM_8(0xDF),

    /*
     * 0xE0 : reserved
     */
    VK_RESERVED_E0(0xE0),

    /*
     * Various extended or enhanced keyboards
     */
    VK_OEM_AX(0xE1), // 'AX' key on Japanese AX kbd
    VK_OEM_102(0xE2), // "<>" or "\|" on RT 102-key kbd.
    VK_ICO_HELP(0xE3), // Help key on ICO
    VK_ICO_00(0xE4), // 00 key on ICO, produces "00"

    VK_PROCESSKEY(0xE5, 0x0400),

    VK_ICO_CLEAR(0xE6),

    VK_PACKET(0xE7, 0x0500),

    /*
     * 0xE8 : unassigned
     */
    VK_UNASSIGNED_E8(0xE8),

    /*
     * Nokia/Ericsson definitions
     */
    VK_OEM_RESET(0xE9), 
    VK_OEM_JUMP(0xEA), 
    VK_OEM_PA1(0xEB), 
    VK_OEM_PA2(0xEC),
    VK_OEM_PA3(0xED), 
    VK_OEM_WSCTRL(0xEE), 
    VK_OEM_CUSEL(0xEF), 
    VK_OEM_ATTN(0xF0),
    VK_OEM_FINISH(0xF1), 
    VK_OEM_COPY(0xF2), 
    VK_OEM_AUTO(0xF3), 
    VK_OEM_ENLW(0xF4),
    VK_OEM_BACKTAB(0xF5),

    VK_ATTN(0xF6), 
    VK_CRSEL(0xF7), 
    VK_EXSEL(0xF8), 
    VK_EREOF(0xF9), 
    VK_PLAY(0xFA),
    VK_ZOOM(0xFB), 
    VK_NONAME(0xFC), 
    VK_PA1(0xFD), 
    VK_OEM_CLEAR(0xFE),
    
    /*
     * 0xFF : reserved
     */
    VK_RESERVED_FF(0xFF);

    /**
     * The code value.
     */
    public final int code;
    /**
     * This VK constant was introduced in this WinNT version.
     */
    public final int introducedVersion;

    private Win32VK(int code, int introducedVersion) {
        this.code = code;
        this.introducedVersion = introducedVersion;
    }

    private Win32VK(int code) {
        this(code, 0);
    }

    /**
     * This will return the first of the multiple VK constants mapped to the same
     * value. First as defined in the order of the header file listing the
     * constants.
     * 
     * @param code the code value.
     * @return the VK enum instance.
     */
    public static Win32VK fromValue(final int code) {
        for (Win32VK vk : Win32VK.values()) {
            if (vk.code == code) {
                return vk;
            }
        }
        throw new IllegalArgumentException(String.format("No mapping for %02x", code));
    }
}