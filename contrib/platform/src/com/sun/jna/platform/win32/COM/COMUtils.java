package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.WinNT.HRESULT;

public abstract class COMUtils {

	public static final int S_OK = 0, S_FALSE = 1,
			REGDB_E_CLASSNOTREG = 0x80040154,
			CLASS_E_NOAGGREGATION = 0x80040110,
			CO_E_NOTINITIALIZED = 0x800401F0;

	public static final int E_UNEXPECTED = 0x8000FFFF;
	public static final int E_NOTIMPL = 0x80004001;
	public static final int E_OUTOFMEMORY = 0x8007000E;
	public static final int E_INVALIDARG = 0x80070057;
	public static final int E_NOINTERFACE = 0x80004002;
	public static final int E_POINTER = 0x80004003;
	public static final int E_HANDLE = 0x80070006;
	public static final int E_ABORT = 0x80004004;
	public static final int E_FAIL = 0x80004005;
	public static final int E_ACCESSDENIED = 0x80070005;

	public static final int DISP_E_BADVARTYPE = -2147352568;
	public static final int DISP_E_NOTACOLLECTION = -2147352559;
	public static final int DISP_E_MEMBERNOTFOUND = -2147352573;
	public static final int DISP_E_ARRAYISLOCKED = -2147352563;
	public static final int DISP_E_EXCEPTION = -2147352567;
	public static final int DISP_E_TYPEMISMATCH = -2147352571;
	public static final int DISP_E_BADINDEX = -2147352565;
	public static final int DISP_E_BADCALLEE = -2147352560;
	public static final int DISP_E_OVERFLOW = -2147352566;
	public static final int DISP_E_UNKNOWNINTERFACE = -2147352575;
	public static final int DISP_E_DIVBYZERO = -2147352558;
	public static final int DISP_E_UNKNOWNLCID = -2147352564;
	public static final int DISP_E_PARAMNOTOPTIONAL = -2147352561;
	public static final int DISP_E_PARAMNOTFOUND = -2147352572;
	public static final int DISP_E_BADPARAMCOUNT = -2147352562;
	public static final int DISP_E_BUFFERTOOSMALL = -2147352557;
	public static final int DISP_E_UNKNOWNNAME = -2147352570;
	public static final int DISP_E_NONAMEDARGS = -2147352569;

	public static boolean SUCCEEDED(HRESULT err) throws COMException {
		return SUCCEEDED(err.intValue());
	}

	public static boolean SUCCEEDED(int err) throws COMException {
		switch (err) {
		case S_OK:
			return true;
		case E_NOTIMPL:
			throw new COMException("Not implemented!");
		case E_INVALIDARG:
			throw new COMException("Invalid argument!");
		case E_OUTOFMEMORY:
			throw new COMException("Out of memory!");
		case E_UNEXPECTED:
			throw new COMException("Error " + Integer.toHexString(err));
		case CO_E_NOTINITIALIZED:
			throw new COMException("CoInitialized wasn't called!");
		case E_NOINTERFACE:
			throw new COMException("Interface does not inherit from class!");
		case E_POINTER:
			throw new COMException("Allocated pointer pointer is null!");
		case DISP_E_UNKNOWNNAME:
			throw new COMException(
					"One or more of the names were not known. The returned array of DISPIDs contains DISPID_UNKNOWN for each entry that corresponds to an unknown name!");
		case DISP_E_UNKNOWNLCID:
			throw new COMException(
					"The locale identifier (LCID) was not recognized!");
		case DISP_E_BADPARAMCOUNT:
			throw new COMException(
					"The number of elements provided to DISPPARAMS is different from the number of arguments accepted by the method or property!");
		case DISP_E_BADVARTYPE:
			throw new COMException(
					"One of the arguments in DISPPARAMS is not a valid variant type!");
		case DISP_E_EXCEPTION:
			throw new COMException(
					"The application needs to raise an exception. In this case, the structure passed in pexcepinfo should be filled in!");
		case DISP_E_MEMBERNOTFOUND:
			throw new COMException("The requested member does not exist!");
		case DISP_E_NONAMEDARGS:
			throw new COMException(
					"This implementation of IDispatch does not support named arguments!");
		case DISP_E_OVERFLOW:
			throw new COMException(
					"One of the arguments in DISPPARAMS could not be coerced to the specified type!");
		case DISP_E_PARAMNOTFOUND:
			throw new COMException(
					"One of the parameter IDs does not correspond to a parameter on the method. In this case, puArgErr is set to the first argument that contains the error!");
		case DISP_E_TYPEMISMATCH:
			throw new COMException(
					"One or more of the arguments could not be coerced. The index of the first parameter with the incorrect type within rgvarg is returned in puArgErr!");
		case DISP_E_UNKNOWNINTERFACE:
			throw new COMException(
					"The interface identifier passed in riid is not IID_NULL!");
		default:
			throw new COMException("Unexpected COM error code : " + err);
		}
	}
}
