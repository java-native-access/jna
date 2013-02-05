package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.sun.jna.ptr.IntByReference;

// TODO: Auto-generated Javadoc
/**
 * The Class COMUtils.
 */
public abstract class COMUtils {

	/** The Constant CO_E_NOTINITIALIZED. */
	public static final int S_OK = 0, S_FALSE = 1,
			REGDB_E_CLASSNOTREG = 0x80040154,
			CLASS_E_NOAGGREGATION = 0x80040110,
			CO_E_NOTINITIALIZED = 0x800401F0;

	/** The Constant E_UNEXPECTED. */
	public static final int E_UNEXPECTED = 0x8000FFFF;

	/** The Constant E_NOTIMPL. */
	public static final int E_NOTIMPL = 0x80004001;

	/** The Constant E_OUTOFMEMORY. */
	public static final int E_OUTOFMEMORY = 0x8007000E;

	/** The Constant E_INVALIDARG. */
	public static final int E_INVALIDARG = 0x80070057;

	/** The Constant E_NOINTERFACE. */
	public static final int E_NOINTERFACE = 0x80004002;

	/** The Constant E_POINTER. */
	public static final int E_POINTER = 0x80004003;

	/** The Constant E_HANDLE. */
	public static final int E_HANDLE = 0x80070006;

	/** The Constant E_ABORT. */
	public static final int E_ABORT = 0x80004004;

	/** The Constant E_FAIL. */
	public static final int E_FAIL = 0x80004005;

	/** The Constant E_ACCESSDENIED. */
	public static final int E_ACCESSDENIED = 0x80070005;

	/** The Constant DISP_E_BADVARTYPE. */
	public static final int DISP_E_BADVARTYPE = -2147352568;

	/** The Constant DISP_E_NOTACOLLECTION. */
	public static final int DISP_E_NOTACOLLECTION = -2147352559;

	/** The Constant DISP_E_MEMBERNOTFOUND. */
	public static final int DISP_E_MEMBERNOTFOUND = -2147352573;

	/** The Constant DISP_E_ARRAYISLOCKED. */
	public static final int DISP_E_ARRAYISLOCKED = -2147352563;

	/** The Constant DISP_E_EXCEPTION. */
	public static final int DISP_E_EXCEPTION = -2147352567;

	/** The Constant DISP_E_TYPEMISMATCH. */
	public static final int DISP_E_TYPEMISMATCH = -2147352571;

	/** The Constant DISP_E_BADINDEX. */
	public static final int DISP_E_BADINDEX = -2147352565;

	/** The Constant DISP_E_BADCALLEE. */
	public static final int DISP_E_BADCALLEE = -2147352560;

	/** The Constant DISP_E_OVERFLOW. */
	public static final int DISP_E_OVERFLOW = -2147352566;

	/** The Constant DISP_E_UNKNOWNINTERFACE. */
	public static final int DISP_E_UNKNOWNINTERFACE = -2147352575;

	/** The Constant DISP_E_DIVBYZERO. */
	public static final int DISP_E_DIVBYZERO = -2147352558;

	/** The Constant DISP_E_UNKNOWNLCID. */
	public static final int DISP_E_UNKNOWNLCID = -2147352564;

	/** The Constant DISP_E_PARAMNOTOPTIONAL. */
	public static final int DISP_E_PARAMNOTOPTIONAL = -2147352561;

	/** The Constant DISP_E_PARAMNOTFOUND. */
	public static final int DISP_E_PARAMNOTFOUND = -2147352572;

	/** The Constant DISP_E_BADPARAMCOUNT. */
	public static final int DISP_E_BADPARAMCOUNT = -2147352562;

	/** The Constant DISP_E_BUFFERTOOSMALL. */
	public static final int DISP_E_BUFFERTOOSMALL = -2147352557;

	/** The Constant DISP_E_UNKNOWNNAME. */
	public static final int DISP_E_UNKNOWNNAME = -2147352570;

	/** The Constant DISP_E_NONAMEDARGS. */
	public static final int DISP_E_NONAMEDARGS = -2147352569;

	/** The Constant CO_E_OBJNOTCONNECTED. */
	public static final int CO_E_OBJNOTCONNECTED = -2147220995;

	/**
	 * Succeeded.
	 *
	 * @param hr
	 *            the hr
	 * @return true, if successful
	 */
	public static boolean SUCCEEDED(HRESULT hr) {
		return SUCCEEDED(hr.intValue());
	}

	/**
	 * Succeeded.
	 *
	 * @param hr
	 *            the hr
	 * @return true, if successful
	 */
	public static boolean SUCCEEDED(int hr) {
		if (hr == S_OK)
			return true;
		else
			return false;
	}

	/**
	 * Failed.
	 *
	 * @param hr
	 *            the hr
	 * @return true, if successful
	 */
	public static boolean FAILED(HRESULT hr) {
		return FAILED(hr.intValue());
	}

	/**
	 * Failed.
	 *
	 * @param hr
	 *            the hr
	 * @return true, if successful
	 */
	public static boolean FAILED(int hr) {
		if (hr != S_OK)
			return true;
		else
			return false;
	}

	/**
	 * Throw new exception.
	 *
	 * @param hr
	 *            the hr
	 */
	public static void checkAutoRC(HRESULT hr) {
		checkAutoRC(hr, null, null);
	}

	/**
	 * Throw new exception.
	 *
	 * @param hr
	 *            the hr
	 * @param pExcepInfo
	 *            the excep info
	 * @param puArgErr
	 *            the pu arg err
	 */
	public static void checkAutoRC(HRESULT hr, EXCEPINFO pExcepInfo,
			IntByReference puArgErr) {
		switch (hr.intValue()) {
		case S_OK:
			return;
		case E_NOTIMPL:
			throw new COMException("Not implemented!");
		case E_INVALIDARG:
			throw new COMException("Invalid argument!");
		case E_OUTOFMEMORY:
			throw new COMException("Out of memory!");
		case E_UNEXPECTED:
			throw new COMException("Error "
					+ Integer.toHexString(hr.intValue()));
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
					"The application needs to raise an exception. In this case, the structure passed in pexcepinfo should be filled in!",
					pExcepInfo, puArgErr);
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
					"One of the parameter IDs does not correspond to a parameter on the method. In this case, puArgErr is set to the first argument that contains the error!",
					pExcepInfo, puArgErr);
		case DISP_E_TYPEMISMATCH:
			throw new COMException(
					"One or more of the arguments could not be coerced. The index of the first parameter with the incorrect type within rgvarg is returned in puArgErr!",
					pExcepInfo, puArgErr);
		case DISP_E_UNKNOWNINTERFACE:
			throw new COMException(
					"The interface identifier passed in riid is not IID_NULL!");
		case CO_E_OBJNOTCONNECTED:
			throw new COMException(
					"The method is not connected to the Dispatch pointer!");
		default:
			throw new COMException("Unexpected COM error code : "
					+ toHexStr(hr));
		}
	}

	public static void checkTypeLibRC(HRESULT hr) {
		switch (hr.intValue()) {
		case S_OK:
			return;
		case WinError.E_INVALIDARG:
			throw new COMException("One or more of the arguments is not valid.");
		case WinError.E_OUTOFMEMORY:
			throw new COMException(
					"Insufficient memory to complete the operation.");
		case WinError.TYPE_E_IOERROR:
			throw new COMException("The function could not write to the file.");
		case WinError.TYPE_E_INVALIDSTATE:
			throw new COMException("The type library could not be opened.");
		case WinError.TYPE_E_INVDATAREAD:
			throw new COMException(
					"The function could not read from the file. ");
		case WinError.TYPE_E_UNSUPFORMAT:
			throw new COMException("The type library has an older format.");
		case WinError.TYPE_E_UNKNOWNLCID:
			throw new COMException(
					"The LCID could not be found in the OLE-supported DLLs.");
		case WinError.TYPE_E_CANTLOADLIBRARY:
			throw new COMException(
					"The type library or DLL could not be loaded.");
		case WinError.TYPE_E_ELEMENTNOTFOUND:
			throw new COMException(
					"No type description was found in the library with the specified GUID.");
		default:
			throw new COMException("Unexpected Typelib error code : "
					+ toHexStr(hr));
		}
	}

	public static String getAllProgIDsOnSystem() {
		String result = "";
		int rc = 0;
		
		String[] regCLSIDkeys = Advapi32Util.registryGetKeys(WinReg.HKEY_CLASSES_ROOT, "CLSID");
		
		for (int i = 0; i < regCLSIDkeys.length; i++) {
			String value = Advapi32Util.registryGetStringValue(WinReg.HKEY_CLASSES_ROOT, "CLSID\\{" + regCLSIDkeys[i]+"}", null);
			System.out.println(value);
		}
		
		// open root key
		String subKey = "CLSID";
		HKEYByReference phkResult = new HKEYByReference();
		rc = Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CLASSES_ROOT, "CLSID",
				0, WinNT.KEY_READ, phkResult);
		checkRegistryRC(rc);
		
		// open subkey
		IntByReference lpcSubKeys = new IntByReference();
		rc = Advapi32.INSTANCE.RegQueryInfoKey(phkResult.getValue(), null,
				null, null, lpcSubKeys, null, null, null, null, null,
				new IntByReference(WinNT.KEY_READ), null);
		checkRegistryRC(rc);

		for (int i = 1; i < lpcSubKeys.getValue(); i++) {
			char[] lpName = new char[WinNT.MAX_PATH];
			IntByReference lpcClass = new IntByReference(WinNT.MAX_PATH);
			rc = Advapi32.INSTANCE.RegEnumKeyEx(phkResult.getValue(), i,
					lpName, lpcClass, null, null, null, null);
			checkRegistryRC(rc);
			subKey = Native.toString(lpName);

			HKEYByReference phkResult2 = new HKEYByReference();
			rc = Advapi32.INSTANCE.RegOpenKeyEx(phkResult.getValue(), subKey,
					0, WinNT.KEY_READ, phkResult2);
			checkRegistryRC(rc);
			
			for (int y = 0; y < 2; y++) {
				char[] lpName2 = new char[WinNT.MAX_PATH];
				IntByReference lpcClass2 = new IntByReference(WinNT.MAX_PATH);
				rc = Advapi32.INSTANCE.RegEnumKeyEx(phkResult2.getValue(), 1,
						lpName2, lpcClass2, null, null, null, null);
				checkRegistryRC(rc);
				String subKey2 = Native.toString(lpName2);
				
				if(subKey2.equals("ProgID")) {
					HKEYByReference phkResult3 = new HKEYByReference();
					rc = Advapi32.INSTANCE.RegOpenKeyEx(phkResult2.getValue(), subKey2,
							0, WinNT.KEY_READ, phkResult3);
					checkRegistryRC(rc);
					
					IntByReference lpcbData = new IntByReference();
					rc = Advapi32.INSTANCE.RegQueryValueEx(phkResult3.getValue(), null, 0, null, (char[])null, lpcbData);
					checkRegistryRC(rc);
					
					char[] lpData = new char[lpcbData.getValue()];
					rc = Advapi32.INSTANCE.RegQueryValueEx(phkResult3.getValue(), null, 0, null, lpData, lpcbData);
					checkRegistryRC(rc);
					
					result += Native.toString(lpData) + "\n";
				}
			}
		}

		return result;
	}
	
	public static void checkRegistryRC(int rc) {
		 if (rc != W32Errors.ERROR_SUCCESS && rc != W32Errors.ERROR_INSUFFICIENT_BUFFER) {
				throw new Win32Exception(rc);
		 }		
	}

	private static String toHexStr(HRESULT hr) {
		return "0x" + Integer.toHexString(hr.intValue()).toUpperCase();
	}
}
