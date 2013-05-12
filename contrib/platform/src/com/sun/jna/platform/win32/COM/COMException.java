/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.ptr.IntByReference;

/**
 * Exception class for all COM related classes.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class COMException extends RuntimeException {

	/** The p excep info. */
	private EXCEPINFO pExcepInfo;

	/** The pu arg err. */
	private IntByReference puArgErr;

	/**
	 * Instantiates a new automation exception.
	 */
	public COMException() {
		super();
	}

	/**
	 * Instantiates a new automation exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public COMException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new automation exception.
	 * 
	 * @param message
	 *            the message
	 */
	public COMException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new automation exception.
	 * 
	 * @param message
	 *            the message
	 * @param pExcepInfo
	 *            the excep info
	 * @param puArgErr
	 *            the pu arg err
	 */
	public COMException(String message, EXCEPINFO pExcepInfo,
			IntByReference puArgErr) {
		super(message);
		this.pExcepInfo = pExcepInfo;
		this.puArgErr = puArgErr;
	}

	/**
	 * Instantiates a new automation exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public COMException(Throwable cause) {
		super(cause);
	}

	/**
	 * Gets the excep info.
	 * 
	 * @return the excep info
	 */
	public EXCEPINFO getExcepInfo() {
		return pExcepInfo;
	}

	/**
	 * Gets the arg err.
	 * 
	 * @return the arg err
	 */
	public IntByReference getArgErr() {
		return puArgErr;
	}
}
