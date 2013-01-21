package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.ptr.IntByReference;

// TODO: Auto-generated Javadoc
/**
 * The Class AutomationException.
 */
public class AutomationException extends RuntimeException {

	/** The p excep info. */
	private EXCEPINFO pExcepInfo;

	/** The pu arg err. */
	private IntByReference puArgErr;

	/**
	 * Instantiates a new automation exception.
	 */
	public AutomationException() {
		super();
	}

	/**
	 * Instantiates a new automation exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public AutomationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new automation exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public AutomationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new automation exception.
	 *
	 * @param message the message
	 */
	public AutomationException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new automation exception.
	 *
	 * @param message the message
	 * @param pExcepInfo the excep info
	 * @param puArgErr the pu arg err
	 */
	public AutomationException(String message, EXCEPINFO pExcepInfo,
			IntByReference puArgErr) {
		super(message);
		this.pExcepInfo = pExcepInfo;
		this.puArgErr = puArgErr;
	}

	/**
	 * Instantiates a new automation exception.
	 *
	 * @param cause the cause
	 */
	public AutomationException(Throwable cause) {
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
