/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinNT.ACCESS_ACEStructure;
import com.sun.jna.platform.win32.WinNT.ACL;
import com.sun.jna.platform.win32.WinNT.EVENTLOGRECORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SECURITY_DESCRIPTOR_RELATIVE;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import static com.sun.jna.platform.win32.WinDef.BOOLByReference;
import static com.sun.jna.platform.win32.WinDef.DWORD;
import static com.sun.jna.platform.win32.WinDef.DWORDByReference;
import static com.sun.jna.platform.win32.WinNT.*;


/**
 * Advapi32 utility API.
 *
 * @author dblock[at]dblock.org
 */
public abstract class Advapi32Util {
	/**
	 * An account.
	 */
	public static class Account {
		/**
		 * Account name.
		 */
		public String name;

		/**
		 * Account domain.
		 */
		public String domain;

		/**
		 * Account SID.
		 */
		public byte[] sid;

		/**
		 * String representation of the account SID.
		 */
		public String sidString;

		/**
		 * Account type, one of SID_NAME_USE.
		 */
		public int accountType;

		/**
		 * Fully qualified account name.
		 */
		public String fqn;
	}


	/**
	 * Retrieves the name of the user associated with the current thread.
	 *
	 * @return A user name.
	 */
	public static String getUserName() {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len);

		if (!result) {
			switch (Kernel32.INSTANCE.GetLastError()) {
			case W32Errors.ERROR_INSUFFICIENT_BUFFER:
				buffer = new char[len.getValue()];
				break;

			default:
				throw new Win32Exception(Native.getLastError());
			}

			result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
		}

		if (!result) {
			throw new Win32Exception(Native.getLastError());
		}

		return Native.toString(buffer);
	}

	/**
	 * Retrieves a security identifier (SID) for the account on the current
	 * system.
	 *
	 * @param accountName
	 *            Specifies the account name.
	 * @return A structure containing the account SID;
	 */
	public static Account getAccountByName(String accountName) {
		return getAccountByName(null, accountName);
	}

	/**
	 * Retrieves a security identifier (SID) for a given account.
	 *
	 * @param systemName
	 *            Name of the system.
	 * @param accountName
	 *            Account name.
	 * @return A structure containing the account SID.
	 */
	public static Account getAccountByName(String systemName, String accountName) {
		IntByReference pSid = new IntByReference(0);
		IntByReference cchDomainName = new IntByReference(0);
		PointerByReference peUse = new PointerByReference();

		if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, null,
				pSid, null, cchDomainName, peUse)) {
			throw new RuntimeException(
					"LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
		}

		int rc = Kernel32.INSTANCE.GetLastError();
		if (pSid.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}

		Memory sidMemory = new Memory(pSid.getValue());
		PSID result = new PSID(sidMemory);
		char[] referencedDomainName = new char[cchDomainName.getValue() + 1];

		if (!Advapi32.INSTANCE.LookupAccountName(systemName, accountName,
				result, pSid, referencedDomainName, cchDomainName, peUse)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		Account account = new Account();
		account.accountType = peUse.getPointer().getInt(0);
		account.name = accountName;

		String[] accountNamePartsBs = accountName.split("\\\\", 2);
		String[] accountNamePartsAt = accountName.split("@", 2);

		if (accountNamePartsBs.length == 2) {
			account.name = accountNamePartsBs[1];
		} else if (accountNamePartsAt.length == 2) {
			account.name = accountNamePartsAt[0];
		} else {
			account.name = accountName;
		}

		if (cchDomainName.getValue() > 0) {
			account.domain = Native.toString(referencedDomainName);
			account.fqn = account.domain + "\\" + account.name;
		} else {
			account.fqn = account.name;
		}

		account.sid = result.getBytes();
		account.sidString = convertSidToStringSid(new PSID(account.sid));
		return account;
	}

	/**
	 * Get the account by SID on the local system.
	 *
	 * @param sid
	 *            SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(PSID sid) {
		return getAccountBySid(null, sid);
	}

	/**
	 * Get the account by SID.
	 *
	 * @param systemName
	 *            Name of the system.
	 * @param sid
	 *            SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String systemName, PSID sid) {
		IntByReference cchName = new IntByReference();
		IntByReference cchDomainName = new IntByReference();
		PointerByReference peUse = new PointerByReference();

		if (Advapi32.INSTANCE.LookupAccountSid(null, sid, null, cchName, null,
				cchDomainName, peUse)) {
			throw new RuntimeException(
					"LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
		}

		int rc = Kernel32.INSTANCE.GetLastError();
		if (cchName.getValue() == 0
				|| rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}

		char[] domainName = new char[cchDomainName.getValue()];
		char[] name = new char[cchName.getValue()];

		if (!Advapi32.INSTANCE.LookupAccountSid(null, sid, name, cchName,
				domainName, cchDomainName, peUse)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		Account account = new Account();
		account.accountType = peUse.getPointer().getInt(0);
		account.name = Native.toString(name);

		if (cchDomainName.getValue() > 0) {
			account.domain = Native.toString(domainName);
			account.fqn = account.domain + "\\" + account.name;
		} else {
			account.fqn = account.name;
		}

		account.sid = sid.getBytes();
		account.sidString = convertSidToStringSid(sid);
		return account;
	}

	/**
	 * Convert a security identifier (SID) to a string format suitable for
	 * display, storage, or transmission.
	 *
	 * @param sid
	 *            SID bytes.
	 * @return String SID.
	 */
	public static String convertSidToStringSid(PSID sid) {
		PointerByReference stringSid = new PointerByReference();
		if (!Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		String result = stringSid.getValue().getWideString(0);
		Kernel32.INSTANCE.LocalFree(stringSid.getValue());
		return result;
	}

	/**
	 * Convert a string representation of a security identifier (SID) to a
	 * binary format.
	 *
	 * @param sidString
	 *            String SID.
	 * @return SID bytes.
	 */
	public static byte[] convertStringSidToSid(String sidString) {
		PSIDByReference pSID = new PSIDByReference();
		if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return pSID.getValue().getBytes();
	}

	/**
	 * Compares a SID to a well known SID and returns TRUE if they match.
	 *
	 * @param sidString
	 *            String representation of a SID.
	 * @param wellKnownSidType
	 *            Member of the WELL_KNOWN_SID_TYPE enumeration to compare with
	 *            the SID at pSid.
	 * @return True if the SID is of the well-known type, false otherwise.
	 */
	public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
		PSIDByReference pSID = new PSIDByReference();
		if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return Advapi32.INSTANCE.IsWellKnownSid(pSID.getValue(),
				wellKnownSidType);
	}

	/**
	 * Compares a SID to a well known SID and returns TRUE if they match.
	 *
	 * @param sidBytes
	 *            Byte representation of a SID.
	 * @param wellKnownSidType
	 *            Member of the WELL_KNOWN_SID_TYPE enumeration to compare with
	 *            the SID at pSid.
	 * @return True if the SID is of the well-known type, false otherwise.
	 */
	public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
		PSID pSID = new PSID(sidBytes);
		return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);
	}

	/**
	 * Get an account name from a string SID on the local machine.
	 *
	 * @param sidString
	 *            SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String sidString) {
		return getAccountBySid(null, sidString);
	}

	/**
	 * Get an account name from a string SID.
	 *
	 * @param systemName
	 *            System name.
	 * @param sidString
	 *            SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String systemName, String sidString) {
		return getAccountBySid(systemName, new PSID(
				convertStringSidToSid(sidString)));
	}

	/**
	 * This function returns the groups associated with a security token, such
	 * as a user token.
	 *
	 * @param hToken
	 *            Token.
	 * @return Token groups.
	 */
	public static Account[] getTokenGroups(HANDLE hToken) {
		// get token group information size
		IntByReference tokenInformationLength = new IntByReference();
		if (Advapi32.INSTANCE.GetTokenInformation(hToken,
				WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, null, 0,
				tokenInformationLength)) {
			throw new RuntimeException(
					"Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
		}
		int rc = Kernel32.INSTANCE.GetLastError();
		if (rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		// get token group information
		WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(
				tokenInformationLength.getValue());
		if (!Advapi32.INSTANCE.GetTokenInformation(hToken,
				WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, groups,
				tokenInformationLength.getValue(), tokenInformationLength)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		ArrayList<Account> userGroups = new ArrayList<Account>();
		// make array of names
		for (SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
			Account group = null;
			try {
				group = Advapi32Util.getAccountBySid(sidAndAttribute.Sid);
			} catch (Exception e) {
				group = new Account();
				group.sid = sidAndAttribute.Sid.getBytes();
				group.sidString = Advapi32Util
						.convertSidToStringSid(sidAndAttribute.Sid);
				group.name = group.sidString;
				group.fqn = group.sidString;
				group.accountType = SID_NAME_USE.SidTypeGroup;
			}
			userGroups.add(group);
		}
		return userGroups.toArray(new Account[0]);
	}

	/**
	 * This function returns the information about the user who owns a security
	 * token,
	 *
	 * @param hToken
	 *            Token.
	 * @return Token user.
	 */
	public static Account getTokenAccount(HANDLE hToken) {
		// get token group information size
		IntByReference tokenInformationLength = new IntByReference();
		if (Advapi32.INSTANCE.GetTokenInformation(hToken,
				WinNT.TOKEN_INFORMATION_CLASS.TokenUser, null, 0,
				tokenInformationLength)) {
			throw new RuntimeException(
					"Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
		}
		int rc = Kernel32.INSTANCE.GetLastError();
		if (rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		// get token user information
		WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(
				tokenInformationLength.getValue());
		if (!Advapi32.INSTANCE.GetTokenInformation(hToken,
				WinNT.TOKEN_INFORMATION_CLASS.TokenUser, user,
				tokenInformationLength.getValue(), tokenInformationLength)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return getAccountBySid(user.User.Sid);
	}

	/**
	 * Return the group memberships of the currently logged on user.
	 *
	 * @return An array of groups.
	 */
	public static Account[] getCurrentUserGroups() {
		HANDLEByReference phToken = new HANDLEByReference();
		try {
			// open thread or process token
			HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
			if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle,
					TOKEN_DUPLICATE | TOKEN_QUERY, true, phToken)) {
				if (W32Errors.ERROR_NO_TOKEN != Kernel32.INSTANCE
						.GetLastError()) {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}
				HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
				if (!Advapi32.INSTANCE.OpenProcessToken(processHandle,
						TOKEN_DUPLICATE | TOKEN_QUERY, phToken)) {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}
			}
			return getTokenGroups(phToken.getValue());
		} finally {
			if (phToken.getValue() != WinBase.INVALID_HANDLE_VALUE) {
				if (!Kernel32.INSTANCE.CloseHandle(phToken.getValue())) {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}
			}
		}
	}

	/**
	 * Checks whether a registry key exists.
	 *
	 * @param root
	 *            HKEY_LOCAL_MACHINE, etc.
	 * @param key
	 *            Path to the registry key.
	 * @return True if the key exists.
	 */
	public static boolean registryKeyExists(HKEY root, String key) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		switch (rc) {
		case W32Errors.ERROR_SUCCESS:
			Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			return true;
		case W32Errors.ERROR_FILE_NOT_FOUND:
			return false;
		default:
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Checks whether a registry value exists.
	 *
	 * @param root
	 *            HKEY_LOCAL_MACHINE, etc.
	 * @param key
	 *            Registry key path.
	 * @param value
	 *            Value name.
	 * @return True if the value exists.
	 */
	public static boolean registryValueExists(HKEY root, String key,
			String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		try {
			switch (rc) {
			case W32Errors.ERROR_SUCCESS:
				break;
			case W32Errors.ERROR_FILE_NOT_FOUND:
				return false;
			default:
				throw new Win32Exception(rc);
			}
			IntByReference lpcbData = new IntByReference();
			IntByReference lpType = new IntByReference();
			rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0,
					lpType, (char[]) null, lpcbData);
			switch (rc) {
			case W32Errors.ERROR_SUCCESS:
			case W32Errors.ERROR_MORE_DATA:
			case W32Errors.ERROR_INSUFFICIENT_BUFFER:
				return true;
			case W32Errors.ERROR_FILE_NOT_FOUND:
				return false;
			default:
				throw new Win32Exception(rc);
			}
		} finally {
			if (phkKey.getValue() != WinBase.INVALID_HANDLE_VALUE) {
				rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
				if (rc != W32Errors.ERROR_SUCCESS) {
					throw new Win32Exception(rc);
				}
			}
		}
	}

	/**
	 * Get a registry REG_SZ value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String registryGetStringValue(HKEY root, String key,
			String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetStringValue(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry REG_SZ value.
	 *
	 * @param hKey
	 *            Parent Key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String registryGetStringValue(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_SZ
				&& lpType.getValue() != WinNT.REG_EXPAND_SZ) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue()
					+ ", expected REG_SZ or REG_EXPAND_SZ");
		}
		char[] data = new char[lpcbData.getValue()];
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		return Native.toString(data);		
	}
	
	/**
	 * Get a registry REG_EXPAND_SZ value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String registryGetExpandableStringValue(HKEY root,
			String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetExpandableStringValue(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry REG_EXPAND_SZ value.
	 *
	 * @param hKey
	 *            Parent Key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String registryGetExpandableStringValue(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_EXPAND_SZ) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue() + ", expected REG_SZ");
		}
		char[] data = new char[lpcbData.getValue()];
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		return Native.toString(data);
	}
	
	/**
	 * Get a registry REG_MULTI_SZ value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String[] registryGetStringArray(HKEY root, String key,
			String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetStringArray(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry REG_MULTI_SZ value.
	 *
	 * @param hKey
	 *            Parent Key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static String[] registryGetStringArray(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_MULTI_SZ) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue() + ", expected REG_SZ");
		}
		Memory data = new Memory(lpcbData.getValue());
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		ArrayList<String> result = new ArrayList<String>();
		int offset = 0;
		while (offset < data.size()) {
			String s = data.getWideString(offset);
			offset += s.length() * Native.WCHAR_SIZE;
			offset += Native.WCHAR_SIZE;
			if (s.length() == 0 && offset == data.size()) {
				// skip the final NULL
			} else {
				result.add(s);
			}
		}
		return result.toArray(new String[0]);
	}
	
	/**
	 * Get a registry REG_BINARY value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static byte[] registryGetBinaryValue(HKEY root, String key,
			String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetBinaryValue(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry REG_BINARY value.
	 *
	 * @param hKey
	 *            Parent Key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return String value.
	 */
	public static byte[] registryGetBinaryValue(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_BINARY) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue() + ", expected REG_BINARY");
		}
		byte[] data = new byte[lpcbData.getValue()];
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		return data;
	}
	
	/**
	 * Get a registry DWORD value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry key path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return Integer value.
	 */
	public static int registryGetIntValue(HKEY root, String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetIntValue(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry DWORD value.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return Integer value.
	 */
	public static int registryGetIntValue(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_DWORD) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue() + ", expected REG_DWORD");
		}
		IntByReference data = new IntByReference();
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		return data.getValue();
	}
	
	/**
	 * Get a registry QWORD value.
	 *
	 * @param root
	 *            Root key.
	 * @param key
	 *            Registry key path.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return Integer value.
	 */
	public static long registryGetLongValue(HKEY root, String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetLongValue(phkKey.getValue(), value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}
	
	/**
	 * Get a registry QWORD value.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param value
	 *            Name of the value to retrieve.
	 * @return Integer value.
	 */
	public static long registryGetLongValue(HKEY hKey, String value) {
		IntByReference lpcbData = new IntByReference();
		IntByReference lpType = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, (char[]) null, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		if (lpType.getValue() != WinNT.REG_QWORD) {
			throw new RuntimeException("Unexpected registry type "
					+ lpType.getValue() + ", expected REG_QWORD");
		}
		LongByReference data = new LongByReference();
		rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0,
				lpType, data, lpcbData);
		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}
		return data.getValue();
	}

	/**
	 * Get a registry value and returns a java object depending on the value
	 * type.
	 *
	 * @param hkKey
	 *            Root key.
	 * @param subKey
	 *            Registry key path.
	 * @param lpValueName
	 *            Name of the value to retrieve or null for the default value.
	 * @return Object value.
	 */
	public static Object registryGetValue(HKEY hkKey, String subKey,
			String lpValueName) {
		Object result = null;
		IntByReference lpType = new IntByReference();
		byte[] lpData = new byte[Advapi32.MAX_VALUE_NAME];
		IntByReference lpcbData = new IntByReference(Advapi32.MAX_VALUE_NAME);

		int rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName,
				Advapi32.RRF_RT_ANY, lpType, lpData, lpcbData);

		// if lpType == 0 then the value is empty (REG_NONE)!
		if (lpType.getValue() == WinNT.REG_NONE)
			return null;

		if (rc != W32Errors.ERROR_SUCCESS
				&& rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}

		Memory byteData = new Memory(lpcbData.getValue());
		byteData.write(0, lpData, 0, lpcbData.getValue());

		if (lpType.getValue() == WinNT.REG_DWORD) {
			result = new Integer(byteData.getInt(0));
		} else if (lpType.getValue() == WinNT.REG_QWORD) {
			result = new Long(byteData.getLong(0));
		} else if (lpType.getValue() == WinNT.REG_BINARY) {
			result = byteData.getByteArray(0, lpcbData.getValue());
		} else if ((lpType.getValue() == WinNT.REG_SZ)
				|| (lpType.getValue() == WinNT.REG_EXPAND_SZ)) {
			result = byteData.getWideString(0);
		}

		return result;
	}

	/**
	 * Create a registry key.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param keyName
	 *            Key name.
	 * @return True if the key was created, false otherwise.
	 */
	public static boolean registryCreateKey(HKEY hKey, String keyName) {
		HKEYByReference phkResult = new HKEYByReference();
		IntByReference lpdwDisposition = new IntByReference();
		int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, null,
				WinNT.REG_OPTION_NON_VOLATILE, WinNT.KEY_READ, null, phkResult,
				lpdwDisposition);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		rc = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		return WinNT.REG_CREATED_NEW_KEY == lpdwDisposition.getValue();
	}

	/**
	 * Create a registry key.
	 *
	 * @param root
	 *            Root key.
	 * @param parentPath
	 *            Path to an existing registry key.
	 * @param keyName
	 *            Key name.
	 * @return True if the key was created, false otherwise.
	 */
	public static boolean registryCreateKey(HKEY root, String parentPath,
			String keyName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0,
				WinNT.KEY_CREATE_SUB_KEY, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryCreateKey(phkKey.getValue(), keyName);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set an integer value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetIntValue(HKEY hKey, String name, int value) {
		byte[] data = new byte[4];
		data[0] = (byte) (value & 0xff);
		data[1] = (byte) ((value >> 8) & 0xff);
		data[2] = (byte) ((value >> 16) & 0xff);
		data[3] = (byte) ((value >> 24) & 0xff);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0,
				WinNT.REG_DWORD, data, 4);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set an integer value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetIntValue(HKEY root, String keyPath,
			String name, int value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetIntValue(phkKey.getValue(), name, value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set a long value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetLongValue(HKEY hKey, String name, long value) {
		byte[] data = new byte[8];
		data[0] = (byte) (value & 0xff);
		data[1] = (byte) ((value >> 8) & 0xff);
		data[2] = (byte) ((value >> 16) & 0xff);
		data[3] = (byte) ((value >> 24) & 0xff);
		data[4] = (byte) ((value >> 32) & 0xff);
		data[5] = (byte) ((value >> 40) & 0xff);
		data[6] = (byte) ((value >> 48) & 0xff);
		data[7] = (byte) ((value >> 56) & 0xff);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0,
				WinNT.REG_QWORD, data, 8);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set a long value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetLongValue(HKEY root, String keyPath,
			String name, long value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetLongValue(phkKey.getValue(), name, value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set a string value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetStringValue(HKEY hKey, String name,
			String value) {
		char[] data = Native.toCharArray(value);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, WinNT.REG_SZ,
				data, data.length * Native.WCHAR_SIZE);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set a string value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetStringValue(HKEY root, String keyPath,
			String name, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetStringValue(phkKey.getValue(), name, value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set an expandable string value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetExpandableStringValue(HKEY hKey, String name,
			String value) {
		char[] data = Native.toCharArray(value);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0,
				WinNT.REG_EXPAND_SZ, data, data.length * Native.WCHAR_SIZE);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set a string value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param value
	 *            Value to write to registry.
	 */
	public static void registrySetExpandableStringValue(HKEY root,
			String keyPath, String name, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetExpandableStringValue(phkKey.getValue(), name, value);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set a string array value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Name.
	 * @param arr
	 *            Array of strings to write to registry.
	 */
	public static void registrySetStringArray(HKEY hKey, String name,
			String[] arr) {
		int size = 0;
		for (String s : arr) {
			size += s.length() * Native.WCHAR_SIZE;
			size += Native.WCHAR_SIZE;
		}
		size += Native.WCHAR_SIZE;

		int offset = 0;
		Memory data = new Memory(size);
		for (String s : arr) {
			data.setWideString(offset, s);
			offset += s.length() * Native.WCHAR_SIZE;
			offset += Native.WCHAR_SIZE;
		}
		for (int i = 0; i < Native.WCHAR_SIZE; i++) {
			data.setByte(offset++, (byte) 0);
		}

		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0,
				WinNT.REG_MULTI_SZ, data.getByteArray(0, size), size);

		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set a string array value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param arr
	 *            Array of strings to write to registry.
	 */
	public static void registrySetStringArray(HKEY root, String keyPath,
			String name, String[] arr) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetStringArray(phkKey.getValue(), name, arr);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Set a binary value in registry.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param name
	 *            Value name.
	 * @param data
	 *            Data to write to registry.
	 */
	public static void registrySetBinaryValue(HKEY hKey, String name,
			byte[] data) {
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0,
				WinNT.REG_BINARY, data, data.length);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Set a binary value in registry.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param name
	 *            Value name.
	 * @param data
	 *            Data to write to registry.
	 */
	public static void registrySetBinaryValue(HKEY root, String keyPath,
			String name, byte[] data) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registrySetBinaryValue(phkKey.getValue(), name, data);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Delete a registry key.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param keyName
	 *            Name of the key to delete.
	 */
	public static void registryDeleteKey(HKEY hKey, String keyName) {
		int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Delete a registry key.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param keyName
	 *            Name of the key to delete.
	 */
	public static void registryDeleteKey(HKEY root, String keyPath,
			String keyName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registryDeleteKey(phkKey.getValue(), keyName);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Delete a registry value.
	 *
	 * @param hKey
	 *            Parent key.
	 * @param valueName
	 *            Name of the value to delete.
	 */
	public static void registryDeleteValue(HKEY hKey, String valueName) {
		int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Delete a registry value.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to an existing registry key.
	 * @param valueName
	 *            Name of the value to delete.
	 */
	public static void registryDeleteValue(HKEY root, String keyPath,
			String valueName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registryDeleteValue(phkKey.getValue(), valueName);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get names of the registry key's sub-keys.
	 *
	 * @param hKey
	 *            Registry key.
	 * @return Array of registry key names.
	 */
	public static String[] registryGetKeys(HKEY hKey) {
		IntByReference lpcSubKeys = new IntByReference();
		IntByReference lpcMaxSubKeyLen = new IntByReference();
		int rc = Advapi32.INSTANCE
				.RegQueryInfoKey(hKey, null, null, null, lpcSubKeys,
						lpcMaxSubKeyLen, null, null, null, null, null, null);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		ArrayList<String> keys = new ArrayList<String>(lpcSubKeys.getValue());
		char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
		for (int i = 0; i < lpcSubKeys.getValue(); i++) {
			IntByReference lpcchValueName = new IntByReference(
					lpcMaxSubKeyLen.getValue() + 1);
			rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, i, name, lpcchValueName,
					null, null, null, null);
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
			keys.add(Native.toString(name));
		}
		return keys.toArray(new String[0]);
	}

	/**
	 * Get names of the registry key's sub-keys.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to a registry key.
	 * @return Array of registry key names.
	 */
	public static String[] registryGetKeys(HKEY root, String keyPath) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetKeys(phkKey.getValue());
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry key, the caller is responsible to close the key after use.
	 *
	 * @param root
	 *            Root key.
	 * @param keyPath
	 *            Path to a registry key.
	 *
	 * @param samDesired
	 *            Access level (e.g. WinNT.KEY_READ)
	 *
	 * @return HKEYByReference.
	 */
	public static HKEYByReference registryGetKey(HKEY root, String keyPath,
			int samDesired) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, samDesired,
				phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}

		return phkKey;
	}
	
	/**
	 * Close the registry key
	 *
	 * @param hKey
	 *            Registry key.
	 */
	public static void registryCloseKey(HKEY hKey) {
		int rc = Advapi32.INSTANCE.RegCloseKey(hKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Get a table of registry values.
	 *
	 * @param hKey
	 *            Registry key.
	 * @return Table of values.
	 */
	public static TreeMap<String, Object> registryGetValues(HKEY hKey) {
		IntByReference lpcValues = new IntByReference();
		IntByReference lpcMaxValueNameLen = new IntByReference();
		IntByReference lpcMaxValueLen = new IntByReference();
		int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null,
				null, null, null, lpcValues, lpcMaxValueNameLen,
				lpcMaxValueLen, null, null);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		TreeMap<String, Object> keyValues = new TreeMap<String, Object>();
		char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
		byte[] data = new byte[lpcMaxValueLen.getValue()];
		for (int i = 0; i < lpcValues.getValue(); i++) {
			IntByReference lpcchValueName = new IntByReference(
					lpcMaxValueNameLen.getValue() + 1);
			IntByReference lpcbData = new IntByReference(
					lpcMaxValueLen.getValue());
			IntByReference lpType = new IntByReference();
			rc = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName,
					null, lpType, data, lpcbData);
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}

			String nameString = Native.toString(name);

			if (lpcbData.getValue() == 0) {
				switch (lpType.getValue()) {
				case WinNT.REG_BINARY: {
					keyValues.put(nameString, new byte[0]);
					break;
				}
				case WinNT.REG_SZ:
				case WinNT.REG_EXPAND_SZ: {
					keyValues.put(nameString, new char[0]);
					break;
				}
				case WinNT.REG_MULTI_SZ: {
					keyValues.put(nameString, new String[0]);
					break;
				}
				case WinNT.REG_NONE: {
					keyValues.put(nameString, null);
					break;
				}
				default:
					throw new RuntimeException("Unsupported empty type: "
							+ lpType.getValue());
				}
				continue;
			}

			Memory byteData = new Memory(lpcbData.getValue());
			byteData.write(0, data, 0, lpcbData.getValue());

			switch (lpType.getValue()) {
			case WinNT.REG_QWORD: {
				keyValues.put(nameString, byteData.getLong(0));
				break;
			}
			case WinNT.REG_DWORD: {
				keyValues.put(nameString, byteData.getInt(0));
				break;
			}
			case WinNT.REG_SZ:
			case WinNT.REG_EXPAND_SZ: {
				keyValues.put(nameString, byteData.getWideString(0));
				break;
			}
			case WinNT.REG_BINARY: {
				keyValues.put(nameString,
						byteData.getByteArray(0, lpcbData.getValue()));
				break;
			}
			case WinNT.REG_MULTI_SZ: {
				Memory stringData = new Memory(lpcbData.getValue());
				stringData.write(0, data, 0, lpcbData.getValue());
				ArrayList<String> result = new ArrayList<String>();
				int offset = 0;
				while (offset < stringData.size()) {
					String s = stringData.getWideString(offset);
					offset += s.length() * Native.WCHAR_SIZE;
					offset += Native.WCHAR_SIZE;
					if (s.length() == 0 && offset == stringData.size()) {
						// skip the final NULL
					} else {
						result.add(s);
					}
				}
				keyValues.put(nameString, result.toArray(new String[0]));
				break;
			}
			default:
				throw new RuntimeException("Unsupported type: "
						+ lpType.getValue());
			}
		}
		return keyValues;
	}

	/**
	 * Get a table of registry values.
	 *
	 * @param root
	 *            Registry root.
	 * @param keyPath
	 *            Regitry key path.
	 * @return Table of values.
	 */
	public static TreeMap<String, Object> registryGetValues(HKEY root,
			String keyPath) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0,
				WinNT.KEY_READ, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			return registryGetValues(phkKey.getValue());
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Queries the information about a specified key.
	 *
	 * @param hKey
	 *            Current registry key.
	 *
	 * @return A InfoKey value object.
	 */
	public static InfoKey registryQueryInfoKey(HKEY hKey,
			int lpcbSecurityDescriptor) {

		InfoKey infoKey = new InfoKey(hKey, lpcbSecurityDescriptor);
		int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, infoKey.lpClass,
				infoKey.lpcClass, null, infoKey.lpcSubKeys,
				infoKey.lpcMaxSubKeyLen, infoKey.lpcMaxClassLen,
				infoKey.lpcValues, infoKey.lpcMaxValueNameLen,
				infoKey.lpcMaxValueLen, infoKey.lpcbSecurityDescriptor,
				infoKey.lpftLastWriteTime);

		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}

		return infoKey;
	}

	public static class InfoKey {
		public HKEY hKey;
		public char[] lpClass = new char[WinNT.MAX_PATH];
		public IntByReference lpcClass = new IntByReference(WinNT.MAX_PATH);
		public IntByReference lpcSubKeys = new IntByReference();
		public IntByReference lpcMaxSubKeyLen = new IntByReference();
		public IntByReference lpcMaxClassLen = new IntByReference();
		public IntByReference lpcValues = new IntByReference();
		public IntByReference lpcMaxValueNameLen = new IntByReference();
		public IntByReference lpcMaxValueLen = new IntByReference();
		public IntByReference lpcbSecurityDescriptor = new IntByReference();
		public FILETIME lpftLastWriteTime = new FILETIME();

		public InfoKey() {
		}

		public InfoKey(HKEY hKey, int securityDescriptor) {
			this.hKey = hKey;
			this.lpcbSecurityDescriptor = new IntByReference(securityDescriptor);
		}
	}

	/**
	 * Queries the information about a specified key.
	 *
	 * @param hKey
	 *            Current registry key.
	 *
	 * @return A InfoKey value object.
	 */
	public static EnumKey registryRegEnumKey(HKEY hKey, int dwIndex) {
		EnumKey enumKey = new EnumKey(hKey, dwIndex);
		int rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, enumKey.dwIndex,
				enumKey.lpName, enumKey.lpcName, null, enumKey.lpClass,
				enumKey.lpcbClass, enumKey.lpftLastWriteTime);

		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}

		return enumKey;
	}

	public static class EnumKey {
		public HKEY hKey;
		public int dwIndex = 0;
		public char[] lpName = new char[Advapi32.MAX_KEY_LENGTH];
		public IntByReference lpcName = new IntByReference(
				Advapi32.MAX_KEY_LENGTH);
		public char[] lpClass = new char[Advapi32.MAX_KEY_LENGTH];
		public IntByReference lpcbClass = new IntByReference(
				Advapi32.MAX_KEY_LENGTH);
		public FILETIME lpftLastWriteTime = new FILETIME();

		public EnumKey() {
		}

		public EnumKey(HKEY hKey, int dwIndex) {
			this.hKey = hKey;
			this.dwIndex = dwIndex;
		}
	}

	/**
	 * Converts a map of environment variables to an environment block suitable
	 * for {@link Advapi32#CreateProcessAsUser}. This environment block consists
	 * of null-terminated blocks of null-terminated strings. Each string is in
	 * the following form: name=value\0
	 *
	 * @param environment
	 *            Environment variables
	 * @return A environment block
	 */
	public static String getEnvironmentBlock(Map<String, String> environment) {
		StringBuilder out = new StringBuilder(environment.size() * 32 /* some guess about average name=value length*/);
		for (Entry<String, String> entry : environment.entrySet()) {
		    String    key=entry.getKey(), value=entry.getValue();
			if (value != null) {
				out.append(key).append("=").append(value).append('\0');
			}
		}
		return out.append('\0').toString();
	}

	/**
	 * Event log types.
	 */
	public static enum EventLogType {
		Error, Warning, Informational, AuditSuccess, AuditFailure
	}

	/**
	 * An event log record.
	 */
	public static class EventLogRecord {
		private EVENTLOGRECORD _record = null;
		private String _source;
		private byte[] _data;
		private String[] _strings;

		/**
		 * Raw record data.
		 *
		 * @return EVENTLOGRECORD.
		 */
		public EVENTLOGRECORD getRecord() {
			return _record;
		}

		/**
		 * Event Id.
		 *
		 * @return Integer.
		 */
		public int getEventId() {
			return _record.EventID.intValue();
		}

		/**
		 * Event source.
		 *
		 * @return String.
		 */
		public String getSource() {
			return _source;
		}

		/**
		 * Status code for the facility, part of the Event ID.
		 *
		 * @return Status code.
		 */
		public int getStatusCode() {
			return _record.EventID.intValue() & 0xFFFF;
		}

		/**
		 * Record number of the record. This value can be used with the
		 * EVENTLOG_SEEK_READ flag in the ReadEventLog function to begin reading
		 * at a specified record.
		 *
		 * @return Integer.
		 */
		public int getRecordNumber() {
			return _record.RecordNumber.intValue();
		}

		/**
		 * Record length, with data.
		 *
		 * @return Number of bytes in the record including data.
		 */
		public int getLength() {
			return _record.Length.intValue();
		}

		/**
		 * Strings associated with this event.
		 *
		 * @return Array of strings or null.
		 */
		public String[] getStrings() {
			return _strings;
		}

		/**
		 * Event log type.
		 *
		 * @return Event log type.
		 */
		public EventLogType getType() {
			switch (_record.EventType.intValue()) {
			case WinNT.EVENTLOG_SUCCESS:
			case WinNT.EVENTLOG_INFORMATION_TYPE:
				return EventLogType.Informational;
			case WinNT.EVENTLOG_AUDIT_FAILURE:
				return EventLogType.AuditFailure;
			case WinNT.EVENTLOG_AUDIT_SUCCESS:
				return EventLogType.AuditSuccess;
			case WinNT.EVENTLOG_ERROR_TYPE:
				return EventLogType.Error;
			case WinNT.EVENTLOG_WARNING_TYPE:
				return EventLogType.Warning;
			default:
				throw new RuntimeException("Invalid type: "
						+ _record.EventType.intValue());
			}
		}

		/**
		 * Raw data associated with the record.
		 *
		 * @return Array of bytes or null.
		 */
		public byte[] getData() {
			return _data;
		}

		public EventLogRecord(Pointer pevlr) {
			_record = new EVENTLOGRECORD(pevlr);
			_source = pevlr.getWideString(_record.size());
			// data
			if (_record.DataLength.intValue() > 0) {
				_data = pevlr.getByteArray(_record.DataOffset.intValue(),
						_record.DataLength.intValue());
			}
			// strings
			if (_record.NumStrings.intValue() > 0) {
				ArrayList<String> strings = new ArrayList<String>();
				int count = _record.NumStrings.intValue();
				long offset = _record.StringOffset.intValue();
				while (count > 0) {
					String s = pevlr.getWideString(0);
					strings.add(s);
					offset += s.length() * Native.WCHAR_SIZE;
					offset += Native.WCHAR_SIZE;
					count--;
				}
				_strings = strings.toArray(new String[0]);
			}
		}
	}

	/**
	 * An iterator for Event Log entries.
	 */
	public static class EventLogIterator implements Iterable<EventLogRecord>,
			Iterator<EventLogRecord> {

		private HANDLE _h = null;
		private Memory _buffer = new Memory(1024 * 64); // memory buffer to
														// store events
		private boolean _done = false; // no more events
		private int _dwRead = 0; // number of bytes remaining in the current
									// buffer
		private Pointer _pevlr = null; // pointer to the current record
		private int _flags = WinNT.EVENTLOG_FORWARDS_READ;

		public EventLogIterator(String sourceName) {
			this(null, sourceName, WinNT.EVENTLOG_FORWARDS_READ);
		}

		public EventLogIterator(String serverName, String sourceName, int flags) {
			_flags = flags;
			_h = Advapi32.INSTANCE.OpenEventLog(serverName, sourceName);
			if (_h == null) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
		}

		private boolean read() {
			// finished or bytes remain, don't read any new data
			if (_done || _dwRead > 0) {
				return false;
			}

			IntByReference pnBytesRead = new IntByReference();
			IntByReference pnMinNumberOfBytesNeeded = new IntByReference();

			if (!Advapi32.INSTANCE
					.ReadEventLog(_h, WinNT.EVENTLOG_SEQUENTIAL_READ | _flags,
							0, _buffer, (int) _buffer.size(), pnBytesRead,
							pnMinNumberOfBytesNeeded)) {

				int rc = Kernel32.INSTANCE.GetLastError();

				// not enough bytes in the buffer, resize
				if (rc == W32Errors.ERROR_INSUFFICIENT_BUFFER) {
					_buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());

					if (!Advapi32.INSTANCE.ReadEventLog(_h,
							WinNT.EVENTLOG_SEQUENTIAL_READ | _flags, 0,
							_buffer, (int) _buffer.size(), pnBytesRead,
							pnMinNumberOfBytesNeeded)) {
						throw new Win32Exception(
								Kernel32.INSTANCE.GetLastError());
					}
				} else {
					// read failed, no more entries or error
					close();
					if (rc != W32Errors.ERROR_HANDLE_EOF) {
						throw new Win32Exception(rc);
					}
					return false;
				}
			}

			_dwRead = pnBytesRead.getValue();
			_pevlr = _buffer;
			return true;
		}

		/**
		 * Call close() in the case when the caller needs to abandon the
		 * iterator before the iteration completes.
		 */
		public void close() {
			_done = true;
			if (_h != null) {
				if (!Advapi32.INSTANCE.CloseEventLog(_h)) {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}
				_h = null;
			}
		}

		// @Override - @todo restore Override annotation after we move to source
		// level 1.6
		public Iterator<EventLogRecord> iterator() {
			return this;
		}

		// @Override - @todo restore Override annotation after we move to source
		// level 1.6
		public boolean hasNext() {
			read();
			return !_done;
		}

		// @Override - @todo restore Override annotation after we move to source
		// level 1.6
		public EventLogRecord next() {
			read();
			EventLogRecord record = new EventLogRecord(_pevlr);
			_dwRead -= record.getLength();
			_pevlr = _pevlr.share(record.getLength());
			return record;
		}

		// @Override - @todo restore Override annotation after we move to source
		// level 1.6
		public void remove() {
		}
	}

	public static ACCESS_ACEStructure[] getFileSecurity(String fileName,
			boolean compact) {
		int infoType = WinNT.DACL_SECURITY_INFORMATION;
		int nLength = 1024;
		boolean repeat = false;
		Memory memory = null;

		do {
			repeat = false;
			memory = new Memory(nLength);
			IntByReference lpnSize = new IntByReference();
			boolean succeded = Advapi32.INSTANCE.GetFileSecurity(new WString(
					fileName), infoType, memory, nLength, lpnSize);

			if (!succeded) {
				int lastError = Kernel32.INSTANCE.GetLastError();
				memory.clear();
				if (W32Errors.ERROR_INSUFFICIENT_BUFFER != lastError) {
					throw new Win32Exception(lastError);
				}
			}
			int lengthNeeded = lpnSize.getValue();
			if (nLength < lengthNeeded) {
				repeat = true;
				nLength = lengthNeeded;
				memory.clear();
			}
		} while (repeat);

		SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(
				memory);
		memory.clear();
		ACL dacl = sdr.getDiscretionaryACL();
		ACCESS_ACEStructure[] aceStructures = dacl.getACEStructures();

		if (compact) {
			Map<String, ACCESS_ACEStructure> aceMap = new HashMap<String, ACCESS_ACEStructure>();
			for (ACCESS_ACEStructure aceStructure : aceStructures) {
				boolean inherted = ((aceStructure.AceFlags & WinNT.VALID_INHERIT_FLAGS) != 0);
				String key = aceStructure.getSidString() + "/" + inherted + "/"
						+ aceStructure.getClass().getName();
				ACCESS_ACEStructure aceStructure2 = aceMap.get(key);
				if (aceStructure2 != null) {
					int accessMask = aceStructure2.Mask;
					accessMask = accessMask | aceStructure.Mask;
					aceStructure2.Mask = accessMask;
				} else {
					aceMap.put(key, aceStructure);
				}
			}
			return aceMap.values().toArray(
					new ACCESS_ACEStructure[aceMap.size()]);
		}
		return aceStructures;
	}

    public static enum AccessCheckPermission {
        READ(GENERIC_READ),
        WRITE(GENERIC_WRITE),
        EXECUTE(GENERIC_EXECUTE);

        final int code;

        AccessCheckPermission(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }


    private static Memory getSecurityDescriptorForFile(final String absoluteFilePath) {
        final int infoType = OWNER_SECURITY_INFORMATION | GROUP_SECURITY_INFORMATION |
                DACL_SECURITY_INFORMATION;

        final IntByReference lpnSize = new IntByReference();
        boolean succeeded = Advapi32.INSTANCE.GetFileSecurity(
                new WString(absoluteFilePath),
                infoType,
                null,
                0, lpnSize);

        if (!succeeded) {
            final int lastError = Kernel32.INSTANCE.GetLastError();
            if (W32Errors.ERROR_INSUFFICIENT_BUFFER != lastError) {
                throw new Win32Exception(lastError);
            }
        }

        final int nLength = lpnSize.getValue();
        final Memory securityDescriptorMemoryPointer = new Memory(nLength);
        succeeded = Advapi32.INSTANCE.GetFileSecurity(new WString(
                absoluteFilePath), infoType, securityDescriptorMemoryPointer, nLength, lpnSize);

        if (!succeeded) {
            securityDescriptorMemoryPointer.clear();
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        return securityDescriptorMemoryPointer;
    }

    /**
     * Checks if the current process has the given permission for the file.
     * @param file the file to check
     * @param permissionToCheck the permission to check for the file
     * @return true if has access, otherwise false
     */
    public static boolean accessCheck(File file, AccessCheckPermission permissionToCheck) {
        boolean hasAccess = false;
        final Memory securityDescriptorMemoryPointer = getSecurityDescriptorForFile(file.getAbsolutePath().replaceAll("/", "\\"));

        HANDLEByReference openedAccessToken = null;
        final HANDLEByReference duplicatedToken = new HANDLEByReference();
        try{
            openedAccessToken = new HANDLEByReference();

            final int desireAccess = TOKEN_IMPERSONATE | TOKEN_QUERY | TOKEN_DUPLICATE | STANDARD_RIGHTS_READ;
            if(!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), desireAccess, openedAccessToken)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            if(!Advapi32.INSTANCE.DuplicateToken(openedAccessToken.getValue(), SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation, duplicatedToken)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            final GENERIC_MAPPING mapping = new GENERIC_MAPPING();
            mapping.genericRead = new DWORD(FILE_GENERIC_READ);
            mapping.genericWrite = new DWORD(FILE_GENERIC_WRITE);
            mapping.genericExecute = new DWORD(FILE_GENERIC_EXECUTE);
            mapping.genericAll = new DWORD(FILE_ALL_ACCESS);

            final DWORDByReference rights = new DWORDByReference(new DWORD(permissionToCheck.getCode()));
            Advapi32.INSTANCE.MapGenericMask(rights, mapping);

            final PRIVILEGE_SET privileges = new PRIVILEGE_SET(1);
            privileges.PrivilegeCount = new DWORD(0);
            final DWORDByReference privilegeLength = new DWORDByReference(new DWORD(privileges.size()));

            final DWORDByReference grantedAccess = new DWORDByReference();
            final BOOLByReference result = new BOOLByReference();
            if(!Advapi32.INSTANCE.AccessCheck(securityDescriptorMemoryPointer,
                    duplicatedToken.getValue(),
                    rights.getValue(),
                    mapping,
                    privileges, privilegeLength, grantedAccess, result)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            hasAccess = result.getValue().booleanValue();

        } finally {

            if(openedAccessToken != null && openedAccessToken.getValue() != null) {
                Kernel32.INSTANCE.CloseHandle(openedAccessToken.getValue());
            }

            if(duplicatedToken != null && duplicatedToken.getValue() != null) {
                Kernel32.INSTANCE.CloseHandle(duplicatedToken.getValue());
            }

            if(securityDescriptorMemoryPointer != null) {
                securityDescriptorMemoryPointer.clear();
            }
        }

        return hasAccess;
    }
	
    /**
     * Encrypts a file or directory.
     *
     * @param file
     *         The file or directory to encrypt.
     */
    public static void encryptFile(File file) {
        WString lpFileName = new WString(file.getAbsolutePath());
        if (!Advapi32.INSTANCE.EncryptFile(lpFileName)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Decrypts an encrypted file or directory.
     *
     * @param file
     *         The file or directory to decrypt.
     */
    public static void decryptFile(File file) {
        WString lpFileName = new WString(file.getAbsolutePath());
        if (!Advapi32.INSTANCE.DecryptFile(lpFileName, new DWORD(0))) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    /**
     * Checks the encryption status of a file.
     *
     * @param file
     *         The file to check the status for.
     * @return The status of the file.
     */ 
    public static int fileEncryptionStatus(File file) {
        DWORDByReference status = new DWORDByReference();
        WString lpFileName = new WString(file.getAbsolutePath());
        if (!Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, status)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return status.getValue().intValue();
    }

    /**
     * Disables or enables encryption of the specified directory and the files in
     * it.
     *
     * @param directory
     *         The directory for which to enable or disable encryption.
     * @param disable
     *         TRUE to disable encryption. FALSE to enable it.
     */
    public static void disableEncryption(File directory, boolean disable) {
        WString dirPath = new WString(directory.getAbsolutePath());
        if (!Advapi32.INSTANCE.EncryptionDisable(dirPath, disable)) {
            throw new Win32Exception(Native.getLastError());
        }
    }

    /**
     * Backup an encrypted file or folder without decrypting it. A file named
     * "bar/sample.text" will be backed-up to "destDir/sample.text". A directory
     * named "bar" will be backed-up to "destDir/bar". This method is NOT
     * recursive. If you have an encrypted directory with encrypted files, this
     * method must be called once for the directory, and once for each encrypted
     * file to be backed-up.
     *
     * @param src
     *         The encrypted file or directory to backup.
     * @param destDir
     *         The directory where the backup will be saved.
     */
    public static void backupEncryptedFile(File src, File destDir) {
        if (!destDir.isDirectory()) {
            throw new IllegalArgumentException("destDir must be a directory.");
        }

        ULONG readFlag = new ULONG(0); // Open the file for export (backup)
        ULONG writeFlag = new ULONG(CREATE_FOR_IMPORT); // Import (restore) file

        if (src.isDirectory()) {
            writeFlag.setValue(CREATE_FOR_IMPORT | CREATE_FOR_DIR);
        }
        
        // open encrypted file for export
        WString srcFileName = new WString(src.getAbsolutePath());
        PointerByReference pvContext = new PointerByReference();
        if (Advapi32.INSTANCE.OpenEncryptedFileRaw(srcFileName, readFlag,
                pvContext) != W32Errors.ERROR_SUCCESS) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        // read encrypted file
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FE_EXPORT_FUNC pfExportCallback = new FE_EXPORT_FUNC() {
            @Override
            public DWORD callback(ByteByReference pbData, Pointer pvCallbackContext,
                                  ULONG ulLength) {
                byte[] arr = pbData.getPointer().getByteArray(0, ulLength.intValue());
                try {
                    outputStream.write(arr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new DWORD(W32Errors.ERROR_SUCCESS);
            }
        };

        if (Advapi32.INSTANCE.ReadEncryptedFileRaw(pfExportCallback, null, 
                pvContext.getValue()) != W32Errors.ERROR_SUCCESS) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        // close
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());

        // open file for import
        WString destFileName = new WString(destDir.getAbsolutePath() + File.separator
                        + src.getName());
        pvContext = new PointerByReference();
        if (Advapi32.INSTANCE.OpenEncryptedFileRaw(destFileName, writeFlag,
                pvContext) != W32Errors.ERROR_SUCCESS) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        // write encrypted file
        final IntByReference elementsReadWrapper = new IntByReference(0);
        FE_IMPORT_FUNC pfImportCallback = new FE_IMPORT_FUNC() {
            @Override
            public DWORD callback(ByteByReference pbData, Pointer pvCallbackContext, 
                                  ULONGByReference ulLength) {
                int elementsRead = elementsReadWrapper.getValue();
                int remainingElements = outputStream.size() - elementsRead;
                int length = Math.min(remainingElements, ulLength.getValue().intValue());
                pbData.getPointer().write(0, outputStream.toByteArray(), elementsRead, 
                        length);
                elementsReadWrapper.setValue(elementsRead + length);
                ulLength.setValue(new ULONG(length));
                return new DWORD(W32Errors.ERROR_SUCCESS);
            }
        };

        if (Advapi32.INSTANCE.WriteEncryptedFileRaw(pfImportCallback, null, 
                pvContext.getValue()) != W32Errors.ERROR_SUCCESS) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        // close
        Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
    }
}
