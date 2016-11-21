/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinRas.RASCONN;
import com.sun.jna.platform.win32.WinRas.RASCREDENTIALS;
import com.sun.jna.platform.win32.WinRas.RASDIALPARAMS;
import com.sun.jna.platform.win32.WinRas.RASENTRY;
import com.sun.jna.platform.win32.WinRas.RASPPPIP;
import com.sun.jna.platform.win32.WinRas.RasDialFunc2;
import com.sun.jna.ptr.IntByReference;

/**
 * Rasapi32 utility API.
 */
@SuppressWarnings("unchecked")
public abstract class Rasapi32Util {
	private static final int RASP_PppIp = 0x8021;

	private static Object phoneBookMutex = new Object();
	@SuppressWarnings("rawtypes")
	public static final Map CONNECTION_STATE_TEXT = new HashMap();

	static {
		CONNECTION_STATE_TEXT.put(0, "Opening the port...");
		CONNECTION_STATE_TEXT.put(1, "Port has been opened successfully");
		CONNECTION_STATE_TEXT.put(2, "Connecting to the device...");
		CONNECTION_STATE_TEXT.put(3, "The device has connected successfully.");
		CONNECTION_STATE_TEXT.put(4, "All devices in the device chain have successfully connected.");
		CONNECTION_STATE_TEXT.put(5, "Verifying the user name and password...");
		CONNECTION_STATE_TEXT.put(6, "An authentication event has occurred.");
		CONNECTION_STATE_TEXT.put(7, "Requested another validation attempt with a new user.");
		CONNECTION_STATE_TEXT.put(8, "Server has requested a callback number.");
		CONNECTION_STATE_TEXT.put(9, "The client has requested to change the password");
		CONNECTION_STATE_TEXT.put(10, "Registering your computer on the network...");
		CONNECTION_STATE_TEXT.put(11, "The link-speed calculation phase is starting...");
		CONNECTION_STATE_TEXT.put(12, "An authentication request is being acknowledged.");
		CONNECTION_STATE_TEXT.put(13, "Reauthentication (after callback) is starting.");
		CONNECTION_STATE_TEXT.put(14, "The client has successfully completed authentication.");
		CONNECTION_STATE_TEXT.put(15, "The line is about to disconnect for callback.");
		CONNECTION_STATE_TEXT.put(16, "Delaying to give the modem time to reset for callback.");
		CONNECTION_STATE_TEXT.put(17, "Waiting for an incoming call from server.");
		CONNECTION_STATE_TEXT.put(18, "Projection result information is available.");
		CONNECTION_STATE_TEXT.put(19, "User authentication is being initiated or retried.");
		CONNECTION_STATE_TEXT.put(20, "Client has been called back and is about to resume authentication.");
		CONNECTION_STATE_TEXT.put(21, "Logging on to the network...");
		CONNECTION_STATE_TEXT.put(22, "Subentry has been connected");
		CONNECTION_STATE_TEXT.put(23, "Subentry has been disconnected");
		CONNECTION_STATE_TEXT.put(4096, "Terminal state supported by RASPHONE.EXE.");
		CONNECTION_STATE_TEXT.put(4097, "Retry authentication state supported by RASPHONE.EXE.");
		CONNECTION_STATE_TEXT.put(4098, "Callback state supported by RASPHONE.EXE.");
		CONNECTION_STATE_TEXT.put(4099, "Change password state supported by RASPHONE.EXE.");
		CONNECTION_STATE_TEXT.put(4100, "Displaying authentication UI");
		CONNECTION_STATE_TEXT.put(8192, "Connected to remote server successfully");
		CONNECTION_STATE_TEXT.put(8193, "Disconnected");
	}

	/**
	 * Exceptions
	 */
	public static class Ras32Exception extends RuntimeException {

		private static final long serialVersionUID = 1L;

		private final int code;

		/**
		 * Returns the error code of the error.
		 * @return
		 *  Error code.
		 */
		public int getCode() {
			return code;
		}

		/**
		 * New Win32 exception from an error code, usually obtained from GetLastError.
		 * @param code
		 *  Error code.
		 */
		public Ras32Exception(int code) {
			super(getRasErrorString(code));
			this.code = code;
		}
	}

	/**
	 * Get the RAS error description
	 * @param code the error code
	 * @return the RAS description
	 */
	public static String getRasErrorString(int code) {
		char[] msg = new char[1024];
		int err = Rasapi32.INSTANCE.RasGetErrorString(code, msg, msg.length);
		if (err != WinError.ERROR_SUCCESS) return "Unknown error " + code;
		int len = 0;
		for (; len < msg.length; len++) if (msg[len] == 0) break;
		return new String(msg, 0, len);
	}

	/**
	 * Translate the connection status value to text
	 * @param connStatus the connection status
	 * @return the descriptive text
	 */
	public static String getRasConnectionStatusText(int connStatus) {
		if (!CONNECTION_STATE_TEXT.containsKey(connStatus)) return Integer.toString(connStatus);
		return (String)CONNECTION_STATE_TEXT.get(connStatus);
	}

	/**
	 * Return a RAS connection by name
	 * @param connName the connection name
	 * @return the RAS connection structure
	 * @throws Ras32Exception errors
	 */
	public static HANDLE getRasConnection(String connName) throws Ras32Exception {
		// size the array needed
		IntByReference lpcb  = new IntByReference(0);
		IntByReference lpcConnections  = new IntByReference();
		int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
		if (err != WinError.ERROR_SUCCESS && err != WinRas.ERROR_BUFFER_TOO_SMALL) throw new Ras32Exception(err);
		if (lpcb.getValue() == 0) return null;

		// get the connections
		RASCONN[] connections = new RASCONN[lpcConnections.getValue()];
		for (int i = 0; i < lpcConnections.getValue(); i++) connections[i] = new RASCONN();
		lpcb  = new IntByReference(connections[0].dwSize * lpcConnections.getValue());
		err = Rasapi32.INSTANCE.RasEnumConnections(connections, lpcb, lpcConnections);
		if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);

		// find connection
		for (int i = 0; i < lpcConnections.getValue(); i++) {
			if (new String(connections[i].szEntryName).equals(connName)) return connections[i].hrasconn;
		}
		return null;
	}

	/**
	 * Hangup a connection by name
	 * @param connName the connection name
	 * @throws Ras32Exception errors
	 */
	public static void hangupRasConnection(String connName) throws Ras32Exception {
		HANDLE hrasConn = getRasConnection(connName);
		if (hrasConn == null) return;
		int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
		if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
	}

	/**
	 * Hangup a connection
	 * @param hrasConn the connection
	 * @throws Ras32Exception errors
	 */
	public static void hangupRasConnection(HANDLE hrasConn) throws Ras32Exception {
		if (hrasConn == null) return;
		int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
		if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
	}

	/**
	 * Get the connection's IP projection
	 * @param hrasConn the RAS connection handle
	 * @return the IP projection
	 * @throws Ras32Exception errors
	 */
	public static RASPPPIP getIPProjection(HANDLE hrasConn) throws Ras32Exception {
		RASPPPIP pppIpProjection = new RASPPPIP();
		IntByReference lpcb = new IntByReference(pppIpProjection.size());
		pppIpProjection.write();
		int err = Rasapi32.INSTANCE.RasGetProjectionInfo(hrasConn, RASP_PppIp, pppIpProjection.getPointer(), lpcb);
		if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
		pppIpProjection.read();
		return pppIpProjection;
	}

	/**
	 * Return the phone book entry.
	 * @param entryName the entry name
	 * @return the RAS entry
	 * @throws Ras32Exception any errors
	 */
	public static RASENTRY.ByReference getPhoneBookEntry(String entryName) throws Ras32Exception {
		synchronized (phoneBookMutex) {
			RASENTRY.ByReference rasEntry = new RASENTRY.ByReference();
			IntByReference lpdwEntryInfoSize = new IntByReference(rasEntry.size());
			int err = Rasapi32.INSTANCE.RasGetEntryProperties(null, entryName, rasEntry, lpdwEntryInfoSize, null, null);
			if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
			return rasEntry;
		}
	}

	/**
	 * Set a phone book entry
	 * @param entryName the phone book entry name
	 * @param rasEntry the entry parameters
	 * @throws Ras32Exception errors
	 */
	public static void setPhoneBookEntry(String entryName, RASENTRY.ByReference rasEntry) throws Ras32Exception {
		synchronized (phoneBookMutex) {
			int err = Rasapi32.INSTANCE.RasSetEntryProperties(null, entryName, rasEntry, rasEntry.size(), null, 0);
			if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
		}
	}

	/**
	 * get a phone book entry's dialing parameters
	 * @param entryName the phone book entry name
	 * @return the entry's dialing parameters parameters
	 * @throws Ras32Exception errors
	 */
	public static RASDIALPARAMS getPhoneBookDialingParams(String entryName) throws Ras32Exception {
		synchronized (phoneBookMutex) {
			RASDIALPARAMS.ByReference rasDialParams = new RASDIALPARAMS.ByReference();
			System.arraycopy(rasDialParams.szEntryName, 0, entryName.toCharArray(), 0, entryName.length());
			BOOLByReference lpfPassword = new BOOLByReference();
			int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
			if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
			return rasDialParams;
		}
	}

	/**
	 * Dial a phone book entry by name (Synchronously)
	 * @param entryName The phone book entry name
         * @return result reference
	 * @throws Ras32Exception errors
	 */
	public static HANDLE dialEntry(String entryName) throws Ras32Exception {
		// get the RAS Credentials
		RASCREDENTIALS.ByReference credentials = new RASCREDENTIALS.ByReference();
		synchronized (phoneBookMutex) {
			credentials.dwMask = WinRas.RASCM_UserName | WinRas.RASCM_Password | WinRas.RASCM_Domain;
			int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
			if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
		}

		// set the dialing parameters
		RASDIALPARAMS.ByReference rasDialParams = new RASDIALPARAMS.ByReference();
		System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
		System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
		System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
		System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);

		// dial
		HANDLEByReference hrasConn = new HANDLEByReference();
		int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 0, null, hrasConn);
		if (err != WinError.ERROR_SUCCESS) {
			if (hrasConn.getValue() != null) Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
			throw new Ras32Exception(err);
		}
		return hrasConn.getValue();
	}

	/**
	 * Dial a phone book entry by name (Asynchronously - callback type 2)
	 * @param entryName The phone book entry name
         * @param func2
	 * @return the HRASCONN for this connection
	 * @throws Ras32Exception errors
	 */
	public static HANDLE dialEntry(String entryName, RasDialFunc2 func2) throws Ras32Exception {
		// get the RAS Credentials
		RASCREDENTIALS.ByReference credentials = new RASCREDENTIALS.ByReference();
		synchronized (phoneBookMutex) {
			credentials.dwMask = WinRas.RASCM_UserName | WinRas.RASCM_Password | WinRas.RASCM_Domain;
			int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
			if (err != WinError.ERROR_SUCCESS) throw new Ras32Exception(err);
		}

		// set the dialing parameters
		RASDIALPARAMS.ByReference rasDialParams = new RASDIALPARAMS.ByReference();
		System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
		System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
		System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
		System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);

		// dial
		HANDLEByReference hrasConn = new HANDLEByReference();
		int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 2, func2, hrasConn);
		if (err != WinError.ERROR_SUCCESS) {
			if (hrasConn.getValue() != null) Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
			throw new Ras32Exception(err);
		}
		return hrasConn.getValue();
	}

}
