/* Copyright (c) 2018 Roshan Muralidharan, All Rights Reserved
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
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.WinCrypt.*;

/**
 * Cryptui.dll Interface.
 * @author roshan[dot]muralidharan[at]cerner[dot]com
 */
public interface Cryptui extends StdCallLibrary {

	Cryptui INSTANCE = (Cryptui) Native.loadLibrary("Cryptui", Cryptui.class, W32APIOptions.UNICODE_OPTIONS);

	/**
	 * The CryptUIDlgSelectCertificateFromStore function displays a dialog box that
	 * allows the selection of a certificate from a specified store.
	 * 
	 * @param hCertStore
	 *            Handle of the certificate store to be searched.
	 * @param hwnd
	 *            Handle of the window for the display. If NULL, defaults to the
	 *            desktop window.
	 * @param pwszTitle
	 *            String used as the title of the dialog box. If NULL, the default
	 *            title, "Select Certificate," is used.
	 * @param pwszDisplayString
	 *            Text statement in the selection dialog box. If NULL, the default
	 *            phrase, "Select a certificate you want to use," is used.
	 * @param dwDontUseColumn
	 *            Flags that can be combined to exclude columns of the display.
	 * @param dwFlags
	 *            Currently not used and should be set to 0.
	 * @param pvReserved
	 *            Reserved for future use.
	 * @return Returns a pointer to the selected certificate context. If no
	 *         certificate was selected, NULL is returned. When you have finished
	 *         using the certificate, free the certificate context by calling the
	 *         CertFreeCertificateContext function.
	 */
	CERT_CONTEXT.ByReference CryptUIDlgSelectCertificateFromStore(HCERTSTORE hCertStore, HWND hwnd, String pwszTitle,
			String pwszDisplayString, int dwDontUseColumn, int dwFlags, PointerType pvReserved);

}
