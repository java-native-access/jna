/*
 * Copyright (c) 2013 Ralf Hamberger, Markus Karg, All Rights Reserved
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
import com.sun.jna.Memory;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.RAWINPUTDEVICELIST;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import com.sun.jna.win32.W32StringUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provides convenient usage of functions defined by {@code User32.dll}.
 *
 * @author Ralf HAMBERGER
 * @author Markus KARG (markus[at]headcrashing[dot]eu)
 */
public final class User32Util {
    public static final int registerWindowMessage(final String lpString) {
        final int messageId = User32.INSTANCE.RegisterWindowMessage(lpString);
        if (messageId == 0)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        return messageId;
    }

    public static final HWND createWindow(final String className, final String windowName, final int style, final int x, final int y, final int width,
            final int height, final HWND parent, final HMENU menu, final HINSTANCE instance, final LPVOID param) {
        return User32Util.createWindowEx(0, className, windowName, style, x, y, width, height, parent, menu, instance, param);
    }

    public static final HWND createWindowEx(final int exStyle, final String className, final String windowName, final int style, final int x, final int y,
            final int width, final int height, final HWND parent, final HMENU menu, final HINSTANCE instance, final LPVOID param) {
        final HWND hWnd = User32.INSTANCE
                .CreateWindowEx(exStyle, className, windowName, style, x, y, width, height, parent, menu, instance, param);
        if (hWnd == null)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        return hWnd;
    }

    public static final void destroyWindow(final HWND hWnd) {
        if (!User32.INSTANCE.DestroyWindow(hWnd))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    }

    public static final List<RAWINPUTDEVICELIST> GetRawInputDeviceList() {
        IntByReference puiNumDevices = new IntByReference(0);
        RAWINPUTDEVICELIST placeholder = new RAWINPUTDEVICELIST();
        int cbSize = placeholder.sizeof();
        // first call is with NULL so we query the expected number of devices
        int returnValue = User32.INSTANCE.GetRawInputDeviceList(null, puiNumDevices, cbSize);
        if (returnValue != 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        int deviceCount = puiNumDevices.getValue();
        RAWINPUTDEVICELIST[] records = (RAWINPUTDEVICELIST[]) placeholder.toArray(deviceCount);
        returnValue = User32.INSTANCE.GetRawInputDeviceList(records, puiNumDevices, cbSize);
        if (returnValue == (-1)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        if (returnValue != records.length) {
            throw new IllegalStateException("Mismatched allocated (" + records.length + ") vs. received devices count (" + returnValue + ")");
        }

        return Arrays.asList(records);
    }

    /**
     * Helper class, that runs a windows message loop as a seperate thread.
     *
     * This is intended to be used in conjunction with APIs, that need a
     * spinning message loop. One example for this are the DDE functions, that
     * can only be used if a message loop is present.
     *
     * To enable interaction with the mainloop the MessageLoopThread allows to
     * dispatch callables into the mainloop and let these Callables be invoked
     * on the message thread.
     *
     * This implies, that the Callables should block the loop as short as possible.
     */
    public static class MessageLoopThread extends Thread {

        public class Handler implements InvocationHandler {

            private final Object delegate;

            public Handler(Object delegate) {
                this.delegate = delegate;
            }

            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    return MessageLoopThread.this.runOnThread(new Callable<Object>() {
                        public Object call() throws Exception {
                            return method.invoke(delegate, args);
                        }
                    });
                } catch (InvocationTargetException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof Exception) {
                        StackTraceElement[] hiddenStack = cause.getStackTrace();
                        cause.fillInStackTrace();
                        StackTraceElement[] currentStack = cause.getStackTrace();
                        StackTraceElement[] fullStack = new StackTraceElement[currentStack.length + hiddenStack.length];
                        System.arraycopy(hiddenStack, 0, fullStack, 0, hiddenStack.length);
                        System.arraycopy(currentStack, 0, fullStack, hiddenStack.length, currentStack.length);
                        cause.setStackTrace(fullStack);
                        throw (Exception) cause;
                    } else {
                        throw ex;
                    }
                }
            }
        }

        private volatile int nativeThreadId = 0;
        private volatile long javaThreadId = 0;
        private final List<FutureTask> workQueue = Collections.synchronizedList(new ArrayList<FutureTask>());
        private static long messageLoopId = 0;

        public MessageLoopThread() {
            setName("JNA User32 MessageLoop " + (++messageLoopId));
        }

        @Override
        public void run() {
            MSG msg = new WinUser.MSG();

            // Make sure message loop is prepared
            User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);
            javaThreadId = Thread.currentThread().getId();
            nativeThreadId = Kernel32.INSTANCE.GetCurrentThreadId();

            int getMessageReturn;
            while ((getMessageReturn = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
                if (getMessageReturn != -1) {
                    // Normal processing
                    while (!workQueue.isEmpty()) {
                        try {
                            FutureTask ft = workQueue.remove(0);
                            ft.run();
                        } catch (IndexOutOfBoundsException ex) {
                            break;
                        }
                    }
                    User32.INSTANCE.TranslateMessage(msg);
                    User32.INSTANCE.DispatchMessage(msg);
                } else {
                    // Error case
                    if(getMessageFailed()) {
                        break;
                    }
                }
            }

            while (!workQueue.isEmpty()) {
                workQueue.remove(0).cancel(false);
            }
        }

        public <V> Future<V> runAsync(Callable<V> command) {
            while(nativeThreadId == 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FutureTask<V> futureTask = new FutureTask<V>(command);
            workQueue.add(futureTask);
            User32.INSTANCE.PostThreadMessage(nativeThreadId, WinUser.WM_USER, null, null);
            return futureTask;
        }

        public <V> V runOnThread(Callable<V> callable) throws Exception {
            while (javaThreadId == 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if(javaThreadId == Thread.currentThread().getId()) {
                return callable.call();
            } else {

                Future<V> ft = runAsync(callable);
                try {
                    return ft.get();
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof Exception) {
                        throw (Exception) cause;
                    } else {
                        throw ex;
                    }
                }
            }
        }

        public void exit() {
            User32.INSTANCE.PostThreadMessage(nativeThreadId, WinUser.WM_QUIT, null, null);
        }

        /**
         * The method is called from the thread, that run the message dispatcher,
         * when the call to {@link com.sun.jna.platform.win32.User32#GetMessage}
         * fails (returns {@code -1}).
         *
         * <p>If the method returns {@code true}, the MainLoop is exitted, if it
         * returns {@code false} the mainloop is resumed.</p>
         *
         * <p>Default behavior: The error code is logged to the
         * com.sun.jna.platform.win32.User32Util.MessageLoopThread logger and
         * the main loop exists.
         * </p>
         *
         * @return true if MainLoop should exit, false it it should resume
         */
        protected boolean getMessageFailed() {
            int lastError = Kernel32.INSTANCE.GetLastError();
            Logger.getLogger("com.sun.jna.platform.win32.User32Util.MessageLoopThread")
                    .log(Level.WARNING,
                            "Message loop was interrupted by an error. [lastError: {0}]",
                            lastError);
            return true;
        }
    }

    /**
     * Load a string value from the string table of an executable.
     *
     * @param location the location, eg. %SystemRoot%\system32\input.dll,-5011
     * @return the string located at the designated location
     * @throws UnsupportedEncodingException
     */
    public static String loadString(String location) throws UnsupportedEncodingException {
        int x = location.lastIndexOf(',');
        String moduleName = location.substring(0, x);
        int index = Math.abs(Integer.parseInt(location.substring(x + 1)));
        String path = Kernel32Util.expandEnvironmentStrings(moduleName);
        HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, null, Kernel32.LOAD_LIBRARY_AS_DATAFILE);
        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            Pointer lpBuffer = new Memory(Native.POINTER_SIZE);
            int length = User32.INSTANCE.LoadString(target, index, lpBuffer, 0);
            if (length == 0) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            Pointer resourcePointer = lpBuffer.getPointer(0);
            return W32StringUtil.toString(resourcePointer, 0, length);
        } else {
            // ANSI version of LoadString does not properly handle cchBufferMax=0
            // See https://social.msdn.microsoft.com/Forums/windowsdesktop/en-US/8d8c5382-1867-460a-a18f-70dc425ffe2f/
            int cchBufferMax = 0;
            Memory lpBuffer;
            int length;
            do {
                cchBufferMax += 256;
                lpBuffer = W32StringUtil.allocateBuffer(cchBufferMax);
                length = User32.INSTANCE.LoadString(target, index, lpBuffer, cchBufferMax);
                if (length == 0) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            } while (lpBuffer.size() - 1  /* w/o the '\0' */ == length);

            return W32StringUtil.toString(lpBuffer);
        }
    }

    /**
     * Set of {@link Win32VK} members that can be mapped to a UniCode code point via
     * a keyboard layout.
     */
    public static final EnumSet<Win32VK> WIN32VK_MAPPABLE = EnumSet.of(Win32VK.VK_BACK, Win32VK.VK_TAB,
            Win32VK.VK_CLEAR, Win32VK.VK_RETURN, Win32VK.VK_ESCAPE, Win32VK.VK_SPACE, Win32VK.VK_SELECT,
            Win32VK.VK_EXECUTE, Win32VK.VK_0, Win32VK.VK_1, Win32VK.VK_2, Win32VK.VK_3, Win32VK.VK_4, Win32VK.VK_5,
            Win32VK.VK_6, Win32VK.VK_7, Win32VK.VK_8, Win32VK.VK_9, Win32VK.VK_A, Win32VK.VK_B, Win32VK.VK_C,
            Win32VK.VK_D, Win32VK.VK_E, Win32VK.VK_F, Win32VK.VK_G, Win32VK.VK_H, Win32VK.VK_I, Win32VK.VK_J,
            Win32VK.VK_K, Win32VK.VK_L, Win32VK.VK_M, Win32VK.VK_N, Win32VK.VK_O, Win32VK.VK_P, Win32VK.VK_Q,
            Win32VK.VK_R, Win32VK.VK_S, Win32VK.VK_T, Win32VK.VK_U, Win32VK.VK_V, Win32VK.VK_W, Win32VK.VK_X,
            Win32VK.VK_Y, Win32VK.VK_Z, Win32VK.VK_NUMPAD0, Win32VK.VK_NUMPAD1, Win32VK.VK_NUMPAD2, Win32VK.VK_NUMPAD3,
            Win32VK.VK_NUMPAD4, Win32VK.VK_NUMPAD5, Win32VK.VK_NUMPAD6, Win32VK.VK_NUMPAD7, Win32VK.VK_NUMPAD8,
            Win32VK.VK_NUMPAD9, Win32VK.VK_MULTIPLY, Win32VK.VK_ADD, Win32VK.VK_SEPARATOR, Win32VK.VK_SUBTRACT,
            Win32VK.VK_DECIMAL, Win32VK.VK_DIVIDE, Win32VK.VK_OEM_NEC_EQUAL, Win32VK.VK_OEM_FJ_MASSHOU,
            Win32VK.VK_OEM_FJ_TOUROKU, Win32VK.VK_OEM_FJ_LOYA, Win32VK.VK_OEM_FJ_ROYA, Win32VK.VK_OEM_1,
            Win32VK.VK_OEM_PLUS, Win32VK.VK_OEM_COMMA, Win32VK.VK_OEM_MINUS, Win32VK.VK_OEM_PERIOD, Win32VK.VK_OEM_2,
            Win32VK.VK_OEM_3, Win32VK.VK_RESERVED_C1, Win32VK.VK_RESERVED_C2, Win32VK.VK_OEM_4, Win32VK.VK_OEM_5,
            Win32VK.VK_OEM_6, Win32VK.VK_OEM_7, Win32VK.VK_OEM_8, Win32VK.VK_OEM_AX, Win32VK.VK_OEM_102,
            Win32VK.VK_ICO_HELP, Win32VK.VK_PROCESSKEY, Win32VK.VK_ICO_CLEAR, Win32VK.VK_PACKET, Win32VK.VK_OEM_RESET,
            Win32VK.VK_OEM_JUMP, Win32VK.VK_OEM_PA1, Win32VK.VK_OEM_PA2, Win32VK.VK_OEM_PA3, Win32VK.VK_OEM_WSCTRL,
            Win32VK.VK_OEM_CUSEL, Win32VK.VK_OEM_ATTN, Win32VK.VK_OEM_FINISH, Win32VK.VK_OEM_COPY, Win32VK.VK_OEM_AUTO,
            Win32VK.VK_OEM_ENLW, Win32VK.VK_OEM_BACKTAB, Win32VK.VK_ATTN, Win32VK.VK_CRSEL, Win32VK.VK_EXSEL,
            Win32VK.VK_EREOF, Win32VK.VK_PLAY, Win32VK.VK_ZOOM, Win32VK.VK_NONAME, Win32VK.VK_PA1,
            Win32VK.VK_OEM_CLEAR);
}
