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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinNT.SID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.SID_NAME_USE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Advapi32 utility API.
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
	 * @return A user name.
	 */
	public static String getUserName() {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len); 
		
		if (! result) {
			
			int rc = Kernel32.INSTANCE.GetLastError();

			switch(rc) {
			case W32Errors.ERROR_INSUFFICIENT_BUFFER:
				buffer = new char[len.getValue()];
				break;
			default:
				throw new Win32Exception(Native.getLastError());
			}
			
			result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
		}
		
		if (! result) {
			throw new Win32Exception(Native.getLastError());
		}
		
		return Native.toString(buffer);
	}

	/**
	 * Retrieves a security identifier (SID) for the account on the current system.
	 * @param accountName Specifies the account name.
	 * @return A structure containing the account SID;
	 */
	public static Account getAccountByName(String accountName) {
		return getAccountByName(null, accountName);
	}
			
	/**
	 * Retrieves a security identifier (SID) for a given account.
	 * @param systemName Name of the system.
	 * @param accountName Account name.
	 * @return A structure containing the account SID.
	 */
	public static Account getAccountByName(String systemName, String accountName) {
		IntByReference pSid = new IntByReference(0);
		IntByReference cchDomainName = new IntByReference(0);
		PointerByReference peUse = new PointerByReference();
		
		if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, null, pSid, null, cchDomainName, peUse)) {
			throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
		}
		
		int rc = Kernel32.INSTANCE.GetLastError();
		if (pSid.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}

		Memory sidMemory = new Memory(pSid.getValue());
		PSID result = new PSID(sidMemory);
		char[] referencedDomainName = new char[cchDomainName.getValue() + 1]; 

		if (! Advapi32.INSTANCE.LookupAccountName(systemName, accountName, result, pSid, referencedDomainName, cchDomainName, peUse)) {
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
	 * @param sid SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(PSID sid) {
		return getAccountBySid(null, sid);
	}
	
	/**
	 * Get the account by SID.
	 * 
	 * @param systemName Name of the system.
	 * @param sid SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String systemName, PSID sid) {
    	IntByReference cchName = new IntByReference();
    	IntByReference cchDomainName = new IntByReference();
    	PointerByReference peUse = new PointerByReference();

    	if (Advapi32.INSTANCE.LookupAccountSid(null, sid, 
    			null, cchName, null, cchDomainName, peUse)) {
			throw new RuntimeException("LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
    	}
    	
    	int rc = Kernel32.INSTANCE.GetLastError();
		if (cchName.getValue() == 0 || rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
			throw new Win32Exception(rc);
		}    	
		
		char[] domainName = new char[cchDomainName.getValue()];
		char[] name = new char[cchName.getValue()];
    	
		if (! Advapi32.INSTANCE.LookupAccountSid(null, sid, 
    			name, cchName, domainName, cchDomainName, peUse)) {
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
	 * Convert a security identifier (SID) to a string format suitable for display, 
	 * storage, or transmission.
	 * @param sid SID bytes.
	 * @return String SID.
	 */
	public static String convertSidToStringSid(PSID sid) {
		PointerByReference stringSid = new PointerByReference();
		if (! Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		String result = stringSid.getValue().getString(0, true);
		Kernel32.INSTANCE.LocalFree(stringSid.getValue()); 
		return result;
	}
	
	/**
	 * Convert a string representation of a security identifier (SID) to 
	 * a binary format.
	 * @param sidString 
	 * 	String SID.
	 * @return SID bytes.
	 */
	public static byte[] convertStringSidToSid(String sidString) {
		PSIDByReference pSID = new PSIDByReference();
		if (! Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return pSID.getValue().getBytes();
	}
	
	/**
	 * Compares a SID to a well known SID and returns TRUE if they match.
	 * @param sidString
	 *  String representation of a SID.
	 * @param wellKnownSidType
	 *  Member of the WELL_KNOWN_SID_TYPE enumeration to compare with the SID at pSid.
	 * @return
	 *  True if the SID is of the well-known type, false otherwise.
	 */
	public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
		PSIDByReference pSID = new PSIDByReference();
		if (! Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
		return Advapi32.INSTANCE.IsWellKnownSid(pSID.getValue(), wellKnownSidType);
	}

	/**
	 * Compares a SID to a well known SID and returns TRUE if they match.
	 * @param sidBytes
	 *  Byte representation of a SID.
	 * @param wellKnownSidType
	 *  Member of the WELL_KNOWN_SID_TYPE enumeration to compare with the SID at pSid.
	 * @return
	 *  True if the SID is of the well-known type, false otherwise.
	 */
	public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
		PSID pSID = new PSID(sidBytes);
		return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);		
	}
	
	/**
	 * Get an account name from a string SID on the local machine.
	 * 
	 * @param sidString SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String sidString) {
		return getAccountBySid(null, sidString); 
	}
	
	/**
	 * Get an account name from a string SID.
	 * 
	 * @param systemName System name.
	 * @param sidString SID.
	 * @return Account.
	 */
	public static Account getAccountBySid(String systemName, String sidString) {
		return getAccountBySid(systemName, new PSID(convertStringSidToSid(sidString))); 
	}
	
	/**
	 * This function returns the groups associated with a security token,
	 * such as a user token.
	 * 
	 * @param hToken Token.
	 * @return Token groups.
	 */
	public static Account[] getTokenGroups(HANDLE hToken) {
    	// get token group information size
        IntByReference tokenInformationLength = new IntByReference();
        if (Advapi32.INSTANCE.GetTokenInformation(hToken, 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenGroups, null, 0, tokenInformationLength)) {
        	throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
        	throw new Win32Exception(rc);
        }
        // get token group information
		WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
        if (! Advapi32.INSTANCE.GetTokenInformation(hToken,
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
    		} catch(Exception e) {
    			group = new Account();
        		group.sid = sidAndAttribute.Sid.getBytes();
        		group.sidString = Advapi32Util.convertSidToStringSid(sidAndAttribute.Sid);
    			group.name = group.sidString;
    			group.fqn = group.sidString;
        		group.accountType = SID_NAME_USE.SidTypeGroup;
    		}
    		userGroups.add(group);
    	}
        return userGroups.toArray(new Account[0]);
	}

	/**
	 * This function returns the information about the user who owns a security token,
	 * 
	 * @param hToken Token.
	 * @return Token user.
	 */
	public static Account getTokenAccount(HANDLE hToken) {
    	// get token group information size
        IntByReference tokenInformationLength = new IntByReference();
        if (Advapi32.INSTANCE.GetTokenInformation(hToken, 
        		WinNT.TOKEN_INFORMATION_CLASS.TokenUser, null, 0, tokenInformationLength)) {
        	throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
        }
        int rc = Kernel32.INSTANCE.GetLastError();
        if (rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
        	throw new Win32Exception(rc);
        }
        // get token user information
		WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
        if (! Advapi32.INSTANCE.GetTokenInformation(hToken,
        		WinNT.TOKEN_INFORMATION_CLASS.TokenUser, user, 
        		tokenInformationLength.getValue(), tokenInformationLength)) {
        	throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }        
        return getAccountBySid(user.User.Sid);
	}
	
	/**
	 * Return the group memberships of the currently logged on user.
	 * @return An array of groups.
	 */
	public static Account[] getCurrentUserGroups() {		
    	HANDLEByReference phToken = new HANDLEByReference();    	
    	try {
    		// open thread or process token
        	HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
        	if (! Advapi32.INSTANCE.OpenThreadToken(threadHandle, 
        			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, true, phToken)) {
            	if (W32Errors.ERROR_NO_TOKEN != Kernel32.INSTANCE.GetLastError()) {
            		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            	}        	
            	HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            	if (! Advapi32.INSTANCE.OpenProcessToken(processHandle, 
            			WinNT.TOKEN_DUPLICATE | WinNT.TOKEN_QUERY, phToken)) {
            		throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            	}
        	}
        	return getTokenGroups(phToken.getValue());
    	} finally {
    		if (phToken.getValue() != WinBase.INVALID_HANDLE_VALUE) {
    			if (! Kernel32.INSTANCE.CloseHandle(phToken.getValue())) {
    				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    			}
    		}
    	}		
	}

	/**
	 * Checks whether a registry key exists.
	 * @param root 
	 *  HKEY_LOCAL_MACHINE, etc.
	 * @param key 
	 *  Path to the registry key.
	 * @return 
	 *  True if the key exists.
	 */
	public static boolean registryKeyExists(HKEY root, String key) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ, phkKey);
		switch(rc) {
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
	 * @param root
	 *  HKEY_LOCAL_MACHINE, etc.
	 * @param key
	 *  Registry key path.
	 * @param value
	 *  Value name.
	 * @return
	 *  True if the value exists.
	 */
	public static boolean registryValueExists(HKEY root, String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ, phkKey);
		try {
			switch(rc) {
			case W32Errors.ERROR_SUCCESS:
				break;
			case W32Errors.ERROR_FILE_NOT_FOUND:
				return false;
			default:
				throw new Win32Exception(rc);
			}
			IntByReference lpcbData = new IntByReference();
			IntByReference lpType = new IntByReference();
			rc = Advapi32.INSTANCE.RegQueryValueEx(
					phkKey.getValue(), value, 0, lpType, (char[]) null, lpcbData);
			switch(rc) {
			case W32Errors.ERROR_SUCCESS:
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
	 * @param root
	 *  Root key.
	 * @param key
	 *  Registry path.
	 * @param value
	 *  Name of the value to retrieve.
	 * @return
	 *  String value.
	 */
	public static String registryGetStringValue(HKEY root, String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			IntByReference lpcbData = new IntByReference();
			IntByReference lpType = new IntByReference();
			rc = Advapi32.INSTANCE.RegQueryValueEx(
					phkKey.getValue(), value, 0, lpType, (char[]) null, lpcbData);
			if (rc != W32Errors.ERROR_SUCCESS && rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
				throw new Win32Exception(rc);
			}
			if (lpType.getValue() != WinNT.REG_SZ) {
				throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
			}
			char[] data = new char[lpcbData.getValue()];
			rc = Advapi32.INSTANCE.RegQueryValueEx(
					phkKey.getValue(), value, 0, lpType, data, lpcbData);
			if (rc != W32Errors.ERROR_SUCCESS && rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
				throw new Win32Exception(rc);
			}
			return Native.toString(data);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}

	/**
	 * Get a registry DWORD value.
	 * @param root
	 *  Root key.
	 * @param key
	 *  Registry key path.
	 * @param value
	 *  Name of the value to retrieve.
	 * @return
	 *  Integer value.
	 */
	public static int registryGetIntValue(HKEY root, String key, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, WinNT.KEY_READ, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			IntByReference lpcbData = new IntByReference();
			IntByReference lpType = new IntByReference();
			rc = Advapi32.INSTANCE.RegQueryValueEx(
					phkKey.getValue(), value, 0, lpType, (char[]) null, lpcbData);
			if (rc != W32Errors.ERROR_SUCCESS && rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
				throw new Win32Exception(rc);
			}
			if (lpType.getValue() != WinNT.REG_DWORD) {
				throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
			}
			IntByReference data = new IntByReference();
			rc = Advapi32.INSTANCE.RegQueryValueEx(
					phkKey.getValue(), value, 0, lpType, data, lpcbData);
			if (rc != W32Errors.ERROR_SUCCESS && rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
				throw new Win32Exception(rc);
			}
			return data.getValue();
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}
	}
	
	/**
	 * Create a registry key.
	 * @param hKey 
	 *  Parent key.
	 * @param keyName 
	 *  Key name.
	 */
	public static void registryCreateKey(HKEY hKey, String keyName) {
		HKEYByReference phkResult = new HKEYByReference();
    	int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, null, 0, 
    			WinNT.KEY_READ, null, phkResult, null);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		rc = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}		
	}

	/**
	 * Create a registry key.
	 * @param root
	 *  Root key.
	 * @param parentPath
	 *  Path to an existing registry key.
	 * @param keyName
	 *  Key name.
	 */
	public static void registryCreateKey(HKEY root, String parentPath, String keyName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0, WinNT.KEY_CREATE_SUB_KEY, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
		try {
			registryCreateKey(phkKey.getValue(), keyName);
		} finally {
			rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
			if (rc != W32Errors.ERROR_SUCCESS) {
				throw new Win32Exception(rc);
			}
		}		
	}
	
	/**
	 * Set an integer value in registry.
	 * @param hKey
	 *  Parent key.
	 * @param name
	 *  Name.
	 * @param value
	 *  Value.
	 */
	public static void registrySetIntValue(HKEY hKey, String name, int value) {
        byte[] data = new byte[4];
        data[0] = (byte)(value & 0xff);
        data[1] = (byte)((value >> 8) & 0xff);
        data[2] = (byte)((value >> 16) & 0xff);
        data[3] = (byte)((value >> 24) & 0xff);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, WinNT.REG_DWORD, data, 4);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}
	}
	
	/**
	 * Set an integer value in registry.
	 * @param root
	 *  Root key.
	 * @param keyPath
	 *  Path to an existing registry key.
	 * @param name
	 *  Name.
	 * @param value
	 *  Value.
	 */
	public static void registrySetIntValue(HKEY root, String keyPath, String name, int value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
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
	 * Set a string value in registry.
	 * @param hKey
	 *  Parent key.
	 * @param name
	 *  Name.
	 * @param value
	 *  Value.
	 */
	public static void registrySetStringValue(HKEY hKey, String name, String value) {
    	char[] data = Native.toCharArray(value);
		int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, WinNT.REG_SZ, data, data.length * 2);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}	
	}

	/**
	 * Set a string value in registry.
	 * @param root
	 *  Root key.
	 * @param keyPath
	 *  Path to an existing registry key.
	 * @param name
	 *  Name.
	 * @param value
	 *  Value.
	 */
	public static void registrySetStringValue(HKEY root, String keyPath, String name, String value) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
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
	 * Delete a registry key.
	 * @param hKey
	 *  Parent key.
	 * @param keyName
	 *  Name of the key to delete.
	 */
	public static void registryDeleteKey(HKEY hKey, String keyName) {
		int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}		
	}
	
	/**
	 * Delete a registry key.
	 * @param root
	 *  Root key.
	 * @param keyPath
	 *  Path to an existing registry key.
	 * @param keyName
	 *  Name of the key to delete.
	 */
	public static void registryDeleteKey(HKEY root, String keyPath, String keyName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
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
	 * @param hKey
	 *  Parent key.
	 * @param valueName
	 *  Name of the value to delete.
	 */
	public static void registryDeleteValue(HKEY hKey, String valueName) {
		int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
		if (rc != W32Errors.ERROR_SUCCESS) {
			throw new Win32Exception(rc);
		}		
	}
	
	/**
	 * Delete a registry value.
	 * @param root
	 *  Root key.
	 * @param keyPath
	 *  Path to an existing registry key.
	 * @param valueName
	 *  Name of the value to delete.
	 */
	public static void registryDeleteValue(HKEY root, String keyPath, String valueName) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ | WinNT.KEY_WRITE, phkKey);
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
	 * @param hKey
	 *  Registry key.
	 * @return
	 *  Array of registry key names.
	 */
	public static String[] registryGetKeys(HKEY hKey) {
    	IntByReference lpcSubKeys = new IntByReference();
    	IntByReference lpcMaxSubKeyLen = new IntByReference();
    	int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, 
    			lpcSubKeys, lpcMaxSubKeyLen, null, null, null, null, null, null);
    	if (rc != W32Errors.ERROR_SUCCESS) {
    		throw new Win32Exception(rc);
    	}
    	ArrayList<String> keys = new ArrayList<String>(lpcSubKeys.getValue());
    	char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
    	for (int i = 0; i < lpcSubKeys.getValue(); i++) {
    		IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
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
	 * @param root
	 *  Root key.
	 * @param keyPath
	 *  Path to a registry key.
	 * @return
	 *  Array of registry key names.
	 */
	public static String[] registryGetKeys(HKEY root, String keyPath) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ, phkKey);
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
	 * Get a table of registry values.
	 * @param hKey
	 *  Registry key.
	 * @return
	 *  Table of values.
	 */
	public static TreeMap<String, Object> registryGetValues(HKEY hKey) {
    	IntByReference lpcValues = new IntByReference();
    	IntByReference lpcMaxValueNameLen = new IntByReference();
    	IntByReference lpcMaxValueLen = new IntByReference();
    	int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, null, 
    			null, null, lpcValues, lpcMaxValueNameLen, lpcMaxValueLen, null, null);
    	if (rc != W32Errors.ERROR_SUCCESS) {
    		throw new Win32Exception(rc);
    	}
		TreeMap<String, Object> keyValues = new TreeMap<String, Object>();
    	char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
    	byte[] data = new byte[lpcMaxValueLen.getValue()];
    	for (int i = 0; i < lpcValues.getValue(); i++) {
    		IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
    		IntByReference lpcbData = new IntByReference(lpcMaxValueLen.getValue());
    		IntByReference lpType = new IntByReference();
        	rc = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName, null, 
        			lpType, data, lpcbData);
        	if (rc != W32Errors.ERROR_SUCCESS) {
        		throw new Win32Exception(rc);
        	}
        	switch(lpType.getValue()) {
        	case WinNT.REG_DWORD:
            	keyValues.put(Native.toString(name), 
            			((int)(data[0] & 0xff)) + 
            			(((int)(data[1] & 0xff)) << 8) + 
            			(((int)(data[2] & 0xff)) << 16) + 
            			(((int)(data[3] & 0xff)) << 24));
        		break;
        	case WinNT.REG_SZ:
            	try {
					keyValues.put(Native.toString(name),
							new String(data, 0, data.length - 2, "UTF-16LE"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e.getMessage());
				}
        		break;
        	default:
        		throw new RuntimeException("Unsupported type: " + lpType.getValue());
        	}
    	}
    	return keyValues;
	}
	
	/**
	 * Get a table of registry values.
	 * @param root
	 *  Registry root.
	 * @param keyPath
	 *  Regitry key path.
	 * @return
	 *  Table of values.
	 */
	public static TreeMap<String, Object> registryGetValues(HKEY root, String keyPath) {
		HKEYByReference phkKey = new HKEYByReference();
		int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, WinNT.KEY_READ, phkKey);
		if (rc != W32Errors.ERROR_SUCCESS) {			throw new Win32Exception(rc);
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

}
