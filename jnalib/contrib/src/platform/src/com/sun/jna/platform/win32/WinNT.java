package com.sun.jna.platform.win32;

/**
 * This module defines the 32-Bit Windows types and constants that are defined
 * by NT, but exposed through the Win32 API.
 * 
 * Ported from WinNT.h.
 * 
 * @author dblock[at]dblock.org Windows SDK 6.0A
 */
public abstract class WinNT {

	//
	// The following are masks for the predefined standard access types
	//

	public static final int DELETE = 0x00010000;
	public static final int READ_CONTROL = 0x00020000;
	public static final int WRITE_DAC = 0x00040000;
	public static final int WRITE_OWNER = 0x00080000;
	public static final int SYNCHRONIZE = 0x00100000;

	public static final int STANDARD_RIGHTS_REQUIRED = 0x000F0000;

	public static final int STANDARD_RIGHTS_READ = READ_CONTROL;
	public static final int STANDARD_RIGHTS_WRITE = READ_CONTROL;
	public static final int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;

	public static final int STANDARD_RIGHTS_ALL = 0x001F0000;

	public static final int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

	//
	// Token Specific Access Rights.
	//

	/**
	 * Required to attach a primary token to a process. The
	 * SE_ASSIGNPRIMARYTOKEN_NAME privilege is also required to accomplish this
	 * task.
	 */
	public static final int TOKEN_ASSIGN_PRIMARY = 0x0001;
	/**
	 * Required to duplicate an access token.
	 */
	public static final int TOKEN_DUPLICATE = 0x0002;
	/**
	 * Required to attach an impersonation access token to a process.
	 */
	public static final int TOKEN_IMPERSONATE = 0x0004;
	/**
	 * Required to query an access token.
	 */
	public static final int TOKEN_QUERY = 0x0008;
	/**
	 * Required to query the source of an access token.
	 */
	public static final int TOKEN_QUERY_SOURCE = 0x0010;
	/**
	 * Required to enable or disable the privileges in an access token.
	 */
	public static final int TOKEN_ADJUST_PRIVILEGES = 0x0020;
	/**
	 * Required to adjust the attributes of the groups in an access token.
	 */
	public static final int TOKEN_ADJUST_GROUPS = 0x0040;
	/**
	 * Required to change the default owner, primary group, or DACL of an access
	 * token.
	 */
	public static final int TOKEN_ADJUST_DEFAULT = 0x0080;
	/**
	 * Required to adjust the session ID of an access token. The SE_TCB_NAME
	 * privilege is required.
	 */
	public static final int TOKEN_ADJUST_SESSIONID = 0x0100;

	public static final int TOKEN_ALL_ACCESS_P = STANDARD_RIGHTS_REQUIRED
			| TOKEN_ASSIGN_PRIMARY | TOKEN_DUPLICATE | TOKEN_IMPERSONATE
			| TOKEN_QUERY | TOKEN_QUERY_SOURCE | TOKEN_ADJUST_PRIVILEGES
			| TOKEN_ADJUST_GROUPS | TOKEN_ADJUST_DEFAULT;

	/**
	 * Combines all possible access rights for a token.
	 */
	public static final int TOKEN_ALL_ACCESS = TOKEN_ALL_ACCESS_P
			| TOKEN_ADJUST_SESSIONID;

	/**
	 * Combines STANDARD_RIGHTS_READ and TOKEN_QUERY.
	 */
	public static final int TOKEN_READ = STANDARD_RIGHTS_READ | TOKEN_QUERY;

	/**
	 * Combines STANDARD_RIGHTS_WRITE, TOKEN_ADJUST_PRIVILEGES,
	 * TOKEN_ADJUST_GROUPS, and TOKEN_ADJUST_DEFAULT.
	 */
	public static final int TOKEN_WRITE = STANDARD_RIGHTS_WRITE
			| TOKEN_ADJUST_PRIVILEGES | TOKEN_ADJUST_GROUPS
			| TOKEN_ADJUST_DEFAULT;

	/**
	 * Combines STANDARD_RIGHTS_EXECUTE and TOKEN_IMPERSONATE.
	 */
	public static final int TOKEN_EXECUTE = STANDARD_RIGHTS_EXECUTE;

	public static final int THREAD_TERMINATE = 0x0001;
	public static final int THREAD_SUSPEND_RESUME = 0x0002;
	public static final int THREAD_GET_CONTEXT = 0x0008;
	public static final int THREAD_SET_CONTEXT = 0x0010;
	public static final int THREAD_QUERY_INFORMATION = 0x0040;
	public static final int THREAD_SET_INFORMATION = 0x0020;
	public static final int THREAD_SET_THREAD_TOKEN = 0x0080;
	public static final int THREAD_IMPERSONATE = 0x0100;
	public static final int THREAD_DIRECT_IMPERSONATION = 0x0200;
	public static final int THREAD_SET_LIMITED_INFORMATION = 0x0400;
	public static final int THREAD_QUERY_LIMITED_INFORMATION = 0x0800;
	public static final int THREAD_ALL_ACCESS = STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0x3FF;
}
