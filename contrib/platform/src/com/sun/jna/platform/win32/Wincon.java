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
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Ported from Wincon.h.
 * @author lgoldstein
 */
public interface Wincon {
    /**
     * Allocates a new console for the calling process.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms681944(v=vs.85).aspx">AllocConsole documentation</a>
     */
    boolean AllocConsole();

    /**
     * Detaches the calling process from its console
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683150(v=vs.85).aspx">FreeConsole documentation</a>
     */
    boolean FreeConsole();

    int ATTACH_PARENT_PROCESS=(-1);
    /**
     * Attaches the calling process to the console of the specified process
     * @param dwProcessId The identifier of the process whose console is to
     * be used. Can be either the process ID or the special {@link #ATTACH_PARENT_PROCESS}
     * value to indicate the console of the parent of the current process.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms681952(v=vs.85).aspx">AttachConsole documentation</a>
     */
    boolean AttachConsole(int dwProcessId);

    /**
     * Flushes the console input buffer. All input records currently in the input
     * buffer are discarded.
     * @param hConsoleInput A handle to the console input buffer. The handle must
     * have the GENERIC_WRITE access right.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683147(v=vs.85).aspx">FlushConsoleInputBuffer documentation</a>
     */
    boolean FlushConsoleInputBuffer(HANDLE hConsoleInput);

    /* Events to be sent by GenerateConsoleCtrlEvent */
    int CTRL_C_EVENT=0;
    int CTRL_BREAK_EVENT=1;

    /**
     * Sends a specified signal to a console process group that shares the console
     * associated with the calling process.
     * @param dwCtrlEvent The type of signal to be generated.
     * @param dwProcessGroupId The identifier of the process group to receive the signal
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683155(v=vs.85).aspx">GenerateConsoleCtrlEvent documentation</a>
     */
    boolean GenerateConsoleCtrlEvent(int dwCtrlEvent, int dwProcessGroupId);

    /**
     * @return Code that identifies the code page
     * @see <a href="https://msdn.microsoft.com/en-us/library/dd317756(v=vs.85).aspx">Code page identifiers</a>
     */
    int GetConsoleCP();

    /**
     * @param wCodePageID The identifier of the code page to be set
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms686013(v=vs.85).aspx">SetConsoleCP documentation</a>
     */
    boolean SetConsoleCP(int wCodePageID);

    /**
     * @return Retrieves the output code page used by the console associated with the calling process.
     * @see <a href="https://msdn.microsoft.com/en-us/library/dd317756(v=vs.85).aspx">Code page identifiers</a>
     */
    int GetConsoleOutputCP();

    /**
     * @param wCodePageID The output code page used by the console associated with the calling process.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms686036(v=vs.85).aspx">SetConsoleOutputCP documentation</a>
     */
    boolean SetConsoleOutputCP(int wCodePageID);

    /**
     * @return The window handle used by the console associated with the calling process
     */
    HWND GetConsoleWindow();

    /**
     * @param hConsoleInput A handle to the console input buffer. The handle must
     * have the GENERIC_READ access right
     * @param lpcNumberOfEvents A  pointer to a variable that receives the number
     * of unread input records in the console's input buffer
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683207(v=vs.85).aspx">GetNumberOfConsoleInputEvents documentation</a>
     */
    boolean GetNumberOfConsoleInputEvents(HANDLE hConsoleInput, IntByReference lpcNumberOfEvents);

    /**
     * @param lpNumberOfMouseButtons A pointer to a variable that receives the number
     * of mouse buttons
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683208(v=vs.85).aspx">GetNumberOfConsoleMouseButtons documentation</a>
     */
    boolean GetNumberOfConsoleMouseButtons(IntByReference lpNumberOfMouseButtons);

    /* The values to use for Get/SetStdHandle */
    int STD_INPUT_HANDLE=(-10);
    int STD_OUTPUT_HANDLE=(-11);
    int STD_ERROR_HANDLE=(-12);

    /**
     * @param nStdHandle The standard device identifier
     * @return A handle to the specified standard device (standard input, output, or error)
     */
    HANDLE GetStdHandle(int nStdHandle);

    /**
     * @param nStdHandle The standard device identifier
     * @param hHandle The handle for the standard device
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms686244(v=vs.85).aspx">SetStdHandle documentation</a>
     */
    boolean SetStdHandle(int nStdHandle, HANDLE hHandle);

    /* console mode values */
    int CONSOLE_FULLSCREEN=1;
    int CONSOLE_FULLSCREEN_HARDWARE=2;

    /**
     * Retrieves the display mode of the current console
     * @param lpModeFlags The display mode of the console
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683164(v=vs.85).aspx">GetConsoleDisplayMode documentation</a>
     */
    boolean GetConsoleDisplayMode(IntByReference lpModeFlags);

    /* console modes used by Get/SetConsoleMode */
    int ENABLE_PROCESSED_INPUT=0x0001;
    int ENABLE_LINE_INPUT=0x0002;
    int ENABLE_ECHO_INPUT=0x0004;
    int ENABLE_WINDOW_INPUT=0x0008;
    int ENABLE_MOUSE_INPUT=0x0010;
    int ENABLE_INSERT_MODE=0x0020;
    int ENABLE_QUICK_EDIT_MODE=0x0040;
    int ENABLE_EXTENDED_FLAGS=0x0080;
    int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
    int DISABLE_NEWLINE_AUTO_RETURN = 0x0008;
    int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

    /* If the hConsoleHandle parameter is a screen buffer handle, the mode
     * can be one or more of the following values
     */
    int ENABLE_PROCESSED_OUTPUT=0x0001;
    int ENABLE_WRAP_AT_EOL_OUTPUT=0x0002;

    /**
     * @param hConsoleHandle A handle to the console input buffer or the console
     * screen buffer. The handle must have the GENERIC_READ access right
     * @param lpMode A pointer to a variable that receives the current mode of
     * the specified buffer
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms683167.aspx">GetConsoleMode documentation</a>
     */
    boolean GetConsoleMode(HANDLE hConsoleHandle, IntByReference lpMode);

    /**
     * @param hConsoleHandle A handle to the console input buffer or a
     * console screen buffer. The handle must have the GENERIC_READ access right
     * @param dwMode The input or output mode mask to be set
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms686033.aspx">SetConsoleMode documentation</a>
     */
    boolean SetConsoleMode(HANDLE hConsoleHandle, int dwMode);

    int MAX_CONSOLE_TITLE_LENGTH=64 * 1024;

    /**
     * @param lpConsoleTitle A pointer to a buffer that receives a null-terminated
     * string containing the title. If the buffer is too small to store the title,
     * the function stores as many characters of the title as will fit in the buffer,
     * ending with a null terminator. <B>Note:</B> use {@link Native#toString(char[])}
     * to convert it to a {@link String} value
     * @param nSize The size of the buffer pointed to by the lpConsoleTitle parameter,
     * in characters.
     * @return If the function succeeds, the return value is the length of the console
     * window's title, in characters. If the function fails, the return value is zero
     * and {@code GetLastError} returns the error code.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683174(v=vs.85).aspx">GetConsoleTitle documentation</a>
     */
    int GetConsoleTitle(char[] lpConsoleTitle, int nSize);

    /**
     * @param lpConsoleTitle A pointer to a buffer that receives a null-terminated
     * string containing the original title. <B>Note:</B> use {@link Native#toString(char[])}
     * to convert it to a {@link String} value
     * @param nSize The size of the lpConsoleTitle buffer, in characters
     * @return If the function succeeds, the return value is the length of the
     * string copied to the buffer, in characters. If the buffer is not large enough
     * to store the title, the return value is zero and {@code GetLastError} returns
     * {@code ERROR_SUCCESS}. If the function fails, the return value is zero
     * and {@code GetLastError} returns the error code.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms683168(v=vs.85).aspx">GetConsoleOriginalTitle documentation</a>
     */
    int GetConsoleOriginalTitle(char[] lpConsoleTitle, int nSize);

    /**
     * @param lpConsoleTitle The string to be displayed in the title bar of the console window.
     * The total size must be less than {@link #MAX_CONSOLE_TITLE_LENGTH}.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms686050(v=vs.85).aspx">SetConsoleTitle documentation</a>
     */
    boolean SetConsoleTitle(String lpConsoleTitle);

    /**
     * Retrieves information about the specified console screen buffer.
     * @param hConsoleOutput A handle to the console screen buffer.
     * @param lpConsoleScreenBufferInfo A pointer to a CONSOLE_SCREEN_BUFFER_INFO structure that receives the console screen buffer information.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://docs.microsoft.com/en-us/windows/console/getconsolescreenbufferinfo">GetConsoleScreenBufferInfo documentation</a>
     */
    boolean GetConsoleScreenBufferInfo(HANDLE hConsoleOutput, CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);

    /**
     * Reads data from a console input buffer and removes it from the buffer.
     * @param hConsoleInput A handle to the console input buffer.
     * @param lpBuffer A pointer to an array of INPUT_RECORD structures that receives the input buffer data.
     * @param nLength The size of the array pointed to by the lpBuffer parameter, in array elements.
     * @param lpNumberOfEventsRead A pointer to a variable that receives the number of input records read.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://docs.microsoft.com/en-us/windows/console/readconsoleinput">ReadConsoleInput documentation</a>
     */
    boolean ReadConsoleInput(HANDLE hConsoleInput, INPUT_RECORD[] lpBuffer, int nLength, IntByReference lpNumberOfEventsRead);

    /**
     * Writes a character string to a console screen buffer beginning at the current cursor location.
     * @param hConsoleOutput A handle to the console screen buffer.
     * @param lpBuffer A pointer to a buffer that contains characters to be written to the console screen buffer.
     * @param nNumberOfCharsToWrite The number of characters to be written.
     * @param lpNumberOfCharsWritten A pointer to a variable that receives the number of characters actually written.
     * @param lpReserved Reserved; must be NULL.
     * @return {@code true} if successful - if {@code false} then use
     * {@code GetLastError()} to get extended error information
     * @see <a href="https://docs.microsoft.com/en-us/windows/console/writeconsole">WriteConsole documentation</a>
     */
    boolean WriteConsole(HANDLE hConsoleOutput, String lpBuffer, int nNumberOfCharsToWrite, IntByReference lpNumberOfCharsWritten, LPVOID lpReserved);

    /**
     * COORD structure
     */
    @FieldOrder({ "X", "Y" })
    public static class COORD extends Structure {

        public short X;
        public short Y;

        @Override
        public String toString() {
            return String.format("COORD(%s,%s)", X, Y);
        }
    }

    /**
     * SMALL_RECT structure
     */
    @FieldOrder({ "Left", "Top", "Right", "Bottom" })
    public static class SMALL_RECT extends Structure {

        public short Left;
        public short Top;
        public short Right;
        public short Bottom;

        @Override
        public String toString() {
            return String.format("SMALL_RECT(%s,%s)(%s,%s)", Left, Top, Right, Bottom);
        }
    }

    /**
     * CONSOLE_SCREEN_BUFFER_INFO structure
     */
    @FieldOrder({ "dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize" })
    public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {

        public COORD dwSize;
        public COORD dwCursorPosition;
        public short wAttributes;
        public SMALL_RECT srWindow;
        public COORD dwMaximumWindowSize;

        @Override
        public String toString() {
            return String.format("CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)", dwSize, dwCursorPosition, wAttributes, srWindow, dwMaximumWindowSize);
        }
    }

    /**
     * INPUT_RECORD structure
     */
    @FieldOrder({ "EventType", "Event" })
    public static class INPUT_RECORD extends Structure {

        public static final short KEY_EVENT = 0x01;
        public static final short MOUSE_EVENT = 0x02;
        public static final short WINDOW_BUFFER_SIZE_EVENT = 0x04;

        public short EventType;
        public Event Event;

        public static class Event extends Union {
            public KEY_EVENT_RECORD KeyEvent;
            public MOUSE_EVENT_RECORD MouseEvent;
            public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
        }

        @Override
        public void read() {
            super.read();
            switch (EventType) {
                case KEY_EVENT:
                    Event.setType("KeyEvent");
                    break;
                case MOUSE_EVENT:
                    Event.setType("MouseEvent");
                    break;
                case WINDOW_BUFFER_SIZE_EVENT:
                    Event.setType("WindowBufferSizeEvent");
                    break;
            }
            Event.read();
        }

        @Override
        public String toString() {
            return String.format("INPUT_RECORD(%s)", EventType);
        }
    }

    /**
     * KEY_EVENT_RECORD structure
     */
    @FieldOrder({ "bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState" })
    public static class KEY_EVENT_RECORD extends Structure {

        public boolean bKeyDown;
        public short wRepeatCount;
        public short wVirtualKeyCode;
        public short wVirtualScanCode;
        public char uChar;
        public int dwControlKeyState;

        @Override
        public String toString() {
            return String.format("KEY_EVENT_RECORD(%s,%s,%s,%s,%s,%s)", bKeyDown, wRepeatCount, wVirtualKeyCode, wVirtualKeyCode, wVirtualScanCode, uChar, dwControlKeyState);
        }
    }

    /**
     * MOUSE_EVENT_RECORD structure
     */
    @FieldOrder({ "dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags" })
    public static class MOUSE_EVENT_RECORD extends Structure {

        public COORD dwMousePosition;
        public int dwButtonState;
        public int dwControlKeyState;
        public int dwEventFlags;

        @Override
        public String toString() {
            return String.format("MOUSE_EVENT_RECORD(%s,%s,%s,%s)", dwMousePosition, dwButtonState, dwControlKeyState, dwEventFlags);
        }
    }

    /**
     * WINDOW_BUFFER_SIZE_RECORD structure
     */
    @FieldOrder({ "dwSize" })
    public static class WINDOW_BUFFER_SIZE_RECORD extends Structure {

        public COORD dwSize;

        @Override
        public String toString() {
            return String.format("WINDOW_BUFFER_SIZE_RECORD(%s)", dwSize);
        }
    }
}
