package com.sun.jna.platform.win32;

/**
 * Windows API Virtual Key codes.
 * 
 * @author Keve M&uuml;ller
 */
public enum Win32VK {
	VK_UNDEFINED(0x00, false),

	/*
	 * Virtual Keys, Standard Set
	 */
	VK_LBUTTON(0x01, false), 
	VK_RBUTTON(0x02, false), 
	VK_CANCEL(0x03, true), 
	VK_MBUTTON(0x04, false), 	/* NOT contiguous with L & RBUTTON */

	VK_XBUTTON1(0x05, false, 0x0500), /* NOT contiguous with L & RBUTTON */
	VK_XBUTTON2(0x06, false, 0x0500), /* NOT contiguous with L & RBUTTON */

	/*
	 * 0x07 : reserved
	 */
	VK_RESERVED_07(0x07, false),

	VK_BACK(0x08, true),
	VK_TAB(0x09, true),

	/*
	 * 0x0A - 0x0B : reserved
	 */
	VK_RESERVED_0A(0x0A, false),
	VK_RESERVED_0B(0x0B, false),

	VK_CLEAR(0x0C, true),
	VK_RETURN(0x0D, true),

	/*
	 * 0x0E - 0x0F : unassigned
	 */
	VK_UNASSIGNED_0E(0x0E, false),
	VK_UNASSIGNED_0F(0x0F, false),

	VK_SHIFT(0x10, false), 
	VK_CONTROL(0x11, false), 
	VK_MENU(0x12, false), 
	VK_PAUSE(0x13, false),
	VK_CAPITAL(0x14, false),

	VK_KANA(0x15, false), 
	VK_HANGEUL(0x15, false), /* old name - should be here for compatibility */
	VK_HANGUL(0x15, false),

	/*
	 * 0x16 : unassigned
	 */
	VK_UNASSIGNED_16(0x16, false),

	VK_JUNJA(0x17, false), 
	VK_FINAL(0x18, false), 
	VK_HANJA(0x19, false), 
	VK_KANJI(0x19, false),

	/*
	 * 0x1A : unassigned
	 */
	VK_UNASSIGNED_1A(0x1A, false),

	VK_ESCAPE(0x1B, true),

	VK_CONVERT(0x1C, false),
	VK_NONCONVERT(0x1D, false), 
	VK_ACCEPT(0x1E, false), 
	VK_MODECHANGE(0x1F, false),

	VK_SPACE(0x20, true), 
	VK_PRIOR(0x21, false), 
	VK_NEXT(0x22, false), 
	VK_END(0x23, false), 
	VK_HOME(0x24, false),
	VK_LEFT(0x25, false), 
	VK_UP(0x26, false), 
	VK_RIGHT(0x27, false), 
	VK_DOWN(0x28, false), 
	VK_SELECT(0x29, true),
	VK_PRINT(0x2A, false), 
	VK_EXECUTE(0x2B, true), 
	VK_SNAPSHOT(0x2C, false), 
	VK_INSERT(0x2D, false),
	VK_DELETE(0x2E, false), 
	VK_HELP(0x2F, false),

	/*
	 * VK_0 - VK_9 are the same as ASCII '0' - '9' (0x30 - 0x39)
	 */
	VK_0(0x30, true), 
	VK_1(0x31, true), 
	VK_2(0x32, true), 
	VK_3(0x33, true), 
	VK_4(0x34, true), 
	VK_5(0x35, true),
	VK_6(0x36, true), 
	VK_7(0x37, true), 
	VK_8(0x38, true), 
	VK_9(0x39, true),

	/*
	 * 0x3A - 0x40 : unassigned
	 */
	VK_UNASSIGNED_3A(0x3A, false), 
	VK_UNASSIGNED_3B(0x3B, false), 
	VK_UNASSIGNED_3C(0x3C, false),
	VK_UNASSIGNED_3D(0x3D, false), 
	VK_UNASSIGNED_3E(0x3E, false), 
	VK_UNASSIGNED_3F(0x3F, false),
	VK_UNASSIGNED_40(0x40, false),

	/*
	 * VK_A - VK_Z are the same as ASCII 'A' - 'Z' (0x41 - 0x5A)
	 */
	VK_A(0x41, true), 
	VK_B(0x42, true), 
	VK_C(0x43, true), 
	VK_D(0x44, true), 
	VK_E(0x45, true), 
	VK_F(0x46, true),
	VK_G(0x47, true), 
	VK_H(0x48, true), 
	VK_I(0x49, true), 
	VK_J(0x4A, true), 
	VK_K(0x4B, true), 
	VK_L(0x4C, true),
	VK_M(0x4D, true), 
	VK_N(0x4E, true), 
	VK_O(0x4F, true), 
	VK_P(0x50, true), 
	VK_Q(0x51, true), 
	VK_R(0x52, true),
	VK_S(0x53, true), 
	VK_T(0x54, true), 
	VK_U(0x55, true), 
	VK_V(0x56, true), 
	VK_W(0x57, true), 
	VK_X(0x58, true),
	VK_Y(0x59, true), 
	VK_Z(0x5A, true),

	/** Left Windows */
	VK_LWIN(0x5B, false),
	/** Right Windows */
	VK_RWIN(0x5C, false),
	/** Application */
	VK_APPS(0x5D, false),

	/*
	 * 0x5E : reserved
	 */
	VK_RESERVED_5E(0x5E, false), 
	VK_SLEEP(0x5F, false),

	VK_NUMPAD0(0x60, true), 
	VK_NUMPAD1(0x61, true), 
	VK_NUMPAD2(0x62, true), 
	VK_NUMPAD3(0x63, true),
	VK_NUMPAD4(0x64, true), 
	VK_NUMPAD5(0x65, true), 
	VK_NUMPAD6(0x66, true), 
	VK_NUMPAD7(0x67, true),
	VK_NUMPAD8(0x68, true), 
	VK_NUMPAD9(0x69, true), 
	VK_MULTIPLY(0x6A, true), 
	VK_ADD(0x6B, true),
	VK_SEPARATOR(0x6C, true), 
	VK_SUBTRACT(0x6D, true), 
	VK_DECIMAL(0x6E, true), 
	VK_DIVIDE(0x6F, true),
	VK_F1(0x70, false), 
	VK_F2(0x71, false), 
	VK_F3(0x72, false), 
	VK_F4(0x73, false), 
	VK_F5(0x74, false),
	VK_F6(0x75, false), 
	VK_F7(0x76, false), 
	VK_F8(0x77, false), 
	VK_F9(0x78, false), 
	VK_F10(0x79, false),
	VK_F11(0x7A, false), 
	VK_F12(0x7B, false), 
	VK_F13(0x7C, false), 
	VK_F14(0x7D, false), 
	VK_F15(0x7E, false),
	VK_F16(0x7F, false), 
	VK_F17(0x80, false), 
	VK_F18(0x81, false), 
	VK_F19(0x82, false), 
	VK_F20(0x83, false),
	VK_F21(0x84, false), 
	VK_F22(0x85, false), 
	VK_F23(0x86, false), 
	VK_F24(0x87, false),

	/*
	 * 0x88 - 0x8F : UI navigation
	 */

	VK_NAVIGATION_VIEW(0x88, false, 0x0604), // reserved
	VK_NAVIGATION_MENU(0x89, false, 0x0604), // reserved
	VK_NAVIGATION_UP(0x8A, false, 0x0604), // reserved
	VK_NAVIGATION_DOWN(0x8B, false, 0x0604), // reserved
	VK_NAVIGATION_LEFT(0x8C, false, 0x0604), // reserved
	VK_NAVIGATION_RIGHT(0x8D, false, 0x0604), // reserved
	VK_NAVIGATION_ACCEPT(0x8E, false, 0x0604), // reserved
	VK_NAVIGATION_CANCEL(0x8F, false, 0x0604), // reserved

	VK_NUMLOCK(0x90, false), 
	VK_SCROLL(0x91, false),

	/*
	 * NEC PC-9800 kbd definitions
	 */
	VK_OEM_NEC_EQUAL(0x92, true), // '=' key on numpad

	/*
	 * Fujitsu/OASYS kbd definitions
	 */
	VK_OEM_FJ_JISHO(0x92, false), // 'Dictionary' key
	VK_OEM_FJ_MASSHOU(0x93, true), // 'Unregister word' key
	VK_OEM_FJ_TOUROKU(0x94, true), // 'Register word' key
	VK_OEM_FJ_LOYA(0x95, true), // 'Left OYAYUBI' key
	VK_OEM_FJ_ROYA(0x96, true), // 'Right OYAYUBI' key

	/*
	 * 0x97 - 0x9F : unassigned
	 */
	VK_UNASSIGNED_97(0x97, false), 
	VK_UNASSIGNED_98(0x98, false), 
	VK_UNASSIGNED_99(0x99, false),
	VK_UNASSIGNED_9A(0x9A, false), 
	VK_UNASSIGNED_9B(0x9B, false), 
	VK_UNASSIGNED_9C(0x9C, false),
	VK_UNASSIGNED_9D(0x9D, false), 
	VK_UNASSIGNED_9E(0x9E, false), 
	VK_UNASSIGNED_9F(0x9F, false),

	/*
	 * VK_L* & VK_R* - left and right Alt, Ctrl and Shift virtual keys. Used only as
	 * parameters to GetAsyncKeyState() and GetKeyState(). No other API or message
	 * will distinguish left and right keys in this way.
	 */
	VK_LSHIFT(0xA0, false), 
	VK_RSHIFT(0xA1, false), 
	VK_LCONTROL(0xA2, false), 
	VK_RCONTROL(0xA3, false),
	VK_LMENU(0xA4, false), 
	VK_RMENU(0xA5, false),

	VK_BROWSER_BACK(0xA6, false, 0x0500), 
	VK_BROWSER_FORWARD(0xA7, false, 0x0500),
	VK_BROWSER_REFRESH(0xA8, false, 0x0500), 
	VK_BROWSER_STOP(0xA9, false, 0x0500),
	VK_BROWSER_SEARCH(0xAA, false, 0x0500), 
	VK_BROWSER_FAVORITES(0xAB, false, 0x0500),
	VK_BROWSER_HOME(0xAC, false, 0x0500),

	VK_VOLUME_MUTE(0xAD, false, 0x0500), 
	VK_VOLUME_DOWN(0xAE, false, 0x0500), 
	VK_VOLUME_UP(0xAF, false, 0x0500),
	VK_MEDIA_NEXT_TRACK(0xB0, false, 0x0500), 
	VK_MEDIA_PREV_TRACK(0xB1, false, 0x0500),
	VK_MEDIA_STOP(0xB2, false, 0x0500), 
	VK_MEDIA_PLAY_PAUSE(0xB3, false, 0x0500), 
	VK_LAUNCH_MAIL(0xB4, false, 0x0500),
	VK_LAUNCH_MEDIA_SELECT(0xB5, false, 0x0500), 
	VK_LAUNCH_APP1(0xB6, false, 0x0500),
	VK_LAUNCH_APP2(0xB7, false, 0x0500),

	/*
	 * 0xB8 - 0xB9 : reserved
	 */
	VK_RESERVED_B8(0xB8, false), 
	VK_RESERVED_B9(0xB9, false),

	VK_OEM_1(0xBA, true), // ';:' for US
	VK_OEM_PLUS(0xBB, true), // '+' any country
	VK_OEM_COMMA(0xBC, true), // ',' any country
	VK_OEM_MINUS(0xBD, true), // '-' any country
	VK_OEM_PERIOD(0xBE, true), // '.' any country
	VK_OEM_2(0xBF, true), // '/?' for US
	VK_OEM_3(0xC0, true), // '`~' for US

	/*
	 * 0xC1 - 0xC2 : reserved
	 */
	VK_RESERVED_C1(0xC1, true), 
	VK_RESERVED_C2(0xC2, true),

	/*
	 * 0xC3 - 0xDA : Gamepad input
	 */

	VK_GAMEPAD_A(0xC3, false, 0x0604), // reserved
	VK_GAMEPAD_B(0xC4, false, 0x0604), // reserved
	VK_GAMEPAD_X(0xC5, false, 0x0604), // reserved
	VK_GAMEPAD_Y(0xC6, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_SHOULDER(0xC7, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_SHOULDER(0xC8, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_TRIGGER(0xC9, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_TRIGGER(0xCA, false, 0x0604), // reserved
	VK_GAMEPAD_DPAD_UP(0xCB, false, 0x0604), // reserved
	VK_GAMEPAD_DPAD_DOWN(0xCC, false, 0x0604), // reserved
	VK_GAMEPAD_DPAD_LEFT(0xCD, false, 0x0604), // reserved
	VK_GAMEPAD_DPAD_RIGHT(0xCE, false, 0x0604), // reserved
	VK_GAMEPAD_MENU(0xCF, false, 0x0604), // reserved
	VK_GAMEPAD_VIEW(0xD0, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_THUMBSTICK_BUTTON(0xD1, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_THUMBSTICK_BUTTON(0xD2, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_THUMBSTICK_UP(0xD3, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_THUMBSTICK_DOWN(0xD4, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_THUMBSTICK_RIGHT(0xD5, false, 0x0604), // reserved
	VK_GAMEPAD_LEFT_THUMBSTICK_LEFT(0xD6, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_THUMBSTICK_UP(0xD7, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_THUMBSTICK_DOWN(0xD8, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_THUMBSTICK_RIGHT(0xD9, false, 0x0604), // reserved
	VK_GAMEPAD_RIGHT_THUMBSTICK_LEFT(0xDA, false, 0x0604), // reserved

	VK_OEM_4(0xDB, true), // '[{' for US
	VK_OEM_5(0xDC, true), // '\|' for US
	VK_OEM_6(0xDD, true), // ']}' for US
	VK_OEM_7(0xDE, true), // ''"' for US
	VK_OEM_8(0xDF, true),

	/*
	 * 0xE0 : reserved
	 */
	VK_RESERVED_E0(0xE0, false),

	/*
	 * Various extended or enhanced keyboards
	 */
	VK_OEM_AX(0xE1, true), // 'AX' key on Japanese AX kbd
	VK_OEM_102(0xE2, true), // "<>" or "\|" on RT 102-key kbd.
	VK_ICO_HELP(0xE3, true), // Help key on ICO
	VK_ICO_00(0xE4, false), // 00 key on ICO, produces "00"

	VK_PROCESSKEY(0xE5, true, 0x0400),

	VK_ICO_CLEAR(0xE6, true),

	VK_PACKET(0xE7, true, 0x0500),

	/*
	 * 0xE8 : unassigned
	 */
	VK_UNASSIGNED_E8(0xE8, false),

	/*
	 * Nokia/Ericsson definitions
	 */
	VK_OEM_RESET(0xE9, true), 
	VK_OEM_JUMP(0xEA, true), 
	VK_OEM_PA1(0xEB, true), 
	VK_OEM_PA2(0xEC, true),
	VK_OEM_PA3(0xED, true), 
	VK_OEM_WSCTRL(0xEE, true), 
	VK_OEM_CUSEL(0xEF, true), 
	VK_OEM_ATTN(0xF0, true),
	VK_OEM_FINISH(0xF1, true), 
	VK_OEM_COPY(0xF2, true), 
	VK_OEM_AUTO(0xF3, true), 
	VK_OEM_ENLW(0xF4, true),
	VK_OEM_BACKTAB(0xF5, true),

	VK_ATTN(0xF6, true), 
	VK_CRSEL(0xF7, true), 
	VK_EXSEL(0xF8, true), 
	VK_EREOF(0xF9, true), 
	VK_PLAY(0xFA, true),
	VK_ZOOM(0xFB, true), 
	VK_NONAME(0xFC, true), 
	VK_PA1(0xFD, true), 
	VK_OEM_CLEAR(0xFE, true),
	
	/*
	 * 0xFF : reserved
	 */
	VK_RESERVED_FF(0xFF, false);

	/**
	 * The code value.
	 */
	public final int code;
	/** This VK may be used to map to a unicode codepoint in a Keyboard Layout. */
	public final boolean mappable;
	/**
	 * This VK contstant was introduced in this WinNT version.
	 */
	public final int introducedVersion;

	private Win32VK(int code, boolean mappable, int introducedVersion) {
		this.code = code;
		this.mappable = mappable;
		this.introducedVersion = introducedVersion;
	}

	private Win32VK(int code, boolean mappable) {
		this(code, mappable, 0);
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