package com.sun.jna.platform.win32;

public abstract class NTStatus {
	
	public static final int STATUS_SUCCESS = 0x00000000; // ntsubauth
	public static final int STATUS_BUFFER_TOO_SMALL  = 0xC0000023;

	//
	// MessageId: STATUS_WAIT_0
	//
	// MessageText:
	//
	//  STATUS_WAIT_0
	//
	public static final int  STATUS_WAIT_0 = 0x00000000;    // winnt

	//
	// MessageId: STATUS_WAIT_1
	//
	// MessageText:
	//
	//  STATUS_WAIT_1
	//
	public static final int  STATUS_WAIT_1 = 0x00000001;

	//
	// MessageId: STATUS_WAIT_2
	//
	// MessageText:
	//
	//  STATUS_WAIT_2
	//
	public static final int  STATUS_WAIT_2 = 0x00000002;

	//
	// MessageId: STATUS_WAIT_3
	//
	// MessageText:
	//
	//  STATUS_WAIT_3
	//
	public static final int  STATUS_WAIT_3 = 0x00000003;

	//
	// MessageId: STATUS_WAIT_63
	//
	// MessageText:
	//
	//  STATUS_WAIT_63
	//
	public static final int  STATUS_WAIT_63 = 0x0000003F;

	//
	// The success status codes 128 - 191 are reserved for wait completion
	// status with an abandoned mutant object.
	//
	public static final int  STATUS_ABANDONED = 0x00000080;

	//
	// MessageId: STATUS_ABANDONED_WAIT_0
	//
	// MessageText:
	//
	//  STATUS_ABANDONED_WAIT_0
	//
	public static final int  STATUS_ABANDONED_WAIT_0 = 0x00000080;    // winnt

	//
	// MessageId: STATUS_ABANDONED_WAIT_63
	//
	// MessageText:
	//
	//  STATUS_ABANDONED_WAIT_63
	//
	public static final int  STATUS_ABANDONED_WAIT_63 = 0x000000BF;
}
