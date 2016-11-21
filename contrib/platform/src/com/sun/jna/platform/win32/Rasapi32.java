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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinRas.RASCONN;
import com.sun.jna.platform.win32.WinRas.RASCONNSTATUS;
import com.sun.jna.platform.win32.WinRas.RASCREDENTIALS;
import com.sun.jna.platform.win32.WinRas.RASDIALEXTENSIONS;
import com.sun.jna.platform.win32.WinRas.RASDIALPARAMS;
import com.sun.jna.platform.win32.WinRas.RASENTRY;
import com.sun.jna.platform.win32.WinRas.RAS_STATS;
import com.sun.jna.platform.win32.WinRas.RasDialFunc2;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Rasapi32.dll Interface.
 */
public interface Rasapi32 extends StdCallLibrary {
	Rasapi32 INSTANCE = Native.loadLibrary("Rasapi32", Rasapi32.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * The RasDial function establishes a RAS connection between a RAS client and a RAS server.
	 * The connection data includes callback and user-authentication information.
	 *
	 * @param lpRasDialExtensions
	 *            Pointer to a RASDIALEXTENSIONS structure that specifies a set of RasDial extended features to enable.
	 *            Set this parameter to NULL if there is not a need to enable these features.
	 * @param lpszPhonebook
	 *            Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
	 *            If this parameter is NULL, the function uses the current default phone-book file.
	 *            The default phone-book file is the one selected by the user in the User Preferences property sheet of the
	 *            Dial-Up Networking dialog box.
	 * @param lpRasDialParams
	 *            Pointer to a RASDIALPARAMS structure that specifies calling parameters for the RAS connection.
	 *            Use the RasGetEntryDialParams function to retrieve a copy of this structure for a particular phone-book entry.
	 * @param dwNotifierType
	 *            Specifies the nature of the lpvNotifier parameter. If lpvNotifier is NULL, dwNotifierType is ignored.
	 *            If lpvNotifier is not NULL, set dwNotifierType to one of the following values.
	 * @param lpvNotifier
	 *            Specifies a window handle or a RasDialFunc, RasDialFunc1, or RasDialFunc2 callback function to receive
	 *            RasDial event notifications.
	 *            The dwNotifierType parameter specifies the nature of lpvNotifier. Please refer to its description preceding for further detail.
	 * @param lphRasConn
	 *            Pointer to a variable of type HRASCONN. Set the HRASCONN variable to NULL before calling RasDial.
	 *            If RasDial succeeds, it stores a handle to the RAS connection into *lphRasConn.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasDial(RASDIALEXTENSIONS.ByReference lpRasDialExtensions, String lpszPhonebook, RASDIALPARAMS.ByReference lpRasDialParams, int dwNotifierType, RasDialFunc2 lpvNotifier, HANDLEByReference lphRasConn);

	/**
	 * The RasEnumConnections function lists all active RAS connections. It returns each connection's handle and phone-book entry name.
	 *
	 * @param lprasconn
	 *            Pointer to a buffer that receives, on output, an array of RASCONN structures, one for each RAS connection.
	 *            On input, an application must set the dwSize member of the first RASCONN structure in the buffer to sizeof(RASCONN)
	 *            in order to identify the version of the structure being passed.
	 * @param lpcb
	 *            Pointer to a variable that, on input, contains the size, in bytes, of the buffer specified by lprasconn.
	 *            On output, the function sets this variable to the number of bytes required to enumerate the RAS connections.
	 * @param lpcConnections
	 *            Pointer to a variable that receives the number of RASCONN structures written to the buffer specified by lprasconn.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasEnumConnections(RASCONN[] lprasconn, IntByReference lpcb, IntByReference lpcConnections);

	/**
	 * The RasGetConnectionStatistics function retrieves accumulated connection statistics for the specified connection.
	 *
	 * @param hrasconn
	 *            Handle to the connection. Use RasDial or RasEnumConnections to obtain this handle.
	 * @param lpStatistics
	 *            Pointer to the RASCONNSTATUS structure that, on output, receives the status information.
	 *            On input, set the dwSize member of the structure to sizeof(RASCONNSTATUS) in order to identify the version of the structure being passed.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetConnectionStatistics(HANDLE hrasconn, RAS_STATS.ByReference lpStatistics);

	/**
	 * The RasGetConnectionStatistics function retrieves accumulated connection statistics for the specified connection.
	 *
	 * @param hrasconn
	 *            Specifies the remote access connection for which to retrieve the status. This handle must have been obtained from RasDial
	 *            or RasEnumConnections.
	 * @param lprasconnstatus
	 *            Pointer to the RASCONNSTATUS structure that, on output, receives the status information.
	 *            On input, set the dwSize member of the structure to sizeof(RASCONNSTATUS) in order to identify the version of the structure being passed.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetConnectStatus(HANDLE hrasconn, RASCONNSTATUS.ByReference lprasconnstatus);

	/**
	 * The RasGetCredentials function retrieves the user credentials associated with a specified RAS phone-book entry.
	 *
	 * @param lpszPhonebook
	 *            Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
	 *            If this parameter is NULL, the function uses the current default phone-book file. The default phone-book file is the
	 *            one selected by the user in the User Preferences property sheet of the Dial-Up Networking dialog box.
	 * @param lpszEntry
	 *            Pointer to a null-terminated string that specifies the name of a phone-book entry.
	 * @param lpCredentials
	 *            Pointer to the RASCREDENTIALS structure that, on output, receives the user credentials associated with the specified
	 *            phone-book entry.
	 *            On input, set the dwSize member of the structure to sizeof(RASCREDENTIALS), and set the dwMask member to indicate the
	 *            credential information to retrieve. When the function returns, dwMask indicates the members that were successfully retrieved.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetCredentials(String lpszPhonebook, String lpszEntry, RASCREDENTIALS.ByReference lpCredentials);

	/**
	 * The RasGetEntryProperties function retrieves the properties of a phone-book entry.
	 *
	 * @param lpszPhonebook
	 *            Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
	 *            If this parameter is NULL, the function uses the current default phone-book file.
	 *            The default phone-book file is the one selected by the user in the User Preferences property sheet of the
	 *            Dial-Up Networking dialog box.
	 * @param lpszEntry
	 *            Pointer to a null-terminated string that specifies an existing entry name.
	 *            If an empty string is specified, the function returns default values in the buffers pointed to by the
	 *            lpRasEntry and lpbDeviceInfo parameters.
	 * @param lpRasEntry
	 *            Pointer to a RASENTRY structure followed by additional bytes for the alternate phone number list, if there is one.
	 * @param lpdwEntryInfoSize
	 *            Pointer to a variable that, on input, specifies the size, in bytes, of the lpRasEntry buffer.
	 * @param lpbDeviceInfo
	 *            This parameter is no longer used. The calling function should set this parameter to NULL.
	 * @param lpdwDeviceInfoSize
	 *            This parameter is unused. The calling function should set this parameter to NULL.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetEntryProperties(String lpszPhonebook, String lpszEntry, RASENTRY.ByReference lpRasEntry, IntByReference lpdwEntryInfoSize, Pointer lpbDeviceInfo, Pointer lpdwDeviceInfoSize);

	/**
	 * The RasGetProjectionInfo function obtains information about a remote access projection operation for a specified remote access
	 * component protocol.
	 *
	 * @param hrasconn
	 *            Handle to the remote access connection of interest. An application obtains a RAS connection handle from the RasDial
	 *            or RasEnumConnections function.
	 * @param rasprojection
	 *            Specifies the RASPROJECTION enumerated type value that identifies the protocol of interest.
	 * @param lpprojection
	 *            Pointer to a buffer that receives the information specified by the rasprojection parameter. The information is in a
	 *            structure appropriate to the rasprojection value.
	 * @param lpcb
	 *            Pointer to a variable that, on input, specifies the size, in bytes, of the buffer pointed to by lpprojection.
	 *            On output, this variable receives the size, in bytes, of the lpprojection buffer.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetProjectionInfo(HANDLE hrasconn, int rasprojection, Pointer lpprojection, IntByReference lpcb);

	/**
	 * The RasHangUp function terminates a remote access connection. The connection is specified with a RAS connection handle.
	 * The function releases all RASAPI32.DLL resources associated with the handle.
	 *
	 * @param hrasconn
	 *            Specifies the remote access connection to terminate. This is a handle returned from a previous call to RasDial or RasEnumConnections.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasHangUp(HANDLE hrasconn);

	/**
	 * The RasSetEntryProperties function changes the connection information for an entry in the phone book or creates a new phone-book entry.
	 *
	 * @param lpszPhonebook
	 *            Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
	 *            If this parameter is NULL, the function uses the current default phone-book file.
	 *            The default phone-book file is the one selected by the user in the User Preferences property sheet of the Dial-Up
	 *            Networking dialog box.
	 * @param lpszEntry
	 *            Pointer to a null-terminated string that specifies an entry name.
	 * @param lpRasEntry
	 *            Pointer to the RASENTRY structure that specifies the new connection data to be associated with the
	 *            phone-book entry indicated by the lpszEntry parameter.
	 * @param dwEntryInfoSize
	 *            Specifies the size, in bytes, of the buffer identified by the lpRasEntry parameter.
	 * @param lpbDeviceInfo
	 *            Pointer to a buffer that specifies device-specific configuration information.
	 *            This is opaque TAPI device configuration information. For more information about TAPI device configuration,
	 *            see the lineGetDevConfig function in Telephony Application Programming Interfaces (TAPI) in the Platform SDK.
	 * @param dwDeviceInfoSize
	 *            Specifies the size, in bytes, of the lpbDeviceInfo buffer.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasSetEntryProperties(String lpszPhonebook, String lpszEntry, RASENTRY.ByReference lpRasEntry, int dwEntryInfoSize, byte[] lpbDeviceInfo, int dwDeviceInfoSize);

	/**
	 * The RasGetEntryDialParams function retrieves the connection information saved by the last successful call to the RasDial or
	 * RasSetEntryDialParams function for a specified phone-book entry.
	 *
	 * @param lpszPhonebook
	 *           Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
	 *           If this parameter is NULL, the function uses the current default phone-book file.
	 *           The default phone-book file is the one selected by the user in the User Preferences property sheet of the
	 *           Dial-Up Networking dialog box.
	 * @param lprasdialparams
	 *            Pointer to a RASDIALPARAMS structure.
	 * @param lpfPassword
	 *            Pointer to a flag that indicates whether the function retrieved the password associated with the user
	 *            name for the phone-book entry. The lpfPassword parameter is TRUE if the system has saved a password for
	 *            the specified entry. If the system has no password saved for this entry, lpfPassword is FALSE.
	 * @return If the function succeeds, the return value is ERROR_SUCCESS.
	 */
	int RasGetEntryDialParams(String lpszPhonebook, RASDIALPARAMS.ByReference lprasdialparams, BOOLByReference lpfPassword);

	/**
	 * The RasGetErrorString function obtains an error message string for a specified RAS error value.
	 *
	 * @param uErrorValue
	 *           Specifies the error value of interest. These are values returned by one of the RAS functions:
	 *           those listed in the RasError.h header file.
	 * @param lpszErrorString
	 *            Pointer to a buffer that receives the error string. This parameter must not be NULL.
	 * @param cBufSize
	 *            Specifies the size, in characters, of the buffer pointed to by lpszErrorString.
         * @return status
	 */
	int RasGetErrorString(int uErrorValue, char[] lpszErrorString, int cBufSize);
}
