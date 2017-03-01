/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.COPYDATASTRUCT;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.platform.win32.WinUser.WindowProc;

public class Win32WindowMessagesDemo {

	public static final int MSG_CODE = 101;

	public static final int DATA_STRUCT_CODE = 129;

	public static final int MSG_CODE_HOOKED = 711;

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		new Win32WindowMessagesDemo();
	}

	/**
	 * Instantiates 2 windows and make them communicate through windows messages, even complex ones throught
	 * WM_COPYDATA.
	 */
	public Win32WindowMessagesDemo() {
		// Create window 1 named "ping"
		createWindow("ping");

		// let windows create the window before searching its handle.
		sleepCurrThread(4000);
		// Retrieves the created window's handle.
		HWND hwndPing = determineHWNDFromWindowClass("ping");

		// DEMO 1 : sends a simple message to ping window with code MSG_CODE and value 123456.
		LRESULT result = User32.INSTANCE.SendMessage(hwndPing, WinUser.WM_USER, new WPARAM(MSG_CODE), new LPARAM(123456));
		log("User Message sent to " + hwndPing + ", result = " + result);

		// DEMO 2 : send of structured message.
		COPYDATASTRUCT copyDataStruct = createStructuredMessage();
		result = User32.INSTANCE.SendMessage(hwndPing, WinUser.WM_COPYDATA, null /* No current hwnd for this demo */,
		        new LPARAM(copyDataStruct.getPointer().getLong(0)));
		log("COPYDATASTRUCT sent message to " + hwndPing + "(size=" + copyDataStruct.size() + ") code ="
		        + copyDataStruct.dwData);

		// DEMO 3 : hook winproc then send a message to the hooked proc.
		HHOOK hook = hookwinProc(hwndPing);
		result = User32.INSTANCE.SendMessage(hwndPing, WinUser.WM_USER, new WPARAM(MSG_CODE_HOOKED), new LPARAM(654321));
		log("User Message sent to hooked proc " + hwndPing + ", result = " + result);

		// Waits e few moment before shutdown message.
		sleepCurrThread(3000);

		User32.INSTANCE.PostMessage(hwndPing, WinUser.WM_CLOSE, null, null);
		log("WM_CLOSE posted to " + hwndPing);
		
		//Remember to unhook the win proc.
		User32.INSTANCE.UnhookWindowsHookEx(hook);
		log("Unhook done correctly");
	}

	private COPYDATASTRUCT createStructuredMessage() {
		MsgStruct myData = new MsgStruct();
		myData.number = 5;
		myData.message = "Sending a structured message :)";
		myData.write(); // writes to native memory the data structure otherwise nothing is sent...

		// log("Prepared structured content to send : " + myData.toString(true));

		COPYDATASTRUCT copyDataStruct = new COPYDATASTRUCT();
		copyDataStruct.dwData = new ULONG_PTR(DATA_STRUCT_CODE);
		copyDataStruct.cbData = new DWORD(myData.size());
		copyDataStruct.lpData = myData.getPointer();
		copyDataStruct.write(); // writes to native memory the data structure otherwise nothing is sent...
		return copyDataStruct;
	}

	/**
	 * Example of message sent in the copydatastruct.
	 */
	public class MsgStruct extends Structure {

		public MsgStruct() {
			super();
		}

		public MsgStruct(Pointer p) {
			super(p);
			// reads memory effectively.
			read();
		}

		public int number;
		public String message;

		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "number", "message" });
		}
	}

	public void createWindow(final String windowClass) {
		//Runs it in a specific thread because the main thread is blocked in infinite loop otherwise. 
		new Thread(new Runnable() {
			@Override
			public void run() {
				createWindowAndLoop(windowClass);
			}
		}).start();
		log("Window " + windowClass + " created.");
	}

	public void createWindowAndLoop(String windowClass) {
		// define new window class
		HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

		WNDCLASSEX wClass = new WNDCLASSEX();
		wClass.hInstance = hInst;
		wClass.lpfnWndProc = new WindowProc() {

			@Override
			public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
				// log(hwnd + " - received a message : " + uMsg);
				switch (uMsg) {
				case WinUser.WM_CREATE: {
					log(hwnd + " - onCreate: WM_CREATE");
					return new LRESULT(0);
				}
				case WinUser.WM_CLOSE:
					log(hwnd + " WM_CLOSE");
					User32.INSTANCE.DestroyWindow(hwnd);
					return new LRESULT(0);
				case WinUser.WM_DESTROY: {
					log(hwnd + " - on Destroy.");
					User32.INSTANCE.PostQuitMessage(0);
					return new LRESULT(0);
				}
				case WinUser.WM_USER: {
					log(hwnd + " - received a WM_USER message with code : '" + wParam + "' and value : '" + lParam
					        + "'");
					return new LRESULT(0);
				}
				case WinUser.WM_COPYDATA: {

					COPYDATASTRUCT copyDataStruct = new COPYDATASTRUCT(new Pointer(lParam.longValue()));
					ULONG_PTR uMsg1 = copyDataStruct.dwData;
					Pointer lParam1 = copyDataStruct.lpData;
					DWORD wParam1 = copyDataStruct.cbData;
					log(hwnd + " - received a WM_COPYDATA message with code : '" + uMsg1 + "' of size : '" + wParam1
					        + "'");

					switch (uMsg1.intValue()) {
						case DATA_STRUCT_CODE: {
							MsgStruct msg = new MsgStruct(lParam1);
							// log(hwnd + " - received structured content : " + msg.toString(true));
							log(hwnd + " - message is of type MsgStruct with number = " + msg.number + " and message = '"
							        + msg.message + "'");
							assert msg.number == DATA_STRUCT_CODE;
						}
					}
					return new LRESULT(0);
				}
				default:
					return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
				}
			}
		};
		wClass.lpszClassName = windowClass;

		// register window class
		User32.INSTANCE.RegisterClassEx(wClass);
		getLastError();

		// create new window
		HWND hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass,
		        "My hidden helper window, used only to catch the windows events", 0, 0, 0, 0, 0, null, null, hInst,
		        null);

		getLastError();
		log("window sucessfully created! window hwnd: " + hWnd.getPointer().toString());

		MSG msg = new MSG();
		while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0) {
			User32.INSTANCE.TranslateMessage(msg);
			User32.INSTANCE.DispatchMessage(msg);
		}

		User32.INSTANCE.UnregisterClass(windowClass, hInst);
		User32.INSTANCE.DestroyWindow(hWnd);

		log("program exit!");
	}

	public HWND determineHWNDFromWindowClass(String windowClass) {
		CallBackFindWindowHandleByWindowclass cb = new CallBackFindWindowHandleByWindowclass(windowClass);
		User32.INSTANCE.EnumWindows(cb, null);
		return cb.getFoundHwnd();

	}

	private static class CallBackFindWindowHandleByWindowclass implements WNDENUMPROC {

		private HWND found;

		private String windowClass;

		public CallBackFindWindowHandleByWindowclass(String windowClass) {
			this.windowClass = windowClass;
		}

		@Override
		public boolean callback(HWND hWnd, Pointer data) {

			char[] windowText = new char[512];
			User32.INSTANCE.GetClassName(hWnd, windowText, 512);
			String className = Native.toString(windowText);

			if (windowClass.equalsIgnoreCase(className)) {
				// Found handle. No determine root window...
				HWND hWndAncestor = User32.INSTANCE.GetAncestor(hWnd, User32.GA_ROOTOWNER);
				found = hWndAncestor;
				return false;
			}
			return true;
		}

		public HWND getFoundHwnd() {
			return this.found;
		}

	}

	private HHOOK hookwinProc(HWND hwndToHook) {
		HOOKPROC hookProc = new HOOKPROC() {

			/**
			 * Callback method. cf : https://msdn.microsoft.com/en-us/library/windows/desktop/ms644975(v=vs.85).aspx
			 *
			 * nCode [in] : Specifies whether the hook procedure must process the message. If nCode is HC_ACTION, the
			 * hook procedure must process the message. If nCode is less than zero, the hook procedure must pass the
			 * message to the CallNextHookEx function without further processing and must return the value returned by
			 * CallNextHookEx. wParam [in] : Specifies whether the message was sent by the current thread. If the
			 * message was sent by the current thread, it is nonzero; otherwise, it is zero. lParam [in] : A pointer to
			 * a CWPSTRUCT structure that contains details about the message.
			 *
			 */
			@SuppressWarnings("unused") //used by introspection from jna.
			public LRESULT callback(int nCode, WPARAM wParam, LPARAM lParam) {

				if (nCode < 0) {
					return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, lParam);
				}

				try {
					WinUser.CWPSTRUCT cwp = new WinUser.CWPSTRUCT(new Pointer(lParam.longValue()));
					//log(" - received a message in hooked winproc : " + cwp.message.intValue());
					
					switch (cwp.message.intValue()) {
						case WinUser.WM_USER: {
	
							HWND hWndSource = new HWND(new Pointer(cwp.wParam.longValue()));
	
							log(cwp.hwnd + " - Received a message from " + hWndSource + " hooked proc : code= " + cwp.wParam
							        + ", value = " + cwp.lParam);
							assert cwp.wParam.intValue() == MSG_CODE_HOOKED;
							
							return new LRESULT(0);
						}
					// Note : in more complex cases, the message could be structured with copydatastruct containing an
					// effective struct.
					// Read is like so :
					// COPYDATASTRUCT copyDataStruct = new COPYDATASTRUCT(new Pointer(cwp.lParam.longValue()));
					// then read the copy data then the message struct as above...
					}

					// Send message to next hook.
					return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, lParam);
				} catch (Throwable t) {
					t.printStackTrace();
					return new LRESULT(0);
				}
			}
		};

		HINSTANCE hInst = Kernel32.INSTANCE.GetModuleHandle(null);
		int threadtoHook = User32.INSTANCE.GetWindowThreadProcessId(hwndToHook, null);
		// Hook of the wndProc
		return User32.INSTANCE.SetWindowsHookEx(WinUser.WH_CALLWNDPROC, hookProc, hInst, threadtoHook);
	}
	
	/**
	 * Gets the last error.
	 * 
	 * @return the last error
	 */
	public int getLastError() {
		int rc = Kernel32.INSTANCE.GetLastError();

		if (rc != 0)
			log("error: " + rc);

		return rc;
	}

	private void sleepCurrThread(int durationMs) {
		try {
			Thread.sleep(durationMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void log(String message) {
		String currThread = Thread.currentThread().getName();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String currTime = sdf.format(new Date());
		System.out.println(currTime + " [" + currThread + "] " + message);

	}

}
